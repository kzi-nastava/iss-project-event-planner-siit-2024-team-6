package ftn.siit.project.isspoject.selenium;

import ftn.siit.project.isspoject.selenium.pages.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class AgendaTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:4200/events");
    }

    @Test
    public void testAgendaAddActivity() {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);

        myEventsPage.clickFirstEvent();

        EventViewPage eventViewPage = new EventViewPage(driver);

        eventViewPage.openAgendaManagement();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/agenda"));

        AgendaPage agendaPage = new AgendaPage(driver);
        assertTrue(agendaPage.isAt(), "Should be on Agenda page");

        int initialCount = agendaPage.getActivityCount();

        agendaPage.clickAddActivity();

        ActivityFormPage formPage = new ActivityFormPage(driver);
        assertTrue(formPage.isAt(), "Should be on Add Activity form");

        formPage.fillForm(
                "Check-in",
                "Arrival and check-in process",
                "Lobby",
                "2025-12-31T18:00",
                "2025-12-31T18:30"
        );
        formPage.clickSave();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/agenda"));

        AgendaPage refreshedPage = new AgendaPage(driver);
        int newCount = refreshedPage.getActivityCount();
        assertEquals(initialCount + 1, newCount, "Activity should be added");
    }
    @Test
    public void testEditActivity() {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);

        myEventsPage.clickFirstEvent();

        EventViewPage eventViewPage = new EventViewPage(driver);

        eventViewPage.openAgendaManagement();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/agenda"));

        AgendaPage agendaPage = new AgendaPage(driver);
        assertTrue(agendaPage.isAt(), "Should be on Agenda page");

        int count = agendaPage.getActivityCount();
        assertTrue(count > 0, "At least one activity should exist to edit");

        agendaPage.clickEditOnFirstActivity();

        ActivityFormPage formPage = new ActivityFormPage(driver);
        assertTrue(formPage.isAt(), "Should be on Edit Activity form");

        String newName = "Edited Activity Name";
        formPage.fillForm(
                newName,
                "Updated description",
                "Updated Location",
                "2025-12-31T19:00",
                "2025-12-31T19:30"
        );
        formPage.clickSave();

        // Проверка что вернулись на agenda
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/agenda"));

        assertTrue(new AgendaPage(driver).activityExistsWithName(newName), "Edited activity should be visible");
    }
    @Test
    public void testDeleteActivity() {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);

        myEventsPage.clickFirstEvent();

        EventViewPage eventViewPage = new EventViewPage(driver);

        eventViewPage.openAgendaManagement();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/agenda"));

        AgendaPage agendaPage = new AgendaPage(driver);
        assertTrue(agendaPage.isAt(), "Should be on Agenda page");

        int initialCount = agendaPage.getActivityCount();
        assertTrue(initialCount > 0, "There should be at least one activity to delete");

        agendaPage.clickDeleteOnFirstActivity();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(driver ->
                agendaPage.getActivityCount() == initialCount - 1
        );
    }


    //unhappy tests
    @Test
    public void testAddActivityMissingName_ShouldFail() {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);
        myEventsPage.clickFirstEvent();

        EventViewPage eventViewPage = new EventViewPage(driver);
        eventViewPage.openAgendaManagement();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/agenda"));

        AgendaPage agendaPage = new AgendaPage(driver);
        agendaPage.clickAddActivity();

        ActivityFormPage formPage = new ActivityFormPage(driver);
        assertTrue(formPage.isAt(), "Should be on Add Activity form");

        // Leave "name" blank
        formPage.fillForm(
                "",  // Missing name
                "No name activity",
                "Hall A",
                "2025-12-31T10:00",
                "2025-12-31T11:00"
        );

        formPage.clickSave();

        // Assert we’re still on the form (did not navigate)
        assertTrue(formPage.isAt(), "Should stay on form page if validation fails");
    }
    @Test
    public void testAddActivityEndBeforeStart_ShouldFail() {
        HomePage homePage = new HomePage(driver);
        homePage.clickProfileIcon();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("organizer1@example.com", "123456789");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/events"));

        homePage.openSidebar();
        homePage.clickMyEvents();

        MyEventsPage myEventsPage = new MyEventsPage(driver);
        myEventsPage.clickFirstEvent();

        EventViewPage eventViewPage = new EventViewPage(driver);
        eventViewPage.openAgendaManagement();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/agenda"));

        AgendaPage agendaPage = new AgendaPage(driver);
        agendaPage.clickAddActivity();

        ActivityFormPage formPage = new ActivityFormPage(driver);
        assertTrue(formPage.isAt(), "Should be on Add Activity form");

        formPage.fillForm(
                "Invalid Time Activity",
                "This one ends before it starts",
                "Room X",
                "2025-12-31T12:00",  // start
                "2025-12-31T11:00"   // end before start
        );

        formPage.clickSave();

        // Assert we’re still on the form (validation should fail)
        assertTrue(formPage.isAt(), "Should stay on form page due to time validation");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
