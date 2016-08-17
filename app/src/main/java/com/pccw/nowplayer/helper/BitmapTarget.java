package com.pccw.nowplayer.helper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Kevin on 2016/5/27.
 */
public class BitmapTarget implements Target {

    Toolbar toolbar;

    public BitmapTarget(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        toolbar.setLogo(new BitmapDrawable(bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
