package skynet.interactions;

import io.appium.java_client.MobileBy;
import net.serenitybdd.core.Serenity;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import skynet.extensions.StringExtensions;
import skynet.utils.Logger;
import skynet.utils.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Elements {
    private static final WebDriver driver = Serenity.getDriver();

    //---------------------------------------------------------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------ UTILITIES  ---------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------------------//

    /**
     * Gets and sets the By to identify an element using the locator in form of: Repo.RepositoryEnum.Element.name()
     * This is taken from the pre-defined repo and contains a by that will be checked here for its validity and a selector
     * <p>
     * e.g:
     * Frame("id", "selector")
     * <p>
     * <>
     * Explanations:
     * Frame - is the element's name from the repository
     * id - is the By to integrate with selenium locators
     * selector - is the unique identified of that id attribute
     * <p>
     * In selenium the element will be constructed as: element.findElement(By.id("selector"))
     * </>
     */
    @SuppressWarnings(value = "all")
    public static By by (String locator) throws Exception {
        PageElement pageElement = new PageElement(locator);
        if (pageElement.elementBy == null) {
            Logger.exception("null locator provided! Check if you are setting the Repo correctly before you provide the locator!");
        }

        if (!isValidLocatorStrategy(pageElement.elementBy)) {
            Logger.exception("Not a valid locator strategy! Try checking the repository you have set up in your action class!");
        }

        switch (pageElement.elementBy.toLowerCase()) {
            case "class":
            case "classname": {
                return By.className(pageElement.elementSelector);
            }
            case "id": {
                return By.id(pageElement.elementSelector);
            }
            case "css": {
                return By.cssSelector(pageElement.elementSelector);
            }
            case "xpath": {
                return By.xpath(pageElement.elementSelector);
            }
            case "name": {
                return By.name(pageElement.elementSelector);
            }
            case "accessibillity": {
                return MobileBy.AccessibilityId(pageElement.elementSelector);
            }
            case "mobileid": {
                return MobileBy.id(pageElement.elementSelector);
            }
            case "uiautomator": {
                return MobileBy.AndroidUIAutomator(pageElement.elementSelector);
            }
            default:
                throw new Exception("Case not implemented yet for By: " + pageElement.elementBy);
        }
    }

    /**
     * Checks for valid locator strategy
     *
     * @param strategy - locator strategy eg. id, name
     * @return - true on valid locator strategy
     */
    private static boolean isValidLocatorStrategy (String strategy) {
        switch (strategy) {
            case "id":
            case "linkText":
            case "link text":
            case "name":
            case "partialLinkText":
            case "partial link text":
            case "tagName":
            case "tag name":
            case "xpath":
            case "className":
            case "class":
            case "class name":
            case "cssSelector":
            case "css selector":
            case "css":
            case "UIAutomator":
            case "uiAutomator":
            case "mobileid":
            case "accessibillity":
                return true;
            default:
                return false;
        }
    }

    /**
     * Gets the xpath separator got immediate or far child
     *
     * @param getImmediateChild - if true uses "/" to get the immediate child, if false uses "//" to get far child
     * @return - xpath separator
     */
    private static String getXPathSeparator(boolean getImmediateChild) {
        return getImmediateChild ? "/" : "//";
    }

    /**
     * Formats the selector by adding the tag index
     *
     * @param selector - selector
     * @param index    - index
     * @return - returns selector with added index
     */
    private static String addIndex(String selector, int index) {
        return String.format("%s[%d]", selector, index);
    }

    /**
     * Gets the relative xpath for the provided WebElement and adds its index
     *
     * @param element - element to get the relative xpath from
     * @param index   - index to add
     * @return - return formatted xpath as string
     */
    public static String formatXPath(WebElement element, int index) {
        String[] locators = element.toString().split("->");
        String xpath = "";

        for (String s : locators) {
            String newLocator = s.trim().replaceAll("^\\[+", "").replaceAll("]+$", "");
            String[] parts = newLocator.split(": ");
            xpath = parts[1];
            int leftBracketsCount = xpath.length() - xpath.replace("[", "").length();
            int rightBracketscount = xpath.length() - xpath.replace("]", "").length();

            if (leftBracketsCount - rightBracketscount == 1) {
                xpath = xpath + "]";
            }
        }

        return addIndex(xpath, index);
    }

    /**
     * !!! Use the Robot class in order to move mouse cursor to the element (IE and Firefox won't work with Actions class)
     * regardless of the used screen resolution or browser header configuration
     *
     * @param selector - element to move to/hover
     * @throws Exception - throws exception
     */
    public static void moveToElement(String selector)
            throws Exception {
        moveToElement(Elements.findElement(selector));
    }

    /**
     * !!! Use the Robot class in order to move mouse cursor to the element (IE and Firefox won't work with Actions class)
     * regardless of the used screen resolution or browser header configuration
     *
     * @param locator - element to move to/hover
     * @throws Exception - throws exception
     */
    public static void moveToElement(By locator)
            throws Exception {
        moveToElement(Elements.findElement(locator));
    }

    /**
     * !!! Use the Robot class in order to move mouse cursor to the element (IE and Firefox won't work with Actions class)
     * regardless of the used screen resolution or browser header configuration
     *
     * @param element - element to move to/hover
     * @throws Exception - throws exception
     */
    public static void moveToElement(WebElement element) throws Exception {
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------- FIND ELEMENT/S HELPERS --------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------------------//

    /**
     * Finds the element on the webpage using the provided locator from repo
     *
     * @param locator - element locator from repo
     * @return - returns the webelement
     * @throws NoSuchElementException - throws NoSuchElementException if element does not exist
     */
    public static WebElement findElement (String locator) throws Exception {
        return findElement(locator, true);
    }

    /**
     * Finds the element on the webpage using the provided locator from repo
     *
     * @param locator        - element locator from repo
     * @param throwException - if true and element is not found it throws NoSuchElementException
     * @return - returns the webelement
     * @throws NoSuchElementException - throws NoSuchElementException if element does not exist
     */
    public static WebElement findElement (String locator, boolean throwException) throws Exception {
        return findElement(by(locator), throwException);
    }

    /**
     * Finds the element on the webpage using the provided By (By.xpath("element"))
     *
     * @param by - element by
     * @return - returns the webelement
     * @throws NoSuchElementException - throws NoSuchElementException if element does not exist
     */
    public static WebElement findElement (By by) throws Exception {
        return findElement(by, true);
    }


    /**
     * Finds the element on the webpage using the provided By (By.xpath("element"))
     *
     * @param by             - element by
     * @param throwException - if true and element is not found it throws NoSuchElementException
     * @return - returns the webelement
     * @throws NoSuchElementException - throws NoSuchElementException if element does not exist
     */
    public static WebElement findElement (By by, boolean throwException) throws Exception {
        Logger.info("Find element using selector : " + by.toString());
        try {
            try {
                //Waiting just a bit for the element to show up
                Wait.waitUntilJSReady();
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.presenceOfElementLocated(by));
            } catch (Exception e) {
                //nothing to do here
            }

            List<WebElement> elements = driver.findElements(by);
            if (elements == null || elements.size() == 0) {
                throw new NoSuchElementException("Unable to locate an element using selector : " + by);
            }
            List<WebElement> visible = elements.stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
            if (!visible.isEmpty()) {
                elements = visible;
            }

            return elements.get(0);
        } catch (StaleElementReferenceException e) {
            Logger.warn(e.getMessage());
            StaleElementUtils.refreshElement(by);
            findElement(by, throwException);
        } catch (NoSuchElementException ex) {
            if (throwException) {
                Logger.exception("No element found with selector: " + by);
            }
            Logger.warn("No element found with selector: " + by);
        }

        return null;
    }

    /**
     * Retrieves all elements using a selector and filters them with the given Predicate if provided.
     * If no element is found it will throw an exception
     *
     * @param locator - name of the element from the given className/repo
     * @param filter  Predicate to filter results with
     * @return list of WebElements selected by el after filter is applied
     * @throws NoSuchElementException - throws NoSuchElementException if the element does not exist
     */
    public static WebElement findElement (String locator, Predicate<WebElement> filter) throws Exception {
        return findElements(by(locator), filter, true).get(0);
    }

    /**
     * Retrieves all elements using a selector and filters them with the given Predicate if provided.
     * If no element is found it will not throw an exception, just logs an error and returns null
     *
     * @param locator - name of the element from the given className/repo
     * @param filter  Predicate to filter results with
     * @return list of WebElements selected by el after filter is applied
     * @throws NoSuchElementException - throws NoSuchElementException if the element does not exist
     */
    public static WebElement findElement (String locator, Predicate<WebElement> filter, boolean throwException) throws Exception {
        return findElements(by(locator), filter, throwException).size() > 0 ? findElements(by(locator), filter, throwException).get(0) : null;
    }

    /**
     * Retrieves all elements
     *
     * @param locator name of the element from the given className/repo
     * @return list of WebElements selected by el
     * @throws NoSuchElementException - throws NoSuchElementException
     */
    public static List<WebElement> findElements (String locator) throws Exception {
        return findElements(locator, null);
    }

    /**
     * Retrieves all elements using a selector and filters them with the given Predicate if provided.
     * If no element is found it will throw an exception
     *
     * @param locator - name of the element from the given className/repo
     * @param filter  Predicate to filter results with
     * @return list of WebElements selected by el after filter is applied
     * @throws NoSuchElementException - throws NoSuchElementException if the element does not exist
     */
    public static List<WebElement> findElements (String locator, Predicate<WebElement> filter) throws Exception {
        return findElements(by(locator), filter, true);
    }

    /**
     * Retrieves all visible elements using a given selector
     * <p>
     * This will return null if no elements are found, or an empty list if elements are found but not
     * currently displayed.
     * </p>
     *
     * @param locator        name of the item from repository class enum
     * @param throwException if true throws an Exception if no element is found. Otherwise, it returns null
     * @return list of WebElements selected by el
     * @throws NoSuchElementException - throws NoSuchElementException if the element does not exist
     */
    public static List<WebElement> findElements (String locator, boolean throwException) throws Exception {
        return findElements(locator, null, throwException);
    }

    /**
     * Retrieves all elements using a selector and filters them with the given Predicate if provided
     *
     * @param locator        name of the item from repository class enum
     * @param filter         Predicate to filter results with
     * @param throwException if true throws an Exception if no element is found. Otherwise, it returns null
     * @return list of WebElements selected by el after filter is applied
     * @throws NoSuchElementException - throws NoSuchElementException if the element does not exist
     */
    public static List<WebElement> findElements (String locator, Predicate<WebElement> filter, boolean throwException) throws Exception {
        return findElements(by(locator), filter, throwException);
    }

    /**
     * Retrieves all elements using a selector and filters them with the given Predicate if provided
     *
     * @param by             formed from the element type of by, and it's selector
     * @param filter         Predicate to filter results with
     * @param throwException if true throws an Exception if no element is found. Otherwise, it returns null
     * @return list of WebElements selected by el after filter is applied
     * @throws NoSuchElementException - throws NoSuchElementException if the element does not exist
     */
    public static List<WebElement> findElements (By by, Predicate<WebElement> filter, boolean throwException) throws Exception {
        return Elements.findElements(by, filter, throwException, 10);
    }

    /**
     * Retrieves all elements using a selector and filters them with the given Predicate if provided
     *
     * @param by             formed from the element type of by, and it's selector
     * @param filter         Predicate to filter results with
     * @param throwException if true throws an Exception if no element is found. Otherwise, it returns null
     * @return list of WebElements selected by el after filter is applied
     * @throws NoSuchElementException - throws NoSuchElementException if the element does not exist
     */
    public static List<WebElement> findElements (By by, Predicate<WebElement> filter, boolean throwException, int timeout) throws Exception {
        List<WebElement> elements = null;

        Wait.waitNoMsg(ExpectedConditions.presenceOfAllElementsLocatedBy(by), timeout, false);

        try {
            elements = filter != null ?
                    driver.findElements(by).stream().filter(filter).collect(Collectors.toList()) :
                    driver.findElements(by);
        } catch (Exception ex) {
            if (throwException) {
                Logger.exception("No elements found for selector: " + by.toString());
            }
            Utils.threadSleep(100, null);
        }

        if (elements == null || elements.size() == 0) {
            if (throwException) {
                Logger.exception("No elements found with selector : " + by.toString());
            }
            Logger.error("No elements found with selector : " + by.toString());
        }
        return elements;
    }

    public static List<WebElement> findElementsByDescendant (String locator, By descendant) throws Exception {
        List<WebElement> allElements = Elements.findElements(locator);
        List<WebElement> newList = new ArrayList<>();

        for (WebElement e : allElements) {
            if (e.findElements(descendant).size() != 0) {
                newList.add(e);
            }
        }
        return newList;
    }

    public static List<WebElement> findElementsWithoutDescendant (String locator, By descendant) throws Exception {
        List<WebElement> allElements = Elements.findElements(locator);
        List<WebElement> newList = new ArrayList<>();

        for (WebElement e : allElements) {
            if (e.findElements(descendant).size() == 0) {
                newList.add(e);
            }
        }
        return newList;
    }

    /**
     * Finds all descendants for the provided selector using By.xpath()
     *
     * @param selector             - element selector to use with By.xpath()
     * @param descendant           - descendant as xpath
     * @param getImmediateChildren - if true uses "/" to get the immediate child, if false uses "//" to get far child
     * @return - list of found webelements
     * @throws NoSuchElementException - throws NoSuchElementException if element does not exist
     */
    public static List<WebElement> findDescendants(String selector, String descendant, boolean getImmediateChildren) throws Exception {
        selector = String.format("%s%s%s", selector, getXPathSeparator(getImmediateChildren), descendant);

        return findElements(By.xpath(selector), null, true);
    }

    /**
     * Finds all descendants for the provided webelement
     * First it gets the relative xpath and then forms the xpath to use for descendants like ^^
     *
     * @param elem                 - parent element
     * @param descendant           - descendant xpath
     * @param getImmediateChildren - if true uses "/" to get the immediate child, if false uses "//" to get far child
     * @return - list of found webelements
     * @throws NoSuchElementException - throws NoSuchElementException if element does not exist
     */
    public static List<WebElement> findDescendants(WebElement elem, String descendant, boolean getImmediateChildren) throws Exception {
        return findElements(By.xpath(String.format("%s%s%s", getAbsoluteXPath(elem), getXPathSeparator(getImmediateChildren), descendant)), null, true);
    }

    /**
     * Finds all descendants for the provided webelement
     * First it gets the relative xpath and then forms the xpath to use for descendants like ^^
     *
     * @param elem                 - parent element
     * @param descendant           - descendant xpath
     * @param getImmediateChildren - if true uses "/" to get the immediate child, if false uses "//" to get far child
     * @param throwException       - Throws exception if true and no element found
     * @return - List of webelement
     * @throws NoSuchElementException - throws NoSuchElementException if element does not exist
     */
    public static List<WebElement> findDescendants(WebElement elem, String descendant, boolean getImmediateChildren, boolean throwException) throws Exception {
        return findElements(By.xpath(String.format("%s%s%s", getAbsoluteXPath(elem), getXPathSeparator(getImmediateChildren), descendant)), null, throwException);
    }

    /**
     * Finds all descendants of a given element by its tagName
     *
     * @param elem    - element to get the descendants for
     * @param tagName - tagName of descendant/s
     * @return - list of WebElements with all descendants
     */
    public static List<WebElement> findDescendantsByTagName(WebElement elem, String tagName) {
        return elem.findElements(By.tagName(tagName));
    }

    /**
     * Finds all descendants of a given element by xpath.
     * The xpath can contain tag with attributes such as div[@class='class']
     *
     * @param elem               - element to get the descendants for
     * @param xpathOfDescendants - xpath of descendant/s
     * @return - list of WebElements with all descendants
     */
    public static List<WebElement> findDescendantsByXpath(WebElement elem, String xpathOfDescendants) throws Exception {
        return findElements(By.xpath(getAbsoluteXPath(elem) + xpathOfDescendants), null, true);
    }

    /**
     * Finds the first descendant of a given element by xpath.
     * The xpath can contain tag with attributes such as div[@class='class']
     *
     * @param elem               - element to get the descendants for
     * @param xpathOfDescendants - xpath of descendant/s
     * @return - First descendant as WebElement
     */
    public static WebElement findFirstDescendantsByXpath(WebElement elem, String xpathOfDescendants) throws Exception {
        return findElements(By.xpath(getAbsoluteXPath(elem) + xpathOfDescendants), null, true).stream().findFirst().get();
    }

    /**
     * Finds the previous sibling of the given element selector
     *
     * @param selector - selector of current element
     * @return - returns previous sibling as WebElement
     * @throws Exception - throws exception
     */
    public static WebElement findPreviousSibling(String selector, String tag) throws Exception {
        return findElement(By.xpath(selector + "/preceding-sibling::" + tag), true);
    }

    /**
     * Finds the previous sibling of the given element selector
     *
     * @param elem - current element
     * @return - returns previous sibling as WebElement
     */
    public static WebElement findFollowingSibling(WebElement elem, String tag) throws Exception {
        return findElement(By.xpath(getAbsoluteXPath(elem)) + "following-sibling::" + tag);
    }

    /**
     * Finds the following siblings of the given element
     *
     * @param elem - current element
     * @param tag - tag to follow
     * @param getImmediateChild - get immediate child separator
     * @return - returns previous sibling as WebElement
     */
    public static List<WebElement> findFollowingSiblings(WebElement elem, String tag, boolean getImmediateChild) throws Exception {
        return findElements(By.xpath(String.format("%s%s%s%s", getAbsoluteXPath(elem), getXPathSeparator(getImmediateChild), "following-sibling::", tag)), null, true);
    }

    /**
     * Finds the following siblings of the given element
     *
     * @param xpathSelector - current element
     * @param tag - tag to follow
     * @param getImmediateChild - get immediate child separator
     * @return - returns previous sibling as WebElement
     */
    public static List<WebElement> findFollowingSiblings(String xpathSelector, String tag, boolean getImmediateChild) throws Exception {
        return findElements(String.format("%s%s%s%s", xpathSelector, getXPathSeparator(getImmediateChild), "\"following-sibling::\"", tag), null, true);
    }

    /**
     * Gets the parent element of the given one
     *
     * @param el  - element to get its parent
     * @param tag - tag of the parent - can use '*' (asterisk) if you don't know the tag
     * @return - return parent as WebElement
     */
    public static WebElement findParent(WebElement el, String tag) {
        Logger.info(el.toString());
        return el.findElement(By.xpath("parent::" + tag));
    }

    /**
     * Gets the parent element of the given one by a specified attribute
     *
     * @param el        - element to get its parent
     * @param attribute - attribute of the parent
     * @return - return parent as WebElement
     */
    public static WebElement findParentWithAttribute(WebElement el, String attribute, String value) {
        WebElement newEl = findParent(el, "*");

        while (!newEl.getAttribute(attribute).contains(value)) {
            newEl = findParent(el, "*");
        }
        return newEl;
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------- ELEMENT/S EXTENSIONS ----------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------------------//


    /**
     * Gets the text from the provided locator.
     * First it identifies the element on the page, and then it checks either for it's textContent or value attribute.
     * <p>
     * <>
     * Remarks:
     * sometimes, because of the way element are constructed in HTML/DOM selenium can not get the text of a webelement
     * just using element.getText(). Therefore this method to bypass that issue
     * </>
     *
     * @param locator - element locator from repo
     * @return - return text from the element
     */
    public static String getText (String locator) {
        try {
            WebElement element = findElement(locator);

            if (!StringExtensions.isEmpty(element.getText())) {
                return element.getText();
            } else if (!StringExtensions.isEmpty(element.getAttribute("textContent"))) {
                return element.getAttribute("textContent");
            } else if (!StringExtensions.isEmpty(element.getAttribute("value"))) {
                return element.getAttribute("value");
            } else {
                return "";
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        return "";
    }

    /**
     * Gets an attribute value "attr" from element
     *
     * @param elementName name of the item from repository class enum
     * @param attr        attribute to retrieve
     * @return requested attribute value if it exists, otherwise empty string
     */
    public static String getElementAttribute (String elementName, String attr) throws Exception {
        return getElementAttribute(elementName, attr, true);
    }

    /**
     * Gets an attribute value "attr" from element
     *
     * @param locator        name of the item from repository class enum
     * @param attr           attribute to retrieve
     * @param throwException if true, throws exception if the attribute is not found
     * @return requested attribute value if it exists, otherwise empty string
     */
    public static String getElementAttribute (String locator, String attr, boolean throwException) throws Exception {
        try {
            WebElement el = findElement(locator);

            String attribute = el.getAttribute(attr);
            if (attribute == null) {
                if (throwException) {
                    throw new NullPointerException();
                }
                Logger.debug(String.format("Element has no %s attribute", attr));
            }
            return attribute;
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Gets an attribute value "attr" from element
     *
     * @param element        name of the item from repository class enum
     * @param attr           attribute to retrieve
     * @param throwException if true, throws exception if the attribute is not found
     * @return requested attribute value if it exists, otherwise empty string
     */
    public static String getElementAttribute (WebElement element, String attr, boolean throwException) {
        try {
            String attribute = element.getAttribute(attr);
            if (attribute == null) {
                if (throwException) {
                    throw new NullPointerException();
                }
                Logger.debug(String.format("Element has no %s attribute", attr));
            }
            return attribute;
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Gets the xpath of an WebElement
     *
     * @param elem - WebElement to get the xpath from
     * @return - xpath as String
     */
    public static String getXpath (WebElement elem) {
        String value = "";
        String[] locators = elem.toString().split("->");
        for (String s : locators) {
            String newLocator = s.trim().replaceAll("^\\[+", "").replaceAll("]+$", "");
            String[] parts = newLocator.split(": ");
            value = parts[1];
            int leftBracketsCount = value.length() - value.replace("[", "").length();
            int rightBracketscount = value.length() - value.replace("]", "").length();
            if (leftBracketsCount - rightBracketscount == 1) {
                value = value + "]";
            }
        }

        return value;
    }

    /**
     * Gets the absolute xpath of an WebElement
     *
     * @param element - WebElement to get the xpath from
     * @return - xpath as String
     */
    public static String getAbsoluteXPath (WebElement element) {
        return (String) ((JavascriptExecutor) driver).executeScript("function absoluteXPath(element) {" + "var comp, comps = [];" + "var parent = null;" + "var xpath = '';" + "var getPos = function(element) {" + "var position = 1, curNode;" + "if (element.nodeType == Node.ATTRIBUTE_NODE) {" + "return null;" + "}" + "for (curNode = element.previousSibling; curNode; curNode = curNode.previousSibling){" + "if (curNode.nodeName == element.nodeName) {" + "++position;" + "}" + "}" + "return position;" + "};" +

                "if (element instanceof Document) {" + "return '/';" + "}" +

                "for (; element && !(element instanceof Document); element = element.nodeType == Node" + ".ATTRIBUTE_NODE ? element.ownerElement : element.parentNode) {" + "comp = comps[comps.length] = {};" + "switch (element.nodeType) {" + "case Node.TEXT_NODE:" + "comp.name = 'text()';" + "break;" + "case Node.ATTRIBUTE_NODE:" + "comp.name = '@' + element.nodeName;" + "break;" + "case Node.PROCESSING_INSTRUCTION_NODE:" + "comp.name = 'processing-instruction()';" + "break;" + "case Node.COMMENT_NODE:" + "comp.name = 'comment()';" + "break;" + "case Node.ELEMENT_NODE:" + "comp.name = element.nodeName;" + "break;" + "}" + "comp.position = getPos(element);" + "}" +

                "for (var i = comps.length - 1; i >= 0; i--) {" + "comp = comps[i];" + "xpath += '/' + comp.name.toLowerCase();" + "if (comp.position !== null) {" + "xpath += '[' + comp.position + ']';" + "}" + "}" +

                "return xpath;" +

                "} return absoluteXPath(arguments[0]);", element);
    }
}
