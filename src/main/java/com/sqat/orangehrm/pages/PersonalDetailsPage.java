package com.sqat.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Page Object for PIM -> Employee -> Personal Details (/pim/viewPersonalDetails/empNumber/{n}). */
public class PersonalDetailsPage extends AuthenticatedPage {

    private static final String PATH = "/pim/viewPersonalDetails/empNumber/";
    private static final By EMPLOYEE_NAME_HEADER = By.cssSelector(".orangehrm-edit-employee-name h6");
    private static final Pattern EMP_NUMBER = Pattern.compile("/empNumber/(\\d+)");

    public PersonalDetailsPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForUrlContaining(PATH);
        waitForPageReady();
        wait.until(d -> !readText(EMPLOYEE_NAME_HEADER).isEmpty());
    }

    public String employeeName() {
        return readText(EMPLOYEE_NAME_HEADER);
    }

    public int empNumber() {
        Matcher m = EMP_NUMBER.matcher(currentUrl());
        if (!m.find()) {
            throw new IllegalStateException("No empNumber in URL " + currentUrl());
        }
        return Integer.parseInt(m.group(1));
    }

    /**
     * Test-data clean-up: deletes the employee through OrangeHRM's own REST API using the
     * browser's authenticated session (much faster and less brittle than clicking through the UI).
     *
     * @return the HTTP status code returned by the API
     */
    public long deleteThisEmployeeViaApi() {
        String script = """
                const done = arguments[arguments.length - 1];
                fetch(arguments[0], {
                    method: 'DELETE',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ids: [arguments[1]]})
                }).then(r => done(r.status)).catch(() => done(-1));
                """;
        Object status = ((JavascriptExecutor) driver).executeAsyncScript(
                script, com.sqat.orangehrm.config.ConfigReader.baseUrl() + "/api/v2/pim/employees", empNumber());
        return ((Number) status).longValue();
    }
}
