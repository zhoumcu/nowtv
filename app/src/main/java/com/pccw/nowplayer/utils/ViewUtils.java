package com.pccw.nowplayer.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import android.view.ViewTreeObserver;

import com.pccw.nowplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kriz on 30/12/2015.
 */
public class ViewUtils {

    public static <T> T findAscendant(View view, Class<T> targetClass) {
        if (view == null) return null;
        if (targetClass.isInstance(view)) return (T) view;
        ViewParent parent = view.getParent();
        if (!(parent instanceof View)) return null;
        return findAscendant((View) parent, targetClass);
    }

    public static <T> List<T> findViewsFromParent(View root, Class<T> targetClass) {
        List<T> tList = new ArrayList<>();
        if (root == null || targetClass == null) return tList;
        if (targetClass.isInstance(root)) {
            tList.add((T) root);
        }
        if (root instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                tList.addAll(findViewsFromParent(((ViewGroup) root).getChildAt(i), targetClass));
            }
        }
        return tList;
    }


    /**
     * Find view by id string.
     */
    public static View findViewById(View view, String identifier) {
        if (view == null || identifier == null) return null;
        try {
            Context ctx = view.getContext();
            int id = ctx.getResources().getIdentifier(identifier, "id", ctx.getPackageName());
            return view.findViewById(id);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Find descendant views by tag recursively.
     */
    public static ArrayList<View> findViewsWithTag(View root, String tag) {
        ArrayList<View> ret = new ArrayList<>();

        if (root instanceof ViewGroup) {
            // include self if matched
            if (tag != null && tag.equals(root.getTag())) {
                ret.add(root);
            }

            // find matches from children
            ViewGroup rootVg = (ViewGroup) root;
            final int cnt = rootVg.getChildCount();
            for (int i = 0; i < cnt; i++) {
                final View child = rootVg.getChildAt(i);
                if (child instanceof ViewGroup) {
                    ArrayList<View> subviews = findViewsWithTag(child, tag);
                    if (subviews != null) ret.addAll(subviews);
                } else {
                    final Object tagObj = child.getTag();
                    if (tagObj != null && tagObj.equals(tag)) {
                        ret.add(child);
                    }
                }
            }
        } else {
            View v = root.findViewWithTag(tag);
            if (v != null) ret.add(v);
        }
        return ret;
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
        if (view == null) return;

        boolean dirty = false;
        if (top != null && view.getPaddingTop() != top) {
            dirty = true;
        }
        if (right != null && view.getPaddingRight() != right) {
            dirty = true;
        }
        if (bottom != null && view.getPaddingBottom() != bottom) {
            dirty = true;
        }
        if (left != null && view.getPaddingLeft() != left) {
            dirty = true;
        }
        if (dirty) {
            if (top == null) top = view.getPaddingTop();
            if (right == null) right = view.getPaddingRight();
            if (bottom == null) bottom = view.getPaddingBottom();
            if (left == null) left = view.getPaddingLeft();
            view.setPadding(left, top, right, bottom);
        }
    }

    public static void setTextIfEmptyGone(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) textView.setVisibility(View.GONE);
        else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }

    public static void addOnGlobalLayoutListener(final View view, final Runnable runnable) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                runnable.run();
            }
        });
    }

    public static void dismissViewY(final View view) {
        if (view == null) return;
        if (view.getHeight() == 0) return;
        final int height = view.getHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "ObjectAnimation", 1f, 0f).setDuration(300);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                view.getLayoutParams().height = (int) (height * cVal);
                view.requestLayout();
            }
        });
        objectAnimator.start();
    }

    public static void dismissViewX(final View view) {
        if (view == null) return;
        if (view.getWidth() == 0) return;
        final int width = view.getWidth();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "ObjectAnimation", 1f, 0f).setDuration(300);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                view.getLayoutParams().width = (int) (width * cVal);
                view.requestLayout();
            }
        });
        objectAnimator.start();
    }

    public static void showViewX(final View view, final int toX) {
        if (view == null) return;
        if (toX == view.getWidth()) return;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "ObjectAnimation", 0f, 1f).setDuration(300);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                view.getLayoutParams().width = (int) (toX * cVal);
                view.requestLayout();
            }
        });
        objectAnimator.start();
    }

    public static int getColumns(Activity activity, int columnsWidth) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        return width / columnsWidth;
    }

    public static int getColumns(Activity activity) {
        return getColumns(activity, TypeUtils.dpToPx(activity, 110));
    }

    public static void initWatchListEmptyText(Context context, TextView emptyText) {
        ImageSpan imageSpan = new ImageSpan(context, R.drawable.ic_watchlist_off, ImageSpan.ALIGN_BOTTOM) {
            public void draw(Canvas canvas, CharSequence text, int start,
                             int end, float x, int top, int y, int bottom,
                             Paint paint) {
                Drawable b = getDrawable();
                canvas.save();
                int transY = bottom - b.getBounds().bottom;
                // this is the key
                transY -= paint.getFontMetricsInt().descent / 2;
                canvas.translate(x, transY);
                b.draw(canvas);
                canvas.restore();
            }
        };
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.append(context.getString(R.string.press)).append("  ")
                    .append(" ", imageSpan, 0)
                    .append("  ")
                    .append(context.getString(R.string.to_add_favorite_list));
        } else {
            builder = new SpannableStringBuilder();
            builder.append(context.getString(R.string.press))
                    .append("  ")
                    .setSpan(imageSpan,
                            builder.length() - 1, builder.length(), 0);
            builder.append("  ")
                    .append(context.getString(R.string.to_add_favorite_list));
        }
        emptyText.setText(builder);
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        } else {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }
    }
}
