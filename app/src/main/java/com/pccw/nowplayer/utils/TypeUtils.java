package com.pccw.nowplayer.utils;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.gson.JsonElement;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Kriz on 24/6/15.
 */
public class TypeUtils {


    public static int dpToPx(Context context, int dp) {
        if (context == null) return dp;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * displayMetrics.density);
        return px;
    }

    public static String encodeURIComponent(String txt) {
        String result = null;

        try {
            result = URLEncoder.encode(txt, "UTF-8")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = "";
        }

        return result;
    }

    public static Spanned fromHtml(String src) {
        if (src == null) return null;
        return Html.fromHtml(src);
    }

    /**
     * Test for type. Type should be structured like MIME type like main-type/sub-type so that
     * if thisType = "text/plain" calling isSubTypeOf("text") and isSubTypeOf("text/plain") will return true.
     *
     * @param thisType
     * @param thatType to test against thisType. Can be partial type.
     * @return true if thisType is same or sub-type of thatType
     */
    public static boolean isSameOrSubTypeOf(String thisType, String thatType) {
        if (!TextUtils.isEmpty(thisType) && thisType.equals(thatType)) return true;
        if (thisType == null) return false;
        return thisType.startsWith(thatType);
    }

    /**
     * if total same return true
     * if server type start with given type also return true
     *
     * @param serverType server codetype
     * @param codeType   given codetype
     * @return
     */
    public static boolean isType(String serverType, String codeType) {
        if (TextUtils.equals(serverType, codeType)) return true;
        if (serverType == null) return false;
        return serverType.startsWith(codeType);
    }

    public static int pxToDp(Context context, int px) {
        if (context == null) return px;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / displayMetrics.density);
        return dp;
    }

    public static boolean toBoolean(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Number) {
            Number num = (Number) value;
            return (num.intValue() != 0);
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            String str = value.toString().trim();
            if (str.length() == 0) return defaultValue;
            char cha = str.charAt(0);
            return (cha == 't' || cha == 'T' || cha == 'y' || cha == 'Y' || cha == '1');
        }
    }

    public static double toDouble(JsonElement je, double defVal) {
        try {
            return je.getAsDouble();
        } catch (Exception ex) {
            return defVal;
        }
    }

    public static double toDouble(Object obj, double defaultValue) {
        try {
            if (obj == null) {
                return defaultValue;
            } else if (obj instanceof Number) {
                return ((Number) obj).doubleValue();
            } else {
                return Double.parseDouble(obj.toString());
            }
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static <T extends Enum<T>> T toEnum(Class<T> c, String name, T defaultValue) {
        if (c != null && name != null) {
            try {
                return Enum.valueOf(c, name.trim().toUpperCase());
            } catch (Exception ex) {
            }
        }
        return defaultValue;
    }

    public static float toFloat(Object obj, float defaultValue) {
        try {
            if (obj == null) {
                return defaultValue;
            } else if (obj instanceof Number) {
                return ((Number) obj).floatValue();
            } else {
                return Float.parseFloat(obj.toString());
            }
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static String toHexString(byte[] bytes) {
        if (bytes == null) return null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static int toInt(Object obj, int defaultValue) {
        try {
            if (obj == null) {
                return defaultValue;
            } else if (obj instanceof Number) {
                return ((Number) obj).intValue();
            } else {
                return Integer.parseInt(obj.toString());
            }
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static long toLong(JsonElement je, long defVal) {
        try {
            return je.getAsLong();
        } catch (Exception ex) {
            return defVal;
        }
    }

    public static String toString(JsonElement je) {
        try {
            return je.getAsString();
        } catch (Exception ex) {
            return null;
        }
    }

    public static URL toURL(String url) {
        if (url == null) return null;
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL toURL(JsonElement je) {
        try {
            return toURL(je.getAsString());
        } catch (Exception ex) {
            return null;
        }
    }

    public static Uri toUri(String uri) {
        try {
            return Uri.parse(uri);
        } catch (Exception ignored) {
            return null;
        }
    }
}