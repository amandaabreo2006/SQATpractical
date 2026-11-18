package com.sqat.orangehrm.tests;

import com.sqat.orangehrm.base.BaseTest;
import com.sqat.orangehrm.constants.Messages;
import com.sqat.orangehrm.data.StringGenerator;
import com.sqat.orangehrm.pages.AddEmployeePage;
import com.sqat.orangehrm.pages.PersonalDetailsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Black-box: Boundary Value Analysis (and ECP) on PIM -> Add Employee.
 * <p>
 * Validation-only tests always leave one mandatory name field empty, so the form is never saved
 * and no data is created on the public demo. The "Required" message on that other field is also the
 * synchronisation signal that proves validation has run.
 */
@DisplayName("PIM Add Employee - Boundary Value Analysis")
class AddEmployeeBvaTest extends BaseTest {

    private AddEmployeePage addEmployee;

    @BeforeEach
    void openAddEmployee() {
        loginPage.loginAsAdmin();
        addEmployee = new AddEmployeePage(driver).open();
    }

    @ParameterizedTest(name = "[{index}] BVA-01 First Name {0} (len={1}) -> \"{3}\"")
    @MethodSource("com.sqat.orangehrm.data.TestDataProvider#employeeNameBoundaries")
    void firstNameLengthBoundaries(String boundary, int length, String value, String expectedError) {
        addEmployee.enterFirstName(value).enterLastName("").clickSave();

        // Last Name is empty on purpose -> "Required" proves validation ran and the form was blocked.
        assertThat(addEmployee.waitForLastNameError(Messages.REQUIRED)).isEqualTo(Messages.REQUIRED);
        assertThat(addEmployee.waitForFirstNameError(expectedError))
                .as("First Name error for %s (length %d)", boundary, length)
                .isEqualTo(expectedError);
    }

    @ParameterizedTest(name = "[{index}] BVA-02 Last Name {0} (len={1}) -> \"{3}\"")
    @MethodSource("com.sqat.orangehrm.data.TestDataProvider#employeeNameBoundaries")
    void lastNameLengthBoundaries(String boundary, int length, String value, String expectedError) {
        addEmployee.enterFirstName("").enterLastName(value).clickSave();

        assertThat(addEmployee.waitForFirstNameError(Messages.REQUIRED)).isEqualTo(Messages.REQUIRED);
        assertThat(addEmployee.waitForLastNameError(expectedError))
                .as("Last Name error for %s (length %d)", boundary, length)
                .isEqualTo(expectedError);
    }

    /** ECP-03: Employee Id is optional with max length 10. Data lives in a CSV file (data-driven testing). */
    @ParameterizedTest(name = "[{index}] {0}: Employee Id=''{1}'' -> \"{2}\"")
    @CsvFileSource(resources = "/testdata/employee_id_ecp.csv", numLinesToSkip = 1, nullValues = "NULL")
    void employeeIdPartitions(String partition, String employeeId, String expectedError) {
        String id = employeeId == null ? "" : employeeId;
        String expected = expectedError == null ? "" : expectedError;

        addEmployee.enterFirstName("Valid").enterLastName("").enterEmployeeId(id).clickSave();

        assertThat(addEmployee.waitForLastNameError(Messages.REQUIRED)).isEqualTo(Messages.REQUIRED);
        assertThat(addEmployee.waitForEmployeeIdError(expected)).as(partition).isEqualTo(expected);
    }

    @Test
    @DisplayName("TC-E2E-01 Add a valid employee end-to-end, verify profile, then clean up")
    void addEmployeeEndToEnd() {
        String firstName = "Auto" + StringGenerator.uniqueSuffix();
        String lastName = "Tester";

        addEmployee.enterFirstName(firstName).enterMiddleName("QA").enterLastName(lastName);
        addEmployee.enterEmployeeId("");        // let the ID be empty: avoids clashing with other demo users
        addEmployee.clickSave();
        String toast = addEmployee.waitForToastMessage();

        PersonalDetailsPage details = new PersonalDetailsPage(driver);
        details.waitUntilLoaded();
        try {
            assertThat(toast).isEqualTo(Messages.SUCCESSFULLY_SAVED);
            assertThat(details.currentUrl()).contains("/pim/viewPersonalDetails/empNumber/");
            assertThat(details.employeeName()).isEqualTo(firstName + " " + lastName);
        } finally {
            details.deleteThisEmployeeViaApi();
        }
    }
}
