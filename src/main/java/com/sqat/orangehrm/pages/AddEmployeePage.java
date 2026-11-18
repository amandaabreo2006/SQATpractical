package com.sqat.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page Object for PIM -> Add Employee (/pim/addEmployee). */
public class AddEmployeePage extends AuthenticatedPage {

    private static final String PATH = "/pim/addEmployee";

    @FindBy(name = "firstName")
    private WebElement firstNameInput;

    @FindBy(name = "middleName")
    private WebElement middleNameInput;

    @FindBy(name = "lastName")
    private WebElement lastNameInput;

    @FindBy(css = "button[type='submit']")
    private WebElement saveButton;

    private static final By FIRST_NAME_FIELD = fieldByInputName("firstName");
    private static final By MIDDLE_NAME_FIELD = fieldByInputName("middleName");
    private static final By LAST_NAME_FIELD = fieldByInputName("lastName");
    private static final By EMPLOYEE_ID_FIELD = fieldByLabel("Employee Id");

    public AddEmployeePage(WebDriver driver) {
        super(driver);
    }

    public AddEmployeePage open() {
        openPath(PATH);
        waitVisible(firstNameInput);
        // Employee Id is pre-filled asynchronously; wait for it so later typing is not overwritten.
        try {
            wait.until(d -> !d.findElement(inputIn(EMPLOYEE_ID_FIELD)).getDomProperty("value").isEmpty());
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // some installations do not auto-generate IDs; nothing to wait for
        }
        return this;
    }

    public AddEmployeePage enterFirstName(String value) {
        type(firstNameInput, value);
        return this;
    }

    public AddEmployeePage enterMiddleName(String value) {
        type(middleNameInput, value);
        return this;
    }

    public AddEmployeePage enterLastName(String value) {
        type(lastNameInput, value);
        return this;
    }

    public AddEmployeePage enterEmployeeId(String value) {
        type(inputIn(EMPLOYEE_ID_FIELD), value);
        return this;
    }

    public AddEmployeePage clickSave() {
        click(saveButton);
        return this;
    }

    /** Saves a valid form and lands on the employee's Personal Details page. */
    public PersonalDetailsPage saveExpectingSuccess() {
        clickSave();
        PersonalDetailsPage details = new PersonalDetailsPage(driver);
        details.waitUntilLoaded();
        return details;
    }

    // ---- State readers

    public String waitForFirstNameError(String expected) {
        return waitForFieldError(FIRST_NAME_FIELD, expected);
    }

    public String waitForLastNameError(String expected) {
        return waitForFieldError(LAST_NAME_FIELD, expected);
    }

    public String waitForEmployeeIdError(String expected) {
        return waitForFieldError(EMPLOYEE_ID_FIELD, expected);
    }

    public String firstNameError() {
        return fieldError(FIRST_NAME_FIELD);
    }

    public String middleNameError() {
        return fieldError(MIDDLE_NAME_FIELD);
    }

    public String lastNameError() {
        return fieldError(LAST_NAME_FIELD);
    }

    public String employeeIdError() {
        return fieldError(EMPLOYEE_ID_FIELD);
    }
}
