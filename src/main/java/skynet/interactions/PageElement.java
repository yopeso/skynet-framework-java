package skynet.interactions;

import skynet.Statics;
import skynet.utils.Logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map;

public class PageElement {

    /**
     * example : Repo.RepositoryEnum.Frame.by() = id
     */
    public String elementBy = null;

    /**
     * example : Repo.RepositoryEnum.Frame.selector() = "//div[@class='bla']"
     */
    public String elementSelector = null;

    /**
     * example : Repo.RepositoryEnum.Frame.name()
     */
    public String elementName = null;

    /**
     * example : Repo.RepositoryEnum.Frame.name() = [id, cssSelector]
     */
    public ArrayList<String> elementLocators = new ArrayList<>();


    /**
     * Setup and read page element data
     *
     * @param locator - element locator from repo
     * @throws Exception - throws exception
     */
    public PageElement (String locator) throws Exception {
        Object object = new Object();
        try {
            Class<?> ttt = Class.forName(Statics.getRepoName());
            Constructor<?> ctor = ttt.getConstructor();
            object = ctor.newInstance();
        } catch (Exception e) {
            Logger.exception(e.fillInStackTrace().getMessage());
        }

        @SuppressWarnings(value = "unchecked")
        Map<String, ArrayList<String>> locators = (Map<String, ArrayList<String>>) object.getClass().getMethod("getAll").invoke(object);

        parseValue(locators, locator);
    }

    /**
     * Parses the locators and values for a page element
     *
     * @param locators    values in format 'id, selector'
     * @param elementName - element to match
     * @return - returns the locators
     */
    private ArrayList<String> parseValue (Map<String, ArrayList<String>> locators, String elementName) {
        if (locators == null) {
            return elementLocators;
        }

        for (String s : locators.keySet()) {
            if (s.equals(elementName)) {
                this.elementBy = locators.get(s).get(0);
                this.elementSelector = locators.get(s).get(1);
                this.elementName = s;
                setLocators();
            }
        }

        return elementLocators;
    }

    /**
     * Sets the locators
     */
    private void setLocators () {
        elementLocators.add(elementBy);
        elementLocators.add(elementSelector);
    }

    /**
     * Self-explanatory
     *
     * @return - string
     */
    public String toString () {
        return this.elementBy + ", " + this.elementSelector;
    }
}

