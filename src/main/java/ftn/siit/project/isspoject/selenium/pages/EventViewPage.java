package ftn.siit.project.isspoject.selenium.pages;


import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EventViewPage {
    WebDriver driver;

    @FindBy(css = "input.editable-field[placeholder='Event Name']")
    private WebElement nameInput;

    @FindBy(css = "textarea[placeholder='Enter event description']")
    private WebElement descriptionTextarea;

    @FindBy(css = "input[placeholder='Enter location']")
    private WebElement placeInput;

    @FindBy(css = "input[placeholder='Enter max participants']")
    private WebElement participantsInput;

    @FindBy(css = "input[type='date']")
    private WebElement dateInput;

    @FindBy(css = "button.save-button")
    private WebElement saveChangesButton;

    @FindBy(css = "button.delete-button")
    private WebElement deleteButton;

    @FindBy(css = "button.activate-button")
    private WebElement activateButton;

    @FindBy(css = "button.btn-budget")
    private WebElement budgetButton;


    @FindBy(xpath = "//button[text()='Manage agenda']")
    private WebElement manageAgendaButton;

    public EventViewPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    public String getEventName() {
        return nameInput.getAttribute("value");
    }
    public void clearEventName() {
        WebElement name = driver.findElement(By.cssSelector("input[placeholder='Event Name']"));
        name.clear();
    }
    public String getEventDescription() {
        return descriptionTextarea.getAttribute("value");
    }

    public String getEventLocation(){
        return placeInput.getAttribute("value");
    }

    public String getEventParticipants(){
        return participantsInput.getAttribute("value");
    }

    public String getEventDate(){
        return dateInput.getAttribute("value");
    }

    public void changeEventName(String newName) {
        nameInput.clear();
        nameInput.sendKeys(newName);
    }

    public void changeDescription(String newDescription) {
        descriptionTextarea.clear();
        descriptionTextarea.sendKeys(newDescription);
    }

    public void changeLocation(String location) {
        placeInput.clear();
        placeInput.sendKeys(location);
    }

    public void changeMaxParticipants(String number) {
        participantsInput.clear();
        participantsInput.sendKeys(number);
    }

    public void changeDate(String isoDate) {
        dateInput.clear();
        dateInput.sendKeys(isoDate); // формат: "2025-07-15"
    }

    public void saveChanges() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(saveChangesButton)).click();
    }

    public void deleteEvent() {
        deleteButton.click();
    }

    public void activateEvent() {
        activateButton.click();
    }

    public void openBudget() {
        budgetButton.click();
    }

    public void openAgendaManagement() {
        manageAgendaButton.click();
    }

}
