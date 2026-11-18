package com.sqat.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Page Object for /dashboard/index (landing page after a successful login). */
public class DashboardPage extends AuthenticatedPage {

    private static final String PATH = "/dashboard/index";
    private static final By WIDGETS = By.cssSelector(".orangehrm-dashboard-widget");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForUrlContaining(PATH);
        waitVisible(WIDGETS);
    }

    public boolean isOnDashboard() {
        return currentUrl().contains(PATH);
    }
}
