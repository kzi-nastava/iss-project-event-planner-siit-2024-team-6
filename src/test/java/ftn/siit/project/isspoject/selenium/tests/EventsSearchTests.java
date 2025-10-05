package ftn.siit.project.isspoject.selenium.tests;
import ftn.siit.project.isspoject.selenium.pages.EventsPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class EventsSearchTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private EventsPage eventsPage;
    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get("http://localhost:4200/events");
        eventsPage = new EventsPage(driver, wait);
        resetState();

    }
    private void resetState() {
        ((JavascriptExecutor) driver).executeScript("localStorage.clear(); sessionStorage.clear();");
        driver.navigate().refresh();
        eventsPage.waitForEitherCardsOrNoEvents();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testFilterSidebarToggle() {
        eventsPage.openFilter();
        assertTrue(eventsPage.isFilterActive(), "Sidebar must be active when opened");

        eventsPage.closeFilterWithOverlay();
        assertFalse(eventsPage.isFilterActive(), "Sidebar must not be active after closing");
    }


    @Test
    public void testFilterSidebarCloseButton() {
        eventsPage.openFilter();
        assertTrue(eventsPage.isFilterActive(), "Sidebar must be active after opening");

        eventsPage.closeFilterWithButton();
        assertFalse(eventsPage.isFilterActive(), "Sidebar must not be active after clicking close button");
    }


    @Test
    public void testFilterByEventType() {
        eventsPage.openFilter();

        String selectedType = eventsPage.selectFirstEventType();
        assumeTrue(selectedType != null, "No event types available, skipping test");

        eventsPage.applyFilter();

        List<WebElement> events = eventsPage.getEventCards();
        if (events.isEmpty()) {
            assertTrue(eventsPage.getNoEventsMessage().isDisplayed(),
                    "No events message should be shown");
        } else {
            List<String> types = eventsPage.getEventTypes();
            assertFalse(types.isEmpty(), "At least one event type should be present");
            assertTrue(types.stream().allMatch(t -> t.equals(selectedType)),
                    "All results must match the selected type: " + selectedType);
        }
    }



    @Test
    public void testFilterByDateRange() {

        eventsPage.openFilter();

        LocalDate from = LocalDate.now().plusDays(5);
        LocalDate to = LocalDate.now().plusDays(90);

        eventsPage.setDateRange(from, to);
        eventsPage.applyFilter();

        List<WebElement> events = eventsPage.getEventCards();

        if (events.isEmpty()) {
            assertTrue(eventsPage.getNoEventsMessage().isDisplayed(),
                    "No events message should be shown");
        } else {
            List<String> dateStrings = eventsPage.getEventDates();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

            for (String d : dateStrings) {
                LocalDate parsed = LocalDate.parse(d, fmt);
                assertFalse(parsed.isBefore(from) || parsed.isAfter(to),
                        "Date " + parsed + " must be between " + from + " and " + to);
            }
        }
    }

    @Test
    public void testFilterByTypeAndDateRangeTogether() {

        eventsPage.openFilter();

        String selectedType = eventsPage.selectFirstEventType();
        if (selectedType == null || selectedType.isEmpty()) {
            System.out.println("No event types available, skipping test");
            return;
        }

        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = from.plusDays(90);
        eventsPage.setDateRange(from, to);

        eventsPage.applyFilter();

        List<WebElement> events = eventsPage.getEventCards();
        if (events.isEmpty()) {
            assertTrue(eventsPage.getNoEventsMessage().isDisplayed(),
                    "No events message should be shown for combined filter");
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

            for (int i = 0; i < events.size(); i++) {
                String type = eventsPage.getEventType(i);
                String dateStr = eventsPage.getEventDate(i);
                LocalDate parsedDate = LocalDate.parse(dateStr, fmt);

                assertEquals(selectedType, type, "Event type must match the selected filter");
                assertFalse(parsedDate.isBefore(from) || parsedDate.isAfter(to),
                        "Event date must fall within the selected range");
            }
        }
    }


    @Test
    public void testResetFilters() {

        eventsPage.openFilter();

        String selectedType = eventsPage.selectFirstEventType();
        if (selectedType == null || selectedType.isEmpty()) {
            System.out.println("No event types available, skipping test");
            return;
        }

        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = from.plusDays(90);
        eventsPage.setDateRange(from, to);

        eventsPage.applyFilter();

        // get filtered count
        int filteredCount = eventsPage.getEventCards().size();
        System.out.println("Filtered " + filteredCount + " events");

        // reopen filter & reset
        eventsPage.openFilter();
        eventsPage.resetFilter();

        // after reset count
        int afterResetCount = eventsPage.getEventCards().size();
        assertTrue(afterResetCount >= filteredCount,
                "Reset should restore more/equal events compared to filtered");

        // check if inputs cleared
        assertEquals("", eventsPage.getSelectedEventTypeValue(), "Event type must reset");
        assertEquals("", eventsPage.getFromDateValue(), "From date must reset");
        assertEquals("", eventsPage.getToDateValue(), "To date must reset");
    }


    @Test
    public void testPaginationWithFilter() {

        eventsPage.openFilter();

        String selectedType = eventsPage.selectFirstEventType();
        if (selectedType == null || selectedType.isEmpty()) {
            System.out.println("No event types available, skipping test");
            return;
        }

        eventsPage.applyFilter();

        // get first page of events
        List<WebElement> firstPageEvents = eventsPage.getEventCards();
        if (firstPageEvents.isEmpty()) {
            assertTrue(eventsPage.getNoEventsMessage().isDisplayed(),
                    "No events message should be shown");
            return;
        }

        List<WebElement> nextButtons = driver.findElements(By.xpath("//button[@aria-label='Next page']"));
        if (nextButtons.isEmpty() || !nextButtons.get(0).isEnabled()) {
            System.out.println("Pagination not available for current filter — skipping next page test");
            return;
        }

        // save first element and go to next page
        WebElement firstEventPage1 = firstPageEvents.get(0);
        eventsPage.goToNextPage(firstEventPage1);

        // get page 2 events
        List<WebElement> page2Events = eventsPage.getEventCards();

        // check filter consistency on page 2
        if (!page2Events.isEmpty()) {
            List<String> types = eventsPage.getEventTypes();
            assertTrue(types.stream().allMatch(t -> t.equals(selectedType)),
                    "All events on page 2 must still match the filter");
        } else {
            assertTrue(eventsPage.getNoEventsMessage().isDisplayed(),
                    "No events message should be shown on page 2");
        }
    }



    @Test
    public void testSearchUpdatesResults() {

        String keyword = "tech";
        eventsPage.search(keyword);

        List<String> titles = eventsPage.getEventTitles();
        List<String> descriptions = eventsPage.getEventDescriptions();
        List<List<String>> places = eventsPage.getEventPlaces();

        if (titles.isEmpty() && descriptions.isEmpty() && places.isEmpty()) {
            assertTrue(eventsPage.getNoEventsMessage().isDisplayed(),
                    "If no results, 'no events' message must be shown");
        } else {
            String loweredKeyword = keyword.toLowerCase();

            int totalEvents = titles.size();
            for (int i = 0; i < totalEvents; i++) {
                String title = titles.get(i).toLowerCase();
                String description = descriptions.get(i).toLowerCase();
                String place = places.get(i).isEmpty() ? "" : places.get(i).get(0).toLowerCase();

                boolean containsKeyword = title.contains(loweredKeyword)
                        || description.contains(loweredKeyword)
                        || place.contains(loweredKeyword);

                assertTrue(containsKeyword,
                        "Event " + i + " must contain the search keyword in title, description, or place");
            }
        }
    }


    @Test
    public void testSearchAndClearRestoresEvents() {

        // capture initial state
        List<WebElement> initialEvents = eventsPage.getEventCards();
        boolean hadEventsInitially = !initialEvents.isEmpty();

        if (hadEventsInitially) {
            initialEvents = eventsPage.getEventCards();
        } else {
            WebElement noEvents = eventsPage.getNoEventsMessage();
            assertTrue(noEvents.isDisplayed(), "If DB empty, initial state must show 'no events'");
        }

        // perform a search
        String keyword = "yoga";
        eventsPage.search(keyword);

        // clear search input
        eventsPage.clearSearch();
        eventsPage.waitForEitherCardsOrNoEvents();

        // wait to return to initial
        if (hadEventsInitially) {
            List<WebElement> finalEvents = eventsPage.getEventCards();
            assertEquals(initialEvents.size(), finalEvents.size(),
                    "After clearing search, number of events must match initial state");
        } else {
            WebElement noEvents = eventsPage.getNoEventsMessage();
            assertTrue(noEvents.isDisplayed(), "After clearing search, must still show 'no events'");
        }
    }


    @Test
    public void testSearchAndFilterTogether() {

        //search
        String keyword = "colorado";
        eventsPage.search(keyword);

        //filter
        eventsPage.openFilter();

        String selectedType = eventsPage.selectFirstEventType();
        assumeTrue(selectedType != null, "No event types available, skipping test");

        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = LocalDate.now().plusDays(90);
        eventsPage.setDateRange(from, to);

        eventsPage.applyFilter();

        // get fresh event cards
        List<WebElement> events = eventsPage.getEventCards();
        if (events.isEmpty()) {
            assertTrue(eventsPage.getNoEventsMessage().isDisplayed(),
                    "No events message should be shown if nothing matches filters");
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

            for (int i = 0; i < eventsPage.getEventCards().size(); i++) {
                // type
                String type = eventsPage.getEventType(i);
                assertEquals(selectedType, type, "Event type must match selected type");

                // date
                String dateText = eventsPage.getEventDate(i);
                LocalDate parsedDate = LocalDate.parse(dateText, fmt);
                assertFalse(parsedDate.isBefore(from) || parsedDate.isAfter(to),
                        "Event date must be within given range");

                // keyword search in title/description/place
                String title = eventsPage.getEventTitle(i).toLowerCase();
                String description = eventsPage.getEventDescription(i).toLowerCase();
                List<String> places = eventsPage.getEventPlaces().get(i);
                String place = places.isEmpty() ? "" : places.get(0).toLowerCase();

                boolean containsKeyword = title.contains(keyword)
                        || description.contains(keyword)
                        || place.contains(keyword);

                assertTrue(containsKeyword,
                        "Event must contain keyword '" + keyword + "' in title, description, or place");
            }
        }
    }




}