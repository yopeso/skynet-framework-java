package skynet.interactions;

import net.serenitybdd.core.Serenity;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import skynet.extensions.StringExtensions;
import skynet.utils.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public abstract class Type {

    private static final WebDriver driver = Serenity.getDriver();
    private static final int timeout = 5;

    /**
     * Text field replace text using keyboard
     *
     * @param locator - locator of the field to replace the text for
     * @param text    - text to add
     * @throws Exception - throws various exceptions
     */
    public static void textReplace(String locator, String text) throws Exception {
        WebElement element = Elements.findElement(locator);
        Clicks.click(element);

        Robot robot = new Robot();
        robot.keyRelease(KeyEvent.VK_A);

        text(locator, text);
    }

    /**
     * Pastes the provided string into the provided location
     *
     * @param locator - location as element locator to paste the string into
     * @param toPaste - text to paste
     * @throws Exception - throws various exceptions
     */
    public static void pasteKeys(String locator, String toPaste) throws Exception {
        if (StringExtensions.isEmpty(toPaste)) {
            Logger.exception("toPaste cannot be null or empty");
        }

        StringSelection selection = new StringSelection(toPaste);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        WebElement element = Elements.findElement(locator);
        Clicks.click(element);

        Robot robot = new Robot();

        //Ctrl-A to select all text then release. Used so all text can be selected first before pasting.
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);

        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_A);

        //Paste (Ctrl-V)
        Logger.info("Paste keys: " + toPaste);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);

        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);
    }

    /**
     * Types text into a text box
     *
     * @param locator - locator to use
     * @param text    -  text to type in
     * @throws Exception - throws various exceptions
     */
    public static void text(String locator, String text, long... delay) throws Exception {
        text(locator, text, false, delay);
    }

    /**
     * Types text into a text box
     *
     * @param locator      - locator to use
     * @param text         -  text to type in
     * @param removeWhites - removes whitespaces if true
     * @param delay        - types in with delay if provided
     * @throws Exception - throws various exceptions
     */
    public static void text(String locator, String text, boolean removeWhites, long... delay) throws Exception {
        Wait.waitForClickable(locator, timeout,  true);

        if (getCurrentText(Elements.findElement(locator)).trim().equals(text)) {
            //Current text in adapter already equals value to set it to.
            return;
        }

        if (text == null) {
            text = "";
        }
        String adaptorCurrentText = getCurrentText(locator);
        if (adaptorCurrentText.equals(text)) {
            //Current text in adapter already equals value to set it to.
            return;
        }

        if (delay.length > 0) {
            setCurrentText(locator, text, delay[0]);
        }
        else {
            setCurrentText(locator, text);
        }

        //wait for text to show up in adapter
        final int timeoutMilliseconds = 10000;
        int timeoutRemainingMilliseconds = timeoutMilliseconds;
        int delayMilliseconds = 100;
        while (timeoutRemainingMilliseconds > 0) {
            adaptorCurrentText = removeWhites ? StringExtensions.removeWhiteSpaces(getCurrentText(locator)) : getCurrentText(locator);
            if (adaptorCurrentText.contains(text)) {
                break;
            }

            Thread.sleep(delayMilliseconds);
            timeoutRemainingMilliseconds -= delayMilliseconds;
            if (timeoutRemainingMilliseconds <= 0) {
                Logger.exception(String.format("The adapter's value is not the expected string \"%s\" (between the quotes) after setting the adapter's value to the expected " +
                                                       "string and waiting for about %d seconds to elapse. Actual text from adapter (between the quotes):\"%s\". Adapter: %s.",
                                               text, timeoutMilliseconds / 1000, adaptorCurrentText, locator));
            }
        }
    }

    /**
     * Types text into a text box
     *
     * @param locator   - element locator
     * @param text      - text to type
     * @param checkText - if true, it checks the entered text after type is finished
     * @throws Exception - throws exception if element if not found or text after type if not as text var
     */
    public static void text(String locator, String text, boolean checkText) throws Exception {
        Wait.waitForClickable(locator, timeout,  true);

        if (getCurrentText(Elements.findElement(locator)).trim().equals(text)) {
            //Current text in adapter already equals value to set it to.
            return;
        }

        if (text == null) {
            text = "";
        }
        String adaptorCurrentText = getCurrentText(locator);
        if (adaptorCurrentText.equals(text)) {
            //Current text in adapter already equals value to set it to.
            return;
        }

        setCurrentText(locator, text);

        //wait for text to show up in adapter
        if (checkText) {
            final int timeoutMilliseconds = 10000;
            int timeoutRemainingMilliseconds = timeoutMilliseconds;
            int delayMilliseconds = 100;
            while (timeoutRemainingMilliseconds > 0) {
                adaptorCurrentText = getCurrentText(locator);
                if (adaptorCurrentText.contains(text)) {
                    break;
                }

                Thread.sleep(delayMilliseconds);
                timeoutRemainingMilliseconds -= delayMilliseconds;
                if (timeoutRemainingMilliseconds <= 0) {
                    Logger.exception(String.format("The adapter's value is not the expected string \"%s\" (between the quotes) after setting the adapter's value to the expected " +
                                                           "string and waiting for about %d seconds to elapse. Actual text from adapter (between the quotes):\"%s\". Adapter: %s.",
                                                   text, timeoutMilliseconds / 1000, adaptorCurrentText, locator));
                }
            }
        }
    }

    /**
     * Types text into a text box and after text is typed in it checks if text was correctly entered
     *
     * <>
     *     Remarks:
     *     Because of some fields are automatically formatted (like phone text boxes adding spaces after 3-4 chars)
     *     it first removes all white spaces and then matches it
     * </>
     *
     * @param textbox - webelement to type the text into
     * @param text - text to type
     * @param delay - types with delay if set
     * @throws Exception - throws various exceptions
     */
    public static void text(WebElement textbox, String text, long... delay) throws Exception {
        Wait.waitForClickable(textbox, 10, true);

        if (getCurrentText(textbox).trim().equals(text)) {
            //Current text in adapter already equals value to set it to.
            return;
        }

        if (text == null) {
            text = "";
        }
        String adaptorCurrentText = getCurrentText(textbox);
        if (adaptorCurrentText.equals(text)) {
            //Current text in adapter already equals value to set it to.
            return;
        }

        if (delay.length > 0) {
            setCurrentText(textbox, text, delay[0]);
        }
        else {
            setCurrentText(textbox, text, 1);
        }

        //wait for text to show up in adapter
        final int timeoutMilliseconds = 10000;
        int timeoutRemainingMilliseconds = timeoutMilliseconds;
        int delayMilliseconds = 100;
        while (timeoutRemainingMilliseconds > 0) {
            adaptorCurrentText = StringExtensions.removeWhiteSpaces(getCurrentText(textbox));
            if (adaptorCurrentText.contains(text)) {
                break;
            }

            Thread.sleep(delayMilliseconds);
            timeoutRemainingMilliseconds -= delayMilliseconds;
            if (timeoutRemainingMilliseconds <= 0) {
                Logger.exception(String.format("The adapter's value is not the expected string \"%s\" (between the quotes) after setting the adapter's value to the expected " +
                                                       "string and waiting for about %d seconds to elapse. Actual text from adapter (between the quotes):\"%s\". Adapter: %s.",
                                               text, timeoutMilliseconds / 1000, adaptorCurrentText, Elements.getXpath(textbox)));
            }
        }
    }

    /**
     * Helper method that actually types the text into the provided textbox
     * @param locator - textbox element locator
     * @param text - text to type in
     * @throws Exception - throws various exceptions
     */
    private static void setCurrentText(String locator, String text) throws Exception {
        WebElement element = Elements.findElement(locator);
        Wait.waitForClickable(element, 10, true);

        isValidAdapter(element);

        try {
            Logger.info("Sending keys: " + text);
            Clicks.click(element);
            pause(500);
            element.clear();
            pause(500);
            element.sendKeys(text);
        }
        catch (NullPointerException ex) {
            Logger.exception(String.format("Could not type text '%s' into text box. \nException: %s", text, ex.getMessage()));
        }
    }

    /**
     * Helper method that actually types the text into the provided textbox with delay
     * @param locator - textbox element locator
     * @param text - text to type in
     * @param delayMilliseconds - delay
     * @throws Exception - throws various exceptions
     */
    private static void setCurrentText(String locator, String text, long delayMilliseconds) throws Exception {
        WebElement element = Elements.findElement(locator);
        Wait.waitForClickable(element, 10, true);

        isValidAdapter(element);

        try {
            Logger.info("Sendkeys: " + text);
            element.click();
            pause(500);
            element.clear();
            pause(500);
            element.click();
            pause(500);

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                String s = String.valueOf(c);
                element.sendKeys(s);
                pause(delayMilliseconds);
            }
        }
        catch (NullPointerException ex) {
            Logger.exception(String.format("Could not type text '%s' into text box. \nException: %s", text, ex.getMessage()));
        }
    }

    /**
     * Helper method that actually types the text into the provided textbox with delay
     * @param textbox - webelement textbox
     * @param text - text to type in
     * @param delayMilliseconds - delay
     * @throws Exception - throws various exceptions
     */
    private static void setCurrentText(WebElement textbox, String text, long delayMilliseconds) throws Exception {
        Wait.waitForClickable(textbox, 10, true);

        isValidAdapter(textbox);

        try {
            Logger.info("Sending keys: " + text);
            textbox.click();
            pause(delayMilliseconds);
            textbox.clear();
            pause(delayMilliseconds);
            textbox.sendKeys(text);
        }
        catch (NullPointerException ex) {
            Logger.exception(String.format("Could not type text '%s' into text box. \nException: %s", text, ex.getMessage()));
        }
    }

    /**
     * Using sleep :D:D:D
     * @param time - sleep time in millis
     */
    private static void pause(long time) {
        try {
            Thread.sleep(Duration.of(time, ChronoUnit.MILLIS).toMillis());
        }
        catch (InterruptedException ignore) {
        }
    }

    /**
     * Gets the current text set in the provided location
     *
     * @param locator - element locator
     * @return - returns text
     * @throws Exception - throws NoSuchElementException
     */
    public static String getCurrentText(String locator) throws Exception {
        return getCurrentText(Elements.findElement(locator));
    }

    /**
     * Gets the current text set in the provided location
     *
     * @param textbox - webelement
     * @return - returns text
     * @throws Exception - throws NoSuchElementException
     */
    public static String getCurrentText(WebElement textbox) throws Exception {
        String currentText = null;

        if(isValidAdapter(textbox)) {
            currentText = textbox.getAttribute("value");
        }

        if (currentText == null) {
            currentText = "";
        }
        return currentText;
    }

    /**
     * It clears text from the given WebElement - which has to be either input or textarea - using the Keys
     * <p>
     * Remarks: Looks like sometimes CTRL + A doesn't work and Selenium is not able to clear a pseudo-element. So this can
     * be used instead of element.clear()
     * <p>
     * Be aware of the fact that pseudo-elements do not store a value, so this has to be used as a standalone solution
     * (cannot be integrated in the existing setText methods) and only if you are working
     * with that kind of element
     *
     * @param locator - element locator from repo (contains By and selector)
     * @throws InterruptedException - throws exception
     */
    public static void retroClearText(String locator) throws Exception {
        WebElement element = Elements.findElement(locator);

        Clicks.click(element);
        pause(500);

        int count = 15;
        element.sendKeys(Keys.END);
        pause(500);

        while (count > 0) {
            element.sendKeys(Keys.BACK_SPACE);
            pause(500);
            count--;
        }
    }

    private static boolean isValidAdapter(WebElement element) throws Exception {
        String adapterType = element.getTagName().toLowerCase();

        switch (adapterType) {
            case "input":
            case "textarea":
            case "android.widget.edittext": {
                return true;
            }
            default:
                Logger.exception(String.format("Code not implemented yet for adapterType: %s", adapterType));
        }
        return false;
    }
}
