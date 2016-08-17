package com.pccw.nowplayer.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 7/19/2016.
 */
public class OfflineActivity extends ToolBarBaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.subtitle)
    TextView subtitle;
    @Bind(R.id.title_lay)
    LinearLayout titleLay;
    @Bind(R.id.delete)
    ImageView delete;
    @Bind(R.id.edit)
    TextView edit;
    @Bind(R.id.select_count)
    TextView selectCount;
    @Bind(R.id.cancel)
    TextView cancel;
    @Bind(R.id.recycle_view)
    RecyclerView recycleView;
    @Bind(R.id.go_online)
    Button goOnline;

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_offline);
        ButterKnife.bind(this);
        title.setText(R.string.offline_mode);
    }

    @Override
    protected void bindEvents() {

    }
}
