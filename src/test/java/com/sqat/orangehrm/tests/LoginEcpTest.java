package com.sqat.orangehrm.tests;

import com.sqat.orangehrm.base.BaseTest;
import com.sqat.orangehrm.pages.DashboardPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Black-box: Equivalence Class Partitioning on the Login form.
 * Each @CsvSource row is one row of ECP table "ECP-01" in docs/Test-Design-ECP-BVA.docx.
 */
@DisplayName("Login - Equivalence Class Partitioning")
class LoginEcpTest extends BaseTest {

    @ParameterizedTest(name = "[{index}] {0} | user=''{1}'' pass=''{2}'' -> {3}: {4}")
    @CsvSource(delimiter = '|', nullValues = "NULL", textBlock = """
            # id   | username   | password  | outcome        | expected message
            TC-L01 | Admin      | admin123  | DASHBOARD      | Dashboard
            TC-L02 | NoSuchUser | admin123  | ALERT          | Invalid credentials
            TC-L03 | Admin      | wrong123  | ALERT          | Invalid credentials
            TC-L04 | Admin      | ADMIN123  | ALERT          | Invalid credentials
            TC-L05 | @dm!n#$%   | admin123  | ALERT          | Invalid credentials
            TC-L06 | ''         | admin123  | USERNAME_ERROR | Required
            TC-L07 | '   '      | admin123  | USERNAME_ERROR | Required
            TC-L08 | Admin      | ''        | PASSWORD_ERROR | Required
            """)
    void loginPartitions(String id, String username, String password, String outcome, String expected) {
        loginPage.attemptLogin(username, password);

        switch (outcome) {
            case "DASHBOARD" -> {
                DashboardPage dashboard = new DashboardPage(driver);
                dashboard.waitUntilLoaded();
                assertThat(dashboard.topBar().waitForModuleTitle(expected)).isEqualTo(expected);
            }
            case "ALERT" -> assertThat(loginPage.waitForAlertMessage(expected)).isEqualTo(expected);
            case "USERNAME_ERROR" -> {
                assertThat(loginPage.waitForUsernameError(expected)).isEqualTo(expected);
                assertThat(loginPage.passwordError()).isEmpty();
            }
            case "PASSWORD_ERROR" -> {
                assertThat(loginPage.waitForPasswordError(expected)).isEqualTo(expected);
                assertThat(loginPage.usernameError()).isEmpty();
            }
            default -> throw new IllegalArgumentException("Unknown outcome " + outcome);
        }
    }

    @Test
    @DisplayName("TC-L09 Both fields empty -> 'Required' under both fields")
    void bothFieldsEmpty() {
        loginPage.attemptLogin("", "");

        assertThat(loginPage.waitForUsernameError("Required")).isEqualTo("Required");
        assertThat(loginPage.waitForPasswordError("Required")).isEqualTo("Required");
    }

    @Test
    @DisplayName("TC-L10 Logout returns to the login page")
    void logoutReturnsToLogin() {
        DashboardPage dashboard = loginPage.loginAsAdmin();

        var login = dashboard.topBar().logout();

        assertThat(login.isDisplayed()).isTrue();
    }
}
