package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ActivityFormPage {

    WebDriver driver;

    @FindBy(id = "name")
    private WebElement nameInput;

    @FindBy(id = "description")
    private WebElement descriptionInput;

    @FindBy(id = "location")
    private WebElement locationInput;

    @FindBy(id = "startTime")
    private WebElement startTimeInput;

    @FindBy(id = "endTime")
    private WebElement endTimeInput;

    @FindBy(css = "button.btn-save")
    private WebElement saveButton;

    public ActivityFormPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isAt() {
        return driver.getCurrentUrl().contains("/add-activity") || driver.getCurrentUrl().contains("/edit-activity");
    }

    public void fillForm(String name, String description, String location, String startTime, String endTime) {
        nameInput.clear();
        nameInput.sendKeys(name);

        descriptionInput.clear();
        descriptionInput.sendKeys(description);

        locationInput.clear();
        locationInput.sendKeys(location);

        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", startTimeInput, startTime);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", endTimeInput, endTime);
    }

    public void clickSave() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(saveButton));
        saveButton.click();
    }
}
