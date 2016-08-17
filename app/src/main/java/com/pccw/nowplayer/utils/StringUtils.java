package com.pccw.nowplayer.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kriz on 27/1/2016.
 */
public class StringUtils {
    static final String imageHost = "http://images.now-tv.com/shares/vod_images/";

    public static int compare(String a, String b) {
        if (a == b) return 0;
        if (a == null && b != null) return -1;
        if (a != null && b == null) return 1;
        return a.compareTo(b);
    }

    public static String concat(String separator, String str1, String str2) {
        if (str1 == null) str1 = "";
        if (str2 == null) str2 = "";
        if (separator == null) separator = "";
        boolean empty1 = TextUtils.isEmpty(str1);
        boolean empty2 = TextUtils.isEmpty(str2);
        if (empty1 && empty2) return "";
        if (!empty1 && empty2) return str1;
        if (empty1 && !empty2) return str2;
        return str1 + separator + str2;
    }

    public static String formatFloat(float rentPrice) {

        if (rentPrice == (long) rentPrice)
            return String.format("%d", (long) rentPrice);
        else
            return String.format("%s", rentPrice);

    }

    public static String getAbsUrl(String url) {
        if (TextUtils.isEmpty(url)) return null;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        } else {
            return imageHost + url;
        }
    }

    public static String getByteSizeDescription(long size, int decimalPlaces) {

        double KB = 1024;
        double MB = KB * 1024;
        double GB = MB * 1024;
        double TB = GB * 1024;

        double bytes = 1.0 * size;
        double kilobytes = bytes / KB;
        double megabytes = bytes / MB;
        double gigabytes = bytes / GB;
        double terabytes = bytes / TB;

        DecimalFormat f = new DecimalFormat();
        f.setMaximumFractionDigits(decimalPlaces);
        f.setGroupingUsed(false);

        String formatted;
        if (terabytes >= 1.0)
            formatted = f.format(terabytes) + " TB";
        else if (gigabytes >= 1.0)
            formatted = f.format(gigabytes) + " GB";
        else if (megabytes >= 1.0)
            formatted = f.format(megabytes) + " MB";
        else if (kilobytes >= 1.0)
            formatted = f.format(kilobytes) + " KB";
        else
            formatted = ((int) bytes) + " bytes";

        return formatted;
    }

    public static String getCurrentLanguage(Context context, String en, String zh_tw) {
        return LocaleUtils.getCurrentLocale(context) == LocaleUtils.Language.ENGLISH ? en : zh_tw;
    }

    public static String getCurrentUrl(Context context, String en, String zh_tw) {
        String url = getCurrentLanguage(context, en, zh_tw);
        return getAbsUrl(url);
    }

    public static String is2String(InputStream is) {
        Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0);
    }

    public static String join(Collection<?> objects, String separator) {
        if (objects == null) return "";

        StringBuilder buf = new StringBuilder();
        boolean isFirst = true;
        for (Object obj : objects) {
            if (!isFirst) buf.append(separator);
            isFirst = false;
            buf.append(obj);
        }

        return buf.toString();
    }

    public static String join(Object[] objects, String separator) {
        if (objects == null) return "";

        StringBuilder buf = new StringBuilder();
        boolean isFirst = true;
        for (Object obj : objects) {
            if (!isFirst) buf.append(separator);
            isFirst = false;
            buf.append(obj);
        }

        return buf.toString();
    }

    public static String join(String separator, Collection<String> parts) {
        if (parts == null) return null;
        if (separator == null) separator = "";

        StringBuilder buf = new StringBuilder();

        boolean first = true;
        for (String part : parts) {
            if (first) {
                first = false;
            } else {
                buf.append(separator);
            }
            if (part != null) buf.append(part);
        }

        return buf.toString();
    }

    public static String join(String separator, Object... objects) {
        if (objects == null) return "";

        StringBuilder buf = new StringBuilder();
        boolean isFirst = true;
        for (Object obj : objects) {
            if (!isFirst) buf.append(separator);
            isFirst = false;
            buf.append(obj);
        }

        return buf.toString();
    }

    public static String joinIfNotEmpty(String seperator, String... strings) {
        if (strings == null) return null;
        StringBuilder buf = new StringBuilder();

        boolean first = true;
        for (String str : strings) {
            if (isEmpty(str)) continue;

            if (!first) buf.append(seperator);
            buf.append(str);
            first = false;
        }
        return buf.toString();
    }

    public static int parseInt(String stringInteger, int defaultInteger) {
        try {
            return Integer.parseInt(stringInteger);
        } catch (NumberFormatException e) {
            return defaultInteger;
        }
    }

    /**
     * Split string by a delimiter without using regular expression.
     *
     * @param string    to split
     * @param delimiter to split the string
     * @return
     */
    public static String[] splitBySeparator(String string, String delimiter) {
        if (string == null) return null;
        if (string.length() == 0) {
            return new String[]
                    {""};
        }

        int m = delimiter.length();
        int n = string.length();
        int idx = 0;
        int loc = 0;
        ArrayList<String> list = new ArrayList<String>();
        while (loc >= 0 && idx < n) {
            loc = string.indexOf(delimiter, idx);
            if (loc < 0) {
                list.add(string.substring(idx));
            } else {
                list.add(string.substring(idx, loc));
                idx = loc + m;
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static List<String> splitBySeparator(String string, String delimiter, boolean skipEmptyValue) {

        ArrayList<String> list = new ArrayList<String>();
        if (string == null) return list;

        int m = delimiter.length();
        int n = string.length();
        int idx = 0;
        int loc = 0;

        while (loc >= 0 && idx < n) {
            loc = string.indexOf(delimiter, idx);
            if (loc < 0) {
                String value = string.substring(idx);
                if (!skipEmptyValue || !Is.empty(value)) {
                    list.add(value);
                }
            } else {
                String value = string.substring(idx, loc);
                if (!skipEmptyValue || !Is.empty(value)) {
                    list.add(value);
                }
                idx = loc + m;
            }
        }
        return list;
    }

    /**
     * Split string by a delimiter without using regular expression. The result will be filled into
     * the output array. If there are more elements than the output array, the last output element
     * will contain the rest of the string. If there are fewer elements, the extra elements in the
     * output array will not be modified.
     * <p/>
     * The benefit of using this method is that the number of elements in the output array is known
     * beforehand.
     * <p/>
     * Example: when 2 elements are expected in the source string:
     * String list[] = splitBySeparator(sourceString, ",", new String[2]);
     *
     * @param string
     * @param delimiter
     * @param output
     * @return
     */
    public static String[] splitBySeparator(String string, String delimiter, String[] output) {

        // null case
        if (string == null || output == null) return output;

        // empty string case
        if (string.length() == 0) {
            if (output.length > 0) output[0] = "";
            return output;
        }

        int m = delimiter.length();
        int n = string.length();
        int start = 0;
        int pos = 0;
        int i = 0;
        while (pos >= 0 && start < n && i < output.length) {
            if (i == output.length - 1) {
                // the last element is always the rest of the string
                output[i++] = string.substring(start);
            } else {
                pos = string.indexOf(delimiter, start);
                if (pos < 0) {
                    // delimiter not found
                    output[i++] = string.substring(start);
                } else {
                    // found, advance
                    output[i++] = string.substring(start, pos);
                    start = pos + m;
                }
            }
        }
        return output;
    }

    /**
     * Substitute variables into a string. The variable marker format is:
     * <p/>
     * {@code
     * marker = "${", path, "}";
     * path = id_or_list_element, separator, id_or_list_element;
     * id_or_list_element = identifier|list_element;
     * list_element = identifier, "[", index, "]";
     * }
     *
     * @param string to substitute variable markers with the values provided in the params.
     * @param params values to substitute to the string
     * @return
     */
    public static String substitute(String string, Map<String, Object> params, String separator) {
        if (TextUtils.isEmpty(string)) return string;

        String ret = string;

        // find the substitution marker, if no substitution needed, it's done.
        if (ret.indexOf("${") < 0) return ret;

        // substitute variables
        StringBuilder buf = new StringBuilder();
        int lastPosition = 0;
        boolean success = true;

        Pattern ptn = Pattern.compile("\\$\\{([^\\}]+)\\}");
        Matcher m = ptn.matcher(ret);
        while (m.find()) {
            int outerStart = m.start();
            int outerEnd = m.end();
            buf.append(ret.substring(lastPosition, outerStart));

            String subKey = m.group(1);
            String sub = MapUtil.getString(params, subKey, separator);

            if (sub != null) buf.append(sub);
            lastPosition = outerEnd;
        }
        if (!success) return null;

        // append the remaining part
        if (lastPosition < ret.length()) {
            buf.append(ret.substring(lastPosition));
        }
        ret = buf.toString();

        return ret;
    }
}