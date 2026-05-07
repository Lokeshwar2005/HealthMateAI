# Authentication Flow Diagram

## User Journey Map

```
┌─────────────────────────────────────────────────────────────┐
│                     App Launches                             │
│                   (MainActivity)                             │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
         ┌───────────────────────────────┐
         │  Check Firebase Login Status  │
         │   (AuthViewModel.isLoggedIn)  │
         └─────────┬─────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
   Not Logged In        Already Logged In
        │                     │
        ▼                     ▼
   ┌─────────────┐    ┌──────────────┐
   │ AuthScreen  │    │  HomeScreen  │
   └──────┬──────┘    │              │
          │           │  - Welcome   │
          │           │  - Logout    │
          │           └──────────────┘
          │
    ┌─────┴─────┐
    │            │
    ▼            ▼
┌─────────┐  ┌─────────┐
│  Login  │  │  SignUp │
│ Screen  │  │ Screen  │
└─────┬───┘  └────┬────┘
      │           │
      │    ┌──────┴──────────────────┐
      │    │                         │
      │    ▼                         ▼
      │  Enter Name            Enter Details
      │  Enter Email      ┌─────────────────┐
      │  Enter Password   │ • Name          │
      │                   │ • Email         │
      │                   │ • Password      │
      │                   │ • Confirm Pass  │
      │                   │ • Terms Agree   │
      │                   └────────┬────────┘
      │                            │
      │                            ▼
      │            ┌───────────────────────┐
      │            │ Validate Form         │
      │            │ - Email format        │
      │            │ - Password match      │
      │            │ - Terms accepted      │
      │            └───────────┬───────────┘
      │                        │
      │    ┌───────────────────┘
      │    │
      ▼    ▼
   ┌────────────────────────┐
   │  Firebase Auth Action  │
   │                        │
   │ LOGIN:                 │
   │  signInWithEmailAndPwd │
   │                        │
   │ SIGNUP:                │
   │  createUserWithEmailPwd│
   │  updateUserProfile     │
   └────────┬───────────────┘
            │
    ┌───────┴────────┐
    │                │
    ▼                ▼
SUCCESS           ERROR
    │                │
    ▼                ▼
┌──────────┐   ┌──────────────┐
│Set State │   │ Show Error   │
│Success   │   │ Message      │
└────┬─────┘   └──────┬───────┘
     │                │
     │ isLoggedIn     │ Stay on
     │ = true         │ Current Screen
     │                │
     ▼                ▼
Navigate to    ┌─────────────┐
HomeScreen     │ Retry Login │
               │   or        │
               │ Retry SignUp│
               └─────────────┘
```

## State Machine

```
┌──────────────────────────────────────────────────┐
│           AuthViewModel States                   │
└──────────────────────────────────────────────────┘

        ┌──────────────┐
        │    IDLE      │ (Initial state)
        └──────┬───────┘
               │
        User clicks Login/SignUp
               │
               ▼
        ┌──────────────┐
        │   LOADING    │
        └──────┬───────┘
               │
        ┌──────┴──────┐
        │             │
        ▼             ▼
    ┌────────┐   ┌────────┐
    │SUCCESS │   │ ERROR  │
    └────┬───┘   └───┬────┘
         │           │
    Navigate     Show Message
    to Home      Retry
```

## Component Interaction

```
┌────────────────────────────────────────────────────┐
│              MainActivity                           │
│  (Handles: Firebase init, Compose setup)          │
└──────────────────┬─────────────────────────────────┘
                   │
         ┌─────────┴─────────┐
         │                   │
         ▼                   ▼
    ┌─────────────┐  ┌───────────────┐
    │ AuthScreen  │  │  HomeScreen   │
    │ (Router)    │  │ (Post-login)  │
    └──┬──────────┘  └───────────────┘
       │
       ├────────┬────────┐
       │        │        │
       ▼        ▼        ▼
    ┌─────┐ ┌─────┐ ┌──────────┐
    │Login│ │Signup│ │ViewModel │
    │─────│ │─────│ │          │
    │UI   │ │UI   │ │ Firebase │
    │Form │ │Form │ │ State    │
    │     │ │     │ │ Logic    │
    └─────┘ └─────┘ └──────────┘
       │        │        │
       └────────┴────────┘
              │
              ▼
      ┌───────────────────┐
      │  Firebase Auth    │
      │                   │
      │ • createUser      │
      │ • signIn          │
      │ • signOut         │
      │ • updateProfile   │
      └───────────────────┘
```

## Data Flow

```
User Input (LoginScreen / SignUpScreen)
    │
    │ email, password, name, confirmPassword
    │
    ▼
AuthViewModel Methods
    │
    ├─ login(email, password)
    │  ├─ Set state → LOADING
    │  ├─ Call FirebaseAuth.signInWithEmailAndPassword()
    │  └─ Update state → SUCCESS or ERROR
    │
    └─ signUp(name, email, password)
       ├─ Set state → LOADING
       ├─ Call FirebaseAuth.createUserWithEmailAndPassword()
       ├─ Call user.updateProfile(displayName)
       └─ Update state → SUCCESS or ERROR
    
    │
    ▼
StateFlow Updates
    │
    ├─ authState (Idle, Loading, Success, Error)
    └─ isLoggedIn (true/false)
    
    │
    ▼
UI Re-composition (Automatic)
    │
    ├─ Show/hide loading indicator
    ├─ Show/hide error message
    └─ Navigate to next screen
```

## Login Flow Sequence

```
User          LoginScreen      AuthViewModel      FirebaseAuth
 │                 │                 │                  │
 ├─ Enter email ───→│                 │                  │
 │                  │                 │                  │
 ├─ Enter password →│                 │                  │
 │                  │                 │                  │
 ├─ Click Continue──→│                 │                  │
 │                  │                 │                  │
 │                  ├─ Call login() ───→│                 │
 │                  │                   │                 │
 │                  │                   ├─ signInWithEmailAndPassword()
 │                  │                   ├──────────────────→│
 │                  │                   │                   │
 │                  │                   │←─ Success/Error ─┤
 │                  │                   │                  │
 │                  │←─ Success State ──│                  │
 │                  │                   │                  │
 │←─ Navigate Home ─┤                   │                  │
```

## Sign Up Flow Sequence

```
User          SignUpScreen     AuthViewModel      FirebaseAuth
 │                 │                 │                  │
 ├─ Enter name ────→│                 │                  │
 ├─ Enter email ───→│                 │                  │
 ├─ Enter password →│                 │                  │
 ├─ Confirm pwd ───→│                 │                  │
 ├─ Check terms ───→│                 │                  │
 │                  │                 │                  │
 ├─ Click Create ───→│                 │                  │
 │                  │                 │                  │
 │                  ├─ Validate form ──→│                 │
 │                  │                   │                 │
 │                  ├─ Call signUp() ───→│                 │
 │                  │                   │                 │
 │                  │                   ├─ createUserWithEmailAndPassword()
 │                  │                   ├──────────────────→│
 │                  │                   │                   │
 │                  │                   │←─ AuthResult ─────│
 │                  │                   │                  │
 │                  │                   ├─ updateProfile(displayName)
 │                  │                   ├──────────────────→│
 │                  │                   │                   │
 │                  │                   │←─ Success ────────│
 │                  │                   │                  │
 │                  │←─ Success State ──│                  │
 │                  │                   │                  │
 │←─ Navigate Home ─┤                   │                  │
```

---

This visual guide helps understand the app's authentication architecture and data flow.

