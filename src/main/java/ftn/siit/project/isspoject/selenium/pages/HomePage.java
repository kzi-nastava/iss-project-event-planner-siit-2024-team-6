package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {
    WebDriver driver;

    @FindBy(css = ".navbar__profile")
    private WebElement profileIcon;
    @FindBy(css = ".navbar__menu > div")
    private WebElement burgerContainer;

    public void openSidebar() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(burgerContainer)).click();
    }
    @FindBy(css = "li[routerlink='/my_events']")
    private WebElement myEventsMenuItem;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickProfileIcon() {
        profileIcon.click();
    }
    public void clickMyEvents() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(myEventsMenuItem)).click();

        new Actions(driver).moveByOffset(1000, 100).click().perform();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".sidebar-backdrop")));
    }

    public boolean isOnHomePage() {
        return driver.getCurrentUrl().contains("/events");
    }
}
