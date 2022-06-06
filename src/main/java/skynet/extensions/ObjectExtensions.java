package skynet.extensions;

import org.json.JSONArray;
import org.json.JSONObject;

public class ObjectExtensions<T> {

    /**
     * Checks if the provided string is a valid JSONObject
     * @param t - json string
     * @return - true if it is, false otherwise
     */
    public static boolean isValidJSONObject(String t) {
        try {
            new JSONObject(t);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the provided string is a valid JSONArray
     * @param t - json string
     * @return - true if it is, false otherwise
     */
    public static boolean isValidJSONArray(String t) {
        try {
            new JSONArray(t);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
