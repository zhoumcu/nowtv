package com.pccw.nowplayer.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Helper class that can validate {@link java.util.Collections}, array and other types
 */
public class Validations {
    /**
     * Validates if the given {@link Collection} is empty or null
     *
     * @param list to validator
     * @param <T>
     * @return true if empty or null
     */
    public static <T> boolean isEmptyOrNull(Collection<T> list) {
        return list == null || list.size() == 0;
    }

    /**
     * Validates if the given array is empty or null
     *
     * @param array to validator
     * @param <T>
     * @return true if empty or null
     */
    public static <T> boolean isEmptyOrNull(final T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Validates if the given {@link Map} is empty or null
     *
     * @param map to validator
     * @param <K, V>
     * @return true if empty or null
     */
    public static <K, V> boolean isEmptyOrNull(final Map<K, V> map) {
        return map == null || map.size() == 0;
    }

    /**
     * Validates if the given {@link CharSequence} is a valid email address
     *
     * @param string to validate
     * @return true if input matches the email address pattern
     */
    public final static boolean isEmail(CharSequence string) {
        return !TextUtils.isEmpty(string) && android.util.Patterns.EMAIL_ADDRESS.matcher(string).matches();
    }

    /**
     * Validates if the given {@link String} is empty or null or "null"
     *
     * @return true if empty or null
     */
    public static boolean isJsonStringEmpty(final String string) {
        return TextUtils.isEmpty(string) || "null".equals(string);
    }

    /**
     * Returns if the object is of the type {@link List} and class
     *
     * @param object to validate
     * @param clazz  to check type for
     * @return true if object is type of the expected list
     */
    public static boolean isListOfType(final Object object, final Class clazz) {
        if (object instanceof List) {
            final List<?> rawList = (List<?>) object;

            for (final Object rawObject : rawList) {
                if (null != rawObject) {
                    return (clazz.isAssignableFrom(rawObject.getClass()));
                }
            }
        }

        return false;
    }

    public static boolean isEmptyOrNull(JSONObject superProperties) {
        return superProperties == null || superProperties.length() < 1;
    }

    public static boolean isEmptyOrNull(JSONArray experimentsArray) {
        return experimentsArray == null || experimentsArray.length() < 1;
    }

    public static boolean isTrue(Boolean isSubscribe) {
        return isSubscribe != null && isSubscribe;
    }

    public static boolean isFalse(Boolean isSubscribe) {
        return isSubscribe != null && !isSubscribe;
    }
}
