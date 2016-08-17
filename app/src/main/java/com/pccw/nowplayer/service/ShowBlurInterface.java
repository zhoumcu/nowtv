package com.pccw.nowplayer.service;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Kevin on 2016/3/31.
 */
public interface ShowBlurInterface {

    /**
     *
     * @param id
     * @param resources
     * @return
     */
    ShowBlur reduceImage(int id, Resources resources);

    ShowBlur reduceImage(Bitmap bitmap);

    /**
     *
     *
     */
    ShowBlur blurImage();

    /**
     *
     *
     */
    ShowBlur setImageView(ImageView imageView);




    /**
     */
    class BlurVariables{
        //bitmap ratio value
         int scaleRatio ;
        //blur radius
         int blurRadius ;
        //need blur bitmap
         Bitmap bufferBitmap;
        public BlurVariables(int scaleRatio,int blurRadius){
            this.scaleRatio = scaleRatio;
            this.blurRadius = blurRadius;
        }

    }



}
