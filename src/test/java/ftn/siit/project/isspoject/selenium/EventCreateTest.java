package ftn.siit.project.isspoject.selenium;

import ftn.siit.project.isspoject.selenium.pages.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public class EventCreateTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:4200/events");
    }

    @Test
    public void testCreateEvent() {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);

        //wait a bit

        myEventsPage.clickAddEvent();

        CreateEventPage createPage = new CreateEventPage(driver);
        assertTrue(createPage.isAt(), "Should be on Create Event page");

        String name = "Selenium Test Event123";
        String description = "Created via Selenium test";
        String place = "Test City";
        int maxParticipants = 25;
        String date = "2025-12-31T23:59";

        createPage.enterName(name);
        createPage.enterDescription(description);
        createPage.enterMaxParticipants(maxParticipants);
        createPage.setVisibilityPublic();
        createPage.enterPlace(place);
        createPage.enterDate(date);
        createPage.selectFirstEventType();
        createPage.addEventPhoto("https://static.vecteezy.com/system/resources/thumbnails/041/880/991/small_2x/ai-generated-pic-artistic-depiction-of-sunflowers-under-a-vast-cloudy-sky-photo.jpg");
        createPage.submitForm();


        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/budget-planning"));

        BudgetPlanningPage budgetPlanningPage = new BudgetPlanningPage(driver);
        budgetPlanningPage.clickGoBack();
//        assertTrue(eventsPage.isAt(), "Should be on My Events page");
//
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/event"));

        EventViewPage eventView = new EventViewPage(driver);
        assertEquals(name, eventView.getEventName());
        assertEquals(description, eventView.getEventDescription());
    }

    // unhappy test
    @Test
    public void testCreateEvent_MissingName_ShouldFail() throws InterruptedException {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);
        myEventsPage.clickAddEvent();

        CreateEventPage createPage = new CreateEventPage(driver);
        assertTrue(createPage.isAt(), "Should be on Create Event page");

        // Leave name empty
        createPage.enterName("");
        createPage.enterDescription("Description without name");
        createPage.enterMaxParticipants(50);
        createPage.setVisibilityPublic();
        createPage.enterPlace("Belgrade");
        createPage.enterDate("2025-12-31T18:00");
        createPage.selectFirstEventType();
        createPage.submitForm();

        Thread.sleep(1500);

        assertTrue(createPage.isAt(), "Should remain on Create Event page if name is missing");
    }
    @Test
    public void testCreateEvent_InvalidMaxParticipants_ShouldFail() throws InterruptedException {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);
        myEventsPage.clickAddEvent();

        CreateEventPage createPage = new CreateEventPage(driver);
        assertTrue(createPage.isAt(), "Should be on Create Event page");

        createPage.enterName("Invalid Participants Event");
        createPage.enterDescription("Invalid max participants");
        createPage.enterMaxParticipants(0); // Invalid input
        createPage.setVisibilityPublic();
        createPage.enterPlace("Nowhere");
        createPage.enterDate("2025-12-31T21:00");
        createPage.selectFirstEventType();
        createPage.submitForm();
        Thread.sleep(1500);
        assertTrue(createPage.isAt(), "Should remain on Create Event page if max participants is invalid");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
