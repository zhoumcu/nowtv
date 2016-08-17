package com.pccw.nowplayer.utils;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kriz on 4/1/2016.
 */
public class ClassResolver {
    protected static ClassResolver instance;

    public static ClassResolver getInstance() {
        if (instance == null) instance = new ClassResolver();
        return instance;
    }

    protected Map<String, Class> aliasMap = new HashMap<>();

    public ClassResolver addAlias(String alias, Class cls) {
        if (!TextUtils.isEmpty(alias) && cls != null) {
            aliasMap.put(alias, cls);
        }
        return this;
    }

    public Class find(String clsName) throws ClassNotFoundException {
        Class cls = aliasMap.get(clsName);
        if (cls != null) return cls;
        cls = Class.forName(clsName);
        return cls;
    }
}
