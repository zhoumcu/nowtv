package com.pccw.nowplayer.activity.settings;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ThemeActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.utils.Check;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class YourBoxActivity extends ThemeActivity implements View.OnClickListener {

    @Bind(R.id.ll_not_conn)
    LinearLayout llNotConn;
    @Bind(R.id.tv_account_num)
    TextView tvAccountNum;
    @Bind(R.id.ll_connect)
    LinearLayout llConnect;
    @Bind(R.id.bt_disconnect)
    Button btDisconnect;




    @Override
    protected void initViews() {
        setViewUnderToolbar(R.layout.activity_your_box);
        ButterKnife.bind(this);
        llNotConn.setOnClickListener(this);
        btDisconnect.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        String fsa = NowIDClient.getInstance().getFsa();
        if (!Check.isEmpty(fsa)) {
            String str = getString(R.string.your_box_account_number);
            str = String.format(str, fsa);
            tvAccountNum.setText(str);
            switchVisible(true);
        } else {
            switchVisible(false);
        }
    }

    private void switchVisible(boolean hasFSA) {
        if (hasFSA) {
            llNotConn.setVisibility(View.GONE);
            llConnect.setVisibility(View.VISIBLE);
            btDisconnect.setVisibility(View.VISIBLE);
        } else {
            llNotConn.setVisibility(View.VISIBLE);
            llConnect.setVisibility(View.GONE);
            btDisconnect.setVisibility(View.GONE);
        }

    }


    @Override
    protected void bindEvents() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_not_conn) {
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_FSA_BINDING);
        }else if(v.getId() == R.id.bt_disconnect){
            final Dialog dialog = DialogHelper.createProgressDialog(this,getString(R.string.please_wait));
            dialog.show();
            NMAFnowID.getSharedInstance().unbindSTB(new NMAFBaseModule.ErrorCallback() {
                @Override
                public void operationComplete(Throwable throwable) {
                    NMAFnowID.getSharedInstance().updateSession(new NMAFBaseModule.ErrorCallback() {
                        @Override
                        public void operationComplete(Throwable throwable) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    NMAFnowID.getSharedInstance().updateSession(new NMAFBaseModule.ErrorCallback() {
                                        @Override
                                        public void operationComplete(Throwable throwable) {
                                            if(throwable ==null){
                                                YourBoxActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if(dialog!=null){
                                                            dialog.dismiss();
                                                        }
                                                        refreshData();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }
}
