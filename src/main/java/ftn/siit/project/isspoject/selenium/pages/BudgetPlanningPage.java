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
    private final By popupConfirmBtnRel = By.xpath(".//div[contains(@class,'popup-modal')]//button[normalize-space()='Confirm']");
    private final By popupCancelBtnRel = By.xpath(".//div[contains(@class,'popup-modal')]//button[normalize-space()='Cancel']");
    private final By createPopupSelectRel = By.cssSelector(".popup-modal select");
    private final By selectOptionsRel = By.cssSelector("option");

    // Budget table
    private final By budgetRows = By.cssSelector("[data-testid^='budget-row-'], .budget-row");

    private By rowCategory(WebElement row) {
        return By.cssSelector("[data-testid='row-category'], span:nth-of-type(1)");
    }

    private By rowMax(WebElement row) {
        return By.cssSelector("[data-testid='row-max'], span:nth-of-type(2)");
    }

    private By rowCurr(WebElement row) {
        return By.cssSelector("[data-testid='row-curr'], span:nth-of-type(3)");
    }

    private final By rowEditBtn = By.cssSelector("[data-testid='row-edit'], .edit-button");
    private final By rowDeleteBtn = By.cssSelector("[data-testid='row-delete'], .delete-button");

    // Summary & offers
    private final By totalMaxAmount = By.cssSelector(".total-amount");
    private final By spentAmount = By.cssSelector(".leftover-amount");
    private final By searchBtn = By.cssSelector("[data-testid='search-btn'], .search-button");
    private final By offersList = By.cssSelector("[data-testid='offers'], app-budget-offer-list.offers");
    private final By offerCardContentRel = By.cssSelector(".offer-card-content");

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

    /**
     * Navigate to the page and wait until it’s loaded.
     */
    public BudgetPlanningPage open(String baseUrl, long eventId) {
        driver.get(baseUrl + "/budget-planning/" + eventId);
        ensureLoaded();
        return this;
    }

    /**
     * Convenience factory.
     */
    public static BudgetPlanningPage go(WebDriver driver, String baseUrl, long eventId) {
        return new BudgetPlanningPage(driver).open(baseUrl, eventId);
    }

    /**
     * Wait for a key element that proves the page is ready.
     */
    private void ensureLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }

    // ---------- Utilities ----------
    private WebElement mustBeClickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    private WebElement mustBeVisible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private List<WebElement> mustSeeAll(By by) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    private void jsClick(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    private void jsScrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
    }

    // ---------- Basic getters ----------
    public String getTitle() {
        return mustBeVisible(title).getText();
    }

    public String getTotalMaxAmountText() {
        return mustBeVisible(totalMaxAmount).getText();
    }

    public String getSpentAmountText() {
        return mustBeVisible(spentAmount).getText();
    }

    public String getSnackBarText() {
        return mustBeVisible(snackBarLabel).getText().trim();
    }

    private final By offerCardGlobal = By.cssSelector("app-budget-offer-list.offers app-offer-card, .offers app-offer-card, .offers .offer-card, .offer-card");
    private final By noOffersGlobal = By.cssSelector("app-budget-offer-list.offers .no-offers-message, .offers .no-offers-message, .no-offers-message");


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
            try {
                snack = getSnackBarText();
            } catch (Exception ignored) {
            }
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
        waitForRowByCategory(chosen);
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
        wait.until(d -> {
            try {
                return !getOfferCards().isEmpty() || isNoOffersVisible();
            } catch (Exception e) {
                return false;
            }
        });
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
        // Ensure paginator area in view
        By containerBy = By.cssSelector(".mat-mdc-paginator-page-size, .mat-mdc-paginator-page-size-select");
        WebElement container = driver.findElements(containerBy).isEmpty()
                ? mustBeVisible(By.cssSelector("mat-paginator"))
                : mustBeVisible(containerBy);
        jsScrollIntoView(container);

        List<WebElement> nativeSelects = container.findElements(By.cssSelector("select"));
        if (!nativeSelects.isEmpty()) {
            Select sel = new Select(nativeSelects.get(0));
            sel.selectByVisibleText(String.valueOf(size));
            // Wait for list re-render
            waitForOffersSettled();
            wait.until(d -> getOfferCards().size() <= size || isNoOffersVisible());
            return;
        }

        By triggerBy = By.cssSelector(
                ".mat-mdc-paginator-page-size .mat-mdc-select-trigger, " +
                        "mat-select[aria-label='Items per page'] .mat-mdc-select-trigger"
        );
        WebElement trigger = mustBeClickable(triggerBy);
        jsClick(trigger);

        // Panel can take a tick; sometimes the arrow is the clickable target
        By panelBy = By.cssSelector(".cdk-overlay-pane .mat-mdc-select-panel");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(panelBy));
        } catch (org.openqa.selenium.TimeoutException first) {
            // Retry by clicking the arrow icon if present
            List<WebElement> arrow = container.findElements(By.cssSelector(".mat-mdc-select-arrow"));
            if (!arrow.isEmpty()) {
                jsClick(arrow.get(0));
            } else {
                jsClick(trigger); // retry trigger
            }
            wait.until(ExpectedConditions.visibilityOfElementLocated(panelBy));
        }

        List<WebElement> opts = driver.findElements(By.cssSelector(".cdk-overlay-pane .mat-mdc-select-panel mat-option"));
        if (opts.isEmpty()) {
            opts = driver.findElements(By.cssSelector(".cdk-overlay-pane .mat-mdc-select-panel [role='option']"));
        }
        WebElement match = opts.stream()
                .filter(o -> o.getText().trim().equals(String.valueOf(size)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Page-size option not found in panel: " + size));

        jsClick(match);

        // Wait for panel to close and list to settle
        wait.until(ExpectedConditions.invisibilityOfElementLocated(panelBy));
        waitForOffersSettled();
        // Wait until visible count is <= selected size OR panel shows the size
        wait.until(d -> {
            try {
                int visible = getVisibleOfferCardsCount();
                if (visible <= size || isNoOffersVisible()) return true;

                // extra resilience: some themes update the page-size label reliably
                String lbl = getPageSizeValueText();
                if (!lbl.isEmpty() && lbl.equals(String.valueOf(size))) {
                    // still allow a couple of polls for UI to re-render
                    return getVisibleOfferCardsCount() <= size || isNoOffersVisible();
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        });
    }

    private final By pageSizeValue = By.cssSelector(".mat-mdc-paginator-page-size-value");

    public String getPageSizeValueText() {
        List<WebElement> v = driver.findElements(pageSizeValue);
        return v.isEmpty() ? "" : v.get(0).getText().trim();
    }


    // Count only *visible* cards (displayed)
    public int getVisibleOfferCardsCount() {
        int n = 0;
        for (WebElement el : getOfferCards()) {
            try {
                if (el.isDisplayed()) n++;
            } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            }
        }
        return n;
    }

    public boolean hasPageSizeControl() {
        By containerBy = By.cssSelector(".mat-mdc-paginator-page-size, .mat-mdc-paginator-page-size-select, mat-select[aria-label='Items per page']");
        if (driver.findElements(containerBy).isEmpty()) return false;

        if (!driver.findElements(By.cssSelector(".mat-mdc-paginator-page-size select")).isEmpty()) return true;

        return !driver.findElements(By.cssSelector(".mat-mdc-paginator-page-size .mat-mdc-select-trigger")).isEmpty()
                || !driver.findElements(By.cssSelector("mat-select[aria-label='Items per page'] .mat-mdc-select-trigger")).isEmpty();
    }


    public void openOfferCardByIndex(int index) {
        var cards = getOfferCards();
        if (index >= cards.size()) throw new IllegalArgumentException("No offer at index " + index);

        WebElement clickTarget;
        try {
            // Prefer the clickable content; if not found, fall back to the card itself
            clickTarget = cards.get(index).findElement(By.cssSelector(".offer-card-content"));
        } catch (NoSuchElementException e) {
            clickTarget = cards.get(index);
        }
        jsScrollIntoView(clickTarget);
        jsClick(clickTarget);

        // Route change to /offer/:id
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/offer/"),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".offer-title, h1.offer-title"))
        ));
    }

    public double readRowMaxAmount(String category) {
        String text = readRowMax(category);
        try {
            return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }


    private void waitForOffersSettled() {
        wait.until(d -> {
            try {
                // Use fresh, global queries each poll
                if (!d.findElements(offerCardGlobal).isEmpty()) return true;
                if (!d.findElements(noOffersGlobal).isEmpty()) {
                    // ensure the empty element is actually visible
                    for (WebElement el : d.findElements(noOffersGlobal)) {
                        try {
                            if (el.isDisplayed()) return true;
                        } catch (StaleElementReferenceException ignored) {
                        }
                    }
                }
                return false;
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    // Add to BudgetPlanningPage
    public void waitForRowByCategory(String category) {
        wait.until(d -> {
            try {
                // Re-query rows without the "mustSeeAll" hard wait to avoid nested waits
                List<WebElement> rows = d.findElements(budgetRows);
                for (WebElement r : rows) {
                    try {
                        String cat = r.findElement(rowCategory(r)).getText().trim();
                        if (cat.equalsIgnoreCase(category)) return true;
                    } catch (StaleElementReferenceException ignored) {
                    }
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        });
    }

    // ===== Negative test helpers & utilities =====

    // Parse a money label like "€1,234.50" -> 1234.50
    private double parseAmount(String text) {
        String digits = text.replaceAll("[^0-9.,]", "").replace(',', '.');
        if (digits.chars().filter(ch -> ch == '.').count() > 1) {
            // normalize "1.234.567,89" style if needed (best-effort)
            digits = digits.replaceAll("\\.(?=.*\\.)", "");
        }
        return digits.isBlank() ? 0.0 : Double.parseDouble(digits);
    }

    public double readRowCurrAmount(String category) {
        return parseAmount(readRowCurr(category));
    }

    public boolean isCreatePopupOpen() {
        return !driver.findElements(createPopupRoot).isEmpty();
    }

    public boolean isEditPopupOpen() {
        return !driver.findElements(editPopupRoot).isEmpty();
    }

    // Try to create an item but EXPECT a validation failure (e.g., duplicate category)
    public String tryCreateItemExpectingFailure(String category, String amount) {
        openCreateItemPopup();
        WebElement root = mustBeVisible(createPopupRoot);

        waitForCategoryOptionsToLoad(root);
        WebElement selectEl = root.findElement(createPopupSelectRel);
        Select select = new Select(selectEl);

        boolean picked = false;
        for (WebElement opt : select.getOptions()) {
            if (opt.getText().trim().equalsIgnoreCase(category)) {
                opt.click();
                picked = true;
                break;
            }
        }
        if (!picked) throw new NoSuchElementException("Category not found: " + category);

        WebElement input = root.findElement(popupNumberInputRel);
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(amount);

        WebElement confirm = root.findElement(popupConfirmBtnRel);
        wait.until(ExpectedConditions.elementToBeClickable(confirm));
        jsClick(confirm);

        // Expect the popup to remain (validation prevents closing)
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(createPopupRoot));
        } catch (Exception ignored) {
        }

        String snack = "";
        try {
            snack = getSnackBarText();
        } catch (Exception ignored) {
        }

        // clean up popup so the test can proceed
        try {
            WebElement cancel = root.findElement(popupCancelBtnRel);
            jsClick(cancel);
            wait.until(ExpectedConditions.stalenessOf(root));
        } catch (Exception ignored) {
        }

        return snack == null ? "" : snack.trim();
    }

    // Try to edit an item but EXPECT a validation failure (e.g., newMax < spent)
    public String tryEditItemExpectingFailure(String category, String newMax) {
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

        // Expect the popup to remain open
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(editPopupRoot));
        } catch (Exception ignored) {
        }

        String snack = "";
        try {
            snack = getSnackBarText();
        } catch (Exception ignored) {
        }

        // Dismiss popup to recover
        try {
            WebElement cancel = root.findElement(popupCancelBtnRel);
            jsClick(cancel);
            wait.until(ExpectedConditions.stalenessOf(root));
        } catch (Exception ignored) {
        }

        return snack == null ? "" : snack.trim();
    }

    public String attemptDeleteAndCaptureSnack(String category) {
        WebElement row = findBudgetRowByCategory(category);
        if (row == null) throw new NoSuchElementException("No row for " + category);

        WebElement del = row.findElement(rowDeleteBtn);
        jsScrollIntoView(del);

        boolean disabled = !del.isEnabled()
                || "true".equalsIgnoreCase(del.getAttribute("aria-disabled"))
                || del.getAttribute("disabled") != null;

        try {
            jsClick(del);
        } catch (Exception ignored) {
        }

        WebElement dialog = null;
        try {
            dialog = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(matDialog));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            dialog = null; // no dialog -> fall through
        }

        if (dialog != null) {
            WebElement confirm = new WebDriverWait(driver, Duration.ofSeconds(4))
                    .until(d -> {
                        WebElement dlog = topMostDialog();
                        WebElement c = findConfirmButtonIn(dlog);
                        return (c != null && c.isDisplayed() && c.isEnabled()) ? c : null;
                    });
            jsScrollIntoView(confirm);
            jsClick(confirm);

            try {
                new WebDriverWait(driver, Duration.ofSeconds(4))
                        .until(ExpectedConditions.invisibilityOf(dialog));
            } catch (org.openqa.selenium.TimeoutException ignored) {
            }
        }

        // --- In BOTH flows, try to read a snackbar quickly; allow empty
        try {
            WebElement label = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(snackBarLabel));
            String txt = label.getText();
            return (txt == null) ? "" : txt.trim();
        } catch (Exception ignored) {
        }

        // No snackbar visible (some UIs just ignore/disable button) — let the test assert invariants.
        return "";
    }


    // Find any category whose "spent" > 0 (for delete-blocked test)
    public String findAnyCategoryWithSpentPositive() {
        List<WebElement> rows = driver.findElements(budgetRows);
        for (WebElement r : rows) {
            try {
                String cat = r.findElement(rowCategory(r)).getText().trim();
                String currTxt = r.findElement(rowCurr(r)).getText();
                if (parseAmount(currTxt) > 0.0) return cat;
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return null;
    }

    public void deleteItem(String category) {
        WebElement row = findBudgetRowByCategory(category);
        if (row == null) throw new NoSuchElementException("No row for category: " + category);

        WebElement del = row.findElement(rowDeleteBtn);
        jsScrollIntoView(del);
        jsClick(del);

        // Wait for dialog to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(matDialog));
        WebElement dialog = topMostDialog();

        WebElement confirm = wait.until(d -> {
            WebElement dlog = topMostDialog();
            WebElement c = findConfirmButtonIn(dlog);
            return (c != null && c.isDisplayed() && c.isEnabled()) ? c : null;
        });

        jsScrollIntoView(confirm);
        mustBeClickable(matDialogButtons); // small guard to ensure buttons are interactable
        jsClick(confirm);

        wait.until(ExpectedConditions.invisibilityOf(dialog));
        wait.until(d -> findBudgetRowByCategory(category) == null);
    }

    private final By matDialogButtons = By.cssSelector(".mat-mdc-dialog-container button");

    private WebElement topMostDialog() {
        List<WebElement> dialogs = driver.findElements(matDialog);
        if (dialogs.isEmpty()) throw new NoSuchElementException("No dialog visible");
        return dialogs.get(dialogs.size() - 1); // topmost
    }

    private WebElement findConfirmButtonIn(WebElement dialog) {
        List<WebElement> btns = dialog.findElements(matDialogButtons);
        for (WebElement b : btns) {
            String txt = b.getText().trim().toLowerCase();
            if (txt.equals("confirm") || txt.equals("yes") || txt.equals("delete") || txt.equals("ok")) {
                return b;
            }
        }
        for (WebElement b : btns) {
            String txt = b.getText().trim().toLowerCase();
            if (!txt.equals("cancel") && !txt.equals("no") && !txt.contains("close")) {
                return b;
            }
        
        return btns.isEmpty() ? null : btns.get(btns.size() - 1);
    }


}
