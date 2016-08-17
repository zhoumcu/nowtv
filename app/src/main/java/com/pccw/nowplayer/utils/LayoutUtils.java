package com.pccw.nowplayer.utils;

import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by Kriz on 19/8/15.
 */
public class LayoutUtils {
    /**
     * Update the margin properties.
     *
     * @param layoutParams to update its margins
     * @param top          margin, null to keep the original value
     * @param right        margin, null to keep the original value
     * @param bottom       margin, null to keep the original value
     * @param left         margin, null to keep the original value
     * @return true if margin changed
     */
    public static boolean setMargins(ViewGroup.LayoutParams layoutParams, Integer top, Integer right, Integer bottom, Integer left) {

        boolean dirty = false;
        if (!(layoutParams instanceof ViewGroup.MarginLayoutParams)) return dirty;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) layoutParams;

        if (top != null && lp.topMargin != top) {
            dirty = true;
            lp.topMargin = top;
        }
        if (right != null && lp.rightMargin != right) {
            dirty = true;
            lp.rightMargin = right;
        }
        if (bottom != null && lp.bottomMargin != bottom) {
            dirty = true;
            lp.bottomMargin = bottom;
        }
        if (left != null && lp.leftMargin != left) {
            dirty = true;
            lp.leftMargin = left;
        }
        return dirty;
    }

    /**
     * Update the margin properties of the specified <code>view</code> without invoking unnecessary layout request.
     *
     * @param view   to update its margins
     * @param top    margin, null to keep the original value
     * @param right  margin, null to keep the original value
     * @param bottom margin, null to keep the original value
     * @param left   margin, null to keep the original value
     * @return true if layout is updated
     */
    public static boolean setMargins(View view, Integer top, Integer right, Integer bottom, Integer left) {
        if (view == null) return false;
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        boolean dirty = setMargins(lp, top, right, bottom, left);
        if (dirty) view.setLayoutParams(lp);
        return dirty;
    }

    /**
     * Update the margin properties of the specified <code>view</code> without invoking unnecessary layout request.
     *
     * @param view   to update its padding
     * @param top    padding, null to keep the original value
     * @param right  padding, null to keep the original value
     * @param bottom padding, null to keep the original value
     * @param left   padding, null to keep the original value
     */
    public static void setPadding(View view, Integer top, Integer right, Integer bottom, Integer left) {
        ViewUtils.setPadding(view, top, right, bottom, left);
    }

    public static boolean setSize(ViewGroup.LayoutParams lp, Integer width, Integer height) {
        boolean dirty = false;
        if (lp == null) return dirty;

        if (width != null && lp.width != width) {
            dirty = true;
            lp.width = width;
        }
        if (height != null && lp.height != height) {
            dirty = true;
            lp.height = height;
        }
        return dirty;
    }

    /**
     * Update the size of the specified <code>view</code> without invoking unnecessary layout request.
     *
     * @param view   to update its margins
     * @param width  null to keep the original value
     * @param height null to keep the original value
     * @return true if layout is updated
     */
    public static boolean setSize(View view, Integer width, Integer height) {
        if (view == null) return false;
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        boolean dirty = setSize(lp, width, height);
        if (dirty) view.setLayoutParams(lp);
        return dirty;
    }
}
