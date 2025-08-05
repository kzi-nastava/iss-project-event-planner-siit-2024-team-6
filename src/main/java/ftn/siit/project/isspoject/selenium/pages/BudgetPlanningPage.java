package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BudgetPlanningPage {

    private WebDriver driver;

    @FindBy(css = "button.back-button")
    private WebElement goBackButton;

    public BudgetPlanningPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickGoBack() {
        WebElement backButton = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button.back-button")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backButton);
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(backButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", backButton);
    }
}
