package com.sqat.orangehrm.validation;

import com.sqat.orangehrm.constants.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WHITE-BOX SUBJECT.
 * <p>
 * Java port of OrangeHRM 5.9's server-side password check
 * {@code PasswordStrengthService::checkPasswordPolicies()} (src/plugins/orangehrmAuthenticationPlugin/Service).
 * The decision structure (8 sequential decisions D1..D8) is kept identical to the original so that the
 * Control Flow Graph, Cyclomatic Complexity and Basis Paths derived from it describe the real application logic.
 * <p>
 * The first message in the returned list is what the Add User / Add Employee form shows under the Password field.
 */
public class PasswordPolicyValidator {

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern NUMBER = Pattern.compile("[0-9]");
    private static final Pattern SPACE = Pattern.compile("\\s");
    private static final Pattern SPECIAL = Pattern.compile("[@#\\\\/\\-!$%^&*()_+|~=`{}\\[\\]:\";'<>?,.]");

    private final PasswordPolicy policy;

    public PasswordPolicyValidator(PasswordPolicy policy) {
        this.policy = policy;
    }

    /**
     * @param password the candidate password (non-null)
     * @param strength strength score 0..4 computed for the password (zxcvbn-style, as OrangeHRM does)
     * @return all policy violations, in OrangeHRM's order; empty list = password accepted
     */
    public List<String> checkPasswordPolicies(String password, int strength) {
        List<String> messages = new ArrayList<>();                                       // N1

        if (password.length() < policy.minLength()) {                                    // D1
            messages.add(Messages.passwordMinLength(policy.minLength()));                // N3
        }
        if (password.length() > policy.maxLength()) {                                    // D2
            messages.add(Messages.passwordMaxLength(policy.maxLength()));                // N5
        }
        if (count(LOWERCASE, password) < policy.minLowercase()) {                        // D3
            messages.add(Messages.passwordLowercase(policy.minLowercase()));             // N7
        }
        if (count(UPPERCASE, password) < policy.minUppercase()) {                        // D4
            messages.add(Messages.passwordUppercase(policy.minUppercase()));             // N9
        }
        if (count(NUMBER, password) < policy.minNumbers()) {                             // D5
            messages.add(Messages.passwordNumbers(policy.minNumbers()));                 // N11
        }
        if (count(SPECIAL, password) < policy.minSpecialCharacters()) {                  // D6
            messages.add(Messages.passwordSpecialCharacters(policy.minSpecialCharacters())); // N13
        }
        if (violatesSpaceRule(password)) {                                               // D7
            messages.add(Messages.PASSWORD_NO_SPACES);                                   // N15
        }
        if (strength < policy.requiredStrength() && messages.isEmpty()) {               // D8 (compound: C8a && C8b)
            messages.add(Messages.PASSWORD_GUESSABLE);                                   // N17
        }
        return messages;                                                                 // N18
    }

    /** Convenience wrapper equivalent to OrangeHRM's {@code isValidPassword()}. */
    public boolean isValid(String password, int strength) {
        return checkPasswordPolicies(password, strength).isEmpty();
    }

    private boolean violatesSpaceRule(String password) {
        return !policy.spacesAllowed() && SPACE.matcher(password).find();
    }

    private static int count(Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        int n = 0;
        while (m.find()) {
            n++;
        }
        return n;
    }
}
