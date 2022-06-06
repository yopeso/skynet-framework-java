package skynet.interactions;

import net.serenitybdd.core.Serenity;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import skynet.utils.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class StaleElementUtils {
    public static WebElement refreshElement(WebElement elem, Object... params) {
        Object refreshedElem = null;
        try {
            String[] locators = elem.toString().split("->");
            for (String s : locators) {
                String newLocator = s.trim().replaceAll("^\\[+", "").replaceAll("]+$", "");
                String[] parts = newLocator.split(": ");
                String key = parts[0];
                String value = parts[1];

                int leftBracketsCount = value.length() - value.replace("[", "").length();
                int rightBracketscount = value.length() - value.replace("]", "").length();

                if (leftBracketsCount - rightBracketscount == 1) { value = value + "]"; }

                if (refreshedElem == null) {
                    refreshedElem = Serenity.getDriver();
                } else {
                    refreshedElem = getWebElement(refreshedElem, key, value, params);
                }
            }
        } catch (Exception e) {
            Logger.error("Can not refresh element: \n EXCEPTION: " + e.getMessage());
        }

        Logger.info("Refreshed element: " + elem.toString());
        return (WebElement) refreshedElem;
    }

    @SuppressWarnings(value = "all")
    public static WebElement refreshElement(By by, Object... params) {
        Object refreshedElem = null;
        try {
            String[] locators = Elements.findElement(by).toString().split("->");
            for (String s : locators) {
                String newLocator = s.trim().replaceAll("^\\[+", "").replaceAll("]+$", "");
                String[] parts = newLocator.split(": ");
                String key = parts[0];
                String value = parts[1];

                int leftBracketsCount = value.length() - value.replace("[", "").length();
                int rightBracketscount = value.length() - value.replace("]", "").length();

                if (leftBracketsCount - rightBracketscount == 1) { value = value + "]"; }

                if (refreshedElem == null) {
                    refreshedElem = Serenity.getDriver();
                } else {
                    refreshedElem = getWebElement(refreshedElem, key, value, params);
                }
            }
        } catch (Exception e) {
            Logger.error("Can not refresh element: \n EXCEPTION: " + e.getMessage());
        }

        assert refreshedElem != null;
        Logger.info("Refreshed element: " + refreshedElem.toString());
        return (WebElement) refreshedElem;
    }

    public static boolean isElementStale(WebElement element) {
        try {
            element.isDisplayed();
            return false;
        } catch (StaleElementReferenceException ex) {
            return true;
        }
    }

    @SuppressWarnings(value = "all")
    private static WebElement getWebElement(Object lastObject, String key, String value, Object... params) {
        List<WebElement> elements = null;
        try {
            By by = getBy(key, value);
            Method m = getCaseInsensitiveDeclaredMethod(lastObject, "findElements");
            elements = (List<WebElement>) m.invoke(lastObject, by);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        assert elements != null;
        WebElement element = params.length <= 0 ? elements.get(0) : elements.get((Integer) params[0]);

        if (params.length > 1) {
            return (Boolean)params[1] ? element.findElement(By.xpath("..")) : element;
        }
        return element;
    }

    private static By getBy(String key, String value) throws InvocationTargetException, IllegalAccessException {
        Class cls = By.class;
        String methodName = key.replace(" ", "");
        Method mth = getCaseInsensitiveStaticDeclaredMethod(cls, methodName);
        return (By) mth.invoke(null, value);
    }

    private static Method getCaseInsensitiveDeclaredMethod(Object obj, String methodName) {
        Method[] methods = obj.getClass().getMethods();
        Method method = null;

        for (Method m : methods) {
            if (m.getName().equalsIgnoreCase(methodName)) {
                method = m;
                break;
            }
        }

        if (method == null) {
            throw new IllegalStateException(String.format("%s Method name is not found for this Class %s",
                                                          methodName, obj
                                                                  .getClass().toString()));
        }
        return method;
    }

    private static Method getCaseInsensitiveStaticDeclaredMethod(Class cls, String methodName) {
        Method[] methods = cls.getMethods();
        Method method = null;

        for (Method m : methods) {
            if (m.getName().equalsIgnoreCase(methodName)) {
                method = m;
                break;
            }
        }

        if (method == null) {
            throw new IllegalStateException(String.format("%s Method name is not found for this Class %s",
                                                          methodName, cls
                                                                  .toString()));
        }
        return method;
    }
}
