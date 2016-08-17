package com.pccw.nowplayer.helper;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.pccw.nowplayer.activity.video.VideoPlayer;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.model.CheckoutClient;
import com.pccw.nowplayer.model.MyNowClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;

/**
 * Created by Kevin on 2016/7/19.
 */
public class BeginCheckout implements CheckoutClient.Callback {

    private Node node;

    public void beginCheckout(FragmentActivity activity, Node node) {

        Fragment fragment = activity.getFragmentManager().findFragmentByTag("checkout");

        if (fragment != null) {
            return;
        }
        CheckoutClient checkoutClient = CheckoutClient.create(node, false);
        checkoutClient.setCallback(this);
        checkoutClient.attachTo(activity).begin();
        this.node = node;
    }

    @Override
    public void onCheckoutFinished(CheckoutClient checkout) {
        checkout.detach();
        if (checkout.isCancelled() || checkout.getCheckoutData() == null) {
            return;
        }
        MyNowClient.getInstance().addHistory(node);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        bundle.putString(Constants.ARG_CHECK_OUT_DATA, checkout.getCheckoutData().toJSON());
        Context context = checkout.getContext();
        Intent intent = new Intent(context, VideoPlayer.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
