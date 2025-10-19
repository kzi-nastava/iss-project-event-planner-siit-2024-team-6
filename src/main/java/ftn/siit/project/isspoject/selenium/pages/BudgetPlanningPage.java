package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BudgetPlanningPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ---- Page locators ----
    private final By title = By.cssSelector("h2.budget-section-title");
    private final By goBackBtn = By.cssSelector("button.back-button");
    private final By newItemBtn = By.cssSelector("[data-testid='new-item-btn'], .new-item-button");

    // Recommended section
    private final By recommendationRow = By.cssSelector(".recommended-row");
    private final By recommendationChooseBtns = By.cssSelector("[data-testid='rec-choose'], .recommended-row .choose-button");

    // Precise popup roots (no :has(); scoped by headings)
    private final By createPopupRoot = By.xpath(
        "//div[contains(@class,'popup-backdrop')][.//div[contains(@class,'popup-modal')]//h5[normalize-space()='Add new budget item']]"
    );
    private final By editPopupRoot = By.xpath(
        "//div[contains(@class,'popup-backdrop')][.//div[contains(@class,'popup-modal')]//h5[contains(normalize-space(),'Edit maximal price')]]"
    );
    private final By addPopupRoot = By.xpath(
        "//div[contains(@class,'popup-backdrop')][.//div[contains(@class,'popup-modal')]//h4[contains(normalize-space(),'Add \"')]]"
    );

    // Elements inside any popup root (relative selectors)
    private final By popupNumberInputRel = By.cssSelector(".popup-modal input[type='number']");
    private final By popupConfirmBtnRel  = By.xpath(".//div[contains(@class,'popup-modal')]//button[normalize-space()='Confirm']");
    private final By popupCancelBtnRel   = By.xpath(".//div[contains(@class,'popup-modal')]//button[normalize-space()='Cancel']");
    private final By createPopupSelectRel = By.cssSelector(".popup-modal select");
    private final By selectOptionsRel = By.cssSelector("option");

    // Budget table
    private final By budgetRows = By.cssSelector("[data-testid^='budget-row-'], .budget-row");
    private By rowCategory(WebElement row) { return By.cssSelector("[data-testid='row-category'], span:nth-of-type(1)"); }
    private By rowMax(WebElement row)      { return By.cssSelector("[data-testid='row-max'], span:nth-of-type(2)"); }
    private By rowCurr(WebElement row)     { return By.cssSelector("[data-testid='row-curr'], span:nth-of-type(3)"); }
    private final By rowEditBtn = By.cssSelector("[data-testid='row-edit'], .edit-button");
    private final By rowDeleteBtn = By.cssSelector("[data-testid='row-delete'], .delete-button");

    // Summary & offers
    private final By totalMaxAmount = By.cssSelector(".total-amount");
    private final By spentAmount = By.cssSelector(".leftover-amount");
    private final By searchBtn = By.cssSelector("[data-testid='search-btn'], .search-button");
    private final By offersList = By.cssSelector("[data-testid='offers'], app-budget-offer-list.offers");

    // Material components
    private final By snackBarLabel = By.cssSelector(".mdc-snackbar__label, .mat-mdc-snack-bar-label");
    private final By matDialog = By.cssSelector(".mat-mdc-dialog-container");
    private final By matDialogConfirmBtn = By.xpath("//div[contains(@class,'mat-mdc-dialog-container')]//button[.='Confirm' or .='Yes']");

    // Paginator
    private final By paginatorNextBtn = By.cssSelector("button.mat-mdc-paginator-navigation-next");

    public BudgetPlanningPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        PageFactory.initElements(driver, this);
    }

    /** Navigate to the page and wait until it’s loaded. */
    public BudgetPlanningPage open(String baseUrl, long eventId) {
        driver.get(baseUrl + "/budget-planning/" + eventId);
        ensureLoaded();
        return this;
    }

    /** Convenience factory. */
    public static BudgetPlanningPage go(WebDriver driver, String baseUrl, long eventId) {
        return new BudgetPlanningPage(driver).open(baseUrl, eventId);
    }

    /** Wait for a key element that proves the page is ready. */
    private void ensureLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }

    // ---------- Utilities ----------
    private WebElement mustBeClickable(By by) { return wait.until(ExpectedConditions.elementToBeClickable(by)); }
    private WebElement mustBeVisible(By by) { return wait.until(ExpectedConditions.visibilityOfElementLocated(by)); }
    private List<WebElement> mustSeeAll(By by) { return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by)); }
    private void jsClick(WebElement el) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); }
    private void jsScrollIntoView(WebElement el) { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el); }

    // ---------- Basic getters ----------
    public String getTitle() { return mustBeVisible(title).getText(); }
    public String getTotalMaxAmountText() { return mustBeVisible(totalMaxAmount).getText(); }
    public String getSpentAmountText() { return mustBeVisible(spentAmount).getText(); }
    public String getSnackBarText() { return mustBeVisible(snackBarLabel).getText().trim(); }

    // ---------- Actions ----------
    public void clickGoBack() {
        WebElement backButton = mustBeVisible(goBackBtn);
        jsScrollIntoView(backButton);
        mustBeClickable(goBackBtn);
        jsClick(backButton);
    }

    // ====== CREATE POPUP ======
    public void openCreateItemPopup() {
        WebElement btn = mustBeClickable(newItemBtn);
        jsScrollIntoView(btn);
        jsClick(btn);
        mustBeVisible(createPopupRoot);
    }

    public void cancelCreateItemPopup() {
        WebElement root = mustBeVisible(createPopupRoot);
        WebElement cancel = root.findElement(popupCancelBtnRel);
        jsClick(cancel);
        wait.until(ExpectedConditions.stalenessOf(root));
    }

    private void waitForCategoryOptionsToLoad(WebElement root) {
        wait.until(d -> {
            try {
                WebElement select = root.findElement(createPopupSelectRel);
                List<WebElement> opts = select.findElements(selectOptionsRel);
                return opts.size() >= 2 && opts.stream().anyMatch(o -> o.isEnabled() && o.getText().trim().length() > 0);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    public List<String> getDropdownCategories() {
        WebElement root = mustBeVisible(createPopupRoot);
        waitForCategoryOptionsToLoad(root);
        WebElement select = root.findElement(createPopupSelectRel);
        return new Select(select).getOptions().stream()
                .map(o -> o.getText().trim())
                .filter(t -> t.length() > 0 && !t.equalsIgnoreCase("Select a category"))
                .toList();
    }

    public boolean isCategoryInDropdown(String category) {
        return getDropdownCategories().stream().anyMatch(t -> t.equalsIgnoreCase(category));
    }

    public void createNewItem(String category, String amount) {
        // Open & capture THIS popup root instance
        openCreateItemPopup();
        WebElement root = mustBeVisible(createPopupRoot);

        // Ensure we pick a non-duplicate category
        waitForCategoryOptionsToLoad(root);
        WebElement selectEl = root.findElement(createPopupSelectRel);
        Select select = new Select(selectEl);

        boolean picked = false;
        for (WebElement opt : select.getOptions()) {
            String text = opt.getText().trim();
            if (text.equalsIgnoreCase(category)) {
                opt.click();
                picked = true;
                break;
            }
        }
        if (!picked) {
            throw new NoSuchElementException("Category not found in dropdown: '" + category +
                    "'. Available: " + getDropdownCategories());
        }

        // Enter amount & confirm
        WebElement input = root.findElement(popupNumberInputRel);
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(amount);

        WebElement confirm = root.findElement(popupConfirmBtnRel);
        wait.until(ExpectedConditions.elementToBeClickable(confirm));
        jsClick(confirm);

        // Either the popup goes away OR we surface the snackbar text and fail fast
        try {
            wait.until(ExpectedConditions.stalenessOf(root));
        } catch (org.openqa.selenium.TimeoutException te) {
            String snack = "";
            try { snack = getSnackBarText(); } catch (Exception ignored) {}
            throw new AssertionError(
                "Create Item popup did not close. Likely validation failure (duplicate or invalid amount)." +
                (snack.isBlank() ? "" : " Snackbar: " + snack)
            );
        }
    }


    // ====== RECOMMENDATIONS POPUP ======
    public boolean hasRecommendations() {
        List<WebElement> rows = driver.findElements(recommendationRow);
        return !rows.isEmpty();
    }

    public void addFromRecommendationByIndex(int index, String amount) {
        List<WebElement> chooseBtns = mustSeeAll(recommendationChooseBtns);
        if (index >= chooseBtns.size()) throw new IllegalArgumentException("No recommendation at index " + index);
        jsScrollIntoView(chooseBtns.get(index));
        jsClick(chooseBtns.get(index));

        WebElement root = mustBeVisible(addPopupRoot);
        WebElement input = root.findElement(popupNumberInputRel);
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(amount);

        WebElement confirm = root.findElement(popupConfirmBtnRel);
        wait.until(ExpectedConditions.elementToBeClickable(confirm));
        jsClick(confirm);

        wait.until(ExpectedConditions.stalenessOf(root));
    }

    // ====== TABLE OPERATIONS ======
    public WebElement findBudgetRowByCategory(String category) {
        List<WebElement> rows = mustSeeAll(budgetRows);
        for (WebElement r : rows) {
            String cat = r.findElement(rowCategory(r)).getText().trim();
            if (cat.equalsIgnoreCase(category)) return r;
        }
        return null;
    }

    public String readRowMax(String category) {
        WebElement row = findBudgetRowByCategory(category);
        if (row == null) throw new NoSuchElementException("No row for " + category);
        return row.findElement(rowMax(row)).getText();
    }

    public String readRowCurr(String category) {
        WebElement row = findBudgetRowByCategory(category);
        if (row == null) throw new NoSuchElementException("No row for " + category);
        return row.findElement(rowCurr(row)).getText();
    }

    public void editItem(String category, String newMax) {
        WebElement row = findBudgetRowByCategory(category);
        if (row == null) throw new NoSuchElementException("No row for category: " + category);

        WebElement edit = row.findElement(rowEditBtn);
        jsScrollIntoView(edit);
        jsClick(edit);

        WebElement root = mustBeVisible(editPopupRoot);
        WebElement input = root.findElement(popupNumberInputRel);
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(newMax);

        WebElement confirm = root.findElement(popupConfirmBtnRel);
        wait.until(ExpectedConditions.elementToBeClickable(confirm));
        jsClick(confirm);

        wait.until(ExpectedConditions.stalenessOf(root));
    }

    public void deleteItem(String category) {
        WebElement row = findBudgetRowByCategory(category);
        if (row == null) throw new NoSuchElementException("No row for category: " + category);

        WebElement del = row.findElement(rowDeleteBtn);
        jsScrollIntoView(del);
        jsClick(del);

        wait.until(ExpectedConditions.visibilityOfElementLocated(matDialog));
        jsClick(mustBeClickable(matDialogConfirmBtn));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(matDialog));

        wait.until(d -> findBudgetRowByCategory(category) == null);
    }
    // --- Put these inside BudgetPlanningPage ---

    // Parse totals from summary labels
    public double readTotalMax() {
        String text = getTotalMaxAmountText();
        String digits = text.replaceAll("[^0-9.]", "");
        return digits.isEmpty() ? 0.0 : Double.parseDouble(digits);
    }

    public double readSpent() {
        String text = getSpentAmountText();
        String digits = text.replaceAll("[^0-9.]", "");
        return digits.isEmpty() ? 0.0 : Double.parseDouble(digits);
    }

    // Count rows in the budget table
    public int countBudgetRows() {
        return driver.findElements(budgetRows).size();
    }

    public String createAnyAvailableCategoryAndReturnName(String amount) {
        openCreateItemPopup();
        var candidates = getAvailableCategoriesNotInTable();
        cancelCreateItemPopup();
        if (candidates.isEmpty())
            throw new IllegalStateException("No selectable categories available (all are already in the table).");
        String chosen = candidates.get(0);
        createNewItem(chosen, amount);
        return chosen;
    }

        // Return category names present in the table
    public List<String> getTableCategories() {
        return driver.findElements(budgetRows).stream()
                .map(r -> r.findElement(rowCategory(r)).getText().trim())
                .filter(t -> !t.isEmpty())
                .toList();
    }

    // From the create popup, return only categories NOT in table
    public List<String> getAvailableCategoriesNotInTable() {
        WebElement root = mustBeVisible(createPopupRoot);
        waitForCategoryOptionsToLoad(root);
        List<String> dropdown = root.findElements(selectOptionsRel).stream()
                .map(o -> o.getText().trim())
                .filter(t -> t.length() > 0 && !t.equalsIgnoreCase("Select a category"))
                .toList();
        var present = getTableCategories().stream().map(String::toLowerCase).toList();
        return dropdown.stream()
                .filter(c -> !present.contains(c.toLowerCase()))
                .toList();
    }



    // ====== SEARCH / PAGINATION ======
    public void clickSearch() {
        WebElement btn = mustBeClickable(searchBtn);
        jsScrollIntoView(btn);
        jsClick(btn);
        mustBeVisible(offersList);
    }

    public void nextPage() {
        WebElement next = mustBeClickable(paginatorNextBtn);
        jsScrollIntoView(next);
        jsClick(next);
    }
    // --- OFFERS & PAGINATOR (add to your BudgetPlanningPage) ---

    // Offer cards inside the child component
    private final By offerCard = By.cssSelector("app-offer-card, .offer-card");
    // Message when no offers
    private final By noOffersMsg = By.cssSelector(".no-offers-message");
    // Paginator range (e.g., "1 – 8 of 23")
    private final By paginatorRange = By.cssSelector(".mat-mdc-paginator-range-label");
    // Page-size select trigger
    private final By pageSizeSelect = By.cssSelector(".mat-mdc-paginator-page-size-select, mat-select[aria-label='Items per page']");
    // Page-size option panel
    private final By matOption = By.cssSelector("mat-option");

    // Return offer cards currently shown
    public List<WebElement> getOfferCards() {
        // Search inside the offers host if present; otherwise search globally as a fallback
        List<WebElement> host = driver.findElements(offersList);
        if (!host.isEmpty()) {
            return host.get(0).findElements(offerCard);
        }
        return driver.findElements(offerCard);
    }

    // Whether the "No matching offers." message is visible
    public boolean isNoOffersVisible() {
        List<WebElement> host = driver.findElements(offersList);
        if (!host.isEmpty()) {
            List<WebElement> msg = host.get(0).findElements(noOffersMsg);
            return !msg.isEmpty() && msg.get(0).isDisplayed();
        }
        List<WebElement> msg = driver.findElements(noOffersMsg);
        return !msg.isEmpty() && msg.get(0).isDisplayed();
    }

    // Read paginator range label text (e.g., "1 – 8 of 23")
    public String getPaginatorRangeText() {
        return mustBeVisible(paginatorRange).getText().trim();
    }

    // Change page size (e.g., 4, 8, 12)
    public void setPageSize(int size) {
        WebElement trigger = mustBeClickable(pageSizeSelect);
        jsClick(trigger);
        // Wait for options panel and click matching option text
        wait.until(ExpectedConditions.visibilityOfElementLocated(matOption));
        List<WebElement> options = driver.findElements(matOption);
        WebElement match = options.stream()
                .filter(o -> o.getText().trim().equals(String.valueOf(size)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Page-size option not found: " + size));
        jsClick(match);
        // Wait for the menu to close (option panel becomes stale or invisible)
        wait.until(ExpectedConditions.invisibilityOf(match));
        // Give Angular a tick to update the list
        mustBeVisible(offersList);
    }

}
