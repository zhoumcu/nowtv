package com.pccw.nowplayer.activity.mynow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ToolBarBaseActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 6/2/2016.
 */
public class LiveChatActivity extends ToolBarBaseActivity {

    @Bind(R.id.scroll_view)
    ScrollView scrollView;
    @Bind(R.id.t_and_c)
    TextView tAndC;
    @Bind(R.id.proceed)
    TextView proceed;

    private final String ModuleName = "now TV_nowplayer_app";
    @Bind(R.id.progress)
    ProgressBar progress;

    private NMAFnowID.GetLiveChatUrlCallback liveChatUrlCallback = new NMAFnowID.GetLiveChatUrlCallback() {
        @Override
        public void onGetLiveChatUrlSuccess(@NonNull final String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress.setVisibility(View.GONE);
                    Intent intent = new Intent(LiveChatActivity.this, LiveChatWebActivity.class);
                    intent.putExtra(Constants.ARG_LIVE_CHAT_URL, s);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onGetLiveChatUrlFailed(@NonNull final Throwable throwable) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(LiveChatActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_live_chat);
        ButterKnife.bind(this);
        tAndC.setMovementMethod(new ScrollingMovementMethod());
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                tAndC.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        tAndC.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                tAndC.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
    }

    @Override
    protected void bindEvents() {
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                NMAFnowID.getSharedInstance().getLiveChatUrl(ModuleName, liveChatUrlCallback);
            }
        });
    }
}
