package com.pccw.nowplayer.service;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.pccw.nowplayer.utils.L;
import com.pccw.nowplayer.widget.SwipeDismissTouchListener;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.mediaplayer.NMAFMediaPlayerController;

/**
 * Created by Root on 2016/5/17.
 */
public class FloatVideo {

    private static Context mContext;
    private int type = 0;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams layoutParams;
    private static FloatVideo floatVideo;
    FrameLayout root;
    FrameLayout root2;

    boolean isShow = false;



    int width = 0;
    int height = 0;
    private float x;
    private float y;
    private float startX;
    private float startY;

    private FloatVideo() {

    }

    public static FloatVideo getInstance(Application application) {
        if (floatVideo == null) {
            floatVideo = new FloatVideo();
        }
        mContext = application;
        return floatVideo;
    }


    public void showFloatVideo(FrameLayout frameLayout) {
        remove();
        initWindows();
        isShow = true;

        convert(frameLayout);
        SwipeDismissTouchListener swipeDismissTouchListener = new SwipeDismissTouchListener(root2, "", dismissCallbacks);
        frameLayout.setOnTouchListener(swipeDismissTouchListener);
    }

    private void convert(FrameLayout frameLayout) {
        if (frameLayout.getParent() instanceof ViewGroup) {
            ViewManager view = (ViewManager) frameLayout.getParent();
            view.removeView(frameLayout);
            root2.addView(frameLayout);
            frameLayout.setOnTouchListener(null);
        }
    }


    private void initWindows() {
        if (mContext == null) {
            return;
        }
        root = new FrameLayout(mContext);
        root.setBackgroundColor(Color.RED);
        root2 = new FrameLayout(mContext);

        root2.setBackgroundColor(Color.BLACK);

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        if (Build.VERSION.SDK_INT >= 19) {
//            type = WindowManager.LayoutParams.TYPE_TOAST;
//        } else {
            type = WindowManager.LayoutParams.TYPE_TOAST;
//        }
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = type;
        width = mWindowManager.getDefaultDisplay().getWidth();
        height = mWindowManager.getDefaultDisplay().getHeight();
        layoutParams.format = PixelFormat.TRANSLUCENT;

        int videoWidth = height / 3 * 2;
        layoutParams.width = height;
        layoutParams.height = videoWidth / 16 * 9;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.x = 0;
        layoutParams.y = height / 2 + layoutParams.height;
        mWindowManager.addView(root, layoutParams);


        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(videoWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.width = videoWidth;
        layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        root.addView(root2, layoutParams);


    }

    SwipeDismissTouchListener.DismissCallbacks dismissCallbacks = new SwipeDismissTouchListener.DismissCallbacks() {
        @Override
        public boolean canDismiss(Object token) {
            return true;
        }

        @Override
        public void onDismiss(View view, Object token) {
            remove();
        }
    };


    public void remove() {
        if (isShow) {
            isShow = false;
            mWindowManager.removeView(root);
        } else {
            return;
        }
    }



}
