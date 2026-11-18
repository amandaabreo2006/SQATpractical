package com.sqat.orangehrm.extensions;

import com.sqat.orangehrm.base.BaseTest;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Saves a PNG to target/screenshots when a test fails.
 * Runs in AfterTestExecution, i.e. BEFORE {@code @AfterEach} quits the browser.
 */
public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    private static final Path DIR = Path.of("target", "screenshots");
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isEmpty()) {
            return;
        }
        Object instance = context.getRequiredTestInstance();
        if (!(instance instanceof BaseTest test) || test.getDriver() == null) {
            return;
        }
        WebDriver driver = test.getDriver();
        String name = (context.getRequiredTestClass().getSimpleName() + "_" + context.getDisplayName())
                .replaceAll("[^A-Za-z0-9._-]", "_");
        if (name.length() > 120) {
            name = name.substring(0, 120);
        }
        try {
            Files.createDirectories(DIR);
            Path src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
            Path dest = DIR.resolve(name + "_" + LocalDateTime.now().format(TS) + ".png");
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[screenshot] " + dest.toAbsolutePath());
        } catch (IOException | RuntimeException e) {
            System.err.println("[screenshot] could not save: " + e.getMessage());
        }
    }
}
