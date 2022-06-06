package skynet.interactions;

import net.serenitybdd.core.Serenity;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import skynet.utils.Logger;

import java.time.Duration;

public abstract class Clicks {

    private static final WebDriver driver = Serenity.getDriver();

    /**
     * Clicks an element
     *
     * @param locator string locator
     * @throws NoSuchElementException thrown if no element is found
     */
    public static void click(String locator) throws Exception {
        click(Elements.findElement(locator));
    }

    /**
     * Clicks an element
     *
     * @param by By selector to use
     * @throws NoSuchElementException thrown if no element is found
     */
    public static void click(By by) throws Exception {
        click(Elements.findElement(by));
    }

    /**
     * Runs a pre-condition lambda, then clicks an element
     *
     * @param preCondition code to run before element is clicked
     * @param by           By selector to use
     * @throws NoSuchElementException thrown if no element is found
     */
    public static void click(Runnable preCondition, By by) throws Exception {
        preCondition.run();
        click(Elements.findElement(by));
    }

    /**
     * Clicks an element, then runs an exit condition lambda
     *
     * @param by            By selector to use
     * @param exitCondition code to run after element is clicked
     * @throws NoSuchElementException thrown if no element is found
     */
    public static void click(By by, Runnable exitCondition) throws Exception {
        click(Elements.findElement(by));
        exitCondition.run();
    }

    /**
     * clicks a WebElement e
     *
     * @param el By selector to use
     * @throws NoSuchElementException thrown if no element is found
     */
    public static void click(WebElement el) throws Exception {
        int timeout = 10;
        if (el == null) {
            throw new NoSuchElementException("Unable to click null element!");
        }

        Actions actions = new Actions(driver);
        try {
            el = new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.elementToBeClickable(el));
        } catch (Exception ex) {
            try {
                throw new NoSuchElementException("Element not clickable: " + el.getTagName() + ": " + el
                        .getText() + ": " + ex.getMessage());
            } catch (StaleElementReferenceException exc) {
                throw new NoSuchElementException("Element not clickable: " + exc.getMessage());
            }
        }

        try {
            actions.moveToElement(el).build().perform();
            actions.click(el).build().perform();
            Logger.debug("Clicked element: " + el.toString());
        } catch (WebDriverException ex) {
            Logger.warn("Error while clicking, trying JS: " + ex.getMessage());
            javascriptClick(el);
        }
    }

    public static void moveToAndClickOn(WebElement elementToMoveTo, WebElement elementToClick) throws Exception {
        Elements.moveToElement(elementToMoveTo);
        click(elementToClick);

        //get the cursor off the dropdown
        Elements.moveToElement(By.className("logo"));
    }

    /**
     * Double-clicks on the given element
     *
     * @param locator - element to double-click on
     * @throws Exception - if element not found, throw exception
     */
    public static void dubleClick(String locator) throws Exception {
        WebElement element = Elements.findElement(locator);
        dubleClick(element);
    }

    /**
     * Double-clicks on the given element
     *
     * @param element - element to double-click on
     * @throws Exception - if element not found, throw exception
     */
    public static void dubleClick(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).click().click().build().perform();
    }

    /**
     * Clicks an element using javascript
     *
     * @param by By selector to use
     */
    public static void javascriptClick(By by) throws Exception {
        javascriptClick(Elements.findElement(by));
    }

    /**
     * Clicks an element e using javascript
     *
     * @param e element to be clicked
     */
    public static void javascriptClick(WebElement e) throws Exception {
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();arguments[0].click();", e);
    }

    /**
     * Clicks on an element
     * Waits for up to a specified timeout to see if the clicked element is no longer visible.
     *
     * @param locator - The locator to validate no longer exists to signify the page loaded.
     * @param tries   (optional)  - Number of tries for clicking the element and waiting for it to be no longer visible.
     *                - default executes the loop for two times.
     * @param timeout - Milliseconds to wait for the page to load. If timeOut &lt;= 0 then the default timeout
     *                is used (5000 milliseconds).
     * @throws Exception - throws exception
     */
    public static void clickAndRetry(String locator, int timeout, int... tries) throws Exception {

        int numberOfTries = tries.length > 0 ? tries[0] : 1;
        do {
            if (Wait.waitForVisible(locator, 1, false)) {
                click(locator);
            } else break;
            Wait.waitForNotVisible(locator, timeout, false);
            numberOfTries--;
        }
        while (numberOfTries == 0);
    }

    /*
    Description:
    This method clicks repeatedly on the first locator (locator1) until the second locator (locator2) becomes visible
    It has a provided timeout in seconds and an optional number of tries (which will multiply the the timeout)sky
    It is useful in triggering the tooltips for a non clickable elements
    Note: It has a first click which has the purpose to disband an active tooltip if this is already present (e.g. for another element if the method was used in a step before)
     */
    public static void clickUntilElementIsDisplayed(String locator1, String locator2, int timeout, int... tries) throws Exception {
        int numberOfTries = tries.length > 0 ? tries[0] : 1;
        Clicks.click(locator1);
        while (!(Wait.waitForVisible(locator2, 1, false))) {
            Clicks.click(locator1);
            Wait.waitForVisible(locator2, timeout, false);
            --numberOfTries;
            if (numberOfTries != 0) {
                break;
            }
        }
    }
}