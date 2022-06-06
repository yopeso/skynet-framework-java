package skynet.interactions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import skynet.utils.Logger;

public abstract class Checkbox {
    /**
     * Checks a checkbox if it is not already checked
     * @param locator String selector
     */
    public static void check(String locator) throws Exception {
        check(Elements.by(locator));
    }

    /**
     * Checks a checkbox if it is not already checked
     * @param element WebElement to click on
     */
    public static void check(WebElement element) throws Exception {
        if (!element.isSelected()) {
            Clicks.click(element);

            return;
        }

        Logger.debug("Checkbox already checked.");
    }

    /**
     * Checks a checkbox if it is not already checked
     * @param by By to use
     */
    public static void check(By by) throws Exception {
        WebElement checkBox = Elements.findElement(by, false);
        if (checkBox != null && !checkBox.isSelected()) {
            Clicks.click(checkBox);
            return;
        }

        if(checkBox == null) Logger.exception("Couldn't find the given checkbox!");
        Logger.debug("Checkbox already checked.");
    }

    /**
     * Unchecks a checkbox if it is checked
     * @param locator String selector in format "page_name.element_name"
     */
    public static void uncheck(String locator) throws Exception {
        uncheck(Elements.by(locator));
    }

    /**
     * Unchecks a checkbox if it is checked
     * @param element WebElement to click on
     */
    public static void uncheck(WebElement element) throws Exception {
        if (element != null && element.isSelected()) {
            Clicks.click(element);

            return;
        }

        Logger.debug("Checkbox already unchecked.");
    }

    /**
     * Unchecks a checkbox if it is checked
     * @param by By selector to use
     */
    public static void uncheck(By by) throws Exception {
        WebElement checkBox = Elements.findElement(by, false);
        if (checkBox != null && checkBox.isSelected()) {
            Clicks.click(checkBox);
            return;
        }

        if(checkBox == null) Logger.exception("Couldn't find the given checkbox!");
        Logger.debug("Checkbox already unchecked.");
    }

    /**
     * Verify the status of existing checkbox
     *
     * @param locator element locator from repo to use
     */
    public static void verifyStatusOfCheckBox(String locator) throws Exception {
        WebElement checkBox = Elements.findElement(locator, false);
        if (checkBox != null && !checkBox.isSelected()) {
            Logger.info("checkbox is not checked");
            return;
        }

        if (checkBox == null) { Logger.exception("Couldn't find the given checkbox!"); }
        Logger.debug("Checkbox already checked.");
    }
}