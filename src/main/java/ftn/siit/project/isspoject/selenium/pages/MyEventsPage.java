package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MyEventsPage {
    WebDriver driver;

    @FindBy(css = ".event-section-title")
    private WebElement title;

    @FindBy(css = "button.fab")
    private WebElement addEventButton;

//    @FindBy(css = "app-event-card")
//    private List<WebElement> eventCards;
    @FindBy(css = ".event-card")
    private List<WebElement> eventCards;


    public MyEventsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isAt() {
        return title.getText().trim().equalsIgnoreCase("Events");
    }

    public void clickAddEvent() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(addEventButton))
                .click();
    }

    public int getEventCount() {
        return eventCards.size();
    }

//    public void clickFirstEvent() {
//        if (!eventCards.isEmpty()) {
//            eventCards.get(0).click();
//        }
//    }
public void clickFirstEvent() {
    if (!eventCards.isEmpty()) {
        WebElement clickableArea = eventCards.get(0).findElement(By.cssSelector(".event-card-content"));
        try {
            clickableArea.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableArea);
        }
    }
}

    public void clickEventByName(String name) {
        for (WebElement card : eventCards) {
            try {
                String cardText = card.getText();
                if (cardText.contains(name)) {
                    WebElement clickableArea = card.findElement(By.cssSelector(".event-card-content"));
                    try {
                        clickableArea.click();
                    } catch (ElementClickInterceptedException e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableArea);
                    }
                    return;
                }
            } catch (StaleElementReferenceException e) {
            }
        }
        throw new NoSuchElementException("No event card found with name: " + name);
    }

}

