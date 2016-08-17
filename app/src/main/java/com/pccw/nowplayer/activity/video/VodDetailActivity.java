package com.pccw.nowplayer.activity.video;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.BaseActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.helper.Judge;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.VODClient;
import com.pccw.nowplayer.model.WatchListClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.service.ShowBlur;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowplayer.utils.ViewUtils;
import com.pccw.nowplayer.widget.LemonToolbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kevin on 2016/3/26.
 */
public class VodDetailActivity extends BaseActivity implements View.OnClickListener {


    AboutFragment aboutFragment;
    @Bind(R.id.bt_rent)
    Button btRent;
    @Bind(R.id.btn_subscribe)
    LinearLayout btnSubscribe;
    @Bind(R.id.fl_tabs)
    FrameLayout flTabs;
    @Bind(R.id.fl_vod)
    FrameLayout flVod;
    boolean isDestory = false;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.iv_detail_add)
    ImageView ivDetailAdd;
    @Bind(R.id.iv_detail_logo)
    ImageView ivDetailLogo;
    @Bind(R.id.iv_detail_play)
    ImageView ivDetailPlay;
    @Bind(R.id.iv_detail_screencast)
    ImageView ivDetailScreencast;
    @Bind(R.id.iv_download)
    ImageView ivDownload;
    @Bind(R.id.ll_options)
    LinearLayout llOptions;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    Node node;
    @Bind(R.id.rb_about)
    RadioButton rbAbout;
    @Bind(R.id.rb_episodes)
    RadioButton rbEpisodes;
    @Bind(R.id.rb_recommended)
    RadioButton rbRecommended;
    RecommendedFragment recommendedFragment;
    @Bind(R.id.rg_tab)
    RadioGroup rgTab;
    @Bind(R.id.sv_main)
    ScrollView svMain;
    @Bind(R.id.tb_toolbar)
    LemonToolbar tbToolbar;
    @Bind(R.id.tv_detail_title)
    TextView tvDetailTitle;
        @Override
    protected void bindEvents() {
        ivDetailAdd.setOnClickListener(this);
        btRent.setOnClickListener(this);
        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = null;
                if (checkedId == R.id.rb_about) {
                    fragment = aboutFragment;
                } else if (checkedId == R.id.rb_recommended) {
                    fragment = recommendedFragment;
                }

                if (fragment != null) {
                    FragmentTransaction f = getSupportFragmentManager().beginTransaction();
                    f.show(fragment).commitAllowingStateLoss();
                    if (aboutFragment != fragment) {
                        if (aboutFragment != null)
                            f.hide(aboutFragment);
                    }
                    if (recommendedFragment != fragment) {
                        if (recommendedFragment != null)
                            f.hide(recommendedFragment);
                    }
                }
            }
        });
    }

    private void fillView(Node node) {
        llRoot.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(node.getImageUrl()))
            Picasso.with(this).load(node.getImageUrl()).into(new Target() {
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    ivBack.post(new Runnable() {
                        @Override
                        public void run() {
                            new ShowBlur()
                                    .reduceImage(bitmap)
                                    .setImageView(ivBack)
                                    .blurImage();
                        }
                    });
                    ivCover.post(new Runnable() {
                        @Override
                        public void run() {
                            ivCover.setImageBitmap(bitmap);
                        }
                    });
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });
        updateButton();
        ImageUtils.loadImage(ivDetailLogo, node.getLibraryImageUrl());
        ivDetailPlay.setOnClickListener(this);
        initFragment();
    }

    private void initFragment() {
        int checkid = 0;
        FragmentTransaction f = getSupportFragmentManager().beginTransaction();
        rbEpisodes.setVisibility(View.GONE);
        rbAbout.setVisibility(View.GONE);
        rbRecommended.setVisibility(View.VISIBLE);

        // about fragment
        if (getFragmentManager().findFragmentByTag("aboutFragment") != null) {
            f.remove(aboutFragment);
        }
        rbAbout.setVisibility(View.VISIBLE);
        aboutFragment = AboutFragment.getAboutFragment(node);
        checkid = R.id.rb_about;
        f.add(R.id.fl_tabs, aboutFragment, "aboutFragment");

        // recommended fragment
        if (getFragmentManager().findFragmentByTag("recommendedFragment") != null) {
            f.remove(recommendedFragment);
        }
        recommendedFragment = RecommendedFragment.getRecommendedFragment(node);
        f.add(R.id.fl_tabs, recommendedFragment, "recommendedFragment");

        // commit
        f.commitAllowingStateLoss();
        if (checkid != 0) {
            rgTab.check(checkid);
        }
    }


    @Override
    protected void initToolBar() {
        setSupportActionBar(tbToolbar);
        ViewUtils.addOnGlobalLayoutListener(tbToolbar, new Runnable() {
            @Override
            public void run() {
                tbToolbar.setNavigationIcon(R.drawable.ic_action_back);
                tbToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                tbToolbar.setTitleInCenter();
            }
        });
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_vod_detail);
        ButterKnife.bind(this);
        svMain.smoothScrollTo(0, 0);
        node = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
        if (node == null) node = Node.emptyNode();
        tvDetailTitle.setText(node.getTitle());
        ivDetailAdd.setOnClickListener(this);
        loadDetails();
    }


    private void loadDetails() {
        DialogHelper.generateProgressLayer(this, flVod);
        VODClient.getInstance().loadProgramOrSeriesDetailsWithVEDetails(node).done(new DoneCallback<Node>() {
            @Override
            public void onDone(Node data) {
                if (isFinishing()) return;
                if (data != null) node = data;
                fillView(data);
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                if (isFinishing()) return;
                L.e(result);
                DialogHelper.createRequstFailDialog(VodDetailActivity.this).show();
            }
        }).always(new AlwaysCallback<Node, Throwable>() {
            @Override
            public void onAlways(Promise.State state, Node resolved, Throwable rejected) {
                if (isFinishing()) return;
                DialogHelper.removeProgressLayer(VodDetailActivity.this, flVod);
            }
        });
    }

    private boolean loggedIn() {
        return Judge.isLogin(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_VE_CODE) {
            if (resultCode == Constants.SUCCESS_CODE) {
                if (node != null) {
                    if (node.isVE()) {
                        Judge.checkVE(this, node);
                    }
                }
            } else if (resultCode == Constants.REQUEST_VE_SUCCESS_CODE) {
                loadDetails();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_detail_play) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, node);

            List<Node> playList = null;
            if (node.isSeries()) {
                playList = node.getEpisodes();
            } else if (recommendedFragment != null) {
                playList = recommendedFragment.getRecommendedPrograms();
            }
            if (playList != null) {
                ArrayList<List<Node>> list = new ArrayList<>();
                list.add(playList);
                bundle.putSerializable(Constants.ARG_NODE_ARRAY, list);
            }
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_VIDEO_PLAYER, bundle);
        } else if (v.getId() == R.id.bt_rent) {
            Judge.checkVE(this, node);
        } else if (v.getId() == R.id.iv_detail_add) {
            if (Judge.isLogin(this))
                WatchListClient.getInstance().toggleWatchListItem(node).always(new AlwaysCallback<Boolean, Throwable>() {
                    @Override
                    public void onAlways(Promise.State state, Boolean resolved, Throwable rejected) {
                        updateWatchListButton();
                    }
                });
        }
    }

    // Subscribe button
    private void subscribeView() {
        boolean enabled = false;
        if (node.isChannel()) {
            if (!node.isSubscribed() && loggedIn()) {
                enabled = true;
            }
        }
        btnSubscribe.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void updateButton() {
        if (node.isRentable()) {
            btRent.setVisibility(View.VISIBLE);
            btRent.setText(String.format(getString(R.string.rent_price), StringUtils.formatFloat(node.getRentPrice())));
        } else {
            btRent.setVisibility(View.GONE);
        }

        ivDetailPlay.setVisibility(node.isPlayEnabled() ? View.VISIBLE : View.GONE);
        ivDetailScreencast.setVisibility(node.canScreencastOnly() ? View.VISIBLE : View.GONE);
        ivDownload.setVisibility(node.isDownloadable() ? View.VISIBLE : View.GONE);
        ivDetailAdd.setVisibility(node.canAddWatchList() ? View.VISIBLE : View.GONE);
        subscribeView();
        updateWatchListButton();
    }

    private void updateWatchListButton() {
        if (ivDetailAdd == null) return;
        if (node != null && node.isInWatchList()) {
            ivDetailAdd.setImageResource(R.drawable.ic_watchlist_on);
        } else {
            ivDetailAdd.setImageResource(R.drawable.ic_watchlist_off);
        }
    }

}
