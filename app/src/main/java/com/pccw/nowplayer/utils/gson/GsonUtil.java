package com.pccw.nowplayer.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.pccw.nowplayer.utils.ClassResolver;
import com.pccw.nowtv.nmaf.utilities.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonUtil {
    protected static final String TAG = GsonUtil.class.getSimpleName();

    private static class NaturalDeserializer implements JsonDeserializer<Object> {
        @Override
        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            if (json.isJsonNull())
                return null;
            else if (json.isJsonPrimitive())
                return handlePrimitive(json.getAsJsonPrimitive());
            else if (json.isJsonArray())
                return handleArray(json.getAsJsonArray(), context);
            else
                return handleObject(json.getAsJsonObject(), context);
        }

        private Object handleArray(JsonArray json, JsonDeserializationContext context) {
            Object[] array = new Object[json.size()];
            for (int i = 0; i < array.length; i++)
                array[i] = context.deserialize(json.get(i), Object.class);
            return array;
        }

        private Object handleObject(JsonObject json, JsonDeserializationContext context) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet())
                map.put(entry.getKey(), context.deserialize(entry.getValue(), Object.class));
            return map;
        }

        private Object handlePrimitive(JsonPrimitive json) {
            if (json.isBoolean())
                return json.getAsBoolean();
            else if (json.isString())
                return json.getAsString();
            else {
                BigDecimal bigDec = json.getAsBigDecimal();
                // Find out if it is an int type
                try {
                    bigDec.toBigIntegerExact();
                    try {
                        return bigDec.intValueExact();
                    } catch (ArithmeticException e) {
                    }
                    return bigDec.longValue();
                } catch (ArithmeticException e) {
                }
                // Just return it as a double
                return bigDec.doubleValue();
            }
        }
    }

    public static <T> T fromJson(String json, Class<T> cls, boolean returnNullIfError) {
        return fromJson(json, cls, null, null, returnNullIfError);
    }

    public static <T> T fromJson(String json, Class<T> outerClass, Class itemType, Object typeAdapter, boolean returnNullIfError) {
        Gson gson = getGson(itemType, typeAdapter);
        try {
            Log.d(TAG, "Gson json  " + json);
            return gson.fromJson(json, outerClass);
        } catch (JsonSyntaxException e) {
            System.out.println(json);
            e.printStackTrace();
        }
        try {
            if (returnNullIfError) {
                return null;
            } else {
                return outerClass.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T, U> T fromJson(String json, Class<T> outerClass, Class<U> itemClass, String classNameProperty, boolean returnNullIfError) {
        Object typeAdapter = new GenericObjectDeserializer<U>(classNameProperty);
        return fromJson(json, outerClass, itemClass, typeAdapter, returnNullIfError);
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        return fromJson(json, cls, false);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        Gson gson = new Gson();
        return gson.fromJson(json, typeOfT);
    }

    public static <T> List<T> fromJson(String json, Class<T> itemClass, String classNameProperty) {

        Gson gson = getGson(itemClass, new GenericObjectDeserializer<T>(classNameProperty));

        JSONArray jsonArray;
        List<T> items = new ArrayList<>();
        try {
            jsonArray = new JSONArray(json);
        } catch (Exception e) {
            Log.d(TAG, "Invalid JSON array", e);
            e.printStackTrace();
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                T item = null;
                String className = null;
                try {
                    className = obj.getString(classNameProperty);
                    Class c = ClassResolver.getInstance().find(className);
                    item = (T) gson.fromJson(obj.toString(), c);
                } catch (Throwable t) {
                    Log.i(TAG, "Unknown class: " + className);
                }
                if (item != null) {
                    items.add(item);
                }
            } catch (Exception e) {
            }
        }
        return items;
    }

    public static Gson getGson(Class itemType, Object typeAdapter) {
        Gson gson;
        if (typeAdapter != null && itemType != null) {
            gson = new GsonBuilder().registerTypeAdapter(itemType, typeAdapter).setDateFormat("yyyy-MM-dd").create();
        } else {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        }
        return gson;
    }

    public static <T> ArrayList<T> parseMappingJsonToArray(Class<T> baseClass, String json) {
        ArrayList<T> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                String mapItem = jsonArray.get(i).toString();
                Log.d("GsonUtil", mapItem);
                JSONArray jsonArray2 = new JSONArray(mapItem);
                if (jsonArray2.length() == 2) {
                    Class childClass = ClassResolver.getInstance().find(jsonArray2.getString(0));
                    Log.d("GsonUtil", jsonArray2.get(1).toString() + " " + childClass.getName());
                    T childClassClass = (T) GsonUtil.fromJson(jsonArray2.get(1).toString(), childClass);
                    list.add(childClassClass);
                }
            }
        } catch (Exception e) {
            Log.w("GsonUtil", e.toString());
            e.printStackTrace();
        }
        return list;
    }

    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
}
