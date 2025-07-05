package ftn.siit.project.isspoject.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    WebDriver driver;

    @FindBy(css = ".navbar__profile")
    private WebElement profileIcon;
    @FindBy(css = ".navbar__icon")
    private WebElement burgerIcon;

    public void openSidebar() {
        burgerIcon.click();
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
        myEventsMenuItem.click();
    }
    public void clickSideBar(){
        burgerIcon.click();
    }
    public boolean isOnHomePage() {
        return driver.getCurrentUrl().contains("/events");
    }
}
