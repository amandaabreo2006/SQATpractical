package com.sqat.orangehrm.tests;

import com.sqat.orangehrm.base.BaseTest;
import com.sqat.orangehrm.pages.DashboardPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Smoke tests: every main module opens, and the side-menu search filters correctly. */
@DisplayName("Navigation - smoke")
class NavigationSmokeTest extends BaseTest {

    private DashboardPage dashboard;

    @BeforeEach
    void login() {
        dashboard = loginPage.loginAsAdmin();
    }

    @ParameterizedTest(name = "[{index}] menu ''{0}'' opens module ''{1}''")
    @CsvSource({
            "Admin, Admin",
            "PIM, PIM",
            "Leave, Leave",
            "Time, Time",
            "Recruitment, Recruitment",
            "My Info, PIM",
            "Performance, Performance",
            "Dashboard, Dashboard",
            "Directory, Directory",
            "Claim, Claim",
            "Buzz, Buzz"
    })
    void menuOpensModule(String menuItem, String expectedTitle) {
        dashboard.sideMenu().open(menuItem);

        assertThat(dashboard.topBar().waitForModuleTitle(expectedTitle)).isEqualTo(expectedTitle);
    }

    @ParameterizedTest(name = "[{index}] search ''{0}'' shows only matching items")
    @ValueSource(strings = {"Admin", "pim", "Lea", "Time"})
    void menuSearchFilters(String term) {
        List<String> items = dashboard.sideMenu().visibleItemsAfterFilter(term);

        assertThat(items)
                .isNotEmpty()
                .allSatisfy(item -> assertThat(item).containsIgnoringCase(term));
    }
}
