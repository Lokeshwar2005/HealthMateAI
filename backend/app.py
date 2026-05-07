from flask import Flask, request, jsonify
import joblib
import pandas as pd
import shap
import os
from openai import OpenAI

app = Flask(__name__)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_DIR = os.path.abspath(os.path.join(BASE_DIR, ".."))
MODEL_DIR = os.path.join(PROJECT_DIR, "ml_notebooks")

# Load models once
DIABETES_MODEL = joblib.load(os.path.join(MODEL_DIR, "diabetes_model.pkl"))
DIABETES_FEATURES = joblib.load(os.path.join(MODEL_DIR, "diabetes_features.pkl"))
DIABETES_THRESHOLD = joblib.load(os.path.join(MODEL_DIR, "diabetes_threshold.pkl"))

HEART_MODEL = joblib.load(os.path.join(MODEL_DIR, "model.pkl"))
HEART_FEATURES = joblib.load(os.path.join(MODEL_DIR, "features.pkl"))
HEART_THRESHOLD = 0.35

FEATURE_MAP = {
    "Age": "Age-related risk",
    "Glucose": "High blood sugar level",
    "BMI": "High body weight (BMI)",
    "Insulin": "Insulin imbalance",
    "BloodPressure": "Blood pressure level",
    "DiabetesPedigreeFunction": "Genetic diabetes risk",
    "bmi_age": "Combined effect of age and body weight",
    "glucose_bmi": "Combined effect of glucose and BMI",
    "age": "Age-related risk",
    "anaemia": "Reduced oxygen-carrying blood capacity",
    "diabetes": "Diabetes comorbidity burden",
    "high_blood_pressure": "Chronic blood pressure burden",
    "sex": "Sex-related risk modifier",
    "smoking": "Smoking-related cardiovascular risk",
    "platelets": "Platelet count and clotting profile",
    "ejection_fraction": "Heart pumping efficiency",
    "serum_creatinine": "Kidney function level",
    "serum_sodium": "Electrolyte balance marker",
    "creatinine_phosphokinase": "Muscle damage indicator",
    "creatinine_sodium_ratio": "Kidney-electrolyte balance",
    "bp_dummy": "Blood pressure risk flag",
}


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})


def build_risk_level(probability, threshold):
    """Use the model threshold to anchor Medium/High boundary."""
    if probability >= threshold + 0.2:
        return "High"
    elif probability >= threshold - 0.1:
        return "Medium"
    else:
        return "Low"


def prepare_input_frame(disease, input_data, features):
    df = pd.DataFrame([input_data])

    # Convert all columns to numeric
    for col in df.columns:
        df[col] = pd.to_numeric(df[col], errors="coerce")

    # Add disease-aware defaults for missing model features
    defaults = {}
    if disease == "heart":
        defaults = {
            "anaemia": 1,
            "diabetes": 1,
            "high_blood_pressure": 1,
            "sex": 1,
            "smoking": 0,
            "platelets": 250000,
        }

    for key, value in defaults.items():
        if key not in df.columns:
            df[key] = value

    # Feature engineering
    if disease == "heart":
        df["bp_dummy"] = pd.to_numeric(
            df.get("high_blood_pressure", 0), errors="coerce"
        )
        if "serum_creatinine" in df.columns and "serum_sodium" in df.columns:
            df["creatinine_sodium_ratio"] = (
                df["serum_creatinine"] / df["serum_sodium"]
            )

    df = df.fillna(0)

    return df.reindex(columns=features, fill_value=0)


def validate_inputs(disease, input_data):
    """Basic input validation. Returns an error string or None."""
    required = {
        "diabetes": ["Glucose", "BMI", "Age"],
        "heart": ["ejection_fraction", "serum_creatinine", "serum_sodium", "age"],
    }
    missing = [f for f in required.get(disease, []) if f not in input_data]
    if missing:
        return f"Missing required fields: {', '.join(missing)}"
    return None


@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json(force=True)
    disease = (data.get("disease") or "").lower()
    input_data = data.get("inputs") or {}

    if disease == "diabetes":
        model = DIABETES_MODEL
        features = DIABETES_FEATURES
        threshold = DIABETES_THRESHOLD
    elif disease == "heart":
        model = HEART_MODEL
        features = HEART_FEATURES
        threshold = HEART_THRESHOLD
    else:
        return jsonify({"error": "Invalid disease. Use 'diabetes' or 'heart'."}), 400

    # Validate inputs before proceeding
    validation_error = validate_inputs(disease, input_data)
    if validation_error:
        return jsonify({"error": validation_error}), 422

    df = prepare_input_frame(disease, input_data, features)

    probability = model.predict_proba(df)[0][1]
    risk_level = build_risk_level(probability, threshold)

    # Rule-based clinical override for diabetes (only escalates, never downgrades)
    if disease == "diabetes":
        try:
            glucose = float(input_data.get("Glucose", 0))
        except (TypeError, ValueError):
            glucose = 0.0

        if glucose >= 160:
            risk_level = "High"
        elif glucose >= 130 and risk_level == "Low":
            risk_level = "Medium"

    # SHAP explanation — handle both binary and multi-output shap_values shapes
    explainer = shap.TreeExplainer(model)
    shap_values = explainer.shap_values(df)

    # For binary classifiers sklearn may return a list [class_0, class_1]
    if isinstance(shap_values, list):
        shap_array = shap_values[1]   # use positive-class SHAP values
    else:
        shap_array = shap_values      # already a single 2-D array

    shap_series = pd.Series(abs(shap_array[0]), index=df.columns)
    top_features = shap_series.sort_values(ascending=False).head(5).index.tolist()
    readable = [FEATURE_MAP.get(f, f) for f in top_features]

    prompt = f"""
Risk Level: {risk_level}
Factors: {", ".join(readable)}
Give short medical advice.
"""

    openrouter_api_key = os.getenv("OPENROUTER_API_KEY")
    if not openrouter_api_key:
        return jsonify({"error": "Missing OPENROUTER_API_KEY environment variable"}), 500

    # Initialize OpenRouter client (OpenAI-compatible API)
    client = OpenAI(
        api_key=openrouter_api_key,
        base_url="https://openrouter.ai/api/v1",
        timeout=60.0,
    )

    try:
        # Stream the response from OpenRouter
        stream = client.chat.completions.create(
            model="tencent/hy3-preview:free",
            messages=[
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            stream_options={"include_usage": True},
            stream=True
        )

        advice = ""
        reasoning_tokens = 0
        
        for chunk in stream:
            if chunk.choices[0].delta.content:
                advice += chunk.choices[0].delta.content
            
            # Usage information comes in the final chunk
            if chunk.usage:
                reasoning_tokens = getattr(chunk.usage, 'reasoning_tokens', 0)
    except Exception as e:
        if "timed out" in str(e).lower():
            advice = "Advice service timed out. Please consult a healthcare professional."
        else:
            advice = f"Advice service unavailable: {str(e)}"

    return jsonify(
        {
            "risk": risk_level,
            "probability": float(probability),
            "factors": readable,
            "advice": advice,
        }
    )


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.getenv("PORT", 5000)), debug=False)