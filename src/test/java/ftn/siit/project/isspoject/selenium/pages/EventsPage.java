package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.util.List;

public class EventsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By filterButton = By.id("openFilterButton");
    private final By filterSidebar = By.cssSelector(".filter-sidebar");
    private final By filterCloseButton = By.cssSelector(".filter-sidebar .close-button");
    private final By overlay = By.cssSelector(".overlay.active");
    private final By eventTypeSelect = By.id("eventTypeSelect");
    private final By fromDateInput = By.id("fromDate");
    private final By toDateInput = By.id("toDate");
    private final By applyButton = By.id("applyButton");
    private final By resetButton = By.id("resetButton");
    private final By eventCards = By.cssSelector("app-event-card");
    private final By eventType = By.cssSelector(".event-type");
    private final By eventDate = By.cssSelector(".event-date");
    private final By eventTitle = By.cssSelector(".event-card-title");
    private final By eventDescription = By.cssSelector(".event-description");
    private final By infoItem = By.cssSelector(".info-item span");
    private final By noEventsMessage = By.cssSelector(".no-events-message");
    private final By searchInput = By.cssSelector(".search input");
    private final By searchButton = By.cssSelector(".search button");
    private final By paginationNext = By.xpath("//button[@aria-label='Next page']");

    public EventsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // --- Filter sidebar ---
    public void openFilter() {
        wait.until(ExpectedConditions.elementToBeClickable(filterButton)).click();
        waitForEitherCardsOrNoEvents();

    }

    public void closeFilterWithOverlay() {
        wait.until(ExpectedConditions.elementToBeClickable(overlay)).click();
        waitForEitherCardsOrNoEvents();

    }

    public void closeFilterWithButton() {
        wait.until(ExpectedConditions.elementToBeClickable(filterCloseButton)).click();
        waitForEitherCardsOrNoEvents();

    }

    public boolean isFilterActive() {
        WebElement sidebar = driver.findElement(filterSidebar);
        return sidebar.isDisplayed() && sidebar.getAttribute("class").contains("active");
    }

    // --- Event type select ---
    public String selectFirstEventType() {
        WebElement selectEl = wait.until(ExpectedConditions.presenceOfElementLocated(eventTypeSelect));
        Select select = new Select(selectEl);

        // Wait until at least 1 option is present
        wait.until(driver -> !select.getOptions().isEmpty());

        List<WebElement> options = select.getOptions();
        WebElement firstOption = wait.until(ExpectedConditions.visibilityOf(options.get(0)));

        String selectedType = firstOption.getText().trim();
        select.selectByIndex(0);
        return selectedType;
    }


    // --- Date inputs ---
    public void setDateRange(LocalDate from, LocalDate to) {
        WebElement fromInput = wait.until(ExpectedConditions.presenceOfElementLocated(fromDateInput));
        WebElement toInput = wait.until(ExpectedConditions.presenceOfElementLocated(toDateInput));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input'));",
                fromInput, from.toString());
        js.executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input'));",
                toInput, to.toString());
    }

    public void applyFilter() {
        wait.until(ExpectedConditions.elementToBeClickable(applyButton)).click();
        waitForEitherCardsOrNoEvents();

    }

    public void resetFilter() {
        wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
        waitForEitherCardsOrNoEvents();

    }


    public List<WebElement> getEventCards() {
        waitForEitherCardsOrNoEvents();
        return driver.findElements(eventCards);
    }

    public WebElement getNoEventsMessage() {
        waitForEitherCardsOrNoEvents();
        return driver.findElement(noEventsMessage);
    }

    public List<String> getEventTypes() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".event-type")));
        return driver.findElements(By.cssSelector(".event-type"))
                .stream()
                .map(e -> e.getText().trim())
                .filter(s -> !s.isEmpty())
                .toList();
    }


    public List<String> getEventDates() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(eventDate));

        return driver.findElements(eventDate).stream()
                .map(e -> e.getText().trim())
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public String getEventType(int index) {
        List<WebElement> elements = wait.until(
                ExpectedConditions.refreshed(
                        ExpectedConditions.visibilityOfAllElementsLocatedBy(eventType)
                )
        );
        return elements.get(index).getText().trim();
    }

    public String getEventDate(int index) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(eventDate))
                .get(index).getText().trim();
    }


    public String getEventTitle(int index) {
        return wait.until(ExpectedConditions
                        .presenceOfAllElementsLocatedBy(eventCards))
                .get(index)
                .findElement(eventTitle)
                .getText();
    }
    public List<String> getEventTitles() {
        waitForEitherCardsOrNoEvents();
        List<WebElement> cards = driver.findElements(eventCards);

        return cards.stream()
                .map(card -> card.findElement(eventTitle).getText().trim())
                .toList();
    }

    public List<String> getEventDescriptions() {
        waitForEitherCardsOrNoEvents();
        List<WebElement> cards = driver.findElements(eventCards);

        return cards.stream()
                .map(card -> card.findElement(eventDescription)
                        .getAttribute("textContent")
                        .trim())
                .toList();
    }

    public List<List<String>> getEventPlaces() {
        waitForEitherCardsOrNoEvents();
        List<WebElement> cards = driver.findElements(eventCards);

        return cards.stream()
                .map(card -> card.findElements(infoItem).stream()
                        .map(WebElement::getText)
                        .toList()
                )
                .toList();
    }

    public String getEventDescription(int index) {
        waitForEitherCardsOrNoEvents();
        List<WebElement> cards = driver.findElements(eventCards);

        if (cards.isEmpty()) {
            return "";
        }

        return cards.get(index)
                .findElement(eventDescription)
                .getText()
                .trim();
    }




    public void goToNextPage(WebElement firstCard) {
        List<WebElement> nextButtons = driver.findElements(paginationNext);
        if (nextButtons.isEmpty()) {
            return;
        }

        WebElement nextBtn = nextButtons.get(0);

        if (!nextBtn.isEnabled()) {
            return;
        }

        wait.until(ExpectedConditions.elementToBeClickable(nextBtn)).click();
        wait.until(ExpectedConditions.stalenessOf(firstCard));
        waitForEitherCardsOrNoEvents();
    }


    // --- Search ---
    public void search(String keyword) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        input.clear();
        input.sendKeys(keyword);
        driver.findElement(searchButton).click();
        waitForEitherCardsOrNoEvents();

    }

    public void clearSearch() {
        WebElement input = driver.findElement(searchInput);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value=''; arguments[0].dispatchEvent(new Event('input'));", input
        );
        driver.findElement(searchButton).click();

        waitForEitherCardsOrNoEvents();

    }

    // --- Input getters ---

    public String getSelectedEventTypeValue() {
        WebElement select = wait.until(ExpectedConditions.presenceOfElementLocated(eventTypeSelect));
        return select.getAttribute("value").trim();
    }

    public String getFromDateValue() {
        WebElement fromInput = wait.until(ExpectedConditions.presenceOfElementLocated(fromDateInput));
        return fromInput.getAttribute("value").trim();
    }

    public String getToDateValue() {
        WebElement toInput = wait.until(ExpectedConditions.presenceOfElementLocated(toDateInput));
        return toInput.getAttribute("value").trim();
    }
    public void waitForEitherCardsOrNoEvents() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(eventCards),
                ExpectedConditions.visibilityOfElementLocated(noEventsMessage)
        ));
    }


}
