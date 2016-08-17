package com.pccw.nowplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Kriz on 21/1/2016.
 */
public class NetworkBitmapDrawable extends DrawableWrapper {
    final TaskStatus loading = new TaskStatus();
    Context context;
    Uri uri;

    public NetworkBitmapDrawable(Context context, int backupColor, Uri uri) {
        this(context, new ColorDrawable(backupColor), uri);
    }

    public NetworkBitmapDrawable(Context context, Drawable backupDrawable, Uri uri) {
        super(backupDrawable);
        this.uri = uri;
        this.context = context;
        load();
    }

    public static boolean isNetworkConnected(Context context) {

        if (context == null) return false;

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivityManager) {
            final NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (loading.shouldStart() && loading.lastTrialOver(5000) && isNetworkConnected(context)) {
            load();
        }
    }

    public void load() {
        if (!loading.shouldStart()) return;
        loading.started();

        Picasso.with(context).load(uri).into(new Target() {
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                loading.failed();
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                loading.finished();
                BitmapDrawable d = new BitmapDrawable(context.getResources(), bitmap);
                if (d != null) {
                    setDrawable(d);
                    invalidateSelf();
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }
}
