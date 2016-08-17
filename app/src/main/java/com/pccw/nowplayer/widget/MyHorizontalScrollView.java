package com.pccw.nowplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by Swifty on 5/13/2016.
 */
public class MyHorizontalScrollView extends HorizontalScrollView {
    private int initialPosition;

    private int newCheck = 100;
    private static final String TAG = "MyScrollView";

    public interface OnScrollStoppedListener{
        void onScrollStopped();
    }

    private OnScrollStoppedListener onScrollStoppedListener;

    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    Runnable scrollerTask = new Runnable() {

        public void run() {

            int newPosition = getScrollX();
            if(initialPosition - newPosition == 0){//has stopped

                if(onScrollStoppedListener!=null){

                    onScrollStoppedListener.onScrollStopped();
                }
            }else{
                initialPosition = getScrollX();
                MyHorizontalScrollView.this.postDelayed(scrollerTask, newCheck);
            }
        }
    };
    public void setOnScrollStoppedListener(MyHorizontalScrollView.OnScrollStoppedListener listener){
        onScrollStoppedListener = listener;
    }

    public void startScrollerTask(){
        initialPosition = getScrollX();
        MyHorizontalScrollView.this.postDelayed(scrollerTask, newCheck);
    }
}