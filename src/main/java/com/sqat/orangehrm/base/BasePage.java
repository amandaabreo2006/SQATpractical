package com.sqat.orangehrm.base;

import com.sqat.orangehrm.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Parent of every Page Object. Holds the driver, the explicit-wait strategy and the
 * reusable web actions (click / type / read). Page classes contain locators and actions only;
 * they never assert. Assertions live exclusively in the JUnit test classes.
 */
public abstract class BasePage {

    /** OrangeHRM (OXD UI library) shows these while a page or form is busy. */
    private static final By LOADERS = By.cssSelector(".oxd-loading-spinner, .oxd-form-loader");
    private static final By FIELD_ERROR = By.cssSelector("span.oxd-input-field-error-message");
    private static final By TOAST_MESSAGE = By.cssSelector(".oxd-toast .oxd-text--toast-message");

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Duration timeout;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.timeout = Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds"));
        this.wait = new WebDriverWait(driver, timeout);
        // Initialises every @FindBy field as a lazy proxy.
        PageFactory.initElements(driver, this);
    }

    // ------------------------------------------------------------------ navigation

    protected void openPath(String path) {
        driver.get(ConfigReader.baseUrl() + path);
        waitForPageReady();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    // ------------------------------------------------------------------ waits

    protected void waitForPageReady() {
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
        waitForLoadersToDisappear();
    }

    protected void waitForLoadersToDisappear() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADERS));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void waitForUrlContaining(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    /**
     * Polls the text of {@code locator} until it equals {@code expected}. On timeout it does NOT throw:
     * it returns whatever text is there, so the test's assertion reports a meaningful actual value.
     */
    protected String waitForText(By locator, String expected) {
        FluentWait<WebDriver> fluent = new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(Duration.ofMillis(250))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        try {
            fluent.until(d -> expected.equals(readText(locator)));
        } catch (TimeoutException ignored) {
            // fall through and return the actual value
        }
        return readText(locator);
    }

    // ------------------------------------------------------------------ actions

    protected void click(WebElement element) {
        waitForLoadersToDisappear();
        waitClickable(element).click();
    }

    protected void click(By locator) {
        waitForLoadersToDisappear();
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /** Clears the field the way a user would (Ctrl+A, Delete) so Vue's v-model sees the change, then types. */
    protected void type(WebElement element, String text) {
        WebElement field = waitVisible(element);
        field.click();
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        if (text != null && !text.isEmpty()) {
            field.sendKeys(text);
        }
    }

    protected void type(By locator, String text) {
        type(waitVisible(locator), text);
    }

    /** Returns the element's text, or an empty string if it is not in the DOM. */
    protected String readText(By locator) {
        List<WebElement> found = driver.findElements(locator);
        return found.isEmpty() ? "" : found.get(0).getText().trim();
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    // ------------------------------------------------------------------ OXD form helpers

    /**
     * The form-field container for a labelled field: the outermost {@code div} around the label that still
     * contains only that one label. It therefore holds the label, the input and the field's error message.
     */
    protected static By fieldByLabel(String label) {
        return By.xpath("//label[normalize-space()='" + label + "']/ancestor::div[count(.//label)=1][last()]");
    }

    /** Same idea for an input identified by its {@code name} attribute (used for the 3-part name field). */
    protected static By fieldByInputName(String name) {
        return By.xpath("//input[@name='" + name + "']/ancestor::div[count(.//input)=1][last()]");
    }

    protected static By inputIn(By field) {
        return new ByChained(field, By.tagName("input"));
    }

    protected static By errorIn(By field) {
        return new ByChained(field, FIELD_ERROR);
    }

    /** Current validation message under a field ("" when the field is valid). */
    protected String fieldError(By field) {
        return readText(errorIn(field));
    }

    /** Waits for an expected validation message under a field and returns the actual message. */
    protected String waitForFieldError(By field, String expected) {
        return waitForText(errorIn(field), expected);
    }

    /** Text of the OXD toast notification shown after save/delete, or "" if none appeared. */
    public String waitForToastMessage() {
        try {
            return waitVisible(TOAST_MESSAGE).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }
}
