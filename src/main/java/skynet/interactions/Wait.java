package skynet.interactions;


import net.serenitybdd.core.Serenity;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import skynet.utils.Logger;

import java.time.Duration;
import java.util.Date;

public abstract class Wait {
    private static final WebDriver driver = Serenity.getDriver();
    private static By by;

    /**
     * Awaits for the provided element to be clickable within a given timeout.
     * If element is not clickable, throws exception but only if throwException == true
     *
     * @param locator        - element locator
     * @param timeout        - time to wait
     * @param throwException - if true, it throws ElementNotInteractableException if element is not clickable within the provided timeout
     * @return - true if element clickable, false otherwise
     * @throws ElementNotInteractableException - throw
     */
    public static boolean waitForClickable(String locator, int timeout, boolean throwException) throws Exception {
        Wait.by = Elements.by(locator);
        return wait(by, ExpectedConditions.elementToBeClickable(by), timeout, throwException);
    }

    /**
     * Awaits for the provided element to be clickable within a given timeout.
     * If element is not clickable, throws exception but only if throwException == true
     *
     * @param el        - element locator
     * @param timeout        - time to wait
     * @param throwException - if true, it throws ElementNotInteractableException if element is not clickable within the provided timeout
     * @return - true if element clickable, false otherwise
     * @throws ElementNotInteractableException - throw
     */
    public static boolean waitForClickable(WebElement el, int timeout, boolean throwException) throws Exception {
        Wait.by = By.xpath(Elements.getAbsoluteXPath(el));
        return wait(by, ExpectedConditions.elementToBeClickable(by), timeout, throwException);
    }

    /**
     * Awaits for the provided element to be visible within a provided timeout.
     * If element is not visible, throws exception but only if throwException == true
     *
     * @param locator        - element locator
     * @param timeout        - time to wait
     * @param throwException - if true, it throws ElementNotVisibleException if element is not visible within the provided timeout
     * @return - true if element is visible, false otherwise
     * @throws Exception - throw
     */
    public static boolean waitForVisible(String locator, int timeout, boolean throwException) throws Exception {
        Wait.by = Elements.by(locator);
        return wait(by, ExpectedConditions.visibilityOfElementLocated(by), timeout, throwException);
    }

    /**
     * Awaits for the provided element to be visible within a default timeout.
     *
     * @param by - selenium By
     */
    public static void waitForVisible(By by) {
        wait(Wait.by = by, ExpectedConditions.visibilityOfElementLocated(by), 1000, true);
    }

    /**
     * Awaits for the provided element to be visible within a provided timeout.
     *
     * @param by      - selenium By
     * @param timeout - time to wait for
     * @return - returns true if element becomes visible, false otherwise
     */
    public static boolean waitForVisible(By by, int timeout) {
         return wait(Wait.by = by, ExpectedConditions.visibilityOfElementLocated(by), timeout, true);
    }

    /**
     * Awaits for the provided element to be visible within a provided timeout.
     * It throws ElementNotVisibleException but only if throwException is true
     *
     * @param by      - selenium By
     * @param timeout - time to wait for
     * @return - returns true if element becomes visible, false otherwise
     */
    public static boolean waitForVisible(By by, int timeout, boolean throwException) {
        return wait(Wait.by = by, ExpectedConditions.visibilityOfElementLocated(by), timeout, throwException);
    }

    /**
     * Awaits for the provided element to be visible within a default timeout.
     * It throws ElementNotVisibleException but only if throwException is true
     *
     * @param webElement - webElement
     * @return - returns true if element becomes visible, false otherwise
     */
    public static boolean waitForVisible(WebElement webElement, boolean throwException) {
        return wait(Wait.by, ExpectedConditions.visibilityOf(webElement), 0, throwException);
    }

    /**
     * Waits for an element to no longer be present
     * Waits for up to a specified timeout.
     * Optionally throws an exception if the page does not load / element still present within the specified timeout.
     *
     * @param locator        - The locator to validate no longer exists to signify the page loaded.
     * @param throwException - If true, throws an exception if the page does not load. False, does not.
     * @param timeout        - Milliseconds to wait for the page to load. If timeOut &lt;= 0 then the default timeout
     *                       is used (5000 milliseconds).
     * @return - True if page loaded successfully, false otherwise.
     * @throws Exception - throws exception
     */
    public static boolean waitForNotVisible(String locator, int timeout, boolean throwException) throws Exception {
        Wait.by = Elements.by(locator);

        //Initialize timeout for waiting for the page to load.
        Duration waitForLoadTimeout = timeout <= 0 ? Duration.ofSeconds(10) : Duration.ofSeconds(timeout);

        Logger.debug(String.format("Waiting for page to load; waiting for the following to not be visible: %s.\n" +
                "Waiting for load timeout: %s milliseconds", by.toString(), waitForLoadTimeout.toMillis()));

        Date dateTimeBeforeWaitForLoad;
        try {
            dateTimeBeforeWaitForLoad = new Date();
            waitUntilJSReady();
            new WebDriverWait(driver, waitForLoadTimeout).until(ExpectedConditions.invisibilityOfElementLocated(by));
        }
        catch (Exception e) {
            Logger.error(String.format("Failed to wait for page to load since failed to validate invisibility of %s " +
                    "within %d milliseconds.", by.toString(), waitForLoadTimeout.toMillis()));

            if (throwException) {
                throw e;
            }
            return false;
        }

        long elapsed = new Date().getTime() - dateTimeBeforeWaitForLoad.getTime();
        Logger.debug(String.format("Page loaded; the following not visible: %s. \nFound in %d milliseconds", by.toString(), elapsed));
        return true;
    }

    /**
     * Waits for an element to no longer be present
     * Waits for up to a specified timeout.
     * Optionally throws an exception if the page does not load / element still present within the specified timeout.
     *
     * @param element        - The element to validate no longer exists to signify the page loaded.
     * @param throwException - If true, throws an exception if the page does not load. False, does not.
     * @param timeout        - Milliseconds to wait for the page to load. If timeOut &lt;= 0 then the default timeout
     *                       is used (5000 milliseconds).
     * @return - True if page loaded successfully, false otherwise.
     * @throws Exception - throws exception
     */
    public static boolean waitForNotVisible(WebElement element, int timeout, boolean throwException) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        boolean visibility = wait.until(new ExpectedCondition<Boolean>() {
            private WebElement element;

            private ExpectedCondition<Boolean> init(WebElement element) {
                this.element = element;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                try {
                    return !element.isDisplayed();
                }
                catch (NoSuchElementException | StaleElementReferenceException var3) {
                    return true;
                }
            }
        }.init(element));

        if(!visibility && throwException) {
            Logger.exception(String.format("Failed to wait for page to load since failed to validate invisibility of %s " +
                    "within %d seconds.", by.toString(), timeout));
        }
        return visibility;
    }

    /**
     * Awaits for an element to exist within a default timeout
     *
     * @param locator - element locator
     * @throws NoSuchElementException - throws NoSuchElementException
     */
    public static void waitForExists(String locator) throws Exception {
        waitForExists(locator, true);
    }

    /**
     * Awaits for an element to exist within a default timeout
     * If element does not exist and throwException == true, it throws an exception
     *
     * @param locator        - element locator
     * @param throwException - if true, throws exception if element does not exist
     * @return - true if element exists, false otherwise
     * @throws NoSuchElementException - throws NoSuchElementException
     */
    public static boolean waitForExists(String locator, boolean throwException) throws Exception {
        return waitForExists(locator, 0, throwException);
    }

    public static boolean waitForExists(String locator, int timeout, boolean throwException) throws Exception {
        Wait.by = Elements.by(locator);
        return wait(by, ExpectedConditions.presenceOfElementLocated(by), timeout, throwException);
    }

    /**
     * Waits for an element to no longer be present
     * Waits for up to a specified timeout.
     * Optionally throws an exception if the page does not load / element still present within the specified timeout.
     *
     * @param locator        - The locator to validate no longer exists to signify the page loaded.
     * @param throwException - If true, throws an exception if the page does not load. False, does not.
     * @param timeout        - Milliseconds to wait for the page to load. If timeOut &lt;= 0 then the default timeout
     *                       is used (5000 milliseconds).
     * @return - True if page loaded successfully, false otherwise.
     */
    public static boolean waitForNotExists(String locator, int timeout, boolean throwException) throws Exception {
        Wait.by = Elements.by(locator);

        //Initialize timeout for waiting for the page to load.
        Duration waitForLoadTimeout = timeout <= 0 ? Duration.ofSeconds(10) : Duration.ofSeconds(timeout);

        Logger.debug(String.format("Waiting for page to load; waiting for the following to not exist: %s.\n" +
                "Waiting for load timeout: %s milliseconds", by.toString(), waitForLoadTimeout.toMillis()));

        Date dateTimeBeforeWaitForLoad;
        try {
            dateTimeBeforeWaitForLoad = new Date();
            waitUntilJSReady();
            new WebDriverWait(driver, waitForLoadTimeout).until(ExpectedConditions.presenceOfElementLocated(by));
        }
        catch (Exception e) {
            Logger.error(String.format("Failed to wait for page to load since failed to validate invisibility of %s " +
                    "within %d milliseconds.", by.toString(), waitForLoadTimeout.toMillis()));

            if (throwException) {
                throw e;
            }
            return false;
        }

        long elapsed = new Date().getTime() - dateTimeBeforeWaitForLoad.getTime();
        Logger.debug(String.format("Page loaded; the following not exists: %s. \nFound in %d milliseconds", by.toString(), elapsed));
        return true;
    }

    /**
     * Awaits for the provided child element to be not visible within a provided timeout.
     * If element is visible, throws exception but only if throwException == true
     *
     * @param parentEl        - parent element
     * @param childLocator       - child locator
     * @param timeout        - time to wait
     * @param throwException - if true, throws exception
     * @return - true if child not visible, false otherwise
     * @throws ElementNotInteractableException - throw
     */
    public static boolean waitForChildNotVisible(WebElement parentEl, String childLocator, int timeout, boolean throwException) throws Exception {
        Wait.by = Elements.by(childLocator);
        //Initialize timeout for waiting for the page to load.
        Duration waitForLoadTimeout = timeout <= 0 ? Duration.ofSeconds(10) : Duration.ofSeconds(timeout);

        Logger.debug(String.format("Waiting for page to load; waiting for the following to not be visible: %s.\n" +
                "Waiting for load timeout: %s milliseconds", by.toString(), waitForLoadTimeout.toMillis()));

        Date dateTimeBeforeWaitForLoad;
        try {
            dateTimeBeforeWaitForLoad = new Date();
            waitUntilJSReady();
            new WebDriverWait(driver, waitForLoadTimeout).until(x -> parentEl.findElements(by).size() == 0);
        }
        catch (Exception e) {
            Logger.error(String.format("Failed to wait for page to load since failed to validate invisibility of %s " +
                    "within %d milliseconds.", by.toString(), waitForLoadTimeout.toMillis()));

            if (throwException) {
                throw e;
            }
            return false;
        }

        long elapsed = new Date().getTime() - dateTimeBeforeWaitForLoad.getTime();
        Logger.debug(String.format("Page loaded; the following not visible: %s. \nFound in %d milliseconds", by.toString(), elapsed));
        return true;
    }

    /**
     * Awaits for the provided element to contain the provided text
     * It throws ElementNotVisibleException but only if throwException is true
     *
     * @param webElement - webElement
     * @param text - text to exist
     * @param throwException - throws if true
     * @return - returns true if element becomes visible, false otherwise
     */
    public static boolean waitForText(WebElement webElement, String text, boolean throwException) {
        return wait(Wait.by, ExpectedConditions.textToBePresentInElement(webElement, text), 0, throwException);
    }

    /**
     * Awaits for the provided element to contain the provided text
     * It throws ElementNotVisibleException but only if throwException is true
     *
     * @param locator - locator
     * @param text - text to exist
     * @param throwException - throws if true
     * @return - returns true if element becomes visible, false otherwise
     */
    public static boolean waitForText(String locator, String text, boolean throwException) throws Exception {
        Wait.by = Elements.by(locator);
        return wait(Wait.by, ExpectedConditions.textToBePresentInElementLocated(by, text), 0, throwException);
    }

    /**
     * Awaits until an element's attribute has changed
     *
     * @param element       - element to use
     * @param attr          -   attribute to use
     * @param expectedValue - value to wait for attribute to become
     * @param condition     - AttributeCondition condition
     */
    public static void attributeChanged(WebElement element, String attr, String expectedValue, AttributeCondition condition) {
        attributeChanged(element, attr, expectedValue, condition, 10);
    }

    /**
     * Awaits until an element's attribute has changed
     *
     * @param element       - element to use
     * @param attr          -   attribute to use
     * @param expectedValue - value to wait for attribute to become
     * @param condition     - AttributeCondition condition
     * @param timeout       - timeout
     */
    public static void attributeChanged(WebElement element, String attr, String expectedValue, AttributeCondition condition, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        wait.until(new ExpectedCondition<Boolean>() {
            private WebElement element;
            private String attr;
            private String expectedValue;
            private AttributeCondition condition;

            private ExpectedCondition<Boolean> init(WebElement element, String attr, String expectedValue,
                                                    AttributeCondition condition) {
                this.element = element;
                this.attr = attr;
                this.expectedValue = expectedValue;
                this.condition = condition;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                String enabled = element.getAttribute(this.attr);
                Logger.info("wait: init = (" + expectedValue + "), enabled = (" + enabled + ")");
                Logger.info("Waiting 10seconds for " + enabled + " to " + condition.name()
                        .toLowerCase() + " " +
                        expectedValue);

                switch (condition) {
                    case EQUALS: {
                        return enabled.equals(this.expectedValue);
                    }
                    case NOT_EQUALS: {
                        return !enabled.equals(this.expectedValue);
                    }
                    case CONTAINS: {
                        return enabled.contains(this.expectedValue);
                    }
                    case NOT_CONTAINS: {
                        return !enabled.contains(this.expectedValue);
                    }
                    default:
                        return null;
                }
            }
        }.init(element, attr, expectedValue, condition));
    }

    /**
     * Waits until an element's attribute has changed
     * It deals with List<WebDriver> using the index param in order to get the exact element of that list
     *
     * @param element       element to use - we will further use its locator, so you can provide either a WebElement
     *                      that will return a single one or a list of web elements
     * @param attr          attribute to use
     * @param expectedValue value to wait for attribute to become
     * @param index         index of element in list
     * @param getParent     optional parameter that tells the refresher whether or not to get the element parent
     */
    public static void attributeChanged(WebElement element, String attr, String expectedValue, AttributeCondition
            condition, int index, Boolean... getParent) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(new ExpectedCondition<Boolean>() {
            private WebElement element;
            private String attr;
            private String expectedValue;
            private AttributeCondition condition;

            private ExpectedCondition<Boolean> init(WebElement element, String attr, String expectedValue,
                                                    AttributeCondition condition) {
                this.element = StaleElementUtils.refreshElement(element, index, getParent[0]);
                this.attr = attr;
                this.expectedValue = expectedValue;
                this.condition = condition;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                String enabled = element.getAttribute(this.attr);
                Logger.info("wait: init = (" + expectedValue + "), enabled = (" + enabled + ")");
                Logger.info("Waiting 10 seconds for " + enabled + " to " + condition.name().toLowerCase() + " " + expectedValue);

                switch (condition) {
                    case EQUALS: {
                        return enabled.equals(this.expectedValue);
                    }
                    case NOT_EQUALS: {
                        return !enabled.equals(this.expectedValue);
                    }
                    case CONTAINS: {
                        return enabled.contains(this.expectedValue);
                    }
                    case NOT_CONTAINS: {
                        return !enabled.contains(this.expectedValue);
                    }
                    default:
                        return null;
                }
            }
        }.init(element, attr, expectedValue, condition));
    }

    @SuppressWarnings("all")
    /**
     * Waits for the page to load.
     * Waits for up to a specified timeout.
     * Optionally throws an exception if the page does not load within the specified timeout.
     *
     * @param condition      - condition to be met in order to validate existence of the element on page
     * @param throwException - If true, throws an excpetion if the page does not load. False, does not.
     * @param timeout        - Milliseconds to wait for the page to load. If timeOut &lt;= 0 then the default timeout
     *                       is used (5000 milliseconds).
     * @return - True if page loaded successfully, false otherwise.
     */
    public static boolean waitNoMsg(ExpectedCondition condition, int timeout, boolean throwException) {
        //Initialize timeout for waiting for the page to load.
        Duration waitForLoadTimeout = timeout <= 0 ? Duration.ofSeconds(10) : Duration.ofSeconds(timeout);

        try {
            new WebDriverWait(driver, waitForLoadTimeout).until(condition);
            waitUntilJSReady();
        }
        catch (Exception e) {
            if (throwException) {
                throw e;
            }
            return false;
        }
        return true;
    }

    @SuppressWarnings("all")
    private static boolean wait (By by, ExpectedCondition condition, int timeout, boolean throwException) {
        Duration waitForLoadTimeout = timeout <= 0 ? Duration.ofSeconds(10) : Duration.ofSeconds(timeout);

        Logger.debug(String.format("Waiting for page to load; waiting for the following to exist: %s.\n" +
                "Waiting for load timeout: %s milliseconds", by.toString(), waitForLoadTimeout.toMillis()));

        Date dateTimeBeforeWaitForLoad;

        try {
            dateTimeBeforeWaitForLoad = new Date();
            new WebDriverWait(Serenity.getDriver(), waitForLoadTimeout).until(condition);
        }
        catch (TimeoutException e) {
            if (throwException) {
                Logger.error(String.format("Failed to wait for page to load since failed to validate existence of %s within %d milliseconds!",
                        by.toString(), waitForLoadTimeout.toMillis()));
                throw e;
            }

            Logger.warn(String.format("Element %s not found within %d milliseconds!", by.toString(), waitForLoadTimeout.toMillis()));
            return false;
        }

        long elapsed = new Date().getTime() - dateTimeBeforeWaitForLoad.getTime();
        Logger.debug(String.format("Page loaded; the following exists: %s. \nFound in %d milliseconds", by.toString(), elapsed));
        return true;
    }

    /**
     * Utility method to wait for JS to fully load on page
     */
    static void waitUntilJSReady() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor jsExec = (JavascriptExecutor) driver;

        //wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = driver -> {
            assert driver != null;
            return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
        };

        //Wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = driver -> {
            assert driver != null;
            return ((Long) ((JavascriptExecutor) driver)
                    .executeScript("return jQuery.active") == 0);
        };

        //Get JS is Ready
        boolean jsReady = jsExec.executeScript("return document.readyState").toString().equals("complete");

        //wait Javascript until it is Ready!
        if (!jsReady) {
            System.out.println("JS in NOT Ready!");
            //wait for Javascript to load
            wait.until(jsLoad);
        }
    }

    /**
     * Checks if all ajax calls are complete
     *
     * @return true if no active ajax calls
     */
    static boolean ajaxDone() throws Exception {
        try {

            //below script returns either string or long value, so fetching the results conditionally to avoid type
            // cast error
            Object jsResponse = ((JavascriptExecutor) driver).executeScript("return jQuery.active;");
            Logger.info("response for jQuery active : " + jsResponse);
            long queries;

            if (jsResponse instanceof Long) {
                queries = (Long) jsResponse;
            }
            else if (jsResponse instanceof String) {
                // this means either jquery is not on the current page or not working correctly
                String response = (String) jsResponse;
                return (response.startsWith("{\"hCode\"") || response.isEmpty());
            }
            else {
                Logger.info("Unable to get num ajax calls!");
                return true;
            }

            return queries == 0;
        }
        catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public enum AttributeCondition {
        EQUALS,
        NOT_EQUALS,
        CONTAINS,
        NOT_CONTAINS
    }
}