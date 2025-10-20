package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * /offers view with <app-offer-slider> + <app-offer-list>.
 */
public class OffersListPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // === Structure from your template ===
    private static final By LIST_ROOT = By.cssSelector(".offer-section .offer-list");
    private static final By OFFER_CARDS = By.cssSelector(".offer-list .offer-card");           // one card per offer
    private static final By CARD_CONTENT = By.cssSelector(".offer-card-content");               // click target
    private static final By CATEGORY_IN_CARD = By.cssSelector(".offer-card-info .info-item:nth-of-type(1) span");
    private static final By NO_OFFERS = By.cssSelector(".no-offers-message");
    private static final By Paginator = By.cssSelector("mat-paginator");

    // Search & sort
    private static final By SEARCH_INPUT = By.cssSelector(".filter-container .search input[placeholder='Search']");
    private static final By SEARCH_BTN = By.cssSelector(".filter-container .search button");
    private static final By SORT_SELECT = By.cssSelector("#sortDir");

    // Mat paginator controls
    private static final By NEXT_BTN = By.cssSelector("button[aria-label='Next page']");
    private static final By PREV_BTN = By.cssSelector("button[aria-label='Previous page']");
    private static final By RANGE_LABEL = By.cssSelector(".mat-mdc-paginator-range-label");

    // Detail-page anchors (to know we navigated)
    private static final By DETAIL_TITLE = By.cssSelector(".offer-title, h1.offer-title");

    public OffersListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        waitForLoaded();
    }

    private void waitForLoaded() {
        // Arrive at /offers with shell visible
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(LIST_ROOT),
                ExpectedConditions.presenceOfElementLocated(Paginator),
                ExpectedConditions.presenceOfElementLocated(NO_OFFERS),
                ExpectedConditions.presenceOfAllElementsLocatedBy(OFFER_CARDS)
        ));
        waitForListSettled();
    }

    /**
     * Robust settle: accept (cards) OR (explicit empty) OR (already on detail). Keep polling if only shell is present.
     */
    private void waitForListSettled() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        shortWait.until(d -> {
            try {
                String url = d.getCurrentUrl().toLowerCase(Locale.ROOT);
                if (url.contains("/offer/")) return true;

                for (WebElement c : d.findElements(OFFER_CARDS)) {
                    if (c.isDisplayed()) return true;
                }
                List<WebElement> empty = d.findElements(NO_OFFERS);
                return !empty.isEmpty() && empty.get(0).isDisplayed();
            } catch (StaleElementReferenceException ignored) {
                return false;
            }
        });

        // brief quiet period to let async rating bindings settle
        try {
            Thread.sleep(120);
        } catch (InterruptedException ignored) {
        }
    }


    public List<WebElement> getCards() {
        return driver.findElements(OFFER_CARDS);
    }

    public boolean isNoOffersVisible() {
        List<WebElement> empty = driver.findElements(NO_OFFERS);
        return !empty.isEmpty() && empty.get(0).isDisplayed();
    }

    public void openCardByIndex(int index) {
        List<WebElement> cards = getCards();
        if (index >= cards.size()) throw new IllegalArgumentException("No card at index " + index);

        WebElement card = cards.get(index);
        WebElement target;
        try {
            target = card.findElement(CARD_CONTENT);
        } catch (NoSuchElementException e) {
            target = card; // click the card container if content wrapper missing
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", target);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", target);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlMatches(".*/offer/\\d+.*"),
                ExpectedConditions.visibilityOfElementLocated(DETAIL_TITLE)
        ));
    }

    public void search(String q) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        input.clear();
        input.sendKeys(q);
        driver.findElement(SEARCH_BTN).click();
        waitForListSettled();
    }

    public void sort(String dir) { // "asc" or "desc"
        WebElement sel = wait.until(ExpectedConditions.elementToBeClickable(SORT_SELECT));
        sel.click();
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('change'))", sel, dir);
        waitForListSettled();
    }

    public String rangeText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(RANGE_LABEL)).getText().trim();
    }

    public void nextPage() {
        WebElement n = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BTN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", n);
        waitForListSettled();
    }

    private boolean isNextEnabled() {
        List<WebElement> nexts = driver.findElements(NEXT_BTN);
        if (nexts.isEmpty()) return false;
        WebElement n = nexts.get(0);
        String aria = n.getAttribute("aria-disabled");
        boolean disabled = "true".equalsIgnoreCase(aria) || !n.isEnabled();
        return !disabled;
    }

    private boolean goNextPageIfEnabled() {
        if (!isNextEnabled()) return false;
        WebElement n = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BTN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", n);
        waitForListSettled();
        return true;
    }

    /**
     * Extract the category text from a card (matches your first info-item span).
     */
    private String readCardCategoryByIndex(int index) {
        for (int attempt = 0; attempt < 6; attempt++) {
            try {
                List<WebElement> cards = driver.findElements(OFFER_CARDS);
                if (index >= cards.size()) return "";

                WebElement catSpan = cards.get(index).findElement(CATEGORY_IN_CARD);
                String txt = (catSpan.getText() == null) ? "" : catSpan.getText().trim();
                if (!txt.isEmpty()) return txt;

                // tiny pause in case text is being bound
                Thread.sleep(80);
            } catch (StaleElementReferenceException | InterruptedException ignored) {
                // card re-rendered; loop will re-query
            }
        }
        return "";
    }


    /**
     * Try to open a BUY-NOW product in a NEW category (not in beforeCats) on THIS page; leaves you on detail if found.
     */
    private boolean tryOpenFirstProductWithNewCategoryOnThisPage(HashSet<String> beforeCatsLower) {
        List<WebElement> cards = getCards();
        for (int i = 0; i < cards.size(); i++) {
            String cat = readCardCategoryByIndex(i);
            if (cat.isBlank() || beforeCatsLower.contains(cat.toLowerCase(Locale.ROOT))) continue;

            openCardByIndex(i);
            OfferInfoPage info = new OfferInfoPage(driver);

            if (!info.isBuyNowVisible()) {
                info.goBack();
                waitForListSettled();
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * Scan up to maxPages (current page = 1). Leaves you on detail page when a candidate is found; returns its category.
     */
    public String openFirstProductWithNewCategory(HashSet<String> beforeCats, int maxPages) {
        var beforeCatsLower = new HashSet<String>();
        for (String c : beforeCats) beforeCatsLower.add(c.toLowerCase(Locale.ROOT));

        int scanned = 0;
        while (true) {
            scanned++;
            if (tryOpenFirstProductWithNewCategoryOnThisPage(beforeCatsLower)) {
                // Confirm category on the detail page
                OfferInfoPage info = new OfferInfoPage(driver);
                String cat = info.getCategoryText();
                if (cat == null || cat.isBlank()) {
                    info.goBack();
                    waitForListSettled();
                } else {
                    return cat;
                }
            }
            if (scanned >= Math.max(1, maxPages) || !goNextPageIfEnabled()) break;
        }
        return null;
    }

    /**
     * Same as above but throws when nothing is found.
     */
    public String openFirstProductWithNewCategoryOrThrow(HashSet<String> beforeCats, int maxPages) {
        String cat = openFirstProductWithNewCategory(beforeCats, maxPages);
        if (cat == null) {
            throw new IllegalStateException("No BUY-NOW product with a NEW category found within " + maxPages + " page(s).");
        }
        return cat;
    }

    /**
     * Try to open a BUY-NOW product whose category IS IN beforeCats on THIS page. Leaves you on detail if found.
     */
    private boolean tryOpenFirstProductWithExistingCategoryOnThisPage(HashSet<String> beforeCatsLower) {
        int count = driver.findElements(OFFER_CARDS).size();
        for (int i = 0; i < count; i++) {
            String cat = readCardCategoryByIndex(i);
            if (cat.isBlank() || !beforeCatsLower.contains(cat.toLowerCase(Locale.ROOT))) continue;

            openCardByIndex(i);
            OfferInfoPage info = new OfferInfoPage(driver);
            if (!info.isBuyNowVisible()) {
                info.goBack();
                waitForListSettled();
                // list may have changed; recompute size for safety
                count = driver.findElements(OFFER_CARDS).size();
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * Scan up to maxPages. Leaves you on detail page when a candidate is found; returns its category.
     */
    public String openFirstProductWithExistingCategory(HashSet<String> beforeCats, int maxPages) {
        var beforeCatsLower = new HashSet<String>();
        for (String c : beforeCats) beforeCatsLower.add(c.toLowerCase(Locale.ROOT));

        int scanned = 0;
        while (true) {
            scanned++;
            if (tryOpenFirstProductWithExistingCategoryOnThisPage(beforeCatsLower)) {
                OfferInfoPage info = new OfferInfoPage(driver);
                String cat = info.getCategoryText();
                if (cat == null || cat.isBlank()) {
                    info.goBack();
                    waitForListSettled();
                } else {
                    return cat;
                }
            }
            if (scanned >= Math.max(1, maxPages) || !goNextPageIfEnabled()) break;
        }
        return null;
    }

    public String openFirstProductWithExistingCategoryOrThrow(HashSet<String> beforeCats, int maxPages) {
        String cat = openFirstProductWithExistingCategory(beforeCats, maxPages);
        if (cat == null) {
            throw new IllegalStateException("No BUY-NOW product with an EXISTING category found within " + maxPages + " page(s).");
        }
        return cat;
    }

    public String openFirstBuyNowProduct(int maxPages) {
        int scanned = 0;
        while (true) {
            scanned++;
            int count = driver.findElements(OFFER_CARDS).size();
            for (int i = 0; i < count; i++) {
                try {
                    openCardByIndex(i);
                    OfferInfoPage info = new OfferInfoPage(driver);
                    if (info.isBuyNowVisible()) {
                        String cat = info.getCategoryText();
                        if (cat != null && !cat.isBlank()) return cat;
                        // If category not visible, still treat as found
                        return "";
                    } else {
                        info.goBack();
                        waitForListSettled();
                    }
                } catch (StaleElementReferenceException ignored) {
                    // list re-rendered; continue to next i
                }
            }
            if (scanned >= Math.max(1, maxPages) || !goNextPageIfEnabled()) break;
        }
        return null;
    }


}
