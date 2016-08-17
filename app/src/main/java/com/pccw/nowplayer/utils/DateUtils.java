package com.pccw.nowplayer.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by kriz on 2016-07-18.
 */
public class DateUtils {
    public static int compare(Date a, Date b) {
        if (a == b) return 0;
        if (a == null && b != null) return -1;
        if (a != null && b == null) return 1;
        return a.compareTo(b);
    }

    public static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }

    public static boolean isTomorrow(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);
        return isSameDay(date, gc.getTime());
    }

    public static boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        if (c1.get(Calendar.DATE) == c2.get(Calendar.DATE)) {
            return true;
        } else {
            return false;
        }
    }
}
