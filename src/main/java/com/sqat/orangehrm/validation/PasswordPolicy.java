package com.sqat.orangehrm.validation;

/**
 * Configuration of OrangeHRM's password policy (Admin > Configuration > Password Policy / table hs_hr_config).
 *
 * @param minLength            auth.password_policy.min_password_length
 * @param maxLength            ConfigService::MAX_PASSWORD_LENGTH (fixed at 64)
 * @param minLowercase         auth.password_policy.min_lowercase_letters
 * @param minUppercase         auth.password_policy.min_uppercase_letters
 * @param minNumbers           auth.password_policy.min_numbers_in_password
 * @param minSpecialCharacters auth.password_policy.min_special_characters
 * @param spacesAllowed        auth.password_policy.is_spaces_allowed
 * @param requiredStrength     auth.password_policy.default_required_password_strength mapped to a score
 *                             (veryWeak=0, weak=1, better=2, strong=3, strongest=4)
 */
public record PasswordPolicy(
        int minLength,
        int maxLength,
        int minLowercase,
        int minUppercase,
        int minNumbers,
        int minSpecialCharacters,
        boolean spacesAllowed,
        int requiredStrength) {

    public PasswordPolicy {
        if (minLength < 0 || maxLength < minLength) {
            throw new IllegalArgumentException("Invalid length range " + minLength + ".." + maxLength);
        }
        if (requiredStrength < 0 || requiredStrength > 4) {
            throw new IllegalArgumentException("Strength must be 0..4");
        }
    }

    /** Defaults written by the OrangeHRM 5.4+ installer (installer/Migration/V5_4_0/Migration.php). */
    public static PasswordPolicy orangeHrmDefault() {
        return new PasswordPolicy(8, 64, 1, 1, 1, 1, false, 3);
    }
}
