package com.sqat.orangehrm.pages;

import com.sqat.orangehrm.base.BasePage;
import com.sqat.orangehrm.pages.components.SideMenu;
import com.sqat.orangehrm.pages.components.TopBar;
import org.openqa.selenium.WebDriver;

/** Any page shown after login: exposes the shared side menu and top bar components. */
public abstract class AuthenticatedPage extends BasePage {

    private final SideMenu sideMenu;
    private final TopBar topBar;

    protected AuthenticatedPage(WebDriver driver) {
        super(driver);
        this.sideMenu = new SideMenu(driver);
        this.topBar = new TopBar(driver);
    }

    public SideMenu sideMenu() {
        return sideMenu;
    }

    public TopBar topBar() {
        return topBar;
    }
}
