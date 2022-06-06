package skynet.extensions;

import skynet.utils.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringExtensions {
    /**
     * Checks either or not a string variable is null or empty
     *
     * @param str - string
     * @return - true if empty, false otherwise
     */
    public static boolean isEmpty(String str) {
        try {
            return (str == null || str.trim().isEmpty());
        } catch (Exception ex) {
            return true;
        }
    }

    /**
     * Replaces the given chars in array with the char one
     *
     * @param str   - string to find into
     * @param chars - char array that need to be changed
     * @param c     - char to change into
     * @return - returns converted string
     */
    public static String replaceChars(String str, char[] chars, char c) {
        StringBuilder newStr = new StringBuilder();
        char[] strChars = str.toCharArray();
        char lastAppended = (char) 0;
        for (int i = 0; i < strChars.length; i++) {
            boolean found = false;
            for (int j = 0; j < chars.length; j++) {
                if (strChars[i] == chars[j]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                newStr.append(strChars[i]);
                lastAppended = strChars[i];
            } else {
                if (lastAppended != c) {
                    newStr.append(c);
                    lastAppended = c;
                }
            }
        }
        return newStr.toString();
    }

    /**
     * Gets everything between the two given strings from a string
     *
     * @param str            - string
     * @param strFrom        - start of match
     * @param strTo          - end of match
     * @param throwException - if true, throws exception if nothing is found
     * @return - match string
     * @throws Exception - throws exception
     */
    public static String between(String str, String strFrom, String strTo, Boolean... throwException) throws Exception {
        if ((StringExtensions.isEmpty(str) || str.length() <= strFrom.length() + strTo.length()) && throwException[0]) {
            Logger.exception(String.format("Invalid string given to parse values:\n " + "Given: %s\n" + "Parse from '%s' to '%s'", str, strFrom, strTo));
        }

        if (!str.contains(strFrom) || !str.contains(strTo)) {
            return null;
        }

        String valueFound = null;
        int indexStart, indexEnd;

        List<Integer> allIndexOf = allIndexOf(str, strTo);
        boolean exit = false;

        while (!exit) {
            indexStart = str.indexOf(strFrom);
            if (allIndexOf.size() != 0 && indexStart != -1 && indexStart > allIndexOf.get(allIndexOf.size() - 1)) {
                indexEnd = 0;
            } else {
                int finalIndexStart = indexStart;
                indexEnd = allIndexOf.stream().filter(i -> i > 0 && i > finalIndexStart).findFirst().orElse(0);
            }

            if (indexStart == -1 && indexEnd == -1) {
                if (throwException.length == 0 || throwException[0]) {
                    Logger.exception("Couldn't get valid 'from' or 'to' from given string!");
                }
                return null;
            }
            try {
                valueFound = indexEnd != 0 ? str.substring(indexStart + strFrom.length(), indexEnd) : str.substring(indexEnd + strTo.length());
                exit = true;
            } catch (Exception e) {
                Logger.exception(e.getMessage());
            }
        }
        return valueFound;
    }

    /**
     * Gets everything between the two given strings from a string
     *
     * @param str            - string
     * @param strFrom        - start of match
     * @param strTo          - end of match
     * @param throwException - if true, throws exception if nothing if found
     * @return - List of all match strings
     * @throws Exception - throws exception
     */
    public static List<String> betweenArray(String str, String strFrom, String strTo, Boolean... throwException) throws Exception {
        List<String> valueFound = new ArrayList<>();

        if ((StringExtensions.isEmpty(str) || str.length() <= strFrom.length() + strTo.length())) {
            String format = String.format("Invalid string given to parse values:\n Given: %s\n" + "Parse from '%s' to '%s'", str, strFrom, strTo);
            if (throwException[0]) {
                Logger.exception(format);
            }
            Logger.warn(format);
            return valueFound;
        }

        int indexStart, indexEnd;

        List<Integer> allIndexOf = allIndexOf(str, strTo);

        indexStart = str.indexOf(strFrom);
        for (Integer integer : allIndexOf) {
            if (allIndexOf.size() != 0 && indexStart != -1 && indexStart > allIndexOf.get(allIndexOf.size() - 1)) {
                indexEnd = 0;
            } else {
                indexEnd = integer;
            }

            if (indexStart == -1 && indexEnd == -1) {
                if (throwException[0]) {
                    Logger.exception("Couldn't get valid 'from' or 'to' from given string!");
                }
                return null;
            }
            try {
                valueFound.add(indexEnd != 0 ? str.substring(indexStart + strFrom.length(), indexEnd) : str.substring(indexEnd + strTo.length()));
                indexStart = indexEnd + strTo.length();
            } catch (Exception e) {
                Logger.exception(e.getMessage());
            }
        }
        return valueFound;
    }

    /**
     * Gets all the positions of a given string within a string
     *
     * @param text - string
     * @param str  - string to match
     * @return list with all indexes
     */
    private static List<Integer> allIndexOf(String text, String str) {
        List<Integer> allIndexOf = new ArrayList<>();
        int index = text.indexOf(str);
        while (index != -1) {
            allIndexOf.add(index);
            index = text.indexOf(str, index + str.length());
        }
        return allIndexOf;
    }

    /**
     * Removes a string from another string
     *
     * @param str         - string to remove from
     * @param strToRemove - string to remove
     * @return - returns string without the strToRemove
     * @throws Exception - exception throws upon failure
     */
    public static String remove(String str, String strToRemove) throws Exception {
        if (StringExtensions.isEmpty(str) || str.length() <= strToRemove.length()) {
            Logger.exception(String.format("Invalid string given to parse values:\n " + "Given: %s\n" + "Remove until '%s'", str, strToRemove));
        }

        return str.replace(strToRemove, "");
    }

    /**
     * Removes everything before the given string
     *
     * @param str - string to remove from
     * @param to  - start of the string that has to remain
     * @return - converted string after 'to' (including)
     * @throws Exception - exception throws upon failure
     */
    public static String removeBefore(String str, String to) throws Exception {
        if (StringExtensions.isEmpty(str) || str.length() <= to.length()) {
            Logger.exception(String.format("Invalid string given to parse values:\n " + "Given: %s\n" + "Remove until '%s'", str, to));
        }

        return str.substring(str.indexOf(to) + to.length());
    }

    /**
     * Converts a date string into the given format
     *
     * @param date     - date as string to convert
     * @param toFormat - date format to convert to
     * @return - converted date as string
     */
    public static String dateFormat(String date, String toFormat) {
        //Add 18000 seconds (5 hours) because of the UTC format. Otherwise, you can end up having the previous day formatted
        Instant inst = Instant.parse(date.endsWith("Z") ? date : date + "Z");

        Date d = Date.from(inst);
        return new SimpleDateFormat(toFormat).format(d);
    }

    /**
     * Converts a date string into the given format
     *
     * @param date       - date as string to convert
     * @param fromFormat - date format to convert from
     * @param toFormat   - date format to convert to
     * @return - converted date as string
     * @throws ParseException - throws exception
     */
    public static String dateFormat(String date, String fromFormat, String toFormat) throws ParseException {
        Date d = new SimpleDateFormat(fromFormat).parse(date);
        return new SimpleDateFormat(toFormat).format(d);
    }

    /**
     * Converts a numeric string to format: 450.00 or 1,450.00
     *
     * @param str - String containing numeric value
     * @return - formatted string
     */
    public static String toDoubleLocale(String str) {
        Double dbl = Double.parseDouble(str);
        return String.format("%,.2f", dbl);
    }

    /**
     * Formats a numeric String to appropriate value with currency: 2 -> $2; -200 -> ($200); 1000 -> $1,000; -200.43 -> ($200.34)
     *
     * @param str - String containing numeric value
     * @return - formatted string
     */
    public static String toDecimalAmount(String str, boolean... forceInt) {
        str = StringExtensions.replaceChars(str, new char[]{'$', ')'}, ' ').replace("(", "-");
        str = StringExtensions.removeWhiteSpaces(str);
        Double d = new Double(str);
        DecimalFormat df = new DecimalFormat("0.00;-0.00");

        if (forceInt.length <= 0 || forceInt[0]) {
            if (d % 1 == 0) {
                DecimalFormat df2 = new DecimalFormat("#,##0;-#,##0");
                return df2.format(d);
            }
        }

        return df.format(d);
    }

    /**
     * Converts a numeric string to format: 450 or 1,450
     *
     * @param str - String containing numeric value
     * @return - formatted string
     */
    public static String toIntegerFormat(String str) {
        return NumberFormat.getIntegerInstance().format(Integer.valueOf(str.replaceFirst("[.].*", "")));
    }

    public static String removeWhiteSpaces(String str) {
        return str.replaceAll("\\s+", "");
    }

    public static String removeChars(String str, char[] chars) {
        StringBuilder newStr = new StringBuilder();
        char[] strChars = str.toCharArray();
        for (int i = 0; i < strChars.length; i++) {
            boolean found = false;
            for (int j = 0; j < chars.length; j++) {
                if (strChars[i] == chars[j]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                newStr.append(strChars[i]);
            }
        }
        return newStr.toString();
    }

    public static String formatParagraph(String str) {
        return str.replaceAll("^ +| +$|( )+", "$1").replaceAll("[\n]", "").trim();
    }

    /**
     * Checks if a string is in camel case style after removing all the white spaces
     *
     * @param str - string to match
     * @return - returns true if camel case, false otherwise
     */
    public static boolean isCamelCase(String str) {
        return str.matches("^[a-z]+([A-Z][a-z0-9]+)+");
    }

    /**
     * Checks if a string is in title case style after removing all the white spaces
     *
     * @param str - string to match
     * @return - returns true if title case, false otherwise
     */
    public static boolean isTitleCase(String str) {
        return str.matches("^(?:[A-Z][^\\s]*\\s?|[#])+$");
    }

    /**
     * Converts a camelCase string to camel_case
     *
     * @param str - string to convert
     * @return - converted string
     */
    public static String camelCaseToVarType(String str) {
        StringBuilder newStr = new StringBuilder();
        List<String> arr = new ArrayList<>();
        if (isCamelCase(str)) {
            arr = Arrays.asList(removeWhiteSpaces(str).split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"));
        }

        for (String s : arr) {
            newStr.append(s).append(arr.indexOf(s) == arr.size() - 1 ? "" : "_");
        }
        return newStr.toString().toLowerCase();
    }

    /**
     * Converts a TitleCase string to title_case
     *
     * @param str - string to convert
     * @return - converted string
     */
    public static String titleCaseToVarType(String str) {
        StringBuilder newStr = new StringBuilder();
        List<String> arr = new ArrayList<>();
        if (isTitleCase(str)) {
            arr = Arrays.asList(removeWhiteSpaces(str).split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z]|[#])"));
        }

        for (String s : arr) {
            newStr.append(s).append(arr.indexOf(s) == arr.size() - 1 ? "" : "_");
        }
        return newStr.toString().toLowerCase();
    }

    /**
     * Converts a string to var_type: e.g: Table -> table; Table Header -> table_header
     *
     * @param str - string to convert
     * @return - converted string
     */
    public static String toVarType(String str) {
        return replaceChars(str, new char[]{' ', '/', '\\', '-'}, '_').toLowerCase();
    }

    /**
     * Convert Given String to Camel Case i.e.
     * Capitalize first letter of every word to upper case
     *
     * @param str - string to convert to camel case
     * @return - camelCase string
     */
    public static String camelCase(String str) {
        StringBuilder builder = new StringBuilder(str);
        // Flag to keep track if last visited character is a
        // white space or underline or not
        boolean split;

        // Iterate String from beginning to end.
        for (int i = 0; i < builder.length(); i++) {
            char ch = builder.charAt(i);

            if (i == 0) {
                builder.setCharAt(i, Character.toLowerCase(ch));
            } else {
                split = ch == ' ' || ch == '_';
                if (split) {
                    char next = builder.charAt(i + 1);
                    if ((next >= 'a' && next <= 'z') || (next >= 'A' && next <= 'Z')) {
                        builder.setCharAt(i + 1, Character.toUpperCase(next));
                    }
                    builder.deleteCharAt(i);
                }
            }
        }

        return builder.toString();
    }

    /**
     * Extracts all the digits from a given string
     * <p>
     * Remarks: works with negative, float and locale numbers
     * e.g: -122.00, 120.00, 50, -100, 1,556, -1,899.96
     *
     * @param str - string
     * @return - all the digits from a string
     * @throws Exception - throws exception
     */
    public static String extractDigits(String str) throws Exception {
        return str.replaceAll("[^-?\\d | -?\\d+,?\\d+ | -?\\d+,?\\d.]+", "").trim();
    }

    /**
     * Returns the group by index from multiple matched groups (using regular expressions)
     *
     * @param string     - the string from where the match shall be extracted
     * @param regex      - the regular expression used to make the extraction. recommended to use () for group segmentations.
     *                   online regular expression tester for java: https://www.freeformatter.com/java-regex-tester.html
     * @param groupIndex - the index of the group which will be extracted
     * @return returnedMatch - the group referenced with the index from the matched items
     * @implNote in case that the match is not found will return the regex class exceptions
     */
    public static String getMatchedGroupByIndexFromAString(String string, String regex, int groupIndex) {
        String returnedMatch = null;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            returnedMatch = matcher.group(groupIndex);
        }
        return returnedMatch;
    }
}