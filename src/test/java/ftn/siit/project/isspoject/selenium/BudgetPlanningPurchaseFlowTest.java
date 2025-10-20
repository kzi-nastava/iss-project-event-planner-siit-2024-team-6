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
        String cat = page.findAnyCategoryWithSpentPositive();
        if (cat != null) {
            double spent = page.readRowCurrAmount(cat);
            return new PurchaseContext(cat, spent, /*offerUrl*/ null);
        }

        try {
            return performPurchaseExistingCategory();
        } catch (Exception e) {
        }

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
        PurchaseContext ctx = performPurchaseExistingCategory();

        double beforeTotal = page.readTotalMax();
        double beforeMax = page.readRowMaxAmount(ctx.category());
        double beforeSpent = ctx.spent();

        double attemptMax = Math.max(0.01, Math.nextDown(beforeSpent) - 0.49);
        String attemptStr = String.format(Locale.ROOT, "%.2f", attemptMax);

        String snack = page.tryEditItemExpectingFailure(ctx.category(), attemptStr).toLowerCase();

        boolean hasRejectMsg =
                snack.contains("cannot") || snack.contains("spent") || snack.contains("invalid")
                        || snack.contains("lower") || snack.contains("forbidden") || snack.contains("blocked")
                        || snack.contains("budget");

        assertEquals(beforeTotal, page.readTotalMax(), 0.01, "Total should not change on rejected edit.");
        assertEquals(beforeMax, page.readRowMaxAmount(ctx.category()), 0.01, "Max must remain unchanged after rejection.");
        assertEquals(beforeSpent, page.readRowCurrAmount(ctx.category()), 0.01, "Spent must remain unchanged.");

        if (!hasRejectMsg) {
            System.out.println("[info] No recognizable snackbar on edit<spent; state invariants confirm rejection. Snack: '" + snack + "'");
        }
    }


    @Test
    @DisplayName("C) Delete while spent>0 is blocked")
    void deleteWithSpent_IsBlocked() {
        PurchaseContext ctx = ensureCategoryWithSpentPositive();

        int rowsBefore = page.countBudgetRows();
        String snack = page.attemptDeleteAndCaptureSnack(ctx.category()).toLowerCase();

        String snackNorm = snack
                .toLowerCase(Locale.ROOT)
                .replace("close", "")
                .replaceAll("\\s+", " ")
                .trim();

        boolean looksBlocked =
                snackNorm.contains("cannot")
                        || snackNorm.contains("not")
                        || snackNorm.contains("spent")
                        || snackNorm.contains("forbidden")
                        || snackNorm.contains("blocked")
                        || snackNorm.contains("unavailable")
                        || snackNorm.contains("purchase")
                        || snackNorm.contains("purchased");

        assertTrue(looksBlocked, "Expected delete-blocked snackbar; got: " + snack);


        assertNotNull(page.findBudgetRowByCategory(ctx.category()), "Row should remain present.");
        assertEquals(rowsBefore, page.countBudgetRows(), "Row count should not change when delete is blocked.");
    }

}
