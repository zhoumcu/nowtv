package com.pccw.nowplayer.activity.video;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ThemeActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.model.node.Node;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParentalCtrlActivity extends ThemeActivity implements View.OnClickListener {

    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.bt_confirm)
    Button btConfirm;

    Node node;
    @Bind(R.id.cb_confirm_limit)
    CheckBox cbConfirmLimit;
    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        node = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
        pin = getIntent().getStringExtra(Constants.ARG_VIDEO_PIN);
    }

    @Override
    protected void initViews() {
        setViewUnderToolbar(R.layout.activity_parent_ctrl);
        ButterKnife.bind(this);
        btConfirm.setOnClickListener(this);
        if (node == null) node = Node.emptyNode();
        if (!TextUtils.isEmpty(pin)) {
            etPassword.setText(pin);
            DialogHelper.createOneButtonAlertDialog(this, getString(R.string.parental_control), getString(R.string.incorrect_password), getString(R.string.ok));
        }
        getLemonToolbar().setTitle(getString(R.string.parental_control));
    }

    @Override
    protected void bindEvents() {

    }

    @Override
    public void onClick(View v) {
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            DialogHelper.createOneButtonAlertDialog(this, getString(R.string.please_input_password));
            return;
        }
        if (!cbConfirmLimit.isChecked()) {
            DialogHelper.createOneButtonAlertDialog(this, getString(R.string.please_confirm_age));
            return;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARG_VIDEO_PIN, password);
        intent.putExtras(bundle);
        setResult(Constants.SUCCESS_CODE, intent);
        finish();
    }
}
