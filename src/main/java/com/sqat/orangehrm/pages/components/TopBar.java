package com.sqat.orangehrm.pages.components;

import com.sqat.orangehrm.base.BasePage;
import com.sqat.orangehrm.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Reusable component: header bar with the module breadcrumb and the user dropdown. */
public class TopBar extends BasePage {

    private static final By MODULE_TITLE = By.cssSelector(".oxd-topbar-header-breadcrumb h6");
    private static final By LOGOUT_LINK = By.xpath("//ul[contains(@class,'oxd-dropdown-menu')]//a[normalize-space()='Logout']");

    @FindBy(css = ".oxd-userdropdown-tab")
    private WebElement userDropdown;

    public TopBar(WebDriver driver) {
        super(driver);
    }

    public String moduleTitle() {
        return waitVisible(MODULE_TITLE).getText().trim();
    }

    /** Waits until the breadcrumb shows {@code expected}; returns the actual title either way. */
    public String waitForModuleTitle(String expected) {
        waitVisible(MODULE_TITLE);
        return waitForText(MODULE_TITLE, expected);
    }

    public LoginPage logout() {
        click(userDropdown);
        click(LOGOUT_LINK);
        LoginPage login = new LoginPage(driver);
        login.waitUntilDisplayed();
        return login;
    }
}
