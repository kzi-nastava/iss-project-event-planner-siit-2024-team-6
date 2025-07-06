package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class AgendaPage {

    WebDriver driver;

    @FindBy(css = "button.btn-add")
    private WebElement addActivityButton;

    @FindBy(css = "table.agenda-table tbody tr")
    private List<WebElement> activityRows;

    public AgendaPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isAt() {
        return driver.getCurrentUrl().contains("/agenda");
    }

    public void clickAddActivity() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(addActivityButton));
        addActivityButton.click();
    }

    public int getActivityCount() {
        return activityRows.size();
    }

    public void clickEditOnActivity(int index) {
        WebElement editBtn = activityRows.get(index).findElement(By.cssSelector(".btn-edit"));
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(editBtn));
        editBtn.click();
    }

    public void clickDeleteOnActivity(int index) {
        WebElement deleteBtn = activityRows.get(index).findElement(By.cssSelector(".btn-delete"));
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(deleteBtn));
        deleteBtn.click();

        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (TimeoutException ignored) {}
    }
}
