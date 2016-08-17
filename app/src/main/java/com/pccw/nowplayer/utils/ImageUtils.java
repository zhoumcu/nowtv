package com.pccw.nowplayer.utils;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.model.node.Node;
import com.squareup.picasso.Callback;

/**
 * Created by Kriz on 19/8/15.
 */
public class ImageUtils {
    public static void loadImage(ImageView target, String src) {
        DrawableUtils.loadImage(target, src, true, 0);
    }

    public static void loadImage(ImageView target, String src, @DrawableRes int placeHolderDrawable) {
        DrawableUtils.loadImage(target, src, true, placeHolderDrawable);
    }

    public static void loadImage(ImageView target, String src, @DrawableRes int placeHolderDrawable, Callback callback) {
        DrawableUtils.loadImage(target, src, true, placeHolderDrawable, callback);
    }

    public static void loadImage(final ImageView target, String src, final String src2, @DrawableRes final int placeHolderDrawable) {
        DrawableUtils.loadImage(target, src, true, placeHolderDrawable, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                loadImage(target, src2, placeHolderDrawable);
            }
        });
    }

    public static void loadChannelImage(final ImageView target, int channelId) {
        target.setImageResource(0);
        if (channelId != 0) {
            loadImage(target, String.format(Constants.CHANNEL_IMG_URL, channelId), String.format(Constants.CHANNEL_IMG_URL2, channelId), R.drawable.placeholder);
        }
    }

    public static void changeImageSaturation(ImageView imageView, int i) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(i);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

    public static void loadImage(ImageView image, Node node) {
        loadImage(image, node, false);
    }

    public static void loadImage(ImageView image, Node node, boolean noImageGone) {
        if (image == null || node == null) return;
        if (!TextUtils.isEmpty(node.getImageUrl())) {
            ImageUtils.loadImage(image, node.getImageUrl(), R.drawable.placeholder);
        } else if (node.getChannelId() != 0) {
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ImageUtils.loadChannelImage(image, node.getChannelId());
        } else {
            if (noImageGone) {
                image.setVisibility(View.GONE);
            }
            image.setImageResource(0);
        }
    }
}
