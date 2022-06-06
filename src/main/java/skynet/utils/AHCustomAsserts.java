package skynet.utils;

import skynet.extensions.StringExtensions;

public class AHCustomAsserts {
    /**
     * Validates that two objects are equal.
     *
     * @param actual   Actual result
     * @param expected Expected result
     * @throws Exception throws exception if not equal
     */
    public static <E> void VerifyEqual(E actual, E expected) throws Exception {
        try {
            if (actual.toString().trim().equals(expected.toString().trim())) {
                Logger.success(String.format("Values are correct. Actual: %s Expected: %s", actual.toString().trim(), expected.toString().trim()));
            }
            else {
                Logger.exception("Values are not equal! \n" + String.format("Actual: %s \n", actual.toString().trim()) + String.format("Expected: %s", expected.toString().trim()));
                //take screenshot
            }
        }
        catch (Exception e) {
            Logger.exception(e.getMessage());
        }
    }

    /**
     * Validates that two objects are equal.
     *
     * @param actual       Actual result
     * @param expected     Expected result
     * @param errorMessage Error message to display in case of failure
     * @throws Exception throws exception if not equal
     */
    public static <E> boolean VerifyEqual(E actual, E expected, String errorMessage) throws Exception {
        try {
            if (actual.toString().trim().equals(expected.toString().trim())) {
                Logger.success(String.format("Values are correct. Actual: %s Expected: %s", actual.toString().trim(), expected.toString().trim()));
                return true;
            }
            else {
                String errMsg = String.format("Actual: %s \n Expected: %s \n errorMessage: %s", actual.toString().trim(), expected.toString().trim(), errorMessage);
                Logger.exception(errMsg);

                //Java needs it even though throwing an exception just before it :D
                return false;
                //take screenshot
            }
        }
        catch (Exception e) {
            Logger.exception(errorMessage + "\n" + e.getMessage());
            return false;
        }
    }

    /**
     * Validates that two objects are equal.
     *
     * @param actual        Actual result
     * @param expected      Expected result
     * @param errorMessage  Error message to display in case of failure
     * @param reportFailure If True, error messages are displayed and exception is thrown
     * @throws Exception throws exception if not equal and reportFailure is true, otherwise it just logs an error
     */
    public static <E> boolean VerifyEqual(E actual, E expected, String errorMessage, boolean reportFailure) throws Exception {
        try {
            if (actual.toString().trim().equals(expected.toString().trim())) {
                Logger.success(String.format("Values are correct. Actual: %s Expected: %s", actual.toString().trim(), expected.toString().trim()));
                return true;
            }
            else {

                String errMsg = "";
                if (! StringExtensions.isEmpty(errorMessage)) {
                    errMsg = String.format("Actual: %s \n Expected: %s \n errorMessage: %s", actual.toString().trim(), expected.toString().trim(), errorMessage);
                    Logger.error(errMsg);
                }

                //todo take screenshot

                if (reportFailure) {
                    Logger.exception(errMsg);
                }

                return false;
            }
        }
        catch (NullPointerException npe) {
            if (! (actual == expected)) {
                if (reportFailure) {
                    Logger.exception(errorMessage + "\n" + npe.getMessage());
                }
                Logger.error(errorMessage);
            }
            Logger.success(String.format("Values are correct. Actual: %s Expected: %s", actual, expected));
        }
        return false;
    }

    /**
     * Validates that two objects are equal.
     *
     * @param actual        Actual result
     * @param expected      Expected result
     * @param validationMsg - what is being validated
     * @param errorMessage  Error message to display in case of failure
     * @param reportFailure If True, error messages are displayed and exception is thrown
     * @throws Exception throws exception if not equal and reportFailure is true, otherwise it just logs an error
     */
    public static <E> boolean VerifyEqual(E actual, E expected, String validationMsg, String errorMessage, boolean reportFailure) throws Exception {
        Logger.debug(validationMsg);
        try {
            if (actual.toString().trim().equals(expected.toString().trim())) {
                Logger.success(String.format("Values are correct. Actual: %s Expected: %s", actual.toString().trim(), expected.toString().trim()));
                return true;
            }
            else {
                String errMsg = "";
                if (! StringExtensions.isEmpty(errorMessage)) {
                    errMsg = String.format("Actual: %s \n Expected: %s \n errorMessage: %s", actual.toString().trim(), expected.toString().trim(), errorMessage);
                    Logger.error(errMsg);
                }

                //todo take screenshot

                if (reportFailure) {
                    Logger.exception(errMsg);
                }

                return false;
            }
        }
        catch (NullPointerException npe) {
            if (! (actual == expected)) {
                if (reportFailure) {
                    Logger.exception(errorMessage + "\n" + npe.getMessage());
                }
                Logger.error(errorMessage);
                return false;
            }
            Logger.success(String.format("Values are correct. Actual: %s Expected: %s", actual, expected));
        }

        return false;
    }

    /**
     * Validates that two objects are equal.
     *
     * @param actual        Actual result
     * @param expected      Expected result
     * @param validationMsg - what is being validated
     * @param errorMessage  Error message to display in case of failure
     * @param reportFailure If True, error messages are displayed and exception is thrown
     * @throws Exception throws exception if not equal and reportFailure is true, otherwise it just logs an error
     */
    public static <E> boolean VerifyEqual(E actual, E expected, String validationMsg, String errorMessage, boolean reportFailure, boolean logError) throws Exception {
        if (logError) {
            Logger.debug(validationMsg);
        }

        String successMsg = String.format("%s \nValues are correct. Actual: %s Expected: %s", validationMsg, actual.toString().trim(), expected.toString().trim());
        try {
            if (actual.toString().trim().equals(expected.toString().trim())) {
                Logger.success(successMsg);
                return true;
            }
            else {
                String errMsg = String.format("Actual: %s \n Expected: %s \n errorMessage: %s", actual.toString().trim(), expected.toString().trim(), errorMessage);
                //take screenshot

                if (reportFailure) {
                    Logger.exception(errMsg);
                }

                if (logError) {
                    Logger.error(errMsg);
                }

                return false;
            }
        }
        catch (NullPointerException npe) {
            if (! (actual == expected)) {
                if (reportFailure) {
                    Logger.exception(errorMessage + "\n" + npe.getMessage());
                }

                if (logError) {
                    Logger.error(errorMessage);
                    return false;
                }
            }
            Logger.success(successMsg);

        }

        return false;
    }

    /**
     * Validates if the first object contains the second one.
     *
     * @param actual   Actual result
     * @param expected Expected result
     * @throws Exception throws exception if not equal
     */
    public static <E> void VerifyCondition(E actual, E expected) throws Exception {
        try {
            if (actual.equals(expected)) {
                Logger.success(String.format("Actual: %s Expected: %s", actual, expected));
            }
            else {
                Logger.error("Values are not correct: \n" + String.format("Actual: %s \n", actual) + String.format("Expected: %s", expected));
                Logger.exception("Values are not equal!");
            }
        }
        catch (NullPointerException e) {
            Logger.exception(e.getMessage());
        }
    }

    /**
     * Validates if the first object contains the second one.
     *
     * @param actual       Actual result
     * @param expected     Expected result
     * @param errorMessage Error message to display in case of failure
     * @throws Exception throws exception if not contains
     */
    public static <E> void VerifyCondition(E actual, E expected, String errorMessage) throws Exception {
        try {
            if (actual.equals(expected)) {
                Logger.success(String.format("Actual: %s Expected: %s", actual.toString().trim(), expected.toString().trim()));
            }
            else {
                String errMsg = String.format("Actual: %s \n Expected: %s \n errorMessage: %s", actual.toString().trim(), expected.toString().trim(), errorMessage);
                Logger.exception(errMsg);
                //take screenshot
            }
        }
        catch (NullPointerException e) {
            Logger.exception(errorMessage + "\n" + e.getMessage());
        }
    }

    /**
     * Validates if the first object contains the second one.
     *
     * @param actual        Actual result
     * @param expected      Expected result
     * @param errorMessage  Error message to display in case of failure
     * @param reportFailure If True, error messages are displayed and exception is thrown
     * @throws Exception throws exception if not equal and reportFailure is true, otherwise it just logs an error
     */
    public static <E> boolean VerifyCondition(E actual, E expected, String errorMessage, boolean reportFailure) throws Exception {
        try {
            if (actual.equals(expected)) {
                Logger.success(String.format("Actual: %s Expected: %s", actual.toString().trim(), expected.toString().trim()));
                return true;
            }
            else {
                String errMsg = "";
                if (! StringExtensions.isEmpty(errorMessage)) {
                    errMsg = String.format("Actual: %s \n Expected: %s \n errorMessage: %s", actual.toString().trim(), expected.toString().trim(), errorMessage);
                    Logger.error(errMsg);
                }

                //todo take screenshot

                if (reportFailure) {
                    Logger.exception(errMsg);
                }
                return false;
            }
        }
        catch (NullPointerException e) {
            Logger.exception(errorMessage + "\n" + e.getMessage());
        }
        return false;
    }

    /**
     * Validates if the first object contains the second one.
     *
     * @param actual        Actual result
     * @param expected      Expected result
     * @param errorMessage  Error message to display in case of failure
     * @param reportFailure If True, error messages are displayed and exception is thrown
     * @throws Exception throws exception if not equal and reportFailure is true, otherwise it just logs an error
     */
    public static <E> boolean VerifyCondition(E actual, E expected, String validationMsg, String errorMessage, boolean reportFailure) throws Exception {
        Logger.debug(validationMsg);
        try {
            if (actual.toString().trim().contains(expected.toString().trim())) {
                Logger.success(String.format("Actual: %s Expected: %s", actual.toString().trim(), expected.toString().trim()));
                return true;
            }
            else {
                String errMsg = "";
                if (! StringExtensions.isEmpty(errorMessage)) {
                    errMsg = String.format("Actual: %s \n Expected: %s \n errorMessage: %s", actual.toString().trim(), expected.toString().trim(), errorMessage);
                    Logger.error(errMsg);
                }

                //todo take screenshot

                if (reportFailure) {
                    Logger.exception(errMsg);
                }
                return false;
            }
        }
        catch (NullPointerException e) {
            Logger.exception(errorMessage + "\n" + e.getMessage());
        }
        return false;
    }
}
