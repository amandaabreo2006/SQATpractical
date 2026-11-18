package com.sqat.orangehrm.constants;

/**
 * User-facing messages of OrangeHRM OS 5.9 (en_US), taken verbatim from the application's
 * language-string migrations (installer/Migration/V5_0_0, V5_2_0, V5_4_0).
 */
public final class Messages {

    private Messages() {
    }

    // ---- Generic client-side validators (src/client/src/core/util/validation/rules.ts)
    public static final String REQUIRED = "Required";

    public static String shouldNotExceed(int n) {
        return "Should not exceed " + n + " characters";
    }

    public static String shouldBeAtLeast(int n) {
        return "Should be at least " + n + " characters";
    }

    // ---- Authentication
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match";
    public static final String SUCCESSFULLY_SAVED = "Successfully Saved";

    // ---- Server-side password policy (PasswordStrengthService::checkPasswordPolicies)
    public static String passwordMinLength(int n) {
        return n == 1 ? "Should have at least 1 character" : "Should have at least " + n + " characters";
    }

    public static String passwordMaxLength(int n) {
        return n == 1 ? "Should not exceed 1 character" : "Should not exceed " + n + " characters";
    }

    public static String passwordLowercase(int n) {
        return "Your password must contain minimum " + n + (n == 1 ? " lower-case letter" : " lower-case letters");
    }

    public static String passwordUppercase(int n) {
        return "Your password must contain minimum " + n + (n == 1 ? " upper-case letter" : " upper-case letters");
    }

    public static String passwordNumbers(int n) {
        return "Your password must contain minimum " + n + (n == 1 ? " number" : " numbers");
    }

    public static String passwordSpecialCharacters(int n) {
        return "Your password must contain minimum " + n + (n == 1 ? " special character" : " special characters");
    }

    public static final String PASSWORD_NO_SPACES = "Your password should not contain spaces";
    public static final String PASSWORD_GUESSABLE =
            "Your password meets the minimum requirements, but it could be guessable";
}
