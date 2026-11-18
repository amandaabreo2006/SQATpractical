package com.sqat.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for Admin -> User Management -> Add User (/admin/saveSystemUser).
 * Tests on this page never fill User Role / Employee Name, so the form can never actually
 * be saved: no data is created on the shared public demo.
 */
public class AddUserPage extends AuthenticatedPage {

    private static final String PATH = "/admin/saveSystemUser";

    private static final By USERNAME_FIELD = fieldByLabel("Username");
    private static final By PASSWORD_FIELD = fieldByLabel("Password");
    private static final By CONFIRM_PASSWORD_FIELD = fieldByLabel("Confirm Password");
    private static final By USER_ROLE_FIELD = fieldByLabel("User Role");
    private static final By PASSWORD_STRENGTH_CHIP = By.cssSelector(".orangehrm-password-chip");

    @FindBy(css = "button[type='submit']")
    private WebElement saveButton;

    public AddUserPage(WebDriver driver) {
        super(driver);
    }

    public AddUserPage open() {
        openPath(PATH);
        waitVisible(inputIn(USERNAME_FIELD));
        return this;
    }

    public AddUserPage enterUsername(String value) {
        type(inputIn(USERNAME_FIELD), value);
        return this;
    }

    public AddUserPage enterPassword(String value) {
        type(inputIn(PASSWORD_FIELD), value);
        return this;
    }

    public AddUserPage enterConfirmPassword(String value) {
        type(inputIn(CONFIRM_PASSWORD_FIELD), value);
        return this;
    }

    /** Submits and waits until client-side validation has run (User Role is always left empty). */
    public AddUserPage clickSaveAndWaitForValidation() {
        click(saveButton);
        waitForFieldError(USER_ROLE_FIELD, com.sqat.orangehrm.constants.Messages.REQUIRED);
        return this;
    }

    /** The password strength chip appears once the server-side password check has answered. */
    public AddUserPage waitForPasswordCheckToComplete() {
        try {
            waitVisible(PASSWORD_STRENGTH_CHIP);
        } catch (TimeoutException ignored) {
            // e.g. empty password: no server check is made, nothing to wait for
        }
        return this;
    }

    // ---- State readers

    public String waitForUsernameError(String expected) {
        return waitForFieldError(USERNAME_FIELD, expected);
    }

    public String waitForPasswordError(String expected) {
        return waitForFieldError(PASSWORD_FIELD, expected);
    }

    public String waitForConfirmPasswordError(String expected) {
        return waitForFieldError(CONFIRM_PASSWORD_FIELD, expected);
    }

    public String usernameError() {
        return fieldError(USERNAME_FIELD);
    }

    public String passwordError() {
        return fieldError(PASSWORD_FIELD);
    }

    public String confirmPasswordError() {
        return fieldError(CONFIRM_PASSWORD_FIELD);
    }

    public String userRoleError() {
        return fieldError(USER_ROLE_FIELD);
    }
}
