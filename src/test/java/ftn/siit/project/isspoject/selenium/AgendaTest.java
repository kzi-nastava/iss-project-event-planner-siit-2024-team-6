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

        // Проверка: количество активностей увеличилось
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/agenda"));

        AgendaPage refreshedPage = new AgendaPage(driver);
        int newCount = refreshedPage.getActivityCount();
        assertEquals(initialCount + 1, newCount, "Activity should be added");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
