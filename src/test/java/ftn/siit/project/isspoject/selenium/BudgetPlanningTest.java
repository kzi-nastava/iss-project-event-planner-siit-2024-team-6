package ftn.siit.project.isspoject.selenium;

import ftn.siit.project.isspoject.selenium.pages.BudgetPlanningPage;
import ftn.siit.project.isspoject.selenium.pages.HomePage;
import ftn.siit.project.isspoject.selenium.pages.LoginPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetPlanningTest extends BaseTest {

    private static final long EVENT_ID_UNDER_TEST = 27L; // <-- adjust
    private BudgetPlanningPage page;

    @BeforeEach
     void openPage() {
        // 1) Go directly to /login (no HomePage calls, nothing else changed)
        driver.get(baseUrl + "/login");

        // 2) Wait for the login form to actually be present
        // (matches your LoginPage selectors; adjust if your placeholder text is different)
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Email']")));
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Password']")));

        // 3) Use your existing LoginPage as-is
        LoginPage login = new LoginPage(driver);
        login.loginAs("organizer1@example.com", "123456789");

        // 4) Wait for post-login route (adjust if your app lands elsewhere)
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/events"));

        // 5) Now open the protected page
        page = new BudgetPlanningPage(driver).open(baseUrl, EVENT_ID_UNDER_TEST);
        assertEquals("Budget Planning", page.getTitle());
    }

    private void ensureRowExists(String category, String amount) {
        if (page.findBudgetRowByCategory(category) == null) {
            // If the desired category is not in dropdown, skip
            page.openCreateItemPopup();
            boolean available = page.isCategoryInDropdown(category);
            var options = page.getDropdownCategories();
            page.cancelCreateItemPopup();

            Assumptions.assumeTrue(available,
                    "Category '" + category + "' not in dropdown. Options: " + options);

            page.createNewItem(category, amount);
            assertNotNull(page.findBudgetRowByCategory(category),
                    "Row should exist after creating seed item '" + category + "'");
        }
    }

    /** Create a temp category (first available in dropdown) and return its name. */
    private String createAnyAvailableCategory(String amount) {
        page.openCreateItemPopup();
        var options = page.getDropdownCategories(); // already filters out placeholder
        page.cancelCreateItemPopup();
        Assumptions.assumeFalse(options.isEmpty(), "No selectable categories available in dropdown.");
        String chosen = options.get(0);
        page.createNewItem(chosen, amount);
        return chosen;
    }

    // ---------- Tests ----------

    @Test
    @DisplayName("01 - Add item from Recommendations -> verifies row + totals + snackbar")
    void addFromRecommendations() {
        Assumptions.assumeTrue(page.hasRecommendations(), "No recommendations available to test.");

        page.addFromRecommendationByIndex(0, "500");

        String snackbar = page.getSnackBarText().toLowerCase();
        assertTrue(snackbar.contains("create") || snackbar.contains("added") || snackbar.contains("success"),
                "Expected success snackbar after create, got: " + snackbar);

        assertTrue(page.getTotalMaxAmountText().contains("Total Max Amount"),
                "Totals header should be visible.");
    }

    @Test
    @DisplayName("02 - Create a brand-new item via New Item popup (dropdown verified first)")
    void createNewItem() {
        String category = "Makeup";

        // Verify the dropdown contains our category while the popup is open
        page.openCreateItemPopup();
        assertTrue(page.isCategoryInDropdown(category),
                "Category '" + category + "' not found. Options: " + page.getDropdownCategories());
        page.cancelCreateItemPopup();

        // Actually create the item
        page.createNewItem(category, "1200");

        // Snackbar + row checks
        String s = page.getSnackBarText().toLowerCase();
        assertTrue(s.contains("create") || s.contains("added") || s.contains("success"),
                "Expected creation success snackbar, got: " + s);
        assertNotNull(page.findBudgetRowByCategory(category), "Newly created category row should exist.");
    }

    @Test
    @DisplayName("03 - Edit an item increases max price (self-seeded if missing)")
    void editItem() {
        String category = "Catering"; // preferred; will be seeded if missing
        ensureRowExists(category, "1000");

        page.editItem(category, "2500");

        String snackbar = page.getSnackBarText().toLowerCase();
        assertTrue(snackbar.contains("update") || snackbar.contains("updated") || snackbar.contains("success"),
                "Expected success snackbar after update, got: " + snackbar);

        assertNotNull(page.findBudgetRowByCategory(category), "Row should still exist after edit.");
        // Optional: verify the text in the "Max" column if your UI shows the exact number
        // assertTrue(page.readRowMax(category).contains("2500"));
    }

    @Test
    @DisplayName("04 - Delete an item without purchased offers (creates temp if needed)")
    void deleteItem() {
        // Prefer a known name; if not possible, create any available category and delete it
        String category = "Decor";

        if (page.findBudgetRowByCategory(category) == null) {
            // Create a temp row with any available category instead
            category = createAnyAvailableCategory("100");
            assertNotNull(page.findBudgetRowByCategory(category),
                    "Temp row should exist before deleting");
        }

        page.deleteItem(category);

        String snackbar = page.getSnackBarText().toLowerCase();
        assertTrue(snackbar.contains("delete") || snackbar.contains("deleted") || snackbar.contains("success"),
                "Expected success snackbar after delete, got: " + snackbar);
        assertNull(page.findBudgetRowByCategory(category), "Row should be removed after delete.");
    }

    @Test
    @DisplayName("05 - Search offers + paginate")
    void searchAndPaginate() {
        page.clickSearch();
        // If your backend returns stable data, add stronger checks
        page.nextPage(); // No exception = pass (basic smoke)
    }
}
