package com.sqat.orangehrm.base;

import com.sqat.orangehrm.driver.DriverFactory;
import com.sqat.orangehrm.extensions.ScreenshotOnFailureExtension;
import com.sqat.orangehrm.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

/**
 * Parent of every UI test class. Owns the WebDriver lifecycle:
 * a fresh browser per test in {@code @BeforeEach}, always quit in {@code @AfterEach}.
 */
@Tag("ui")
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseTest {

    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeEach
    void startBrowser() {
        driver = DriverFactory.createDriver();
        loginPage = new LoginPage(driver).open();
    }

    @AfterEach
    void quitBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Used by {@link ScreenshotOnFailureExtension}. */
    public WebDriver getDriver() {
        return driver;
    }
}
