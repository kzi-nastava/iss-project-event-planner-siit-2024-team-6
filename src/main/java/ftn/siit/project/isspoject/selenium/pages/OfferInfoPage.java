package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OfferInfoPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Page identity
    private final By offerTitle = By.cssSelector(".offer-title, h1.offer-title");
    private final By backBtn = By.cssSelector(".floating-back-btn");

    // Buttons – match the template text exactly
    private final By btnBuyNow = By.xpath("//button[contains(@class,'btn-buy')][normalize-space()='BUY NOW']");
    private final By btnBookIt = By.xpath("//button[contains(@class,'btn-buy')][normalize-space()='BOOK IT']");

    // Availability marker from template (<p class="text-available">Available.</p> / "Currently unavailable")
    private final By availability = By.cssSelector("p.text-available");

    // Price section
    private final By priceBlock = By.xpath("//strong[normalize-space()='Price']/following-sibling::p[1]");
    private final By salePrice = By.cssSelector(".discount-price");

    // Buy dialog (MatDialog)
    private final By dialog = By.cssSelector(".mat-mdc-dialog-container");
    private final By eventSelect = By.cssSelector("mat-select[formcontrolname], mat-select");
    private final By overlayOption = By.cssSelector(".cdk-overlay-pane mat-option");
    private final By confirmBtn = By.xpath("//button[contains(., 'Confirm Purchase') or contains(., 'Confirm')]");

    // Snackbar & optional review dialog
    private final By snackBar = By.cssSelector(".mdc-snackbar__label, .mat-mdc-snack-bar-label");
    private final By anyDialogContainer = By.cssSelector(".mat-mdc-dialog-container");
    private final By cancelOrCloseBtn = By.xpath("//div[contains(@class,'mat-mdc-dialog-container')]//button[contains(.,'Cancel') or contains(.,'Close')]");
    // in OfferInfoPage.java

    // just below your other locators:
    private final By categoryBlock = By.xpath("//strong[normalize-space()='Category']/following-sibling::p[1]");

    // ...
    public String getCategoryText() {
        try {
            return driver.findElement(categoryBlock).getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public OfferInfoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfElementLocated(offerTitle));
    }

    // ---------- Availability & type helpers ----------

    /**
     * True when the page shows "Available." and not "Currently unavailable".
     */
    public boolean isAvailable() {
        try {
            WebElement p = driver.findElement(availability);
            String t = p.getText().trim().toLowerCase();
            return t.contains("available") && !t.contains("unavailable");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * True if this detail page represents a Service (BOOK IT button present).
     */
    public boolean isServicePage() {
        try {
            WebElement b = driver.findElement(btnBookIt);
            return b.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * True only when BUY NOW is visible (Product & available).
     */
    public boolean isProductBuyable() {
        if (!isAvailable()) return false;
        try {
            WebElement b = driver.findElement(btnBuyNow);
            return b.isDisplayed() && b.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Backwards-compatible alias used by older tests.
     */
    public boolean isBuyNowVisible() {
        return isProductBuyable();
    }

    // ---------- Actions ----------

    public void clickBuyNow() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnBuyNow));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(dialog));
    }

    public void selectEventInDialogById(long eventId) {
        WebElement dlg = wait.until(ExpectedConditions.visibilityOfElementLocated(dialog));

        // Open the mat-select
        WebElement select = dlg.findElement(eventSelect);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", select);

        // Wait for options to render
        wait.until(ExpectedConditions.visibilityOfElementLocated(overlayOption));

        String idStr = String.valueOf(eventId);
        var opts = driver.findElements(overlayOption);
        WebElement match = null;

        for (WebElement o : opts) {
            try {
                // Try angular attributes first
                String v1 = o.getAttribute("ng-reflect-value");
                String v2 = o.getAttribute("value");
                String text = o.getText() == null ? "" : o.getText().trim();
                if (idStr.equals(v1) || idStr.equals(v2) || text.contains(idStr)) {
                    match = o;
                    break;
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }

        if (match == null && !opts.isEmpty()) match = opts.get(0);
        if (match == null) throw new NoSuchElementException("No events available in Buy dialog.");

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", match);
    }

    public void confirmPurchase() {
        WebElement dlg = wait.until(ExpectedConditions.visibilityOfElementLocated(dialog));
        WebElement confirm = dlg.findElement(confirmBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", confirm);
        wait.until(ExpectedConditions.visibilityOfElementLocated(snackBar));
    }

    public String getSnackBarText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(snackBar)).getText().trim();
    }

    // ---------- Price parsing ----------

    public double readDisplayedPrice() {
        WebElement block = wait.until(ExpectedConditions.visibilityOfElementLocated(priceBlock));
        String raw;
        try {
            raw = block.findElement(salePrice).getText();
        } catch (NoSuchElementException e) {
            raw = block.getText();
        }

        // Normalize “1.234,56 $” / “1,234.56 $” / “1234 $”
        String norm = raw.replace("\u00A0", " ").trim(); // no-break space
        norm = norm.replaceAll("[^0-9.,]", "");
        if (norm.contains(".") && norm.contains(",")) {
            norm = norm.replace(".", "").replace(",", "."); // European style
        } else if (norm.contains(",") && !norm.contains(".")) {
            norm = norm.replace(",", "."); // comma as decimal
        } else {
            norm = norm.replace(",", ""); // remove thousands comma
        }

        try {
            return Double.parseDouble(norm);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    // ---------- Misc ----------

    public void closeReviewDialogIfOpen() {
        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {
        }
        var dialogs = driver.findElements(anyDialogContainer);
        if (!dialogs.isEmpty()) {
            var closers = driver.findElements(cancelOrCloseBtn);
            if (!closers.isEmpty()) {
                try {
                    closers.get(0).click();
                } catch (Exception ignored) {
                    dialogs.get(0).sendKeys(Keys.ESCAPE);
                }
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.invisibilityOfElementLocated(cancelOrCloseBtn));
            }
        }
    }

    public void goBack() {
        WebElement back = wait.until(ExpectedConditions.elementToBeClickable(backBtn));
        back.click();
    }

    public String getTitleText() {
        return driver.findElement(offerTitle).getText().trim();
    }
}
