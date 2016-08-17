package com.pccw.nowplayer.activity.video;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lifevibes.lvmediaplayer.LVAudioTrack;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.BaseActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.WatchListClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Check;
import com.pccw.nowplayer.utils.ImageUtils;
import com.pccw.nowplayer.utils.LString;
import com.pccw.nowplayer.widget.SettingDialog;
import com.pccw.nowtv.nmaf.mediaplayer.NMAFMediaPlayerController;
import com.pccw.nowtv.nmaf.utilities.NMAFLanguageUtils;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by Kevin on 2016/6/4.
 */
public abstract class BasePlayer extends BaseActivity {

    public static Node nowPlaying;
    protected Node liveProgram;
    protected Node node;
    @Bind(R.id.subtitle)
    TextView subtitle;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_image)
    ImageView titleImage;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private Menu menu;

    protected abstract NMAFMediaPlayerController getController();

    public Menu getMenu() {
        return menu;
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        node = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
        if (node == null) node = Node.emptyNode();
        nowPlaying = node;
    }

    @Override
    protected void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleImage = (ImageView) findViewById(R.id.title_image);
        title = (TextView) findViewById(R.id.title);
        subtitle = (TextView) findViewById(R.id.subtitle);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        ImageUtils.loadChannelImage(titleImage, node.getChannelId());
        updateTitle();
    }

    @Override
    protected void initViews() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.video_menu, menu);
        menu.findItem(R.id.menu_subtitle_audio).setVisible(false);
        menu.findItem(R.id.menu_add_mynow).setVisible(false);
        updateWatchListButton();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nowPlaying = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_mynow) {
            if (!NowIDClient.getInstance().isLoggedIn()) {
                NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_LOGIN + ":" + Constants.REQUEST_CODE);
                return false;
            }
            final MenuItem menuItem = menu.findItem(R.id.menu_add_mynow);
            menuItem.setEnabled(false);
            WatchListClient.getInstance().toggleWatchListItem(node).always(new AlwaysCallback<Boolean, Throwable>() {
                @Override
                public void onAlways(Promise.State state, Boolean resolved, Throwable rejected) {
                    updateWatchListButton();
                    menuItem.setEnabled(true);
                }
            });

        } else if (item.getItemId() == R.id.menu_subtitle_audio) {
            NMAFMediaPlayerController controller = getController();
            if (controller == null) {
                return true;
            }
            NMAFMediaPlayerController.SubtitleConfig.SubtitleConfigItem[] subtitlesArray = controller.getSubtitleList();
            LVAudioTrack[] audioTracksArray = controller.getAudioTracks();

            if (Check.isEmpty(subtitlesArray)) {
            } else {
            }
            if (Check.isEmpty(audioTracksArray)) {
            } else {
            }
            String notAvailable = getString(R.string.na);
            SettingDialog<String> stringSettingDialog = new SettingDialog<>(this);
            final ArrayList<String> audioTracks = new ArrayList();
            final ArrayList<ArrayList<String>> subtitlesList = new ArrayList();
            final ArrayList<String> subtitles = new ArrayList<>();

            int selectedAudio = 0;
            int selectedSub = 0;


            LString imageUrl = null;
            String local = NMAFLanguageUtils.getSharedInstance().getLanguage();
            if (subtitlesArray != null) for (NMAFMediaPlayerController.SubtitleConfig.SubtitleConfigItem subtitle : subtitlesArray) {
                if (!TextUtils.isEmpty(subtitle.en) || !TextUtils.isEmpty(subtitle.zh)) {
                    imageUrl = LString.make(subtitle.en, subtitle.zh);
                }
                if (imageUrl != null) {
                    subtitles.add(imageUrl.getString(local));
                }
            }

            if (audioTracksArray != null) for (LVAudioTrack audioTrack : audioTracksArray) {
                audioTracks.add(audioTrack.getLanguage());
            }
            if (audioTracks.isEmpty()) {
                audioTracks.add(notAvailable);
            }
            if (subtitles.isEmpty()) {
                subtitles.add(notAvailable);
            }
            subtitlesList.add(subtitles);

            stringSettingDialog.setPicker(audioTracks, subtitlesList);
            stringSettingDialog.setSelectOptions(selectedAudio, selectedSub);
            stringSettingDialog.setOnoptionsSelectListener(new SettingDialog.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2) {
                    NMAFMediaPlayerController controller = getController();
                    if (controller == null) {
                        return;
                    }
                    if (subtitles.size() > 1) {
                        controller.setSubtitle(controller.getSubtitleList()[options1]);
                    }
                    if (audioTracks.size() > 1) {
                        controller.setAudioTrack(controller.getAudioTracks()[options1]);
                    }

                }
            });
            stringSettingDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateTitle() {
        String line1 = null;
        String line2 = null;
        if (node != null) {
            if (node.isVOD()) {
                if (node.isVE()) {
                    line1 = node.getTitle();
                    line2 = getString(R.string.video_express);
                } else {
                    line1 = node.getTitle();
                    line2 = node.getEpisodeName();
                }
            } else {
                line1 = liveProgram == null ? null : liveProgram.getTitle();
                line2 = "CH " + node.getChannelCode();
            }
        }
        if (TextUtils.isEmpty(line1)) {
            line1 = line2;
            line2 = null;
        }
        if (title != null) {
            if (!Check.isEmpty(line1)) {
                title.setText(line1);
                title.setVisibility(View.VISIBLE);
            } else {
                title.setVisibility(View.GONE);
            }
        }

        if (subtitle != null) {
            if (!Check.isEmpty(line2)) {
                subtitle.setText(line2);
                subtitle.setVisibility(View.VISIBLE);
            } else {
                subtitle.setVisibility(View.GONE);
            }
        }
    }

    private void updateWatchListButton() {
        MenuItem menuItem = menu.findItem(R.id.menu_add_mynow);
        if (node != null && node.isInWatchList()) {
            menuItem.setIcon(R.drawable.ic_watchlist_on);
        } else {
            menuItem.setIcon(R.drawable.ic_watchlist_off);
        }
    }
}
