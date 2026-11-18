package com.sqat.orangehrm.pages;

import com.sqat.orangehrm.base.BasePage;
import com.sqat.orangehrm.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page Object for /auth/login. */
public class LoginPage extends BasePage {

    private static final String PATH = "/auth/login";

    // ---- Locators: @FindBy (PageFactory) for static elements ...
    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit'].orangehrm-login-button")
    private WebElement loginButton;

    // ... and By locators for elements that appear only after an action.
    private static final By INVALID_CREDENTIALS_ALERT = By.cssSelector(".oxd-alert-content-text");
    private static final By USERNAME_FIELD = fieldByInputName("username");
    private static final By PASSWORD_FIELD = fieldByInputName("password");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        openPath(PATH);
        waitVisible(usernameInput);
        return this;
    }

    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public void clickLogin() {
        click(loginButton);
    }

    /** Types credentials and submits, without deciding what the outcome should be. */
    public void attemptLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    /** Happy-path login used as a precondition by other tests. */
    public DashboardPage loginAs(String username, String password) {
        attemptLogin(username, password);
        DashboardPage dashboard = new DashboardPage(driver);
        dashboard.waitUntilLoaded();
        return dashboard;
    }

    public DashboardPage loginAsAdmin() {
        return loginAs(ConfigReader.get("admin.username"), ConfigReader.get("admin.password"));
    }

    public boolean isDisplayed() {
        return isPresent(By.name("username")) && currentUrl().contains(PATH);
    }

    public void waitUntilDisplayed() {
        waitForUrlContaining(PATH);
        waitVisible(By.name("username"));
    }

    // ---- State readers (used by assertions in the tests)

    public String waitForAlertMessage(String expected) {
        return waitForText(INVALID_CREDENTIALS_ALERT, expected);
    }

    public String waitForUsernameError(String expected) {
        return waitForFieldError(USERNAME_FIELD, expected);
    }

    public String waitForPasswordError(String expected) {
        return waitForFieldError(PASSWORD_FIELD, expected);
    }

    public String usernameError() {
        return fieldError(USERNAME_FIELD);
    }

    public String passwordError() {
        return fieldError(PASSWORD_FIELD);
    }
}
