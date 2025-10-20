package ftn.siit.project.isspoject.selenium;

import ftn.siit.project.isspoject.selenium.pages.BudgetPlanningPage;
import ftn.siit.project.isspoject.selenium.pages.LoginPage;
import ftn.siit.project.isspoject.selenium.pages.OfferInfoPage;
import ftn.siit.project.isspoject.selenium.pages.OffersListPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Focused tests around purchasing from offers and enforcing budget rules:
 * A) Buy new category -> row max = 0 & spent > 0
 * B) Edit max below spent -> rejected
 * C) Delete with spent > 0 -> blocked
 * D) Over-budget purchase -> rejected
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class BudgetPlanningPurchaseFlowTest extends BaseTest {

    private static final long EVENT_ID_UNDER_TEST = 28L;

    private BudgetPlanningPage page;

    @BeforeEach
    void openBudgetPlanning() {
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

    /**
     * Aggressively close any open Mat dialogs (buy/review/etc.). Safe if none are open.
     */
    private void closeAllDialogsIfAny() {
        final By DIALOG = By.cssSelector(".mat-mdc-dialog-container");
        final By BUTTONS = By.xpath(".//button[contains(.,'Close') or contains(.,'Cancel') or contains(.,'OK') or contains(.,'Dismiss') or contains(.,'No')]");
        for (int attempts = 0; attempts < 6; attempts++) {
            var dialogs = driver.findElements(DIALOG);
            if (dialogs.isEmpty()) return;
            for (WebElement dlg : dialogs) {
                try {
                    var btns = dlg.findElements(BUTTONS);
                    if (!btns.isEmpty()) {
                        try {
                            btns.get(0).click();
                        } catch (Exception ignored) {
                        }
                    } else {
                        try {
                            dlg.sendKeys(Keys.ESCAPE);
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }
        // last resort: send ESC to body once
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            Thread.sleep(150);
        } catch (Exception ignored) {
        }
    }

    /**
     * Shared precondition: go to /offers, find a BUY-NOW product whose category is NOT in the current budget,
     * purchase it for this event, then return (category, spent-from-table, offerUrl).
     * Leaves us back on the budget planning page before returning.
     */
    private PurchaseContext performInitialPurchase() {
        // Snapshot existing categories
        var beforeCats = new HashSet<>(page.getTableCategories());
        var beforeCatsLower = beforeCats.stream().map(String::toLowerCase).toList();

        // Go to /offers and scan
        driver.get(baseUrl + "/offers");
        OffersListPage offersList = new OffersListPage(driver);

        // Find a product with a NEW category (not in budget).
        String newCategory = offersList.openFirstProductWithNewCategoryOrThrow(beforeCats, /*maxPages*/ 5);

        // We are now on the Offer details page
        OfferInfoPage info = new OfferInfoPage(driver);
        assertTrue(info.isBuyNowVisible(), "Expected BUY NOW on product details.");

        String offerUrl = driver.getCurrentUrl();

        // Purchase
        info.clickBuyNow();
        info.selectEventInDialogById(EVENT_ID_UNDER_TEST);
        info.confirmPurchase();          // waits for snackbar
        info.closeReviewDialogIfOpen();  // proactively close review dialog if it popped

        // Instead of waiting for dialog invisibility (flaky when review stays open), drain any dialogs and move on
        closeAllDialogsIfAny();

        // Navigate back to Budget Planning directly (more reliable than history.back())
        driver.get(baseUrl + "/budget-planning/" + EVENT_ID_UNDER_TEST);
        page = new BudgetPlanningPage(driver);
        page.waitForRowByCategory(newCategory);

        // Ground truth: read spent from the table (we do NOT rely on offer price labels)
        double spent = page.readRowCurrAmount(newCategory);

        return new PurchaseContext(newCategory, spent, offerUrl);
    }
    // --- In BudgetPlanningPurchaseFlowTest ---

    /**
     * Buy a BUY-NOW product in a category NOT in the current budget (for test A only).
     */
    private PurchaseContext performPurchaseNewCategory() {
        var beforeCats = new HashSet<>(page.getTableCategories());

        driver.get(baseUrl + "/offers");
        OffersListPage offersList = new OffersListPage(driver);

        String category = offersList.openFirstProductWithNewCategoryOrThrow(beforeCats, 5);

        OfferInfoPage info = new OfferInfoPage(driver);
        assertTrue(info.isBuyNowVisible(), "Expected BUY NOW on product details.");

        String offerUrl = driver.getCurrentUrl();

        info.clickBuyNow();
        info.selectEventInDialogById(EVENT_ID_UNDER_TEST);
        info.confirmPurchase();
        info.closeReviewDialogIfOpen();
        closeAllDialogsIfAny();

        driver.get(baseUrl + "/budget-planning/" + EVENT_ID_UNDER_TEST);
        page = new BudgetPlanningPage(driver);
        page.waitForRowByCategory(category);

        double spent = page.readRowCurrAmount(category);
        return new PurchaseContext(category, spent, offerUrl);
    }

    /**
     * Buy a BUY-NOW product in a category that IS already in the budget (for tests B/C/D).
     */
    private PurchaseContext performPurchaseExistingCategory() {
        var existingCats = new HashSet<>(page.getTableCategories());
        assertFalse(existingCats.isEmpty(), "Need at least one existing budget category.");

        driver.get(baseUrl + "/offers");
        OffersListPage offersList = new OffersListPage(driver);

        // Find a product whose category is already present
        String category = offersList.openFirstProductWithExistingCategoryOrThrow(existingCats, 5);

        OfferInfoPage info = new OfferInfoPage(driver);
        assertTrue(info.isBuyNowVisible(), "Expected BUY NOW on product details.");

        String offerUrl = driver.getCurrentUrl();

        // Purchase to add to 'spent' within existing category
        info.clickBuyNow();
        info.selectEventInDialogById(EVENT_ID_UNDER_TEST);
        info.confirmPurchase();
        info.closeReviewDialogIfOpen();
        closeAllDialogsIfAny();

        driver.get(baseUrl + "/budget-planning/" + EVENT_ID_UNDER_TEST);
        page = new BudgetPlanningPage(driver);
        page.waitForRowByCategory(category);

        double spent = page.readRowCurrAmount(category);
        return new PurchaseContext(category, spent, offerUrl);
    }

    /**
     * Try to find a row with spent > 0; if none, purchase in existing category; if that fails, purchase any BUY-NOW.
     */
    private PurchaseContext ensureCategoryWithSpentPositive() {
        // 1) If we already have one, use it
        String cat = page.findAnyCategoryWithSpentPositive();
        if (cat != null) {
            // No need to buy; read current spent and produce a lightweight context.
            double spent = page.readRowCurrAmount(cat);
            return new PurchaseContext(cat, spent, /*offerUrl*/ null);
        }

        // 2) Try to purchase in an EXISTING budget category
        try {
            return performPurchaseExistingCategory(); // you already have this helper
        } catch (Exception e) {
            // ignore and try fallback
        }

        // 3) Fallback: purchase ANY BUY-NOW product (new or otherwise)
        driver.get(baseUrl + "/offers");
        OffersListPage offersList = new OffersListPage(driver);

        String pickedCat = offersList.openFirstBuyNowProduct(/*maxPages*/ 5);
        if (pickedCat == null) {
            throw new IllegalStateException("No BUY-NOW product found across pages; cannot create spent > 0.");
        }

        OfferInfoPage info = new OfferInfoPage(driver);
        String offerUrl = driver.getCurrentUrl();

        info.clickBuyNow();
        info.selectEventInDialogById(EVENT_ID_UNDER_TEST);
        info.confirmPurchase();
        info.closeReviewDialogIfOpen();
        closeAllDialogsIfAny();

        driver.get(baseUrl + "/budget-planning/" + EVENT_ID_UNDER_TEST);
        page = new BudgetPlanningPage(driver);
        page.waitForRowByCategory(pickedCat);

        double spent = page.readRowCurrAmount(pickedCat);
        return new PurchaseContext(pickedCat, spent, offerUrl);
    }


    private record PurchaseContext(String category, double spent, String offerUrl) {
    }

    // ------------------ Tests ------------------

    @Test
    @DisplayName("A) Buy new category → row exists, max=0, spent>0")
    void buyCreatesRow_MaxZero_SpentPositive() {
        PurchaseContext ctx = performPurchaseNewCategory();
        assertNotNull(page.findBudgetRowByCategory(ctx.category()));
        assertEquals(0.0, page.readRowMaxAmount(ctx.category()), 0.01);
        assertTrue(ctx.spent() > 0.0);
        assertEquals(ctx.spent(), page.readRowCurrAmount(ctx.category()), 0.01);
    }

    @Test
    @DisplayName("B) Edit max below spent is rejected")
    void editBelowSpent_IsRejected() {
        // Ensure we operate on a category with spent > 0
        PurchaseContext ctx = performPurchaseExistingCategory();

        // Snapshot invariants BEFORE attempting edit
        double beforeTotal = page.readTotalMax();
        double beforeMax = page.readRowMaxAmount(ctx.category());
        double beforeSpent = ctx.spent(); // from table, > 0 by precondition

        // Choose a value STRICTLY below spent, with decimal precision to dodge rounding pitfalls
        // e.g., if spent=10.00 -> attempt 9.49 ; if very small, fall back to 0.01
        double attemptMax = Math.max(0.01, Math.nextDown(beforeSpent) - 0.49);
        String attemptStr = String.format(Locale.ROOT, "%.2f", attemptMax);

        // Try to edit and capture any snackbar copy
        String snack = page.tryEditItemExpectingFailure(ctx.category(), attemptStr).toLowerCase();

        // Optional: accept a variety of messages, but don't rely solely on them
        boolean hasRejectMsg =
                snack.contains("cannot") || snack.contains("spent") || snack.contains("invalid")
                        || snack.contains("lower") || snack.contains("forbidden") || snack.contains("blocked")
                        || snack.contains("budget");

        // HARD ORACLE: the edit must not change anything
        assertEquals(beforeTotal, page.readTotalMax(), 0.01, "Total should not change on rejected edit.");
        assertEquals(beforeMax, page.readRowMaxAmount(ctx.category()), 0.01, "Max must remain unchanged after rejection.");
        assertEquals(beforeSpent, page.readRowCurrAmount(ctx.category()), 0.01, "Spent must remain unchanged.");

        // Keep the snackbar as a soft signal (won’t make the test flaky if empty/translated)
        if (!hasRejectMsg) {
            System.out.println("[info] No recognizable snackbar on edit<spent; state invariants confirm rejection. Snack: '" + snack + "'");
        }
    }


    @Test
    @DisplayName("C) Delete while spent>0 is blocked")
    void deleteWithSpent_IsBlocked() {
        // Ensure we have a category whose spent > 0 (by finding or purchasing)
        PurchaseContext ctx = ensureCategoryWithSpentPositive();

        int rowsBefore = page.countBudgetRows();
        String snack = page.attemptDeleteAndCaptureSnack(ctx.category()).toLowerCase();

        // Message is nice-to-have; invariants prove the block
        String snackNorm = snack
                .toLowerCase(Locale.ROOT)
                .replace("close", "")     // drop action button text
                .replaceAll("\\s+", " ")  // collapse whitespace/newlines
                .trim();

// accept common “blocked” wordings, including your actual copy
        boolean looksBlocked =
                snackNorm.contains("cannot")
                        || snackNorm.contains("not")
                        || snackNorm.contains("spent")
                        || snackNorm.contains("forbidden")
                        || snackNorm.contains("blocked")
                        || snackNorm.contains("unavailable")   // <- new
                        || snackNorm.contains("purchase")      // <- new (covers 'purchased')
                        || snackNorm.contains("purchased");    // <- explicit, if you prefer

        assertTrue(looksBlocked, "Expected delete-blocked snackbar; got: " + snack);


        // Hard invariants: row must still exist; row count unchanged
        assertNotNull(page.findBudgetRowByCategory(ctx.category()), "Row should remain present.");
        assertEquals(rowsBefore, page.countBudgetRows(), "Row count should not change when delete is blocked.");
    }

    @Test
    @DisplayName("D) Over-budget purchase is rejected")
    void overBudgetPurchase_IsRejected() {
        // Ensure we operate on an existing category with a known offer URL
        // This buys once (existing cat if possible; otherwise it creates one by buying)
        PurchaseContext ctx = ensureCategoryWithSpentPositive(); // from previous step
        // If ensureCategoryWithSpentPositive() did not produce an offerUrl (e.g., spent>0 existed already),
        // recreate a known offer + url by purchasing in an existing category:
        if (ctx.offerUrl() == null || ctx.offerUrl().isBlank()) {
            ctx = performPurchaseExistingCategory(); // this buys once and returns offerUrl
        }

        // Navigate to the same offer and read its price precisely
        driver.get(ctx.offerUrl());
        OfferInfoPage info = new OfferInfoPage(driver);
        double price = info.readDisplayedPrice();   // robust parser you already wrote
        assertTrue(price > 0.0, "Offer price must be > 0 to construct an over-budget condition.");

        // Go back to budget and set max so that one more purchase will exceed budget
        driver.get(baseUrl + "/budget-planning/" + EVENT_ID_UNDER_TEST);
        page = new BudgetPlanningPage(driver);

        double spentBefore = page.readRowCurrAmount(ctx.category());
        // epsilon to ensure strictly over
        double epsilon = 0.50;
        double targetMax = Math.max(0.01, spentBefore + price - epsilon); // remaining = price - epsilon
        String targetMaxStr = String.format(Locale.ROOT, "%.2f", targetMax);

        page.editItem(ctx.category(), targetMaxStr);
        assertEquals(targetMax, page.readRowMaxAmount(ctx.category()), 0.01, "Max should update to the target value.");

        // Try to buy the same product again -> must be rejected as over-budget
        driver.get(ctx.offerUrl());
        OfferInfoPage info2 = new OfferInfoPage(driver);
        info2.clickBuyNow();
        info2.selectEventInDialogById(EVENT_ID_UNDER_TEST);
        info2.confirmPurchase();
        String rawSnack = info2.getSnackBarText(); // you already normalized label-only text
        String snack = rawSnack.toLowerCase(Locale.ROOT)
                .replace("close", "")
                .replaceAll("\\s+", " ")
                .trim();

        // Accept common over-budget phrasings, including your actual app text
        boolean looksOverBudget =
                snack.contains("over") ||
                        snack.contains("exceed") ||
                        snack.contains("budget") ||
                        snack.contains("cannot") ||
                        snack.contains("invalid") ||
                        snack.contains("not enough") ||          // << new
                        snack.contains("enough allocated") ||    // << new
                        snack.contains("allocated funds");       // << new

        assertTrue(looksOverBudget, "Expected over-budget rejection; got: " + rawSnack);

        // Invariants unchanged after rejection
        driver.get(baseUrl + "/budget-planning/" + EVENT_ID_UNDER_TEST);
        page = new BudgetPlanningPage(driver);
        assertEquals(targetMax, page.readRowMaxAmount(ctx.category()), 0.01, "Max should remain unchanged after rejection.");
        assertEquals(spentBefore, page.readRowCurrAmount(ctx.category()), 0.01, "Spent should remain unchanged after rejection.");
    }

}
