package com.pccw.nowplayer.utils;

import android.text.TextUtils;

import com.pccw.nowtv.nmaf.utilities.NMAFLanguageUtils;

import java.io.Serializable;

/**
 * Created by kriz on 17/5/2016.
 */
public class LString implements Serializable {
    protected String chinese;
    protected String english;

    public static LString make(String eng, String chi) {
        LString ret = new LString();
        ret.english = eng;
        ret.chinese = chi;
        return ret;
    }

    public String getChineseString() {
        return chinese;
    }

    public String getEnglishString() {
        return english;
    }

    public String getString(String locale) {
        if (locale != null && locale.startsWith("en"))
            return !TextUtils.isEmpty(english) ? english : chinese;
        if (locale != null && locale.startsWith("zh"))
            return !TextUtils.isEmpty(chinese) ? chinese : english;
        return english != null ? english : chinese;
    }

    public String toString() {
        return getString(NMAFLanguageUtils.getSharedInstance().getLanguage());
    }
}
