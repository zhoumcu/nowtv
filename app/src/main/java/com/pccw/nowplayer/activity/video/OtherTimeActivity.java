package com.pccw.nowplayer.activity.video;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.BaseActivity;
import com.pccw.nowplayer.activity.ThemeActivity;
import com.pccw.nowplayer.adapter.OtherTimesAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.model.EPGClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.UIUtils;
import com.pccw.nowplayer.utils.ViewUtils;
import com.pccw.nowplayer.widget.LemonToolbar;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/6/4.
 */
public class OtherTimeActivity extends ThemeActivity{

    Node program;
    Node channel;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.time_table)
    RecyclerView timeTable;
    private Dialog progress;

    @Override
    protected void initIntentData() {
        super.initIntentData();
        program = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
        if (program == null) program = Node.emptyNode();
        channel = (Node) getIntent().getSerializableExtra(Constants.ARG_CHANNEL);
        if (channel == null) channel = Node.emptyNode();
    }

    @Override
    protected void initViews() {
        setViewUnderToolbar(R.layout.activity_other_time);
        ButterKnife.bind(this);
        progress = DialogHelper.generateProgressLayer(this);
        UIUtils.setText(tvTitle, program.getTitle(), true);
        timeTable.setLayoutManager(new LinearLayoutManager(this));
        EPGClient.getInstance().loadOtherTimes(program).then(new DoneCallback<Node>() {
            @Override
            public void onDone(Node result) {
                try {
                    List<Node> nodes = result.getOtherTimes();
                    timeTable.setAdapter(new OtherTimesAdapter(OtherTimeActivity.this, nodes, channel));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).always(new AlwaysCallback<Node, Throwable>() {
            @Override
            public void onAlways(Promise.State state, Node resolved, Throwable rejected) {
                progress.dismiss();
            }
        });
        getLemonToolbar().setTitle(getString(R.string.other_times));
    }

    @Override
    protected void bindEvents() {

    }
}
