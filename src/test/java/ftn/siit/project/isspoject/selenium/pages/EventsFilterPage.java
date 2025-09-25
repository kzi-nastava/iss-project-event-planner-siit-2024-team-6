package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EventsFilterPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public EventsFilterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private By sidebar = By.cssSelector(".filter-sidebar");
    private By typeDropdown = By.id("eventTypeSelect");
    private By fromDate = By.id("fromDate");
    private By toDate = By.id("toDate");
    private By applyButton = By.id("applyButton");
    private By resetButton = By.id("resetButton");

    public void openSidebar() {
        driver.findElement(By.id("openFilterButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(sidebar));
    }

    public void selectType(String type) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(typeDropdown, type));
        new Select(driver.findElement(typeDropdown)).selectByVisibleText(type);
    }


    public void setDateRange(String from, String to) {
        WebElement fromInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("fromDate")));
        fromInput.click();
        fromInput.clear();
        fromInput.sendKeys(from);

        WebElement toInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("toDate")));
        toInput.click();
        toInput.clear();
        toInput.sendKeys(to);
    }


    public void apply() {
        driver.findElement(applyButton).click();
    }

    public void reset() {
        driver.findElement(resetButton).click();
    }
}
