package skynet.interactions;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import skynet.extensions.IEnumerableExtensions;
import skynet.utils.AHCustomAsserts;
import skynet.utils.Logger;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Dropdowns {
    /**
     * Selects a value from a drop down menu based on text
     *
     * @param locator String locator in format "BillingInfoRepo.RepositoryEnum.PaymentMethodFrame.name()"
     * @param text     text to select
     */
    public static void selectByText(String locator, String text) throws Exception {
        selectByText(Elements.findElement(locator), text);
    }

    /**
     * Selects a value from a drop down menu based on text
     *
     * @param el   drop down menu element
     * @param text text to select
     */
    public static void selectByText(WebElement el, String text) throws Exception {
        Select select = new Select(el);

        final String[] selectedOption = new String[1];
        select.getOptions().forEach(elem -> {
            if (elem.getText().trim().toLowerCase().equals(text.toLowerCase())) {
                selectedOption[0] = elem.getText();
                elem.click();
            }
        });

        try {
            AHCustomAsserts.VerifyEqual(selectedOption[0], select.getFirstSelectedOption().getText());
        }
        catch (StaleElementReferenceException staleElEx) {
            Logger.warn(staleElEx.getMessage());
            Logger.info("Retrying to get the element after StaleElementReferenceException");
            selectByText(Elements.findElement(By.xpath(Elements.getXpath(el))), text);
        }
    }

    /**
     * Selects a random option from the dropdown - other than existing one
     *
     * @param locator - dropdown locator
     * @throws Exception - throws exception if element is not found
     */
    public static void selectRandomOption(String locator) throws Exception {
        List<String> options = getAllOptionValues(locator).stream().filter(opt -> !opt.trim().equalsIgnoreCase("select") && !opt.toLowerCase().contains("select"))
                                                          .collect(Collectors.toList());

        Logger.info("Available options in dropdown: " + options.toString());

        if (options.size() == 1) {
            Logger.exception("There's only one available option under the dropdown options!");
        }

        int tries = 10;
        String opt = IEnumerableExtensions.random(options).toString();

        while (opt.equalsIgnoreCase(getSelectedOption(locator)) && tries > 0) {
            opt = IEnumerableExtensions.random(options).toString();
            tries--;
        }

        if (tries == 0) {
            Logger.exception("Random couldn't get a different option than the already selected one!");
        }

        Logger.info(String.format("Selecting '%s' option from dropdown.", opt));
        selectByText(locator, opt);
    }

    /**
     * Selects a random option from the dropdown - other than existing one
     *
     * @param locator - dropdown locator
     * @throws Exception - throws exception if element is not found
     */
    public static void selectRandomOption(String locator, Predicate<String> condition) throws Exception {
        String selectedOption = getSelectedOption(locator);
        List<String> options = getAllOptionValues(locator).stream()
                                                          .filter(opt -> !opt.trim().equals(selectedOption))
                                                          .filter(condition)
                                                          .collect(Collectors.toList());

        Logger.info("Available options in dropdown: " + options.toString());

        String opt = IEnumerableExtensions.random(options).toString();

        Logger.info(String.format("Selecting '%s' option from dropdown.", opt));
        selectByText(locator, opt);
    }

    /**
     * Selects the last option from the dropdown
     *
     * @param by - dropdown locator
     * @throws Exception - throws exception if element is not found
     */
    public static void selectLastOption(By by) throws Exception {
        Select dropdown = new Select(Elements.findElement(by));
        dropdown.selectByVisibleText(getLastOption(by));
    }

    /**
     * Returns a list of values from a drop down menu based on element
     *
     * @param locator selector of dropdown element
     */
    public static List<String> getAllOptionValues(String locator) throws Exception {
        Select select = new Select(Elements.findElement(locator));
        return select.getOptions().stream().map(e -> e.getText().trim()).collect(Collectors.toList());
    }

    /**
     * Returns a list of values from a drop down menu based on element
     *
     * @param by selenium by
     */
    public static List<String> getAllOptionValues(By by) throws Exception {
        Select select = new Select(Elements.findElement(by));
        return select.getOptions().stream().map(e -> e.getText().trim()).collect(Collectors.toList());
    }

    /**
     * Returns a list of values from a drop down menu based on element
     *
     * @param el selector of dropdown element
     */
    public static List<String> getAllOptionValues(WebElement el) throws Exception {
        Select select = new Select(el);
        return select.getOptions().stream().map(e -> e.getText().trim()).collect(Collectors.toList());
    }

    /**
     * Gets a random option from the dropdown options
     *
     * @param locator - dropdown selector
     * @return - return the randomly selected option
     * @throws Exception - throws exception if element is not found
     */
    public static String getRandomOption(String locator) throws Exception {
        return IEnumerableExtensions.random(getAllOptionValues(locator)).toString();
    }

    /**
     * Gets the selected option from the dropdown
     *
     * @param locator - dropdown selector
     * @return - return the selected option
     * @throws Exception - throws exception if element is not found
     */
    public static String getSelectedOption(String locator) throws Exception {
        return getSelectedOption(Elements.findElement(locator)).trim();
    }

    /**
     * Gets the selected option from the dropdown
     *
     * @param el - dropdown webelement
     * @return - return the selected option
     */
    public static String getSelectedOption(WebElement el) {
        return new Select(el).getFirstSelectedOption().getText();
    }

    /**
     * Gets the last option from the dropdown
     *
     * @param by - selenium by
     * @return - return the last option
     */
    public static String getLastOption(By by) throws Exception {
        List<String> allDropdownElements = getAllOptionValues(by);
        int dropdownSize = getAllOptionValues(by).size() - 1;
        return allDropdownElements.get(dropdownSize);
    }
}