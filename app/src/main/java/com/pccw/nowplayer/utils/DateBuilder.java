package com.pccw.nowplayer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by kriz on 2016-06-19.
 */
public class DateBuilder {

    protected final Calendar cal = Calendar.getInstance();
    protected SimpleDateFormat chiFormatter;
    protected SimpleDateFormat engFormatter;
    protected TimeZone timeZone;

    public DateBuilder() {
        timeZone = TimeZone.getTimeZone("Asia/Hong_Kong");
    }

    public static DateBuilder create() {
        return new DateBuilder();
    }

    public DateBuilder addDays(int offset) {
        cal.add(Calendar.DATE, offset);
        return this;
    }

    public LString format(String engPattern, String chiPattern) {
        return LString.make(formatEnglish(engPattern), formatChinese(chiPattern));
    }

    public String formatChinese(String pattern) {
        setChineseFormat(pattern);
        String ret = chiFormatter.format(getTime());
        return ret;
    }

    public String formatChinese() {
        String ret = chiFormatter == null ? null : chiFormatter.format(getTime());
        return ret;
    }

    public String formatEnglish(String pattern) {
        setEnglishFormat(pattern);
        return formatEnglish();
    }

    public String formatEnglish() {
        String ret = engFormatter == null ? null : engFormatter.format(getTime());
        return ret;
    }

    public Date getTime() {
        return cal.getTime();
    }

    public DateBuilder setTime(long ts) {
        cal.setTime(new Date(ts));
        return this;
    }

    public boolean isAfter(Date d) {
        if (d == null) return false;
        long t = getTime().getTime();
        return (t > d.getTime());
    }

    public boolean isAfterOrEqual(Date d) {
        if (d == null) return false;
        long t = getTime().getTime();
        return (t >= d.getTime());
    }

    public boolean isBefore(Date d) {
        if (d == null) return false;
        long t = getTime().getTime();
        return (t < d.getTime());
    }

    public boolean isBeforeOrEqual(Date d) {
        if (d == null) return false;
        long t = getTime().getTime();
        return (t <= d.getTime());
    }

    public Date parseChinese(String str) {
        if (Is.empty(str)) return null;
        if (chiFormatter == null) return null;
        try {
            return chiFormatter.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public Date parseEnglish(String str) {
        if (Is.empty(str)) return null;
        if (engFormatter == null) return null;
        try {
            return engFormatter.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public DateBuilder setChineseFormat(String pattern) {
        if (chiFormatter == null) {
            chiFormatter = new SimpleDateFormat(pattern, Locale.TAIWAN);
            chiFormatter.setTimeZone(timeZone);
        } else {
            chiFormatter.applyPattern(pattern);
        }
        return this;
    }

    public DateBuilder setEnglishFormat(String pattern) {
        if (engFormatter == null) {
            engFormatter = new SimpleDateFormat(pattern, Locale.US);
            engFormatter.setTimeZone(timeZone);
        } else {
            engFormatter.applyPattern(pattern);
        }
        return this;
    }

    public DateBuilder setTime(Date date) {
        if (date == null) {
            cal.setTime(new Date(0L));
        } else {
            cal.setTime(date);
        }
        return this;
    }

    public DateBuilder trimTime() {

        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return this;
    }
}
