package com.pccw.nowplayer.utils;


import java.lang.ref.WeakReference;


public class Ref<T> {
    T strongRef;
    WeakReference<T> weakRef;


    public Ref() {
    }


    public Ref(T obj, boolean strongRef) {
        set(obj, strongRef);
    }

    public static <T> Ref<T> create(T obj, boolean strongRef) {
        return new Ref<T>(obj, strongRef);
    }

    public T get() {
        if (strongRef != null) return strongRef;
        if (weakRef != null) return weakRef.get();
        return null;
    }


    public boolean isNotNull() {
        return strongRef != null || (weakRef != null && weakRef.get() != null);
    }


    public boolean isNull() {
        return strongRef == null && (weakRef == null || weakRef.get() == null);
    }


    public Ref<T> makeStrong() {
        if (weakRef != null) {
            T obj = weakRef.get();
            strongRef = obj;
            weakRef = null;
        }
        return this;
    }


    public Ref<T> makeWeak() {
        if (strongRef != null) {
            weakRef = new WeakReference<T>(strongRef);
            strongRef = null;
        }
        return this;
    }


    public void set(T t, boolean usesStrongReference) {
        if (usesStrongReference) {
            strongRef = t;
            weakRef = null;
        } else {
            strongRef = null;
            weakRef = t == null ? null : new WeakReference<T>(t);
        }
    }


    public void setNull() {
        set(null, true);
    }


    public void setStrong(T t) {
        set(t, true);
    }


    public void setWeak(T t) {
        set(t, false);
    }
}
