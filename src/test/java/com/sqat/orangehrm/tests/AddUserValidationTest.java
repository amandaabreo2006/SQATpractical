package com.sqat.orangehrm.tests;

import com.sqat.orangehrm.base.BaseTest;
import com.sqat.orangehrm.constants.Messages;
import com.sqat.orangehrm.data.StringGenerator;
import com.sqat.orangehrm.pages.AddUserPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Black-box: BVA on Username / Password and ECP on Confirm Password, on Admin -> Add User.
 * User Role and Employee Name are never filled, so nothing is ever saved on the demo.
 */
@DisplayName("Admin Add User - BVA & ECP")
class AddUserValidationTest extends BaseTest {

    private AddUserPage addUser;

    @BeforeEach
    void openAddUser() {
        loginPage.loginAsAdmin();
        addUser = new AddUserPage(driver).open();
    }

    @ParameterizedTest(name = "[{index}] BVA-03 Username {0} (len={1}) -> \"{3}\"")
    @MethodSource("com.sqat.orangehrm.data.TestDataProvider#usernameBoundaries")
    void usernameLengthBoundaries(String boundary, int length, String value, String expectedError) {
        addUser.enterUsername(value).clickSaveAndWaitForValidation();

        assertThat(addUser.waitForUsernameError(expectedError))
                .as("Username error for %s (length %d)", boundary, length)
                .isEqualTo(expectedError);
    }

    @ParameterizedTest(name = "[{index}] BVA-04 Password {0} (len={1}) -> \"{3}\"")
    @MethodSource("com.sqat.orangehrm.data.TestDataProvider#passwordBoundaries")
    void passwordLengthBoundaries(String boundary, int length, String value, String expectedError) {
        addUser.enterPassword(value).enterConfirmPassword(value).clickSaveAndWaitForValidation();

        if (expectedError.isEmpty()) {
            // Valid length: wait for the server check, then make sure no LENGTH rule fired.
            // (Other policy messages, e.g. "could be guessable", are outside this boundary test.)
            addUser.waitForPasswordCheckToComplete();
            assertThat(addUser.passwordError())
                    .as("Password error for %s (length %d)", boundary, length)
                    .doesNotContain("Should have at least")
                    .doesNotContain("Should not exceed");
        } else {
            assertThat(addUser.waitForPasswordError(expectedError))
                    .as("Password error for %s (length %d)", boundary, length)
                    .isEqualTo(expectedError);
        }
    }

    @ParameterizedTest(name = "[{index}] {0}: confirm=''{1}'' -> \"{2}\"")
    @CsvSource(delimiter = '|', nullValues = "NULL", textBlock = """
            ECP-05 V1 confirm equals password | SAME      | NULL
            ECP-05 I1 confirm differs         | Other#99x | Passwords do not match
            ECP-05 I2 confirm empty           | NULL      | Passwords do not match
            """)
    void confirmPasswordPartitions(String partition, String confirm, String expectedError) {
        String password = StringGenerator.password(12);
        String confirmValue = "SAME".equals(confirm) ? password : (confirm == null ? "" : confirm);
        String expected = expectedError == null ? "" : expectedError;

        addUser.enterPassword(password).enterConfirmPassword(confirmValue).clickSaveAndWaitForValidation();

        assertThat(addUser.waitForConfirmPasswordError(expected)).as(partition).isEqualTo(expected);
    }

    @ParameterizedTest(name = "[{index}] {0} -> \"{2}\"")
    @CsvSource(delimiter = '|', textBlock = """
            ECP-04 I1 empty username        | ''    | Required
            ECP-04 I2 whitespace only       | '   ' | Required
            """)
    void usernameRequired(String partition, String username, String expectedError) {
        addUser.enterUsername(username).clickSaveAndWaitForValidation();

        assertThat(addUser.waitForUsernameError(expectedError)).as(partition).isEqualTo(Messages.REQUIRED);
    }
}
