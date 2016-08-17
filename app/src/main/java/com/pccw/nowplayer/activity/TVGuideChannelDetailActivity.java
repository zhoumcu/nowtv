package com.pccw.nowplayer.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.fragment.TVGuideChannelFragment;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.FavoriteChannelsClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/14/2016.
 */
public class TVGuideChannelDetailActivity extends ToolBarBaseActivity {

    Node channel;
    @Bind(R.id.channel_icon)
    ImageView channelIcon;
    @Bind(R.id.channel_title)
    TextView channelTitle;
    @Bind(R.id.like_channel)
    ImageView likeChannel;
    @Bind(R.id.btn_play)
    ImageView play;
    @Bind(R.id.btn_subscribe)
    View subscribe;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.viewpager)
    ViewPager viewpager;

    @Override
    protected void bindEvents() {
        handleSubscribe();
        likeChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteChannelsClient.getInstance().toggleFavoriteChannel(TVGuideChannelDetailActivity.this, channel).always(new AlwaysCallback<Node, Throwable>() {
                    @Override
                    public void onAlways(Promise.State state, Node resolved, Throwable rejected) {
                        refreshLikeIcon();
                    }
                });
            }
        });
    }

    private boolean handleSubscribe() {
        if (channel.isOnApp() && (!NowIDClient.getInstance().isLoggedIn() || channel.isSubscribed())) {
            play.setImageResource(R.drawable.ic_play);
            play.setVisibility(View.VISIBLE);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.ARG_NODE, channel);
                    NowPlayerLinkClient.getInstance().executeUrlAction(TVGuideChannelDetailActivity.this, Constants.ACTION_VIDEO_PLAYER, bundle);
                }
            });
            return false;
        } else if (channel.isOnTV() && (!NowIDClient.getInstance().isLoggedIn() || channel.isSubscribed())) {
            play.setImageResource(R.drawable.ic_screen_cast_normal);
            play.setVisibility(View.VISIBLE);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.ARG_NODE, channel);
                    NowPlayerLinkClient.getInstance().executeUrlAction(TVGuideChannelDetailActivity.this, Constants.ACTION_VIDEO_SCREEN_CAST, bundle);
                }
            });
            return false;
        } else {
            subscribe.setVisibility(View.VISIBLE);
            subscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NowPlayerLinkClient.getInstance().executeUrlAction(TVGuideChannelDetailActivity.this, Constants.ACTION_LIVE_CHAT);
                }
            });
            return true;
        }
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        channel = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_channel_detail);
        ButterKnife.bind(this);
        if (channel == null) {
            finish();
            return;
        }
        ImageUtils.loadChannelImage(channelIcon, channel.getChannelId());
        String channelStr = channel.getTitle() + "\n" + getString(R.string.ch).toUpperCase() + " " + channel.getChannelCode();
        channelTitle.setText(channelStr);
        DatePager datePager = new DatePager(getSupportFragmentManager());
        viewpager.setAdapter(datePager);
        tabs.setupWithViewPager(viewpager);
        title.setText(getString(R.string.tv_guide));
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.orange));
        refreshLikeIcon();
    }

    private void refreshLikeIcon() {
        if (!FavoriteChannelsClient.getInstance().isFavoriteChannel(channel)) {
            likeChannel.setImageResource(R.drawable.ic_heart);
        } else {
            likeChannel.setImageResource(R.drawable.ic_orange_heart);
        }
    }

    private class DatePager extends FragmentStatePagerAdapter {

        Map<Integer, String> map = new HashMap<Integer, String>() {{
            put(1, getString(R.string.sun));
            put(2, getString(R.string.mon));
            put(3, getString(R.string.tue));
            put(4, getString(R.string.wed));
            put(5, getString(R.string.thu));
            put(6, getString(R.string.fri));
            put(7, getString(R.string.sat));
        }};

        public DatePager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, channel.copy());
            bundle.putInt(Constants.ARG_DATE_OFFSET, position);
            TVGuideChannelFragment tvGuideChannelFragment = new TVGuideChannelFragment();
            tvGuideChannelFragment.setArguments(bundle);
            return tvGuideChannelFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            super.getPageTitle(position);
            return getTitleStartByToday(position);
        }

        private String getTitleStartByToday(int offset) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_YEAR, offset);
            int dayOFWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int dayOFMonth = calendar.get(Calendar.DAY_OF_MONTH);
            return map.get(dayOFWeek) + "\n" + dayOFMonth;
        }
    }
}
