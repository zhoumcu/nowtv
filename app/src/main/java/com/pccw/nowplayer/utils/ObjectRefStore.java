package com.pccw.nowplayer.utils;

import java.util.HashMap;

/**
 * Created by kriz on 2015-05-12.
 */
public class ObjectRefStore {
    final HashMap<String, Ref<?>> store = new HashMap<String, Ref<?>>();


    public Object get(String key) {
        Ref<?> ref = store.get(key);
        return ref == null ? null : ref.get();
    }


    @SuppressWarnings("unchecked")
    public <T> T getAs(String key, Class<T> cls) {
        if (cls == null) return null;
        Object obj = get(key);
        return cls.isInstance(obj) ? (T) obj : null;
    }


    public Object pop(String key) {
        Ref<?> ref = store.get(key);
        if (ref != null) store.remove(key);
        return ref == null ? null : ref.get();
    }


    /**
     * Pop the object identified by key. Note that the object will be removed even if the object is not of the expected
     * class.
     *
     * @param key Key of object
     * @param cls Expected class of object
     * @return Object for the key
     */
    @SuppressWarnings("unchecked")
    public <T> T popAs(String key, Class<T> cls) {
        if (cls == null) return null;
        Object obj = pop(key);
        return cls.isInstance(obj) ? (T) obj : null;
    }


    public String put(Object obj, boolean strongRef) {
        String key = MathUtil.randomKey();
        store.put(key, Ref.create(obj, strongRef));
        return key;
    }


    /**
     * Put the object as the key. Note that null key is not supported.
     *
     * @param key       Key for the object
     * @param obj       The object to store
     * @param strongRef Use strong reference
     * @return true if stored
     */
    public boolean put(String key, Object obj, boolean strongRef) {
        if (key == null) return false;
        store.put(key, Ref.create(obj, strongRef));
        return true;
    }


    public String putStrong(Object obj) {
        return put(obj, true);
    }


    public String putWeak(Object obj) {
        return put(obj, false);
    }
}
