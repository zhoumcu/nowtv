package com.pccw.nowplayer.activity.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/7/26.
 */
public class BPLResult extends LinearLayout {
    @Bind(R.id.tv_result1)
    TextView tvResult1;
    @Bind(R.id.tv_result2)
    TextView tvResult2;
    @Bind(R.id.tv_result)
    TextView tvResult;
    @Bind(R.id.ll_result)
    LinearLayout llResult;
    @Bind(R.id.tv_schedule)
    TextView tvSchedule;
    @Bind(R.id.ll_schedule)
    LinearLayout llSchedule;
    @Bind(R.id.wv_result_live)
    WebView wvResultLive;
    @Bind(R.id.ll_btl_result)
    LinearLayout llBtlResult;

    public BPLResult(Context context) {
        super(context);
        init();
    }

    public BPLResult(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BPLResult(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_bpl_result, this);
        ButterKnife.bind(this);

    }


}
