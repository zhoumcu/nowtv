package com.pccw.nowplayer.service;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by Kevin on 2016/3/30.
 */
public class ShowBlur implements ShowBlurInterface {

    private ShowBlurInterface.BlurVariables blurVariables;
    private ImageView targetView;

    public ShowBlur(ShowBlurInterface.BlurVariables blurVariables) {
        this.blurVariables = blurVariables;
    }

    public ShowBlur() {
        this.blurVariables = new BlurVariables(2, 8);
    }

    /**
     * blur image
     */
    @Override
    public ShowBlur blurImage() {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... voids) {
                return FastBlurUtil.doBlur(blurVariables.bufferBitmap, blurVariables.blurRadius, true);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                blurVariables.bufferBitmap = bitmap;
                if (targetView != null) {
                    targetView.setImageBitmap(blurVariables.bufferBitmap);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return this;
    }

    /**
     * compress bitmap
     *
     * @return
     */
    @Override
    public ShowBlur reduceImage(Bitmap bitmap) {
        if (blurVariables.bufferBitmap != null) {
            blurVariables.bufferBitmap.recycle();
        }
        if (blurVariables.scaleRatio <= 1) {
            blurVariables.bufferBitmap = bitmap.copy(bitmap.getConfig(), true);
        } else {
            blurVariables.bufferBitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / blurVariables.scaleRatio,
                    bitmap.getHeight() / blurVariables.scaleRatio,
                    false);
        }
        return this;
    }

    /**
     * compress bitmap
     *
     * @param id
     * @param resources
     * @return
     */
    @Override
    public ShowBlur reduceImage(int id, Resources resources) {
        Bitmap originBitmap = BitmapFactory.decodeResource(resources,
                id);
        return reduceImage(originBitmap);
    }

    /**
     * @param imageView
     */
    @Override
    public ShowBlur setImageView(ImageView imageView) {
        targetView = imageView;
        targetView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return this;
    }


}
