package ftn.siit.project.isspoject.selenium.tests;

import ftn.siit.project.isspoject.selenium.pages.EventsFilterPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class EventsSearchTests {

    //kad se pokrenu svi testovi iz nekog razloga uvek neki random padne, pojedinacno svi rade
    //fale mi testovi za kombinovanje search i filter
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("http://localhost:4200/events");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testPrintEventTypes() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();

        Select select = new Select(driver.findElement(By.id("eventTypeSelect")));
        List<WebElement> options = select.getOptions();

        for (WebElement opt : options) {
            System.out.println("Option: " + opt.getText());
        }

        Assertions.assertFalse(options.isEmpty(), "Dropdown za tipove događaja je prazan!");
    }


    @Test
    public void testFilterByDateRangeSafe() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();

        filterPage.setDateRange("2025-12-01", "2025-12-30");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));

        if (events.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.assertTrue(noEvents.isDisplayed(), "Treba da se prikaže poruka da nema događaja");
        } else {
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-date"), 0));

            List<String> dates = driver.findElements(By.cssSelector(".event-date"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            for (String d : dates) {
                System.out.println("Pronađen event sa datumom: " + d);
                }

            Assertions.assertTrue(dates.stream().allMatch(d -> d.contains("Dec")),
                    "Svi datumi moraju biti u septembru 2025");
        }
    }

    @Test
    public void testFilterByOnlyFromDate() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();

        filterPage.setDateRange("2025-12-01", ""); // samo from
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));

        if (events.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.assertTrue(noEvents.isDisplayed(), "Treba da se prikaže poruka da nema događaja");
        } else {
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-date"), 0));

            List<String> dates = driver.findElements(By.cssSelector(".event-date"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
            LocalDate from = LocalDate.of(2025, 12, 1);

            for (String d : dates) {
                LocalDate parsed = LocalDate.parse(d, formatter);
                Assertions.assertFalse(parsed.isBefore(from),
                        "Datum " + parsed + " ne sme biti pre " + from);
            }
        }
    }

    @Test
    public void testFilterByOnlyToDate() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();

        filterPage.setDateRange("", "2025-12-30"); // samo to
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));

        if (events.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.assertTrue(noEvents.isDisplayed(), "Treba da se prikaže poruka da nema događaja");
        } else {
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-date"), 0));

            List<String> dates = driver.findElements(By.cssSelector(".event-date"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
            LocalDate to = LocalDate.of(2025, 12, 30);

            for (String d : dates) {
                LocalDate parsed = LocalDate.parse(d, formatter);
                Assertions.assertFalse(parsed.isAfter(to),
                        "Datum " + parsed + " ne sme biti posle " + to);
            }
        }
    }


    @Test
    public void testFilterByEventTypeAndDateRange() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();

        filterPage.selectType("Conference");

        filterPage.setDateRange("2025-12-01", "2025-12-30");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));

        if (events.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.assertTrue(noEvents.isDisplayed(),
                    "Treba da se prikaže poruka da nema događaja");
        } else {
            // čekaj da tipovi i datumi budu renderovani
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-type"), 0));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-date"), 0));

            List<String> types = driver.findElements(By.cssSelector(".event-type"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            List<String> dates = driver.findElements(By.cssSelector(".event-date"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
            LocalDate from = LocalDate.of(2025, 12, 1);
            LocalDate to = LocalDate.of(2025, 12, 30);

            for (int i = 0; i < events.size(); i++) {
                Assertions.assertEquals("Conference", types.get(i),
                        "Event mora biti tipa Conference");

                LocalDate parsed = LocalDate.parse(dates.get(i), formatter);
                Assertions.assertTrue(
                        !parsed.isBefore(from) && !parsed.isAfter(to),
                        "Datum " + parsed + " mora biti između " + from + " i " + to
                );
            }
        }
    }

    @Test
    public void testFilterByEventTypeAndOnlyFromDate() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();

        filterPage.selectType("Conference");

        filterPage.setDateRange("2025-12-01", "");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));

        if (events.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.assertTrue(noEvents.isDisplayed(),
                    "Treba da se prikaže poruka da nema događaja");
        } else {
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-type"), 0));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-date"), 0));

            List<String> types = driver.findElements(By.cssSelector(".event-type"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            List<String> dates = driver.findElements(By.cssSelector(".event-date"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
            LocalDate from = LocalDate.of(2025, 12, 1);

            for (int i = 0; i < events.size(); i++) {
                Assertions.assertEquals("Conference", types.get(i),
                        "Event mora biti tipa Conference");

                LocalDate parsed = LocalDate.parse(dates.get(i), formatter);
                Assertions.assertFalse(parsed.isBefore(from),
                        "Datum " + parsed + " ne sme biti pre " + from);
            }
        }
    }


    @Test
    public void testFilterByEventTypeAndOnlyToDate() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();

        filterPage.selectType("Conference");

        filterPage.setDateRange("", "2025-12-30");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));

        if (events.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.assertTrue(noEvents.isDisplayed(),
                    "Treba da se prikaže poruka da nema događaja");
        } else {
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-type"), 0));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-date"), 0));

            List<String> types = driver.findElements(By.cssSelector(".event-type"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            List<String> dates = driver.findElements(By.cssSelector(".event-date"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
            LocalDate to = LocalDate.of(2025, 12, 30);

            for (int i = 0; i < events.size(); i++) {
                Assertions.assertEquals("Conference", types.get(i),
                        "Event mora biti tipa Conference");

                LocalDate parsed = LocalDate.parse(dates.get(i), formatter);
                Assertions.assertFalse(parsed.isAfter(to),
                        "Datum " + parsed + " ne sme biti posle " + to);
            }
        }
    }


    @Test
    public void testResetFilters() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);

        filterPage.openSidebar();
        filterPage.selectType("Conference");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        int filteredCount = driver.findElements(By.cssSelector("app-event-card")).size();
        System.out.println("Broj eventa posle filtera: " + filteredCount);

        filterPage.openSidebar();

        WebElement resetBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("resetButton")));
        resetBtn.click();

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("app-event-card"), filteredCount - 1
        ));

        int allEventsCount = driver.findElements(By.cssSelector("app-event-card")).size();
        System.out.println("Broj eventa posle reseta: " + allEventsCount);

        Assertions.assertTrue(allEventsCount >= filteredCount,
                "Posle reseta mora biti više ili makar jednak broj događaja nego sa filtrima");
    }

    @Test
    public void testPaginationWithFilter() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();

        filterPage.selectType("Conference");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> firstPageEvents = driver.findElements(By.cssSelector("app-event-card"));

        if (firstPageEvents.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.assertTrue(noEvents.isDisplayed(), "Ako nema događaja, treba da piše 'No events'");
        } else {
            String firstEventFirstPage = firstPageEvents.get(0).getText();

            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@aria-label='Next page']")));
            nextButton.click();

            wait.until(ExpectedConditions.stalenessOf(firstPageEvents.get(0)));

            List<WebElement> secondPageEvents = driver.findElements(By.cssSelector("app-event-card"));
            Assertions.assertFalse(secondPageEvents.isEmpty(), "Na sledećoj strani moraju postojati eventi");

            String firstEventSecondPage = secondPageEvents.get(0).getText();

            Assertions.assertNotEquals(firstEventFirstPage, firstEventSecondPage,
                    "Prvi event na prvoj i drugoj strani ne sme biti isti");

            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".event-type"), 0));
            List<String> types = driver.findElements(By.cssSelector(".event-type"))
                    .stream()
                    .map(WebElement::getText)
                    .toList();

            Assertions.assertTrue(types.stream().allMatch(t -> t.equals("Conference")),
                    "Svi eventi kroz paginaciju moraju biti tipa Conference");
        }
    }

    @Test
    public void testFilterByEventTypeFestival_NoEvents() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();
        filterPage.selectType("Festival");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.numberOfElementsToBe(By.cssSelector("app-event-card"), 0),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));
        System.out.println("Broj eventa posle filtera: " + events.size());

        if (events.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.assertTrue(noEvents.isDisplayed(),
                    "Treba da se prikaže poruka da nema događaja");
            System.out.println("Prikazana je poruka: " + noEvents.getText());
        } else {
            System.out.println("Neočekivano pronađeni eventi:");
            for (WebElement event : events) {
                System.out.println(event.getText());
            }
            Assertions.fail("Neočekivano su prikazani eventi za filter 'Festival'!");
        }
    }


    @Test
    public void testFilterByConferenceFrom2026_NoEvents() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();
        filterPage.selectType("Conference");
        filterPage.setDateRange("2026-01-01", "");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.numberOfElementsToBe(By.cssSelector("app-event-card"), 0),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));
        System.out.println("Broj eventa (Conference od 2026): " + events.size());

        Assertions.assertTrue(events.isEmpty(), "Ne sme biti događaja za Conference od 2026");
        WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
        Assertions.assertTrue(noEvents.isDisplayed());
        System.out.println("Prikazana poruka: " + noEvents.getText());
    }

    @Test
    public void testFilterByConferenceToAugust30_NoEvents() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();
        filterPage.selectType("Conference");
        filterPage.setDateRange("", "2025-08-30");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.numberOfElementsToBe(By.cssSelector("app-event-card"), 0),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));
        System.out.println("Broj eventa (Conference do 30.08.2025): " + events.size());

        Assertions.assertTrue(events.isEmpty(), "Ne sme biti događaja za Conference do avgusta 2025");
        WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
        Assertions.assertTrue(noEvents.isDisplayed());
        System.out.println("Prikazana poruka: " + noEvents.getText());
    }

    @Test
    public void testFilterByConferenceBetweenAug30AndSept10_NoEvents() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();
        filterPage.selectType("Conference");
        filterPage.setDateRange("2025-08-30", "2025-09-10");
        filterPage.apply();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.numberOfElementsToBe(By.cssSelector("app-event-card"), 0),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));
        System.out.println("Broj eventa (Conference 30.08–10.09.2025): " + events.size());

        Assertions.assertTrue(events.isEmpty(), "Ne sme biti događaja za Conference u ovom opsegu");
        WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
        Assertions.assertTrue(noEvents.isDisplayed());
        System.out.println("Prikazana poruka: " + noEvents.getText());
    }

    @Test
    public void testEmptyFilterKeepsInitialEvents() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);

        List<WebElement> initialEvents = driver.findElements(By.cssSelector("app-event-card"));
        int initialCount = initialEvents.size();
        System.out.println("Broj eventa na početnoj strani: " + initialCount);

        filterPage.openSidebar();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement applyBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("applyButton"))
        );
        applyBtn.click();

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("app-event-card"), initialCount
        ));

        List<WebElement> afterEvents = driver.findElements(By.cssSelector("app-event-card"));
        int afterCount = afterEvents.size();
        System.out.println("Broj eventa posle Apply bez filtera: " + afterCount);

        Assertions.assertEquals(initialCount, afterCount,
                "Broj događaja mora ostati isti kada je filter prazan");
    }


    @Test
    public void testEmptyFilterKeepsSameEvents() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);

        // uzmi evente sa početne strane
        List<String> initialEvents = driver.findElements(By.cssSelector("app-event-card"))
                .stream()
                .map(WebElement::getText)
                .toList();

        System.out.println("Eventi pre Apply bez filtera:");
        initialEvents.forEach(System.out::println);

        filterPage.openSidebar();
        WebElement applyBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("applyButton"))
        );
        applyBtn.click();

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("app-event-card"), initialEvents.size()
        ));

        List<String> afterEvents = driver.findElements(By.cssSelector("app-event-card"))
                .stream()
                .map(WebElement::getText)
                .toList();

        System.out.println("Eventi posle Apply bez filtera:");
        afterEvents.forEach(System.out::println);

        Assertions.assertEquals(initialEvents, afterEvents,
                "Lista događaja mora ostati ista kada se Apply klikne bez filtera");
    }

    @Test
    public void testResetFiltersClearsFormFields() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);
        filterPage.openSidebar();

        filterPage.selectType("Conference");
        filterPage.setDateRange("2025-12-01", "2025-12-30");

        WebElement resetBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("resetButton")));
        resetBtn.click();

        Select select = new Select(driver.findElement(By.id("eventTypeSelect")));
        List<WebElement> selectedOptions = select.getAllSelectedOptions();
        Assertions.assertTrue(selectedOptions.isEmpty(),
                "Dropdown mora biti resetovan i ne sme imati selektovane opcije");

        String fromValue = driver.findElement(By.id("fromDate")).getAttribute("value");
        String toValue = driver.findElement(By.id("toDate")).getAttribute("value");

        Assertions.assertTrue(fromValue.isEmpty(), "From date mora biti prazan");
        Assertions.assertTrue(toValue.isEmpty(), "To date mora biti prazan");
    }


    @Test
    public void testFilterSidebarToggleByClass() {
        EventsFilterPage filterPage = new EventsFilterPage(driver);

        filterPage.openSidebar();
        WebElement sidebar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".filter-sidebar")));

        Assertions.assertTrue(
                sidebar.getAttribute("class").contains("active"),
                "Sidebar mora imati klasu 'active' nakon otvaranja"
        );

        WebElement toggleBtn = driver.findElement(By.id("openFilterButton"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", toggleBtn);

        wait.until(ExpectedConditions.not(
                ExpectedConditions.attributeContains(sidebar, "class", "active")
        ));

        Assertions.assertFalse(
                sidebar.getAttribute("class").contains("active"),
                "Sidebar ne sme imati klasu 'active' nakon zatvaranja"
        );
    }


    //SEARCH


    @Test
    public void testSearchByPlaceChigao() {
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".search input"))
        );
        searchInput.clear();
        searchInput.sendKeys("chicago");

        WebElement searchBtn = driver.findElement(By.cssSelector(".search button"));
        searchBtn.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));
        Assertions.assertFalse(events.isEmpty(), "Mora postojati bar jedan event za 'chicago'");

        System.out.println("Pronađeno eventa: " + events.size());


        List<String> places = driver.findElements(
                        By.xpath("//div[@class='info-item'][i[contains(@class, 'fa-map-marker-alt')]]/span")
                ).stream()
                .map(e -> e.getText().toLowerCase())
                .toList();


        for (String place : places) {
            System.out.println("Mesto eventa: " + place);
            Assertions.assertTrue(place.contains("chicago"),
                    "Event mesto mora sadržati 'chicago'");
        }
    }

    @Test
    public void testSearchByPlaceSerbia_NoResults() {
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".search input"))
        );
        searchInput.clear();
        searchInput.sendKeys("serbia");

        WebElement searchBtn = driver.findElement(By.cssSelector(".search button"));
        searchBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message")));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));
        Assertions.assertTrue(events.isEmpty(), "Ne sme biti eventa za 'serbia'");

        WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
        Assertions.assertTrue(noEvents.isDisplayed(), "Mora se prikazati 'no events' poruka");

        System.out.println("Prikazana poruka: " + noEvents.getText());
    }

    @Test
    public void testSearchByNameYoga() {
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".search input"))
        );
        searchInput.clear();
        searchInput.sendKeys("yoga");

        WebElement searchBtn = driver.findElement(By.cssSelector(".search button"));
        searchBtn.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));

        if (events.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.fail("Očekivan je bar jedan Yoga event, ali je prikazana poruka: " + noEvents.getText());
        } else {
            List<String> names = driver.findElements(By.cssSelector(".event-card-title"))
                    .stream()
                    .map(e -> e.getText().toLowerCase())
                    .toList();

            System.out.println("Pronađeni eventi:");
            names.forEach(System.out::println);

            for (String name : names) {
                Assertions.assertTrue(name.contains("yoga"),
                        "Naziv eventa mora sadržati 'yoga'");
            }
        }
    }

    @Test
    public void testSearchByNameDanger_NoEvents() {
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".search input"))
        );
        searchInput.clear();
        searchInput.sendKeys("danger");

        WebElement searchBtn = driver.findElement(By.cssSelector(".search button"));
        searchBtn.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));

        if (!events.isEmpty()) {
            System.out.println("Neočekivano pronađeni eventi:");
            events.forEach(e -> System.out.println(e.getText()));
            Assertions.fail("Za 'danger' ne sme biti događaja!");
        } else {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.assertTrue(noEvents.isDisplayed(),
                    "Treba da se prikaže poruka da nema događaja");
            System.out.println("Prikazana poruka: " + noEvents.getText());
        }
    }

    @Test
    public void testSearchByDescription_Chef() {
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".search input"))
        );
        searchInput.clear();
        searchInput.sendKeys("chef");

        WebElement searchBtn = driver.findElement(By.cssSelector(".search button"));
        searchBtn.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> events = driver.findElements(By.cssSelector("app-event-card"));

        if (events.isEmpty()) {
            WebElement noEvents = driver.findElement(By.cssSelector(".no-events-message"));
            Assertions.fail("Očekivan je event sa 'chef' u description, ali je prikazana poruka: " + noEvents.getText());
        } else {
            List<String> descriptions = driver.findElements(By.cssSelector(".event-description"))
                    .stream()
                    .map(e -> e.getAttribute("textContent").toLowerCase())
                    .toList();

            Assertions.assertTrue(
                    descriptions.stream().anyMatch(d -> d.contains("chef")),
                    "❌ Barem jedan event description mora sadržati 'chef'"
            );
        }
    }


    @Test
    public void testSearchByNameYoga_AndClearRestoresAllEvents() {

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")));
        List<String> initialNames = driver.findElements(By.cssSelector(".event-card-title"))
                .stream()
                .map(WebElement::getText)
                .map(String::toLowerCase)
                .toList();

        int initialCount = initialNames.size();
        initialNames.forEach(n -> System.out.println("   -> " + n));
        Assertions.assertTrue(initialCount > 0, "⚠️ Na početku mora postojati bar jedan event!");

        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".search input"))
        );
        searchInput.clear();
        searchInput.sendKeys("yoga");

        WebElement searchBtn = driver.findElement(By.cssSelector(".search button"));
        searchBtn.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("app-event-card")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".no-events-message"))
        ));

        List<WebElement> yogaEvents = driver.findElements(By.cssSelector("app-event-card"));
        Assertions.assertFalse(yogaEvents.isEmpty(), "❌ Očekivan je bar jedan Yoga event!");

        List<String> yogaNames = driver.findElements(By.cssSelector(".event-card-title"))
                .stream()
                .map(WebElement::getText)
                .map(String::toLowerCase)
                .toList();

        yogaNames.forEach(System.out::println);

        yogaNames.forEach(name ->
                Assertions.assertTrue(name.contains("yoga"), "Naziv eventa mora sadržati 'yoga'")
        );

        searchInput = driver.findElement(By.cssSelector(".search input")); // refetch
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value=''; arguments[0].dispatchEvent(new Event('input'));", searchInput
        );

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("app-event-card"), initialCount
        ));

        List<String> finalNames = driver.findElements(By.cssSelector(".event-card-title"))
                .stream()
                .map(WebElement::getText)
                .map(String::toLowerCase)
                .toList();

        int finalCount = finalNames.size();
        finalNames.forEach(n -> System.out.println("   -> " + n));

        Assertions.assertEquals(initialCount, finalCount,
                "❌ Posle brisanja search inputa mora se vratiti početni broj eventa");

        Assertions.assertEquals(initialNames, finalNames,
                "❌ Lista događaja posle brisanja search-a mora biti ista kao na početku");

        System.out.println("✅ Test prošao: lista eventa se resetuje na početno stanje.");
    }





}
