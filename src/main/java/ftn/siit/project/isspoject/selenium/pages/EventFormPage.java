package ftn.siit.project.isspoject.selenium.pages;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class EventFormPage {
    WebDriver driver;

    @FindBy(id = "name")
    private WebElement nameInput;

    @FindBy(id = "description")
    private WebElement descriptionInput;

    @FindBy(id = "maxParticipants")
    private WebElement maxParticipantsInput;

    @FindBy(xpath = "//button[text()='Public']")
    private WebElement publicButton;

    @FindBy(xpath = "//button[text()='Private']")
    private WebElement privateButton;

    @FindBy(id = "place")
    private WebElement placeInput;

    @FindBy(id = "date")
    private WebElement dateInput;

    @FindBy(id = "eventType")
    private WebElement eventTypeSelect;

    @FindBy(id = "photos")
    private WebElement photoInput;

    @FindBy(xpath = "//button[text()='Add Photo']")
    private WebElement addPhotoButton;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    public EventFormPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void fillBasicInfo(String name, String description, String maxParticipants, String place, String date) {
        nameInput.clear();
        nameInput.sendKeys(name);

        descriptionInput.clear();
        descriptionInput.sendKeys(description);

        maxParticipantsInput.clear();
        maxParticipantsInput.sendKeys(maxParticipants);

        placeInput.clear();
        placeInput.sendKeys(place);

        dateInput.clear();
        dateInput.sendKeys(date);
    }

    public void selectVisibility(boolean isPublic) {
        if (isPublic) {
            publicButton.click();
        } else {
            privateButton.click();
        }
    }

    public void selectEventTypeByIndex(int index) {
        eventTypeSelect.click();
        eventTypeSelect.findElements(org.openqa.selenium.By.tagName("option")).get(index).click();
    }

    public void addPhoto(String url) {
        photoInput.sendKeys(url);
        addPhotoButton.click();
    }

    public void submitForm() {
        submitButton.click();
    }
}
