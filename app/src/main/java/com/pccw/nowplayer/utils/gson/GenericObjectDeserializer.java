package com.pccw.nowplayer.utils.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pccw.nowplayer.utils.ClassResolver;

import java.lang.reflect.Type;

/**
 * Created by Swifty on 11/1/16.
 */
public class GenericObjectDeserializer<T> implements JsonDeserializer<T> {

    private static final String TAG = GenericObjectDeserializer.class.getSimpleName();

    protected String classNameProperty;

    public GenericObjectDeserializer() {
        classNameProperty = "view_type";
    }

    public GenericObjectDeserializer(String classNameProperty) {
        this.classNameProperty = classNameProperty;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = "";
        try {
            type = jsonObject.get(classNameProperty).getAsString();
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            T ret = context.deserialize(json, ClassResolver.getInstance().find(type));
            return ret;
        } catch (ClassNotFoundException cnfe) {
//            throw new JsonParseException("Unknown element type: " + type, cnfe);
            return null;
        }
    }
}
