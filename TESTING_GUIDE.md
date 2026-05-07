# Testing Guide - HealthMate AI Authentication

## Pre-Testing Checklist

- [x] Firebase project created and configured
- [x] SHA1 & SHA256 keys added to Firebase
- [x] `google-services.json` in app folder
- [x] Email/Password authentication enabled in Firebase Console
- [x] Project builds successfully
- [x] Android emulator or device ready

## Test Scenarios

### Test 1: Sign Up New User

**Steps:**
1. Launch app
2. Click "Sign up" link at bottom of login screen
3. Fill in the form:
   - **Name**: John Doe
   - **Email**: johndoe@example.com
   - **Password**: Password123
   - **Confirm Password**: Password123
   - **Check**: Terms and Conditions checkbox
4. Click "CREATE ACCOUNT" button

**Expected Results:**
✓ Form validates (all fields required)
✓ Loading spinner appears during submission
✓ Firebase creates account and stores display name
✓ Auto-navigates to Home Screen
✓ Home screen shows "Hi, John Doe!"

**Failure Cases to Check:**
- Email already exists → Error message shown
- Password < 6 chars → Error message shown
- Passwords don't match → Button disabled
- Terms not checked → Button disabled
- Invalid email format → Error message shown

---

### Test 2: Login with Existing User

**Steps:**
1. From Home Screen, click "LOGOUT"
2. Back on login screen, enter:
   - **Email**: johndoe@example.com
   - **Password**: Password123
3. Click "CONTINUE" button

**Expected Results:**
✓ Loading spinner appears
✓ Firebase validates credentials
✓ Auto-navigates to Home Screen
✓ No errors shown

**Failure Cases to Check:**
- Wrong password → "Incorrect password" error
- Email not found → "Email not found" error
- Empty fields → Button disabled until filled
- Invalid email → Error message shown

---

### Test 3: Error Handling

**Scenario A: Wrong Password**
1. Sign up a new account
2. Logout
3. Try to login with wrong password
4. Expected: Error message "Incorrect password."

**Scenario B: Email Already Registered**
1. Sign up Account A: email1@test.com
2. Try to sign up again with same email
3. Expected: Error message "This email is already registered"

**Scenario C: Weak Password**
1. Try to sign up with password "123"
2. Expected: Error message about password length

**Scenario D: Password Mismatch**
1. Enter password and different confirm password
2. Expected: Error message "Passwords do not match"

---

### Test 4: UI/UX Features

**Password Visibility Toggle**
1. On Login screen, type in password field
2. Click eye icon (toggle visibility)
3. Expected: Password text becomes visible
4. Click again → Password hidden

**Form Navigation**
1. On Login screen, click "Sign up" link
2. Expected: Switches to Sign Up screen
3. On Sign Up screen, click "Sign in" link
4. Expected: Returns to Login screen

**Button States**
1. On Login: Click CONTINUE with empty fields
2. Expected: Button is disabled (grayed out)
3. Fill in valid email/password
4. Expected: Button becomes enabled (orange)

---

### Test 5: Session Management

**Persistent Login**
1. Sign in successfully
2. Close and reopen app
3. Expected: Should still be on Home screen (session persists)

**Logout**
1. On Home screen, click "LOGOUT"
2. Expected: Returns to Login screen
3. Session is cleared

**Multiple Accounts**
1. Create Account A with email: user1@test.com
2. Logout
3. Create Account B with email: user2@test.com
4. Logout
5. Login with Account A credentials
6. Expected: Account A loads with correct name/email

---

### Test 6: Input Validation

**Email Validation**
- Test cases:
  - `test@example.com` ✓ Valid
  - `test.email@example.co.uk` ✓ Valid
  - `testexample.com` ✗ Invalid
  - `test@.com` ✗ Invalid
  - Empty field ✗ Invalid

**Password Requirements**
- Minimum 6 characters (Firebase requirement)
- Special characters optional
- Case sensitive
- Spaces allowed

**Name Field**
- Any text allowed (letters, numbers, special chars)
- Required for sign up
- Stored as display name in Firebase

---

### Test 7: Performance Testing

**Network Latency**
1. Turn on Airplane Mode (simulate offline)
2. Try to login
3. Expected: Error after timeout or "No network" message
4. Turn off Airplane Mode
5. Try login again
6. Expected: Works correctly

**Loading States**
1. On slow network, observe loading spinner
2. Expected: Spinner shows until response received
3. Button disabled during loading
4. Can't click multiple times

---

### Test 8: Visual Design Verification

**Colors**
- [ ] Orange buttons (#FFA500) on Login/SignUp
- [ ] Blue headers (#003366)
- [ ] Blue links (#0099CC)
- [ ] Red error messages (#D32F2F)
- [ ] Gray text (#666666)

**Layout**
- [ ] Form fields centered and properly spaced
- [ ] Icons align with input fields
- [ ] Error messages clearly visible
- [ ] Buttons span full width
- [ ] Text is readable (good contrast)

**Responsive Design**
- [ ] Test on different screen sizes
- [ ] Content scrolls if too long
- [ ] No overlapping elements
- [ ] Touch targets are adequate (>48dp)

---

## Firebase Console Verification

After testing, verify in Firebase Console:

1. **Authentication Users**
   - Go to Firebase Console → Project → Authentication
   - Should see created user accounts with:
     - Email address
     - Creation date
     - Last sign-in time

2. **User Details**
   - Click on a user
   - Should see display name stored
   - Email verified status
   - UID (unique identifier)

3. **Authentication Logs**
   - Check "Auth Activity" or "Analytics"
   - Should see login/signup events

---

## Troubleshooting Common Issues

### Issue: "google-services.json not found"
**Solution:**
- Place `google-services.json` in `app/` directory
- Rebuild project

### Issue: Firebase authentication fails silently
**Solution:**
- Check Firebase Console → Authentication → Sign-in method
- Ensure Email/Password is enabled
- Check internet connection

### Issue: User info not persisting across sessions
**Solution:**
- Firebase Auth automatically manages session
- Check if user is being properly logged out
- Clear app data and try again

### Issue: UI not showing (Compose error)
**Solution:**
- Verify Compose dependencies are correct
- Check Kotlin compiler extension version: 1.5.14
- Ensure `buildFeatures { compose = true }` is set

### Issue: "Cannot resolve symbol" errors
**Solution:**
- Run `./gradlew.bat clean build`
- Invalidate cache and restart Android Studio
- Check all imports are correct

---

## Load Testing

**Rapid Sign Up / Sign Ins:**
1. Create 5 accounts rapidly
2. Expected: No errors, all accounts created successfully
3. Firebase should handle concurrent requests

**Concurrent Logins:**
1. Login on device A
2. Simultaneously logout and login on device B
3. Expected: Session properly handled on each device

---

## Security Testing

**Password Security:**
1. Never logs password in console
2. Password field is masked
3. Passwords are securely transmitted to Firebase
4. Error messages don't expose valid passwords

**Session Security:**
1. Session token properly stored
2. Logout clears session
3. No sensitive data in shared preferences
4. User data not stored in app cache

---

## Test Results Summary

| Test Case | Status | Notes |
|-----------|--------|-------|
| Sign Up New User | ✓/✗ | |
| Login Existing User | ✓/✗ | |
| Wrong Password Error | ✓/✗ | |
| Email Already Exists | ✓/✗ | |
| Password Visibility | ✓/✗ | |
| Form Navigation | ✓/✗ | |
| Logout | ✓/✗ | |
| Session Persistence | ✓/✗ | |
| Error Messages | ✓/✗ | |
| UI Design | ✓/✗ | |

---

## Notes for QA

- App should handle both online and offline scenarios gracefully
- User feedback (loading, errors, success) should be clear
- Navigation should be smooth and intuitive
- No data loss during network interruptions
- Performance should be acceptable (< 2 seconds for auth operations)

---

**Last Updated**: December 2024

