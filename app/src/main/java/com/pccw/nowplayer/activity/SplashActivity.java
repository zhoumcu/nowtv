package com.pccw.nowplayer.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.utils.NetWorkUtil;
import com.pccw.nowplayer.utils.Pref;
import com.pccw.nowtv.nmaf.appVersionData.NMAFAppVersionDataUtils;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.core.NMAFErrorCodes;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

/**
 * Created by Swifty on 2016/3/15.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Pref.getPref().getBool(Constants.PREF_ISACCEPT_TANDC)) {
            startLauncher();
        } else {
            DialogHelper.createTAndCDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Pref.getPref().putBool(Constants.PREF_ISACCEPT_TANDC, true);
                    dialogInterface.dismiss();
                    startLauncher();
                }
            });
        }
    }

    private void startLauncher() {
        if (NetWorkUtil.isNetworkConnected(this)) {
            onlineMode();
        } else {
            offlineMode();
        }
    }

    private void offlineMode() {
        NowPlayerLinkClient.getInstance().executeUrlAction(SplashActivity.this, Constants.ACTION_OFFLINE_MODE);
    }

    private void onlineMode() {
        PlayerApplication.initNMAF(new NMAFBaseModule.ResultCallback() {
            @Override
            public void operationComplete(int i) {
                if (isFinishing()) return;
                if (i == NMAFErrorCodes.NMAFERR_GEN_SUCCESS) {
                    NMAFAppVersionDataUtils.getSharedInstance().updateApplicationInfoData(new NMAFBaseModule.ResultCallback() {
                        @Override
                        public void operationComplete(int i) {
                            NMAFnowID.getSharedInstance().restoreSession(new NMAFBaseModule.ErrorCallback() {
                                @Override
                                public void operationComplete(Throwable throwable) {
                                    NMAFnowID.getSharedInstance().updateSession(new NMAFBaseModule.ErrorCallback() {
                                        @Override
                                        public void operationComplete(Throwable throwable) {
                                            if (isFinishing()) return;
                                            NowIDClient.getInstance().updateSessionData().always(new AlwaysCallback() {
                                                @Override
                                                public void onAlways(Promise.State state, Object resolved, Object rejected) {
                                                    NowPlayerLinkClient.getInstance().executeUrlAction(SplashActivity.this, Constants.ACTION_MAIN + ":" + Constants.ACTION_FRAGMENT_LANDING);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    DialogHelper.createTwoButtonDialog(SplashActivity.this, getString(R.string.network_error) + " : " + i, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startLauncher();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
                }
            }
        });
        PlayerApplication.initLanguage();
    }

}
