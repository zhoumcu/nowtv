package com.pccw.nowplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.fragment.LandingFragment;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.utils.FragmentUtil;
import com.pccw.nowplayer.utils.LocaleUtils;
import com.pccw.nowplayer.utils.Validations;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @Bind(R.id.addition_lay)
    LinearLayout additionLay;
    @Bind(R.id.container)
    FrameLayout container;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    LandingFragment landingFragment;
    @Bind(R.id.right_draw_lay)
    RelativeLayout rightDrawLay;
    @Bind(R.id.subtitle)
    TextView subtitle;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_image)
    ImageView titleImage;
    @Bind(R.id.title_lay)
    LinearLayout titleLay;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_home)
    TextView tvHome;
    @Bind(R.id.tv_live_chat)
    TextView tvLiveChat;
    @Bind(R.id.tv_more)
    TextView tvMore;
    @Bind(R.id.tv_more_description)
    TextView tvMoreDescription;
    @Bind(R.id.tv_my_now)
    TextView tvMyNow;
    @Bind(R.id.tv_on_demands)
    TextView tvOnDemands;
    @Bind(R.id.tv_support)
    TextView tvSupport;
    @Bind(R.id.tv_tv_guide)
    TextView tvTvGuide;
    @Bind(R.id.tv_tv_remote)
    TextView tvTvRemote;
    private MenuItem actionMenu;
    private MenuItem actionSearch;
    private boolean doubleBackToExitPressedOnce = false;
    private BroadcastReceiver languageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reLoad();
        }
    };

    @Override
    protected void bindEvents() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        drawerLayout.addDrawerListener(mDrawerToggle);
        initDrawer();
    }

    public void changeActionBar(IActionBar.ActionBar actionBar) {
        if (actionBar == null) return;
        if (titleImage != null)
            titleImage.setVisibility(actionBar.showImage ? View.VISIBLE : View.GONE);
        if (title != null) title.setText(actionBar.title);
        if (actionSearch != null) actionSearch.setVisible(actionBar.showSearch);
        if (actionMenu != null) actionMenu.setVisible(actionBar.showMenu);
        setSubTitle(actionBar.subtitle);
        if (titleLay != null && actionBar.titleClickAction != null) {
            titleLay.setOnClickListener(actionBar.titleClickAction);
        }
        if (!Validations.isEmptyOrNull(actionBar.additionView)) {
            for (View view : actionBar.additionView) {
                additionLay.addView(view);
            }
        }
    }

    public void changeFragment(Fragment fragment, boolean needPopStack) {
        if (needPopStack) {
            FragmentUtil.popBottomFragment(getSupportFragmentManager());
        }
        if (fragment != null)
            FragmentUtil.changeFragment(this, container, fragment, FragmentUtil.Animation.FADE);
    }

    public Fragment getCurrentFragment() {
        if (container != null) {
            return getSupportFragmentManager().findFragmentById(container.getId());
        }
        return null;
    }

    private void initDrawer() {
        rightDrawLay.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }
            }
        });
        rightDrawLay.findViewById(R.id.btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
                FragmentUtil.popBottomFragment(getSupportFragmentManager());
            }
        });
        rightDrawLay.findViewById(R.id.btn_live_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowPlayerLinkClient.getInstance().executeUrlAction(MainActivity.this, Constants.ACTION_LIVE_CHAT);
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
        rightDrawLay.findViewById(R.id.btn_ondemands).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowPlayerLinkClient.getInstance().executeUrlAction(MainActivity.this, Constants.ACTION_MAIN + ":" + Constants.ACTION_FRAGMENT_ONDEMAND);
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
        rightDrawLay.findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowPlayerLinkClient.getInstance().executeUrlAction(MainActivity.this, Constants.ACTION_MAIN + ":" + Constants.ACTION_FRAGMENT_MORE);
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
        rightDrawLay.findViewById(R.id.btn_my_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NowIDClient.getInstance().isLoggedIn()) {
                    NowPlayerLinkClient.getInstance().executeUrlAction(MainActivity.this, Constants.ACTION_MAIN + ":" + Constants.ACTION_FRAGMENT_MY_NOW);
                } else {
                    NowPlayerLinkClient.getInstance().executeUrlAction(MainActivity.this, Constants.ACTION_LOGIN);
                }
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
        rightDrawLay.findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowPlayerLinkClient.getInstance().executeUrlAction(MainActivity.this, Constants.ACTION_SETTING);
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
        rightDrawLay.findViewById(R.id.btn_tv_guide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowPlayerLinkClient.getInstance().executeUrlAction(MainActivity.this, Constants.ACTION_MAIN + ":" + Constants.ACTION_FRAGMENT_TV_GUIDE);
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
        rightDrawLay.findViewById(R.id.btn_tv_remote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowPlayerLinkClient.getInstance().executeUrlAction(MainActivity.this, Constants.ACTION_MAIN + ":" + Constants.ACTION_FRAGMENT_TV_REMOTE);
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        action = getIntent().getStringExtra(Constants.ARG_ACTION);
        action = Constants.ACTION_FRAGMENT_LANDING;
    }

    @Override
    protected void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (landingFragment == null) {
            landingFragment = new LandingFragment();
        }
        changeFragment(landingFragment, true);
//        if (action != null)
//            NowPlayerLinkClient.getInstance().executeUrlAction(this, action);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else if (doubleBackToExitPressedOnce) {
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.pressagainToExit), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.registerLanguageBroadcast(this, languageReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuToUse = R.menu.home_menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuToUse, menu);
        actionSearch = menu.findItem(R.id.action_search);
        actionMenu = menu.findItem(R.id.action_menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(languageReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null) return false;
        int id = item.getItemId();
        switch (id) {
            case R.id.action_menu:
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                return true;
            case R.id.action_search:
                NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_SEARCH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reLoad() {
        tvHome.setText(R.string.home);
        tvMyNow.setText(R.string.my_now);
        tvTvGuide.setText(R.string.tv_guide);
        tvOnDemands.setText(R.string.on_demands);
        tvTvRemote.setText(R.string.tv_remote);
        tvMore.setText(R.string.more);
        tvMoreDescription.setText(R.string.more_description);
        tvLiveChat.setText(R.string.live_chat);
        tvSupport.setText(R.string.support);
    }

    public void resetToDefaultActionBar() {
        if (titleImage != null) titleImage.setVisibility(View.VISIBLE);
        if (title != null) title.setText(null);
        if (actionSearch != null) actionSearch.setVisible(true);
        if (actionMenu != null) actionMenu.setVisible(true);
        if (subtitle != null) {
            subtitle.setText(null);
            subtitle.setVisibility(View.GONE);
        }
        if (titleLay != null) {
            titleLay.setOnClickListener(null);
        }
        if (additionLay != null) additionLay.removeAllViews();
    }

    public void setSubTitle(String subtitleStr) {
        if (subtitle != null && !TextUtils.isEmpty(subtitleStr)) {
            subtitle.setText(subtitleStr);
            subtitle.setVisibility(View.VISIBLE);
        }
    }
}
