package com.pccw.nowplayer.activity.video;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ToolBarBaseActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.CheckoutClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.service.ShowBlur;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.utilities.NMAFLanguageUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VEDollarPINActivity extends ToolBarBaseActivity implements View.OnClickListener, CheckoutClient.Callback {


    @Bind(R.id.bt_rent)
    Button btRent;
    @Bind(R.id.bt_watch_now)
    Button btWatchNow;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.iv_sec_type)
    ImageView ivSecType;
    @Bind(R.id.ll_passwd)
    LinearLayout llPasswd;
    @Bind(R.id.ll_success)
    LinearLayout llSuccess;
    @Bind(R.id.ll_success_tip)
    LinearLayout llSuccessTip;
    Node node;
    @Bind(R.id.subtitle)
    TextView subtitle;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_lay)
    LinearLayout titleLay;
    @Bind(R.id.tv_conditions)
    TextView tvConditions;
    @Bind(R.id.tv_display_effect)
    TextView tvDisplayEffect;
    @Bind(R.id.tv_display_mode)
    TextView tvDisplayMode;
    @Bind(R.id.tv_exp)
    TextView tvExp;
    @Bind(R.id.tv_remark)
    TextView tvRemark;
    @Bind(R.id.tv_rent_price)
    TextView tvRentPrice;
    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void bindEvents() {
        btRent.setOnClickListener(this);
        btWatchNow.setOnClickListener(this);
        tvConditions.setOnClickListener(this);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_ve_dollar_pin);
        ButterKnife.bind(this);
        node = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
        if (node == null) node = Node.emptyNode();

        String local = NMAFLanguageUtils.getSharedInstance().getLanguage();
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

        boolean isRented = node.isRented();

        int imageRes = node.getClassificationImageResourceID();
        if (imageRes != 0) {
            ivSecType.setImageResource(imageRes);
        } else {
            ivSecType.setVisibility(View.GONE);
        }

        float price = node.getRentPrice();
        String priceStr = String.format(getString(R.string.dollar), price + "");
        tvRentPrice.setText(priceStr);
        tvRentPrice.setVisibility(isRented || price < 0.f ? View.GONE : View.VISIBLE);
        tvTitle.setText(node.getTitle());
        tvExp.setText(node.getRentalExpiryText());

        String viewingPeriodDesc;
        int viewingPeriod = node.getViewingPeriod();
        if (viewingPeriod <= 3) {
            viewingPeriodDesc = (viewingPeriod * 24) + " Hours";
        } else {
            viewingPeriodDesc = viewingPeriod + " Days";
        }


        String desc = viewingPeriodDesc;
        if (!TextUtils.isEmpty(node.getAvailablePlatformsText())) {
            desc += (": " + node.getAvailablePlatformsText());
        }
        tvDisplayMode.setText(desc);
        String effect = node.is3D() ? "3D" : "2D";
        tvDisplayEffect.setText(effect);

        //remark
        if (isRented) {
            String machineName = CheckoutClient.MACHINE_NAME == null ? "" : CheckoutClient.MACHINE_NAME;
            tvRemark.setText(String.format(getString(R.string.ve_remark), machineName, viewingPeriodDesc));
        } else {
            String str = getString(R.string.ve_success_remark);
            tvRemark.setText(str);
        }
        if (isRented) {
            step2();
        } else {
            step1();
        }


    }

    @Override
    public void onCheckoutFinished(CheckoutClient client) {
        NMAFBasicCheckout.NMAFCheckoutData checkoutData = client.getCheckoutData();
        if (checkoutData != null) {
            client.detach();
            setResult(Constants.REQUEST_VE_SUCCESS_CODE);
            step2();
        } else {

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_rent) {
            if (TextUtils.isEmpty(etPassword.getText().toString())) {
                DialogHelper.createOneButtonAlertDialog(VEDollarPINActivity.this, getString(R.string.please_input_password));
                return;
            }
            CheckoutClient.pin = etPassword.getText().toString();
            CheckoutClient.create(node, false).setCallback(this).attachTo(this).begin();
        } else if (v.getId() == R.id.bt_watch_now) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, node);
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_VIDEO_PLAYER, bundle);
        } else if (v.getId() == R.id.tv_conditions) {
            NowPlayerLinkClient.getInstance().executeUrlAction(this, Constants.ACTION_CONDITION);
        }
    }

    private void step1() {
        llPasswd.setVisibility(View.VISIBLE);
        llSuccess.setVisibility(View.GONE);
        llSuccessTip.setVisibility(View.GONE);

    }

    private void step2() {
        llPasswd.setVisibility(View.GONE);
        llSuccess.setVisibility(View.VISIBLE);
        llSuccessTip.setVisibility(View.VISIBLE);

    }
}
