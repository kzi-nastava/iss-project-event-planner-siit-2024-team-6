package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CreateEventPage {

    WebDriver driver;

    @FindBy(id = "name")
    private WebElement nameInput;

    @FindBy(id = "description")
    private WebElement descriptionInput;

    @FindBy(id = "maxParticipants")
    private WebElement maxParticipantsInput;
    @FindBy(id = "photos")
    private WebElement photosInput;

    @FindBy(xpath = "//button[contains(text(), 'Public')]")
    private WebElement publicButton;

    @FindBy(id = "place")
    private WebElement placeInput;

    @FindBy(id = "date")
    private WebElement dateInput;

    @FindBy(id = "eventType")
    private WebElement eventTypeSelect;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;
    @FindBy(xpath = "//button[contains(text(), 'Add Photo')]")
    private WebElement addPhotoButton;

    public CreateEventPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isAt() {
        return driver.getCurrentUrl().contains("/new-event");
    }

    public void enterName(String name) {
        nameInput.clear();
        nameInput.sendKeys(name);
    }

    public void enterDescription(String desc) {
        descriptionInput.clear();
        descriptionInput.sendKeys(desc);
    }

    public void enterMaxParticipants(int max) {
        maxParticipantsInput.clear();
        maxParticipantsInput.sendKeys(String.valueOf(max));
    }

    public void setVisibilityPublic() {
        publicButton.click();
    }

    public void enterPlace(String place) {
        placeInput.clear();
        placeInput.sendKeys(place);
    }

    public void enterDate(String date) {
        // Format: "2025-12-31T23:59"
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles: true})); arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                dateInput, date
        );
    }

    public void selectFirstEventType() {
        new Select(eventTypeSelect).selectByIndex(0);
    }

    public void submitForm() {
        submitButton.click();
    }

    public void addEventPhoto(String photo) {
        photosInput.clear();
        photosInput.sendKeys(photo);
        addPhotoButton.click();

//        // Вводим текст с генерацией событий
//        ((JavascriptExecutor) driver).executeScript(
//                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
//                photosInput, photo
//        );
//
//        // Дополнительно симулируем нажатие Enter (если работает так в компоненте)
//        photosInput.sendKeys(Keys.ENTER);
//
//        // Или нажимаем кнопку (если не Enter)
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
//        wait.until(ExpectedConditions.elementToBeClickable(addPhotoButton));
//
//        try {
//            addPhotoButton.click();
//        } catch (Exception e) {
//            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addPhotoButton);
//        }
    }
}
