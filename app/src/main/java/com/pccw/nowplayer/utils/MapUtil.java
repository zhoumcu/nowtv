package com.pccw.nowplayer.utils;

import android.text.TextUtils;

import com.pccw.nowtv.nmaf.utilities.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapUtil {

    private static final String TAG = MapUtil.class.getSimpleName();

    public static List<Object> getList(Map<String, Object> map, String keyPath, String separator) {
        Object obj = getObject(map, keyPath, separator);
        return obj instanceof List ? (List<Object>) obj : null;
    }

    public static Object getListElement(Map<String, Object> map, String arrayNotation) {
        if (map == null || TextUtils.isEmpty(arrayNotation)) return null;
        int firstBracket = arrayNotation.indexOf("[");
        if (firstBracket < 0) return map.get(arrayNotation);

        String key = arrayNotation.substring(0, firstBracket);
        Object obj = getList(map, key, null);
        if (obj == null) return null;

        Pattern ptn = Pattern.compile("\\[(\\d+)\\]");
        Matcher m = ptn.matcher(arrayNotation);


        while (m.find()) {
            int start = m.start() + 1;
            int end = m.end() - 1;

            String idxStr = arrayNotation.substring(start, end);
            int idx = TypeUtils.toInt(idxStr, -1);
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                obj = (idx >= 0 && idx < list.size() ? list.get(idx) : null);
                if (obj == null) return null;
            } else {
                return null;
            }
        }
        return obj;
    }

    public static Object getObject(Map<String, Object> map, String path, String separator) {
        if (map == null || TextUtils.isEmpty(path)) return null;

        // if non hierarchical
        if (TextUtils.isEmpty(separator)) {
            return map.get(path);
        }

        // if root path
        if (TextUtils.equals(path, separator)) return map;

        // if single-level
        if (path.indexOf(separator) < 0) {
            return MapUtil.getListElement(map, path);
        }

        // multi-level
        String[] components = StringUtils.splitBySeparator(path, separator);
        return MapUtil.getObject(map, components);
    }

    public static Object getObject(Map<String, Object> map, String[] pathComponents) {
        if (map == null || pathComponents == null) return null;

        Object ret = map;
        for (String key : pathComponents) {
            if (!(ret instanceof Map)) {
                if (ret != null)
                    Log.w(TAG, "Error accessing " + key + ": expecting a map, encountered a " + ret.getClass() + ": " + ret);
                return null;
            }
            ret = getListElement((Map<String, Object>) ret, key);
        }
        return ret;
    }

    public static String getString(Map<String, Object> map, String keyPath, String separator) {
        return getString(map, keyPath, null, separator);
    }

    public static String getString(Map<String, Object> map, String keyPath, String defaultValue, String separator) {
        Object obj = getObject(map, keyPath, separator);
        if (obj == null) return defaultValue;
        if (obj instanceof String) return (String) obj;
        return obj.toString();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>sortByValue(Map<K, V> map) {

        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {

            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {

                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {

            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}