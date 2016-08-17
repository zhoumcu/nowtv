package com.pccw.nowplayer.utils;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class DrawableWrapper extends Drawable {
    int mAlpha = 0xFF;
    private Drawable drawable;


    public DrawableWrapper(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable may not be null");
        }
        this.drawable = drawable;
    }

    @Override
    public void clearColorFilter() {
        if (drawable != null) drawable.clearColorFilter();
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) drawable.draw(canvas);
    }

    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        drawable.setAlpha(alpha);
    }

    @TargetApi(11)
    @Override
    public Callback getCallback() {
        if (drawable != null) return super.getCallback();
        return drawable.getCallback();
    }

    @Override
    public int getChangingConfigurations() {
        if (drawable != null) return super.getChangingConfigurations();
        return drawable.getChangingConfigurations();
    }

    @Override
    public void setChangingConfigurations(int configs) {
        if (drawable == null) super.setChangingConfigurations(configs);
        else
            drawable.setChangingConfigurations(configs);
    }

    @Override
    public ConstantState getConstantState() {
        if (drawable != null) return super.getConstantState();
        return drawable.getConstantState();
    }

    @Override
    public Drawable getCurrent() {
        if (drawable != null) return super.getCurrent();
        return drawable.getCurrent();
    }

    public Drawable getDrawble() {
        return drawable;
    }

    @Override
    public int getIntrinsicHeight() {
        if (drawable != null) return super.getIntrinsicHeight();
        return drawable.getIntrinsicHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        if (drawable != null) return super.getIntrinsicWidth();
        return drawable.getIntrinsicWidth();
    }

    @Override
    public int getMinimumHeight() {
        if (drawable != null) return super.getMinimumHeight();
        return drawable.getMinimumHeight();
    }

    @Override
    public int getMinimumWidth() {
        if (drawable != null) return super.getMinimumWidth();
        return drawable.getMinimumWidth();
    }

    @Override
    public int getOpacity() {
        if (drawable != null) return PixelFormat.TRANSLUCENT;
        return drawable.getOpacity();
    }

    @Override
    public boolean getPadding(Rect padding) {
        if (drawable != null) return super.getPadding(padding);
        return drawable.getPadding(padding);
    }

    @Override
    public int[] getState() {
        if (drawable == null) return super.getState();
        return drawable.getState();
    }

    @Override
    public Region getTransparentRegion() {
        if (drawable == null) return super.getTransparentRegion();
        return drawable.getTransparentRegion();
    }

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        if (drawable == null)
            super.inflate(r, parser, attrs);
        else
            drawable.inflate(r, parser, attrs);
    }

    @Override
    public void invalidateSelf() {
        super.invalidateSelf();
        drawable.invalidateSelf();
    }

    @Override
    public boolean isStateful() {
        if (drawable == null) return super.isStateful();
        return drawable.isStateful();
    }

    @TargetApi(11)
    @Override
    public void jumpToCurrentState() {
        if (drawable == null)
            super.jumpToCurrentState();
        else
            drawable.jumpToCurrentState();
    }

    @Override
    public void scheduleSelf(Runnable what, long when) {
        if (drawable == null)
            scheduleSelf(what, when);
        else
            drawable.scheduleSelf(what, when);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        if (drawable == null) super.setBounds(left, top, right, bottom);
        else
            drawable.setBounds(left, top, right, bottom);
    }

    @Override
    public void setBounds(Rect bounds) {
        if (drawable == null) super.setBounds(bounds);
        else
            drawable.setBounds(bounds);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (drawable != null)
            drawable.setColorFilter(cf);
    }

    @Override
    public void setColorFilter(int color, Mode mode) {
        if (drawable != null)
            drawable.setColorFilter(color, mode);
    }

    @Override
    public void setDither(boolean dither) {
        if (drawable != null)
            drawable.setDither(dither);
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        if (drawable != null) {
            drawable.setAlpha(mAlpha);
        }
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        if (drawable != null)
            drawable.setFilterBitmap(filter);
    }

    @Override
    public boolean setState(int[] stateSet) {
        boolean ret = super.setState(stateSet);
        if (drawable == null) return ret;
        return drawable.setState(stateSet);
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        boolean ret = super.setVisible(visible, restart);
        if (drawable == null) return ret;
        return drawable.setVisible(visible, restart);
    }

    @Override
    public void unscheduleSelf(Runnable what) {
        super.unscheduleSelf(what); // if drawable was null, it was scheduled to self
        if (drawable != null)
            drawable.unscheduleSelf(what);
    }
}
