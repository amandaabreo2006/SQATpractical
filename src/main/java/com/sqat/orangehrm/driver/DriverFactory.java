package com.sqat.orangehrm.driver;

import com.sqat.orangehrm.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.Locale;

/**
 * Creates configured WebDriver instances. One driver per test (created in {@code @BeforeEach},
 * quit in {@code @AfterEach}) keeps every test independent of the others.
 */
public final class DriverFactory {

    /** Browsers the framework knows about. Only Chrome is enabled for this project. */
    public enum BrowserType { CHROME }

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        BrowserType browser = BrowserType.valueOf(ConfigReader.get("browser").toUpperCase(Locale.ROOT));
        WebDriver driver = switch (browser) {
            case CHROME -> createChrome();
        };

        // Explicit waits only: implicit wait stays at 0 so the two never mix.
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("page.load.timeout.seconds")));
        return driver;
    }

    private static WebDriver createChrome() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.addArguments(
                "--window-size=" + ConfigReader.get("window.width") + "," + ConfigReader.get("window.height"),
                "--disable-notifications",
                "--disable-search-engine-choice-screen",
                "--lang=en-US");
        if (ConfigReader.getBoolean("headless")) {
            options.addArguments("--headless=new");
        }
        return new ChromeDriver(options);
    }
}
