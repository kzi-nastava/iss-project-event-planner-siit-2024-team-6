package ftn.siit.project.isspoject.selenium;

import ftn.siit.project.isspoject.selenium.pages.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class EventEditTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:4200/events");
    }

    @Test
    public void testEditFirstEvent() {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);
        assertTrue(myEventsPage.isAt(), "Should be on Events page");

        int count = myEventsPage.getEventCount();
        assertTrue(count > 0, "There should be at least one event");

        myEventsPage.clickFirstEvent();

        EventViewPage eventPage = new EventViewPage(driver);
        String name = eventPage.getEventName();
        String description = eventPage.getEventDescription();
        eventPage.changeEventName("Updated Test Event");
        eventPage.changeDescription("Updated description via Selenium");
        eventPage.saveChanges();
        assertTrue(driver.getCurrentUrl().contains("/my_events"), "Should be transferred to my_events page after saving");

        myEventsPage = new MyEventsPage(driver);
        assertTrue(myEventsPage.isAt(), "Should be on Events page");

        count = myEventsPage.getEventCount();
        assertTrue(count > 0, "There should be at least one event");

        myEventsPage.clickFirstEvent();

        eventPage = new EventViewPage(driver);
        assertEquals(eventPage.getEventName(), "Updated Test Event");
        assertEquals(eventPage.getEventDescription(), "Updated description via Selenium");

        // returning values
        eventPage.changeEventName(name);
        eventPage.changeDescription(description);
        eventPage.saveChanges();

        assertTrue(driver.getCurrentUrl().contains("/my_events"), "Should be transferred to my_events page after saving");

    }
    @Test
    public void testEditEvent_WithEmptyName_ShouldFail() throws InterruptedException {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);
        assertTrue(myEventsPage.isAt(), "Should be on Events page");

        int count = myEventsPage.getEventCount();
        assertTrue(count > 0, "There should be at least one event");

        myEventsPage.clickFirstEvent();

        EventViewPage eventPage = new EventViewPage(driver);

        String oldName = eventPage.getEventName();
        String oldDesc = eventPage.getEventDescription();

        eventPage.clearEventName();
        Thread.sleep(1500);
        eventPage.saveChanges();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(d -> driver.getCurrentUrl().contains("/my_events"));
        } catch (TimeoutException ignored) {
        }
        assertTrue(myEventsPage.isAt(), "Should be on Events page");

        count = myEventsPage.getEventCount();
        assertTrue(count > 0, "There should be at least one event");

        myEventsPage.clickFirstEvent();
        eventPage = new EventViewPage(driver);
        String currentName = eventPage.getEventName();
        assertEquals(oldName, currentName, "Event name should remain unchanged in actual data");

        eventPage.changeEventName(oldName);
        eventPage.changeDescription(oldDesc);
        eventPage.saveChanges();

        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.urlContains("/my_events"));
    }
    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}