package com.sqat.orangehrm.pages.components;

import com.sqat.orangehrm.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/** Reusable component: the left-hand main menu present on every authenticated page. */
public class SideMenu extends BasePage {

    @FindBy(css = ".oxd-main-menu-search input")
    private WebElement searchBox;

    private static final By MENU_ITEM_NAMES = By.cssSelector(".oxd-main-menu .oxd-main-menu-item--name");

    public SideMenu(WebDriver driver) {
        super(driver);
    }

    private static By menuItem(String name) {
        return By.xpath("//a[contains(@class,'oxd-main-menu-item')][.//span[normalize-space()='" + name + "']]");
    }

    public void open(String moduleName) {
        click(menuItem(moduleName));
        waitForPageReady();
    }

    public void search(String term) {
        type(searchBox, term);
    }

    /** Labels of the menu entries currently shown. */
    public List<String> visibleItems() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(MENU_ITEM_NAMES));
        return driver.findElements(MENU_ITEM_NAMES).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
    }

    public List<String> visibleItemsAfterFilter(String term) {
        search(term);
        // The filter is synchronous in Vue, but wait until every remaining item matches the term.
        wait.until(d -> visibleItems().stream().allMatch(i -> i.toLowerCase().contains(term.toLowerCase())));
        return visibleItems();
    }
}
