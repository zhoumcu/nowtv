package com.pccw.nowplayer.utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by Kriz on 2/3/2016.
 */
public class UIUtils {

    public static <T> T findView(View parent, int id, Class<T> cls) {
        if (parent == null || cls == null) return null;
        View view = parent.findViewById(id);
        if (cls.isInstance(view)) return (T) view;
        return null;
    }

    public static <T> T findView(Activity parent, int id, Class<T> cls) {
        if (parent == null || cls == null) return null;
        View view = parent.findViewById(id);
        if (cls.isInstance(view)) return (T) view;
        return null;
    }

    public static <T> T getSerializable(Fragment fragment, String argName, Class<T> cls) {
        if (fragment == null || TextUtils.isEmpty(argName) || cls == null) return null;

        Bundle args = fragment.getArguments();
        if (args == null) return null;

        Serializable obj = args.getSerializable(argName);
        if (cls.isInstance(obj)) return (T) obj;

        return null;
    }

    public static boolean setHtml(TextView tv, String htmlOrText, boolean update_visibility) {
        boolean is_empty = false;
        if (tv == null) return is_empty;

        // set text
        if (TextUtils.isEmpty(htmlOrText)) {
            // empty string
            tv.setText("");
            is_empty = true;
        } else if (htmlOrText.indexOf("<") == -1) {
            // if "<" is not found, assume it is plain text
            tv.setText(htmlOrText);
        } else {
            Spanned spanned = Html.fromHtml(htmlOrText);
            is_empty = spanned.length() == 0;
            tv.setText(spanned);
        }

        // update visibility
        if (update_visibility) {
            tv.setVisibility(is_empty ? View.GONE : View.VISIBLE);
        }

        return is_empty;
    }

    public static boolean setImage(ImageView imageView, int resId, boolean updateVisibility) {
        boolean hasImage = resId != 0;
        if (imageView == null) return hasImage;
        imageView.setImageResource(resId);
        if (updateVisibility) {
            imageView.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        }
        return hasImage;
    }

    public static boolean setText(TextView tv, String text, View additionalViewToShowOrHide) {
        boolean isEmpty = setText(tv, text, true);
        if (additionalViewToShowOrHide != null) {
            additionalViewToShowOrHide.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
        return isEmpty;
    }

    public static boolean setText(TextView tv, String text, boolean update_visibility) {
        boolean is_empty = TextUtils.isEmpty(text);
        if (tv == null) return is_empty;

        // set text
        if (is_empty) {
            // empty string
            tv.setText("");
        } else {
            // if "<" is not found, assume it is plain text
            tv.setText(text);
        }

        // update visibility
        if (update_visibility) {
            tv.setVisibility(is_empty ? View.GONE : View.VISIBLE);
        }

        return is_empty;
    }
}
