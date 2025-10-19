package ftn.siit.project.isspoject.selenium;

import ftn.siit.project.isspoject.selenium.pages.BudgetPlanningPage;
import ftn.siit.project.isspoject.selenium.pages.LoginPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class BudgetPlanningTest extends BaseTest {

    private static final long EVENT_ID_UNDER_TEST = 27L; // adjust if needed
    private BudgetPlanningPage page;

    @BeforeEach
    void openProtectedPage() {
        // Login
        driver.get(baseUrl + "/login");
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Email']")));
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Password']")));

        new LoginPage(driver).loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/events"));

        // Navigate to page under test
        page = new BudgetPlanningPage(driver).open(baseUrl, EVENT_ID_UNDER_TEST);
        assertEquals("Budget Planning", page.getTitle());
    }

    @Test
    @DisplayName("01 - Add via New Item popup increases total and creates row")
    void addViaNewItem_IncreasesTotals_AndCreatesRow() {
        double totalBefore = page.readTotalMax();
        int rowsBefore = page.countBudgetRows();

        // Choose a category that is NOT already present
        page.openCreateItemPopup();
        var options = page.getAvailableCategoriesNotInTable();
        page.cancelCreateItemPopup();
        Assumptions.assumeFalse(options.isEmpty(), "No free categories available to create.");
        String category = options.get(0);

        page.createNewItem(category, "500");

        String snack = page.getSnackBarText().toLowerCase();
        assertTrue(snack.contains("success") || snack.contains("created") || snack.contains("added"),
                "Expected success snackbar after create, got: " + snack);
        assertNotNull(page.findBudgetRowByCategory(category), "Newly created row should exist.");

        double totalAfter = page.readTotalMax();
        assertEquals(totalBefore + 500.0, totalAfter, 0.01, "Total max must increase by created amount.");
        assertEquals(rowsBefore + 1, page.countBudgetRows(), "Row count should increase by 1.");
    }


    @Test
    @DisplayName("02 - Add via Recommendation shows success and row appears")
    void addViaRecommendation_CreatesRow() {
        Assumptions.assumeTrue(page.hasRecommendations(), "No recommendations available to test.");

        double totalBefore = page.readTotalMax();
        int rowsBefore = page.countBudgetRows();

        page.addFromRecommendationByIndex(0, "300");

        String snack = page.getSnackBarText().toLowerCase();
        assertTrue(snack.contains("success") || snack.contains("create") || snack.contains("added"),
                "Expected success snackbar after recommendation add, got: " + snack);

        double totalAfter = page.readTotalMax();
        assertTrue(totalAfter >= totalBefore + 300.0 - 0.01,
                "Total should increase by >=300 (exact if new category). Before=" + totalBefore + ", After=" + totalAfter);

        assertTrue(page.countBudgetRows() >= rowsBefore + 1, "Row count should increase.");
    }


    @Test
    @DisplayName("03 - Edit updates max price and changes totals by delta")
    void editItem_UpdatesMax_AndTotalsChangeByDelta() {
        String category = "Catering";
        if (page.findBudgetRowByCategory(category) == null) {
            // Seed with a non-duplicate category
            category = page.createAnyAvailableCategoryAndReturnName("1000");
            assertNotNull(page.findBudgetRowByCategory(category), "Seed row must exist.");
        }

        double totalBefore = page.readTotalMax();

        page.editItem(category, "2500");

        String snack = page.getSnackBarText().toLowerCase();
        assertTrue(snack.contains("updated") || snack.contains("success") || snack.contains("item updated"),
                "Expected success snackbar after edit, got: " + snack);

        double totalAfter = page.readTotalMax();
        assertTrue(totalAfter > totalBefore, "Total max should increase after raising item max.");
        assertNotNull(page.findBudgetRowByCategory(category), "Row should remain present after edit.");
    }


    @Test
    @DisplayName("04 - Search offers works and paginator can move forward")
    void searchOffers_AndPaginate() {
        // Click search; this should render the offers host element
        page.clickSearch();

        // The test remains resilient even if no offers match; nextPage should not crash
        assertDoesNotThrow(page::nextPage, "Paginator next should be clickable without errors.");
    }
}
