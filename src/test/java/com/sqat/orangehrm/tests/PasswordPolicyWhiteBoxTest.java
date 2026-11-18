package com.sqat.orangehrm.tests;

import com.sqat.orangehrm.constants.Messages;
import com.sqat.orangehrm.data.StringGenerator;
import com.sqat.orangehrm.validation.PasswordPolicy;
import com.sqat.orangehrm.validation.PasswordPolicyValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * White-box: Basis Path Testing of {@link PasswordPolicyValidator#checkPasswordPolicies(String, int)}.
 * <p>
 * CFG: N = 18 nodes, E = 25 edges, P = 8 predicate nodes  ->  V(G) = E - N + 2 = P + 1 = 9.
 * One test per independent path (BP1..BP9), plus 2 extra tests for the compound condition in D8
 * (condition coverage) and for a multi-violation path. No browser needed.
 */
@Tag("whitebox")
@DisplayName("Password policy - White-box basis paths")
class PasswordPolicyWhiteBoxTest {

    private static final PasswordPolicy POLICY = PasswordPolicy.orangeHrmDefault(); // 8..64, 1/1/1/1, no spaces, strong(3)
    private static final PasswordPolicyValidator VALIDATOR = new PasswordPolicyValidator(POLICY);
    private static final int STRONG = 4;

    static Stream<Arguments> basisPaths() {
        return Stream.of(
                Arguments.of("BP1 all decisions false (valid)", "Qa7#Lm9$", STRONG, List.of()),
                Arguments.of("BP2 D1 true: too short", "Qa7#Lm9", STRONG,
                        List.of(Messages.passwordMinLength(8))),
                Arguments.of("BP3 D2 true: too long", StringGenerator.password(65), STRONG,
                        List.of(Messages.passwordMaxLength(64))),
                Arguments.of("BP4 D3 true: no lower-case", "QA7#LM9$X", STRONG,
                        List.of(Messages.passwordLowercase(1))),
                Arguments.of("BP5 D4 true: no upper-case", "qa7#lm9$x", STRONG,
                        List.of(Messages.passwordUppercase(1))),
                Arguments.of("BP6 D5 true: no number", "Qa#Lm$Xyz", STRONG,
                        List.of(Messages.passwordNumbers(1))),
                Arguments.of("BP7 D6 true: no special char", "Qa7Lm9Xyz", STRONG,
                        List.of(Messages.passwordSpecialCharacters(1))),
                Arguments.of("BP8 D7 true: contains space", "Qa7# Lm9$", STRONG,
                        List.of(Messages.PASSWORD_NO_SPACES)),
                Arguments.of("BP9 D8 true: rules met but weak", "Qa7#Lm9$", 2,
                        List.of(Messages.PASSWORD_GUESSABLE)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("basisPaths")
    void basisPath(String path, String password, int strength, List<String> expectedMessages) {
        assertThat(VALIDATOR.checkPasswordPolicies(password, strength))
                .as(path)
                .containsExactlyElementsOf(expectedMessages);
    }

    @Test
    @DisplayName("CC1 D8 short-circuit: weak AND already invalid -> guessable message suppressed")
    void compoundConditionWeakButAlreadyInvalid() {
        // C8a (strength < required) = TRUE, C8b (messages empty) = FALSE -> D8 false
        assertThat(VALIDATOR.checkPasswordPolicies("Qa7#Lm9", 1))
                .containsExactly(Messages.passwordMinLength(8))
                .doesNotContain(Messages.PASSWORD_GUESSABLE);
    }

    @Test
    @DisplayName("MV1 several decisions true at once -> messages in OrangeHRM order")
    void multipleViolationsKeepOrder() {
        assertThat(VALIDATOR.checkPasswordPolicies("ab c", 0)).containsExactly(
                Messages.passwordMinLength(8),
                Messages.passwordUppercase(1),
                Messages.passwordNumbers(1),
                Messages.passwordSpecialCharacters(1),
                Messages.PASSWORD_NO_SPACES);
    }
}
