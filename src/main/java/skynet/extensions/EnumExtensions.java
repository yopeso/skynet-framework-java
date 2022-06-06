package skynet.extensions;

import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Field;

public abstract class EnumExtensions {
    /**
     * Checks if the enum contains a given value
     * @param enumType - enum to check into
     * @param name - name of value to search for
     * @param <E> -
     * @return - true if found, false otherwise
     */
    public static  <E extends  Enum<E>> boolean contains(Class<E> enumType, String name){
        return EnumUtils.isValidEnum(enumType, name);
    }

    /**
     * Checks if the enum contains a given value on a provided field
     * @param enumType - enum to check into
     * @param field - enum field
     * @param value - field value
     * @param <E> -
     * @return - true if found, false otherwise
     *
     * @throws Exception - throws exception if the field does not exist in the enum
     */
    public static  <E extends  Enum<E>> boolean contains(Class<E> enumType, String field, String value) throws Exception {
        return isValidEnumValue(enumType, field, value);
    }

    /**
     * Validates if the provided field contains a provided value
     * @param enumClass - enum to check into
     * @param field - enum field
     * @param value - field value
     * @param <E> -
     * @return - true if found, false otherwise
     * @throws Exception - throws exception if the field does not exist in the enum
     */
    private static <E extends Enum<E>> boolean isValidEnumValue(Class<E> enumClass, String field, String value) throws Exception {
        Object[] objects = enumClass.getEnumConstants();

        for(Object obj : objects) {
            try {
                Field keyField = obj.getClass().getDeclaredField(field);
                keyField.setAccessible(true);

                if(keyField.get(obj).toString().equalsIgnoreCase(value)) {
                    return true;
                }
            } catch (NoSuchFieldException e) {
                return false;
            }
        }
        return false;
    }
}
