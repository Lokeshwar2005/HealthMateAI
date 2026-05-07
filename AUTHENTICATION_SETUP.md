# HealthMate AI - Authentication Setup

## Overview

This project implements a complete authentication system using **Jetpack Compose UI** and **Firebase Authentication**. The app supports both Login and Sign Up functionality with email/password authentication.

## Project Structure

```
com/example/healthmateai/
├── MainActivity.kt                          # Main entry point with Compose
├── ui/
│   ├── screens/
│   │   ├── AuthScreen.kt                   # Navigation between Login/SignUp
│   │   ├── LoginScreen.kt                  # Login UI & logic
│   │   ├── SignUpScreen.kt                 # Sign Up UI & logic
│   │   └── HomeScreen.kt                   # Post-login home screen
│   └── viewmodel/
│       └── AuthViewModel.kt                # Firebase auth business logic
```

## Features Implemented

### 1. **Login Screen**
- Email address input field
- Password input field with visibility toggle
- "Forgot Password?" link
- Error message display
- Loading state indicator
- Sign Up link to navigate to registration

### 2. **Sign Up Screen**
- Full name input field
- Email address input field
- Password input field with visibility toggle
- Confirm password field with validation
- Terms and Conditions checkbox
- Error message display
- Loading state indicator
- Login link to navigate back

### 3. **Authentication Logic (AuthViewModel)**
- Email/Password sign up with Firebase
- Email/Password login with Firebase
- User profile update (stores display name)
- Logout functionality
- Real-time auth state management using Kotlin Flow
- Error handling with user-friendly messages

### 4. **UI Design**
- Clean Material Design 3 with Compose
- Orange accent color (#FFA500) for buttons
- Blue accent color (#0099CC) for interactive elements
- Dark blue headers (#003366)
- Responsive layout with proper spacing
- Scroll support for long content
- Form validation before submission

## Technology Stack

### Dependencies Added:
```
- androidx.activity:activity-compose:1.10.0
- androidx.compose.* (Material3, UI, Foundation, Runtime)
- androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7
- com.google.firebase:firebase-auth:24.0.1
- org.jetbrains.kotlinx:kotlinx-coroutines-*:1.7.3
```

### Kotlin Compiler Extension:
- Version: 1.5.14 (for Compose compatibility)

## Firebase Configuration

### Prerequisites:
1. Firebase project created in Firebase Console
2. SHA1 and SHA256 fingerprints added to project settings
3. `google-services.json` file placed in `app/` directory
4. Authentication enabled in Firebase (Email/Password provider)

### Firestore Rules (Optional):
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{uid} {
      allow read, write: if request.auth.uid == uid;
    }
  }
}
```

## How It Works

### 1. **User Launch**
- MainActivity initializes Firebase and launches Compose UI
- AuthViewModel checks if user is already logged in
- If logged in → Home Screen
- If not logged in → Auth Screen (Login/SignUp)

### 2. **Login Flow**
1. User enters email and password
2. Clicks "CONTINUE" button
3. AuthViewModel calls `FirebaseAuth.signInWithEmailAndPassword()`
4. On success → Home screen displayed
5. On error → Error message shown with specific reason

### 3. **Sign Up Flow**
1. User enters name, email, password, and confirms password
2. Checks Terms & Conditions checkbox
3. Clicks "CREATE ACCOUNT" button
4. AuthViewModel calls `FirebaseAuth.createUserWithEmailAndPassword()`
5. Updates user profile with display name
6. On success → Home screen displayed
7. On error → Error message shown with specific reason

### 4. **Logout Flow**
1. User clicks "LOGOUT" button on Home Screen
2. AuthViewModel calls `FirebaseAuth.signOut()`
3. Returns to Login/SignUp screen

## Error Messages

The app displays user-friendly error messages for:
- Invalid email format
- Email already registered
- Incorrect password
- Email not found
- Weak password
- Network errors

## State Management

Uses Kotlin Flow for reactive state management:
- `authState`: Current authentication state (Idle, Loading, Success, Error)
- `isLoggedIn`: Boolean indicating login status
- Updates UI automatically when state changes

## Running the Project

1. **Build the project:**
   ```bash
   ./gradlew build
   ```

2. **Run on emulator/device:**
   ```bash
   ./gradlew installDebug
   ```

3. **Expected flow:**
   - App launches with Login/SignUp screen
   - Click "Sign up" to register new account
   - Enter details and click "CREATE ACCOUNT"
   - On success, Home Screen appears with welcome message
   - Click "LOGOUT" to return to Login screen

## Testing Credentials

After creating an account via the Sign Up screen, use the same email/password to login.

## Future Enhancements

- [ ] Password reset functionality
- [ ] Email verification
- [ ] Social login (Google, Facebook)
- [ ] Biometric authentication
- [ ] User profile management
- [ ] Remember me functionality

## File Changes Summary

### New Files Created:
- `ui/screens/LoginScreen.kt` (202 lines)
- `ui/screens/SignUpScreen.kt` (280 lines)
- `ui/screens/AuthScreen.kt` (42 lines)
- `ui/screens/HomeScreen.kt` (78 lines)
- `ui/viewmodel/AuthViewModel.kt` (130 lines)

### Modified Files:
- `app/build.gradle.kts` - Added Compose and Firebase dependencies
- `gradle/libs.versions.toml` - Added dependency versions
- `MainActivity.kt` - Converted to Compose-based architecture
- `activity_main.xml` - No longer used (Compose takes over)

## Troubleshooting

### Issue: Build fails with compose-bom
**Solution:** Ensure all Compose libraries use the platform (BOM) version

### Issue: Firebase not initializing
**Solution:** Verify `google-services.json` is in the correct location and Firebase project is properly configured

### Issue: Compose preview not working
**Solution:** Install latest Android Studio and ensure Kotlin compiler extension version matches

---

**Project Status:** Ready for testing and further development

