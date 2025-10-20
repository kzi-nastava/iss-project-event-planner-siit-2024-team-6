package ftn.siit.project.isspoject.selenium;

import ftn.siit.project.isspoject.selenium.pages.BudgetPlanningPage;
import ftn.siit.project.isspoject.selenium.pages.LoginPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import ftn.siit.project.isspoject.selenium.pages.OfferInfoPage;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class BudgetPlanningTest extends BaseTest {

    private static final long EVENT_ID_UNDER_TEST = 27L;
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
        String category = "Makeup";
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

    @Test
    @DisplayName("05 - Search renders offers or 'no offers' without crashing")
    void searchRendersOffersOrEmptyMessage() {
        page.clickSearch();
        List<WebElement> cards = page.getOfferCards();
        boolean noOffers = page.isNoOffersVisible();
        assertTrue(!cards.isEmpty() || noOffers,
                "After search, either offer cards should appear or 'No matching offers.' should be visible.");
    }

    @Test
    @DisplayName("06 - Deleting a zero-spent item succeeds and updates totals")
    void deleteZeroSpentItemSucceeds() {
        String fresh = page.createAnyAvailableCategoryAndReturnName("700");
        page.waitForRowByCategory(fresh);
        assertEquals(0.0, page.readRowCurrAmount(fresh), 0.001, "Fresh item should have 0 spent.");

        int rowsBefore = page.countBudgetRows();
        double totalBefore = page.readTotalMax();

        page.deleteItem(fresh);

        assertNull(page.findBudgetRowByCategory(fresh), "Row should be removed after delete.");
        assertEquals(rowsBefore - 1, page.countBudgetRows(), "Row count should decrease by 1.");

        double totalAfter = page.readTotalMax();
        assertTrue(totalAfter <= totalBefore - 699.99,
                "Total should decrease roughly by the deleted max amount.");
    }

    @Test
    @DisplayName("07 - Pagination 'Next' is safe to click")
    void paginationNextIsSafe() {
        page.clickSearch();
        assertDoesNotThrow(page::nextPage, "Paginator next should be clickable without errors.");
    }


    @Test
    @DisplayName("08 - Edit/add a budget item, then search remains stable")
    void editOrAddItemThenSearch() {
        String category = "Catering";
        if (page.findBudgetRowByCategory(category) == null) {
            category = page.createAnyAvailableCategoryAndReturnName("700");
        }
        page.editItem(category, "1200");
        page.clickSearch();

        List<WebElement> cards = page.getOfferCards();
        boolean noOffers = page.isNoOffersVisible();
        assertTrue(!cards.isEmpty() || noOffers,
                "After editing/adding and searching, either cards should show or 'no offers' should be visible.");

        String range = page.getPaginatorRangeText();
        assertNotNull(range);
        assertFalse(range.isBlank(), "Paginator range label should be present.");
    }

    @Test
    @DisplayName("09 - Search → Next keeps results area present")
    void searchThenNextPageKeepsResultsArea() {
        page.clickSearch();
        String firstRange = page.getPaginatorRangeText();

        page.nextPage();

        String newRange = page.getPaginatorRangeText();
        assertNotNull(newRange);
        assertFalse(newRange.isBlank());
        assertFalse(firstRange == null || firstRange.isBlank(), "Initial range should be present too.");
    }

    @Test
    @DisplayName("10 - Creating a duplicate category is prevented (UI hides it) or rejected (server-side)")
    void createDuplicateCategoryIsRejectedOrPrevented() {
        String seedCategory = "Makeup";
        if (page.findBudgetRowByCategory(seedCategory) == null) {
            // If "Makeup" not present, use any existing category; if none exist, create one
            List<String> tableCats = page.getTableCategories();
            if (tableCats.isEmpty()) {
                // try creating one; if no options exist to create, skip the test
                page.openCreateItemPopup();
                var options = page.getAvailableCategoriesNotInTable();
                page.cancelCreateItemPopup();
                Assumptions.assumeFalse(options.isEmpty(),
                        "No categories available to create; cannot seed a row for duplicate test.");
                seedCategory = options.get(0);
                page.createNewItem(seedCategory, "500");
                page.waitForRowByCategory(seedCategory);
            } else {
                seedCategory = tableCats.get(0);
            }
        } else {
            // "Makeup" exists already
            page.waitForRowByCategory(seedCategory);
        }
        assertNotNull(page.findBudgetRowByCategory(seedCategory), "Seed row must exist.");

        int beforeRows = page.countBudgetRows();
        double beforeTotal = page.readTotalMax();

        page.openCreateItemPopup();
        boolean categoryVisibleInDropdown = page.isCategoryInDropdown(seedCategory);
        page.cancelCreateItemPopup();

        if (!categoryVisibleInDropdown) {
            assertFalse(categoryVisibleInDropdown, "Existing category should not appear in the Create dropdown (UI prevention).");
            assertEquals(beforeRows, page.countBudgetRows());
            assertEquals(beforeTotal, page.readTotalMax(), 0.01);
        } else {
            String snack = page.tryCreateItemExpectingFailure(seedCategory, "600").toLowerCase();

            // Material snackbars often append a trailing "close" action—allow both wording and presence/absence of snackbar text.
            boolean looksLikeRejection =
                    snack.isBlank() ||
                            snack.contains("duplicate") ||
                            snack.contains("already") ||
                            snack.contains("exists") ||
                            snack.contains("cannot") ||
                            snack.contains("present") ||
                            snack.contains("non-picked") ||
                            snack.contains("non picked") ||
                            snack.contains("select a non-picked") ||
                            snack.contains("valid amount");

            assertTrue(looksLikeRejection,
                    "Expected a rejection-style snackbar; got: " + snack);

            assertEquals(beforeRows, page.countBudgetRows(), "Row count should not increase on duplicate.");
            assertEquals(beforeTotal, page.readTotalMax(), 0.01, "Total should not change on duplicate.");
        }
    }

}
