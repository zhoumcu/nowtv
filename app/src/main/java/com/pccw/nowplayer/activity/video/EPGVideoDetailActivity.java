package com.pccw.nowplayer.activity.video;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.BaseActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.constant.VideoTypeIndex;
import com.pccw.nowplayer.helper.BeginCheckout;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.EPGClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.PVRClient;
import com.pccw.nowplayer.model.WatchListClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Check;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.TextUtil;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/5/26.
 */
public class EPGVideoDetailActivity extends BaseActivity implements View.OnClickListener {
    Node channel;
    @Bind(R.id.iv_detail)
    ImageView ivDetail;
    @Bind(R.id.iv_watch_list)
    ImageView ivWatchList;
    @Bind(R.id.ll_more_on_demand)
    LinearLayout llMoreOnDemand;
    @Bind(R.id.ll_other_times)
    LinearLayout llOtherTimes;
    @Bind(R.id.ll_play_now)
    LinearLayout llPlayNow;
    @Bind(R.id.ll_record_on_tv)
    LinearLayout llRecordOnTV;
    @Bind(R.id.ll_screencast)
    LinearLayout llScreencast;
    @Bind(R.id.ll_subscribed)
    LinearLayout llSubscribed;
    @Bind(R.id.ll_synopsis)
    LinearLayout llSynopsis;
    @Bind(R.id.ll_watch_list)
    LinearLayout llWatchList;
    @Bind(R.id.ll_watch_on_demand)
    LinearLayout llWatchOnDemand;
    Node program;
    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_live)
    TextView tvLive;
    @Bind(R.id.tv_sub_title)
    TextView tvSubTitle;
    @Bind(R.id.tv_synopsis)
    TextView tvSynopsis;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_watch_list)
    TextView tvWatchList;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.rl_main)
    FrameLayout rlMain;

    @Override
    protected void bindEvents() {
        llPlayNow.setOnClickListener(this);
        llScreencast.setOnClickListener(this);
        llSubscribed.setOnClickListener(this);
        llWatchList.setOnClickListener(this);
        llRecordOnTV.setOnClickListener(this);
        llOtherTimes.setOnClickListener(this);
        llWatchOnDemand.setOnClickListener(this);
        llMoreOnDemand.setOnClickListener(this);
    }

    protected String getAirTime(Node node) {
        if (node == null) return "";

        StringBuilder str = new StringBuilder();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE dd MMM", Locale.US);
        if (node.getStartTime() != null) {
            String date = dateFormatter.format(node.getStartTime()).toUpperCase();
            str.append(date);
            str.append(" | ");
        }
        if (node.getStartTime() != null && node.getEndTime() != null) {
            dateFormatter.applyPattern("h:mm");
            str.append(dateFormatter.format(node.getStartTime()));
            str.append(" - ");
            dateFormatter.applyPattern("h:mm a");
            str.append(dateFormatter.format(node.getEndTime()));
        } else if (node.getStartTime() != null || node.getEndTime() != null) {
            dateFormatter.applyPattern("h:mm a");
            str.append(dateFormatter.format(node.getStartTime() != null ? node.getStartTime() : node.getEndTime()));
        }
        return str.toString();
    }

    @Override
    protected void initToolBar() {
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_epg_detail);
        ButterKnife.bind(this);
        program = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
        if (program == null) program = Node.emptyNode();
        channel = (Node) getIntent().getSerializableExtra(Constants.ARG_CHANNEL);
        if (channel == null) {
            channel = EPGClient.getInstance().getChannel(program.getChannelId());
        }
        if (channel == null) channel = Node.emptyNode();

        // First, load EPG details
        DialogHelper.generateProgressLayer(this, rlMain);

        EPGClient.getInstance().loadProgramDetails(program).then(new DonePipe<Node, Node, Throwable, Float>() {
            @Override
            public Promise<Node, Throwable, Float> pipeDone(Node result) {
                // Load other times
                if (result != null) program = result;
                return EPGClient.getInstance().loadOtherTimes(program);
            }
        }).then(new DonePipe<Node, Node, Throwable, Float>() {
            @Override
            public Promise<Node, Throwable, Float> pipeDone(Node result) {
                // Load Linked Products
                return EPGClient.getInstance().loadLinkedProducts(program);
            }
        }).done(new DoneCallback<Node>() {
            @Override
            public void onDone(Node result) {
                boolean subscribed = channel.isSubscribed() || !NowIDClient.getInstance().isLoggedIn();
                if (subscribed) {
                    llSubscribed.setVisibility(View.GONE);
                    llRecordOnTV.setVisibility(View.VISIBLE);
                } else {
                    llSubscribed.setVisibility(View.VISIBLE);
                    llRecordOnTV.setVisibility(View.GONE);
                }
                llPlayNow.setVisibility(channel.isPlayable()? View.VISIBLE : View.GONE);
                llScreencast.setVisibility(channel.canScreencastOnly() ? View.VISIBLE : View.GONE);
                llRecordOnTV.setVisibility(channel.isRecordable() ? View.VISIBLE : View.GONE);
                tvLive.setVisibility(channel.isLive() ? View.VISIBLE : View.GONE);
                updateWatchListButton();
                ImageUtils.loadChannelImage(ivDetail, channel.getChannelId());
                tvSubTitle.setText(program.getTitle());
                String airTime = getAirTime(program);
                tvTime.setText(airTime);
                tvTime.setVisibility(TextUtils.isEmpty(airTime) ? View.GONE : View.VISIBLE);
                llWatchOnDemand.setVisibility(Check.isEmpty(program.getLinkedVOD()) ? View.GONE : View.VISIBLE);
                llMoreOnDemand.setVisibility(TextUtils.isEmpty(program.getVodNodeId()) ? View.GONE : View.VISIBLE);
                llOtherTimes.setVisibility(Check.isEmpty(program.getOtherTimes()) ? View.GONE : View.VISIBLE);
                llSynopsis.setVisibility(TextUtils.isEmpty(program.getSynopsis()) ? View.GONE : View.VISIBLE);
                tvSynopsis.setText(program.getSynopsis());
                TextUtil.setMore(tvSynopsis,"...",getString(R.string.more));
                tvSynopsis.setOnClickListener(EPGVideoDetailActivity.this);
                llRoot.setVisibility(View.VISIBLE);
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Dialog dialog1 = DialogHelper.createRequstFailDialog(EPGVideoDetailActivity.this);
                dialog1.show();
            }
        }).always(new AlwaysCallback<Node, Throwable>() {
            @Override
            public void onAlways(Promise.State state, Node resolved, Throwable rejected) {
                DialogHelper.removeProgressLayer(EPGVideoDetailActivity.this, rlMain);
            }
        });
    }

    @Override
    public void onClick(final View v) {

        if(v.getId() == R.id.ll_subscribed){
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_LIVE_CHAT);
        }else  if (v.getId() == R.id.ll_play_now) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, channel);
            NowPlayerLinkClient.getInstance().executeUrlAction(EPGVideoDetailActivity.this, Constants.ACTION_VIDEO_PLAYER, bundle);
        } else if (v.getId() == R.id.ll_screencast) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, channel);
            NowPlayerLinkClient.getInstance().executeUrlAction(EPGVideoDetailActivity.this, Constants.ACTION_VIDEO_SCREEN_CAST, bundle);
        } else if (v.getId() == R.id.ll_watch_list) {
            if (!NowIDClient.getInstance().isLoggedIn()) {
                NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_LOGIN + ":" + Constants.REQUEST_CODE);
                return;
            }
            llWatchList.setEnabled(false);
            WatchListClient.getInstance().toggleWatchListItem(program).always(new AlwaysCallback<Boolean, Throwable>() {
                @Override
                public void onAlways(Promise.State state, Boolean resolved, Throwable rejected) {
                    rejected.printStackTrace();
                    updateWatchListButton();
                    llWatchList.setEnabled(true);
                }
            });
        } else if (v.getId() == R.id.ll_record_on_tv) {
            PVRClient.getInstance().addNodeToPVRList(this, program).then(new DoneCallback<Boolean>() {
                @Override
                public void onDone(Boolean addedToList) {
                    // update button state
                }
            });
        } else if (v.getId() == R.id.ll_other_times) {
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_OTHER_TIME, createBundle());
        } else if (v.getId() == R.id.ll_watch_on_demand) {
            if(Check.isEmpty(program.getLinkedVOD())){
                return ;
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, program.getLinkedVOD().get(0));
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_VIDEO_DETAIL + ":" + VideoTypeIndex.VOD, bundle);
        } else if (v.getId() == R.id.ll_more_on_demand) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, program.getVodNode());
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_SUB_NODES_PAGE, bundle);
        }else if (v.getId() == R.id.tv_synopsis) {
            tvSynopsis.setText(program.getSynopsis());
        }
    }

    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_CHANNEL, channel);
        bundle.putSerializable(Constants.ARG_NODE, program);
        return bundle;
    }

    public void updateWatchListButton() {
        if (program != null && program.isInWatchList()) {
            ivWatchList.setImageResource(R.drawable.ic_watchlist_on);
            tvWatchList.setText(R.string.remove_from_list);
        } else {
            ivWatchList.setImageResource(R.drawable.ic_watchlist_off);
            tvWatchList.setText(R.string.add_to_watch_list);
        }
    }
}
