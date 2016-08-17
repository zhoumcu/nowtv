package com.pccw.nowplayer.activity.video;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ThemeActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParentalCtrl1stActivity extends ThemeActivity implements OnClickListener {

    @Bind(R.id.bt_select_type)
    Button btSelectType;
    @Bind(R.id.et_passport)
    EditText etPassport;
    @Bind(R.id.bt_confirm)
    Button btConfirm;

    String[] options;
    int selected = 0;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.tv_nowid)
    TextView tvNowid;
    @Bind(R.id.tv_program_details)
    TextView tvProgramDetails;
    @Bind(R.id.ll_program)
    LinearLayout llProgram;
    @Bind(R.id.cb_confirm_limit)
    CheckBox cbConfirmLimit;

    Node node;
    private String pin;

    @Override
    protected void initIntentData() {
        super.initIntentData();
        node = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
        pin = getIntent().getStringExtra(Constants.ARG_VIDEO_PIN);
    }

    @Override
    protected void initViews() {
        setViewUnderToolbar(R.layout.activity_parent_ctrl_1st);
        ButterKnife.bind(this);
        if (node == null) node = Node.emptyNode();

        if (node.isEPG()) {
            llProgram.setVisibility(View.VISIBLE);
            String desc = node.getStartTimeText() + node.getTitle();
            tvProgramDetails.setText(desc);
        } else {
            llProgram.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(pin)) {
            etPassword.setText(pin);
            DialogHelper.createOneButtonAlertDialog(this, getString(R.string.parental_control), getString(R.string.incorrect_password), getString(R.string.ok));
        }
        getLemonToolbar().setTitle(getString(R.string.parental_control));
    }

    @Override
    protected void bindEvents() {

        options = new String[]{getString(R.string.hkid), getString(R.string.passport), getString(R.string.hkbr)};


        String nowId = NowIDClient.getInstance().getNowId();
        tvNowid.setText(String.format(getString(R.string.now_id_tips), nowId));

        btSelectType.setText(options[selected]);
        btConfirm.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {


        if (v.getId() == R.id.bt_select_type) {
            DialogHelper.requestPassport(this, options, new DialogHelper.inputDialogCallBack() {
                @Override
                public void inputTextConfirm(DialogInterface dialog, String inputText, int which) {
                    selected = which;
                    btSelectType.setText(options[selected]);
                }

                @Override
                public void inputTextCancel(DialogInterface dialog) {

                }
            });
        } else if (v.getId() == R.id.bt_confirm) {

            if (!cbConfirmLimit.isChecked()) {
                return;
            }

            String idNum = etPassport.getText().toString();
            String pin = etPassword.getText().toString();

            NMAFBasicCheckout.getSharedInstance().parentLockVerifyID(idNum, options[selected], pin, new NMAFBaseModule.ErrorCallback() {
                @Override
                public void operationComplete(Throwable throwable) {
                    if (throwable == null) {
                        DialogHelper.createParentalActivatedDialog(ParentalCtrl1stActivity.this);
                    } else {
                        DialogHelper.createInvalidPassportDialog(ParentalCtrl1stActivity.this);
                    }
                    L.e(throwable);
                }
            });
        }
    }

}
