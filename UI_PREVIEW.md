# HealthMate AI - Login & Sign-Up UI Preview

## Login Screen Layout

```
┌─────────────────────────────────┐
│     HealthMate AI Login         │
├─────────────────────────────────┤
│                                 │
│      ┌─────────────────┐        │
│      │  🔒 (in teal)  │        │
│      └─────────────────┘        │
│                                 │
│   Sign in / Sign up             │
│   Health at your fingertips     │
│                                 │
├─────────────────────────────────┤
│                                 │
│  📧 Enter Mobile Number         │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━    │
│                                 │
│  🔒 Password                    │
│  ━━━━━━━━━━━━━━━━━━━━━━━ 👁   │
│                                 │
│           Forgot Password?       │
│                                 │
│  ┌─────────────────────────────┐│
│  │  CONTINUE (Orange)          ││
│  └─────────────────────────────┘│
│                                 │
│  ─────────── OR ────────────     │
│                                 │
│  ┌─────────────────────────────┐│
│  │  🔍 Sign in with Google     ││
│  └─────────────────────────────┘│
│                                 │
│  Don't have an account? Sign up │
│                                 │
│  ☑ I agree to T&C              │
│    and Privacy Policy            │
│                                 │
└─────────────────────────────────┘
```

## Sign-Up Screen Layout

```
┌─────────────────────────────────┐
│     HealthMate AI SignUp        │
├─────────────────────────────────┤
│                                 │
│      ┌─────────────────┐        │
│      │  👤 (in orange) │        │
│      └─────────────────┘        │
│                                 │
│      Create Account             │
│      Join HealthMate AI today   │
│                                 │
├─────────────────────────────────┤
│                                 │
│  👤 Full Name                   │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━    │
│                                 │
│  📧 Email Address               │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━    │
│                                 │
│  🔒 Password                    │
│  ━━━━━━━━━━━━━━━━━━━━━━━ 👁   │
│                                 │
│  🔒 Confirm Password            │
│  ━━━━━━━━━━━━━━━━━━━━━━━ 👁   │
│                                 │
│  ✓ Passwords match              │
│                                 │
│  ☑ I agree to T&C              │
│    and Privacy Policy            │
│                                 │
│  ┌─────────────────────────────┐│
│  │  CREATE ACCOUNT (Orange)    ││
│  └─────────────────────────────┘│
│                                 │
│  Already have an account?       │
│  Sign in                        │
│                                 │
└─────────────────────────────────┘
```

## Color Reference

| Element | Color | Hex Value |
|---------|-------|-----------|
| Primary Button | Orange | #FFA500 |
| Primary Text | Dark Blue | #003D66 |
| Focus States | Teal | #17A2B8 |
| Background | Light Gray | #F9FAFB |
| Error Text | Red | #D32F2F |
| Success Text | Green | #10B981 |
| Card Background | White | #FFFFFF |
| Border | Light Gray | #E5E7EB |

## Key Design Elements

### Header Section
- **Height**: 260-280dp
- **Background**: White with rounded bottom corners (24dp)
- **Icon Box**: 120dp square with color background
- **Icon Size**: 60dp
- **Spacing**: 20dp between icon and title

### Input Fields
- **Height**: 56dp (with padding)
- **Border Radius**: 12dp
- **Focus Border Color**: Teal (#17A2B8)
- **Unfocused Border**: Light gray (#E5E7EB)
- **Shadow**: 1dp elevation
- **Icon Size**: 20dp
- **Font Size**: 14sp

### Buttons
- **Height**: 56dp
- **Border Radius**: 12dp
- **Width**: Full width
- **Shadow**: 4dp elevation for primary buttons
- **Font Size**: 16sp (bold)
- **Letter Spacing**: 0.5sp

### Text
- **Heading**: 24sp, Bold, Dark Blue
- **Subheading**: 14sp, Regular, Gray
- **Body**: 14sp, Regular, Dark Gray
- **Labels**: 13sp, Regular, Light Gray
- **Small**: 12sp, Regular, Various

## Interactive States

### Button States
- **Normal**: Full color (Orange)
- **Focused**: Slightly darker orange
- **Loading**: Circular progress indicator
- **Disabled**: Light orange (#FFD9A3)

### Field States
- **Normal**: Light gray border
- **Focused**: Teal border, teal cursor
- **Filled**: Dark text, teal cursor
- **Error**: Red border, error text below

### Text States
- **Link**: Teal color, clickable
- **Error**: Red background, red text
- **Success**: Green text with checkmark
- **Disabled**: Light gray text

## Screen Transitions

1. **Login → Sign-Up**: Slide transition (replace login with signup)
2. **Sign-Up → Login**: Slide transition (replace signup with login)
3. **Success**: Navigation to home screen

## Animations

- **Button Press**: Ripple effect (implicit in Material3)
- **Loading**: Rotating progress spinner
- **Keyboard**: Smooth scroll to focused field
- **Error Display**: Instant appearance with fade

## Accessibility Features

✓ High contrast ratios (WCAG AAA compliant)
✓ Minimum 48dp touch target size for buttons
✓ Clear focus indicators (teal borders)
✓ Descriptive labels for all inputs
✓ Password visibility toggle
✓ Screen reader friendly
✓ Appropriate font sizes (14sp minimum)

## Device Compatibility

| Device Type | Screen Width | Status |
|-------------|-------------|--------|
| Small Phone | 320-360dp | ✓ Supported |
| Medium Phone | 360-400dp | ✓ Optimized |
| Large Phone | 400-480dp | ✓ Supported |
| Tablet | 600-800dp | ✓ Supported |

## Future Enhancements

- [ ] Add animated illustrations to header
- [ ] Implement page transitions
- [ ] Add haptic feedback
- [ ] Dark mode support
- [ ] Biometric authentication UI
- [ ] Real-time field validation
- [ ] Toast notifications
- [ ] Loading skeletons

---

## Quick Reference

### Login Screen Components
- Header with icon (lock)
- Email/Mobile input
- Password input with toggle
- Forgot password link
- Error message area
- Buttons: Continue, Google Sign-In
- Links: Terms, Sign-Up

### Sign-Up Screen Components
- Header with icon (person)
- Full name input
- Email input
- Password input
- Confirm password input
- Password validation feedback
- Terms checkbox
- Buttons: Create Account
- Links: Terms, Sign-In

### Shared Components
- ModernTextField
- PrimaryButton
- SecondaryButton
- ErrorMessageCard
- SuccessMessageCard
- DividerWithText
- AuthNavigationLink
- PasswordValidationIndicator

