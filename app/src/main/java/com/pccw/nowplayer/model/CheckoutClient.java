package com.pccw.nowplayer.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.mynow.CheckOutRegistionActivity;
import com.pccw.nowplayer.activity.mynow.FSABindingActivity;
import com.pccw.nowplayer.activity.mynow.LoginActvitiy;
import com.pccw.nowplayer.activity.video.VEDollarPINActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.DebugToast;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.helper.Judge;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.exceptions.CancelledException;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.service.ConfigService;
import com.pccw.nowplayer.utils.Check;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowplayer.utils.NetWorkUtil;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.networking.WebTVAPIModels;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DeferredObject;


/**
 * Created by kriz on 2016-06-18.
 */
public class CheckoutClient extends Fragment {

    private static final int RequestDeviceRegistration = 1003;
    private static final int RequestFsaBinding = 1002;
    private static final int RequestLogin = 1001;
    private static final int RequestParentalControl = 1004;
    public static String MACHINE_NAME = null;
    public static String pin = null;
    boolean allowMobile; //
    Callback callback;
    Context context;
    Deferred<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> deferredObject;
    private boolean cancelled;
    private NMAFBasicCheckout.NMAFCheckoutData checkoutData;
    //over lay to disable the activity
    private Dialog dialog;
    private Throwable error;
    private Node node;
    private boolean resolved = false;

    public static CheckoutClient create(Node node, boolean download) {
        CheckoutClient ret = new CheckoutClient();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_NODE, node);
        args.putBoolean(Constants.ARG_DOWNLOAD, download);
        ret.setArguments(args);
        return ret;
    }

    private org.jdeferred.Promise<String, Throwable, Void> askForMachineName() {

        if (context == null) return PromiseUtils.reject((Throwable) new Exception("Invalid state"));

        // show an input dialog
        return DialogHelper.createInputDialogPromise(getActivity(), context.getString(R.string.name_this_machine), "").then(new DonePipe<String, String, Throwable, Void>() {
            @Override
            public org.jdeferred.Promise<String, Throwable, Void> pipeDone(String result) {

                // check empty / input format
                if (TextUtils.isEmpty(result) || !result.matches("^[a-zA-Z0-9]{3,10}$")) {

                    // remind user to input a machine name in correct format
                    return DialogHelper.createAlertDialogPromise(context, "", context.getString(R.string.invalid_machine_name_alert), context.getString(R.string.re_enter), false).then(new DonePipe<Void, String, Throwable, Void>() {
                        @Override
                        public org.jdeferred.Promise<String, Throwable, Void> pipeDone(Void result) {
                            // then, ask user to input again, until it is valid or user canceled.
                            return askForMachineName();
                        }
                    });
                } else {
                    // non-empty and correct format, done!
                    return PromiseUtils.resolve(result);
                }
            }
        });
    }

    public CheckoutClient attachTo(FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        context = activity;
        if (activity instanceof Callback && callback == null) {
            // auto set callback if activity implements Callback and no callback instance is assigned yet
            setCallback((Callback) activity);
        }
        fm.beginTransaction().add(this, "checkout").commit();
        return this;
    }

    public Deferred<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> begin() {

        if (deferredObject != null) {
            throw new RuntimeException(this.getClass().getSimpleName() + ".begin can only be invoked once!");
        }

        deferredObject = new DeferredObject<>();

        if (getNode() == null) {
            resolve(null, new Exception("Input is null"), false);
            return new AndroidDeferredObject<>(deferredObject);
        }
        if (!checkNetwork()) {
            resolve(null, new Exception("No network"), false);
            return new AndroidDeferredObject<>(deferredObject);
        }
        checkout();
        return new AndroidDeferredObject<>(deferredObject);
    }

    private void cancel() {
        resolve(null, null, true);
    }

    private boolean checkNetwork() {
        if (!NetWorkUtil.isNetworkConnected(context)) {
            DialogHelper.createErrorNetWorkDialog(context);
            return false;
        } else if (!NetWorkUtil.isWIFI(context) && !new ConfigService(context).isMobileDataEnabled()) {
            DialogHelper.createUnderMobileAlert(context);
            return false;
        }
        return true;
    }

    private void checkout() {
        dialog = DialogHelper.generateProgressLayer(context);
        Node node = getNode();
        NMAFBasicCheckout.ItemType type = node.getCheckoutType();
        String itemId = node.getNodeId();
        L.e(type);
        if (type == null) {
            resolve(null, new Exception("Unknown checkout type"), false);
        } else if (type == NMAFBasicCheckout.ItemType.SVod) {

            // TODO remove play button on a series!
            DebugToast.toast(context, "Checking out a series!");
            resolve(null, new Exception("Checking out a series!"), false);
        } else if (TextUtils.isEmpty(itemId)) {

            resolve(null, new Exception("Invalid item: unable to determine product ID"), false);

        } else {

            Promise<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> checkoutPromise;
            if (type == NMAFBasicCheckout.ItemType.VE) {
                if (node.getVeDetails() == null) {
                    checkoutPromise = VODClient.getInstance().loadVEDetails(node).then(new DonePipe<Node, NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void>() {
                        @Override
                        public Promise<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> pipeDone(Node result) {
                            return checkoutVE();
                        }
                    });
                } else {
                    checkoutPromise = checkoutVE();
                }
            } else {
                checkoutPromise = checkoutBasic(itemId, type);
            }

            checkoutPromise.then(new DoneCallback<NMAFBasicCheckout.NMAFCheckoutData>() {
                @Override
                public void onDone(NMAFBasicCheckout.NMAFCheckoutData result) {
                    resolve(result, null, false);
                }
            }).fail(new FailCallback<Throwable>() {
                @Override
                public void onFail(Throwable throwable) {
                    L.e(throwable);
                    if (throwable instanceof NMAFBasicCheckout.NMAFBasicCheckoutException) {
                        handleError((NMAFBasicCheckout.NMAFBasicCheckoutException) throwable);
                        resolve(null, throwable, false);
                    } else if (throwable instanceof CancelledException) {
                        // user cancelled checkout process
                        resolve(null, null, true);
                    } else {
                        // unknown error!
                        resolve(null, throwable, false);
                    }
                }
            });
        }
    }

    private Promise<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> checkoutBasic(String itemId, NMAFBasicCheckout.ItemType type) {
        final DeferredObject<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> deferred = new DeferredObject<>();

        NMAFBasicCheckout.getSharedInstance().checkout(itemId, type, pin, isDownload(), new NMAFBasicCheckout.CheckoutCallback() {
            @Override
            public void onCheckoutComplete(@NonNull NMAFBasicCheckout.NMAFCheckoutData nmafCheckoutData) {
                deferred.resolve(nmafCheckoutData);
            }

            @Override
            public void onCheckoutFailed(@NonNull Throwable throwable) {
                deferred.reject(throwable);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    private Promise<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> checkoutVE() {

        final WebTVAPIModels.GetProductDetailOutputModel.ProductDetailModel ve = node.getVeDetails();
        if (ve == null) return PromiseUtils.reject((Throwable) new Exception("Invalid VE item"));

        final String movieFormat = node.getVeCheckoutMovieFormat();
        if (movieFormat == null)
            return PromiseUtils.reject((Throwable) new Exception("Invalid movie format"));

        final String paymentType = node.getRentalPaymentType();
        if (paymentType == null)
            return PromiseUtils.reject((Throwable) new Exception("Invalid payment type"));

        float price = node.getRentPrice();
        if (price < 0.f)
            return PromiseUtils.reject((Throwable) new Exception("Invalid rental price"));

        return getOrAskForMachineName().then(new DonePipe<String, NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void>() {
            @Override
            public Promise<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> pipeDone(String machineName) {

                final DeferredObject<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> deferred = new DeferredObject<>();
                CheckoutClient.MACHINE_NAME = machineName;
                NMAFBasicCheckout.getSharedInstance().checkoutVE(ve, pin, movieFormat, paymentType, machineName, new NMAFBasicCheckout.CheckoutCallback() {
                    @Override
                    public void onCheckoutComplete(@NonNull NMAFBasicCheckout.NMAFCheckoutData nmafCheckoutData) {
                        deferred.resolve(nmafCheckoutData);
                    }

                    @Override
                    public void onCheckoutFailed(@NonNull Throwable throwable) {
                        deferred.reject(throwable);
                    }
                });

                return new AndroidDeferredObject<>(deferred);
            }
        });
    }

    public void detach() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().remove(this).commit();
    }

    public NMAFBasicCheckout.NMAFCheckoutData getCheckoutData() {
        return checkoutData;
    }

    public Throwable getError() {
        return error;
    }

    private org.jdeferred.Promise<String, Throwable, Void> getMachineName() {
        final DeferredObject<String, Throwable, Void> deferred = new DeferredObject<>();

        NMAFBasicCheckout.getSharedInstance().getMachineName(new NMAFBasicCheckout.GetMachineNameCallback() {
            @Override
            public void onGetMachineNameFailed(@NonNull Throwable throwable) {
                deferred.resolve(null);
            }

            @Override
            public void onGetMachineNameSuccess(@Nullable String s) {
                deferred.resolve(s);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public Node getNode() {
        if (node == null && getArguments() != null) {
            node = (Node) getArguments().getSerializable(Constants.ARG_NODE);
        }
        return node;
    }

    private org.jdeferred.Promise<String, Throwable, Void> getOrAskForMachineName() {

        if (context == null) return PromiseUtils.reject((Throwable) new Exception("Invalid state"));

        return getMachineName().then(new DonePipe<String, String, Throwable, Void>() {
            @Override
            public org.jdeferred.Promise<String, Throwable, Void> pipeDone(String result) {
                if (!TextUtils.isEmpty(result)) {
                    // if this device is registered, done
                    return PromiseUtils.resolve(result);
                } else {
                    // else, ask user to name this device
                    return askForMachineName();
                }
            }
        });
    }

    private void handleConcurrentPlaying(NMAFBasicCheckout.NMAFBasicCheckoutException error) {
        DialogHelper.createConcurrentDialog(getActivity()).show();
    }

    private void handleDeviceRegistration(NMAFBasicCheckout.NMAFBasicCheckoutException error) {
        Intent intent = new Intent(getActivity(), CheckOutRegistionActivity.class);
        intent.putExtra(Constants.ARG_CHECK_OUT_REQUEST_CODE, "" + error.getErrorNumber());
        startActivityForResult(intent, RequestDeviceRegistration);
    }

    private void handleError(NMAFBasicCheckout.NMAFBasicCheckoutException error) {
        L.e(error);
        String errCode = error.getErrorCode();
        int errNum = error.getErrorNumber();
        if (Check.isEmpty(errCode)) {
            return;
        } else if (errCode.equals("NOT_LOGIN") || errCode.equals("NEED_NOWID")) {
            NowIDClient.getInstance().logout();
            handleLogin(error);
        } else if (errCode.equals("BINDING_NOT_FOUND") || (errCode.equals("NEED_FSA"))) {
            handleFsaBinding(error);
        } else if (errCode.equals("NEED_SUB")) {
            handleSubscription(error);
        } else if (errCode.equals("DEVICE_NOT_REGISTER") || errCode.equals("ACCOUNT_NOT_REGISTER")) {
            handleDeviceRegistration(error);
        } else if (errCode.equals("FIRST_TIME_SETUP")) {
            handleParentalLock(error, true);
        } else if (errCode.equals("NO_PIN") || errCode.equals("INVALID_PIN")) {
            handleParentalLock(error, false);
        } else if (errCode.equals("INVALID_CHECKOUT_PASSWORD")) {
            handleInvalidCheckoutPassword(error);
        } else if (errCode.equals("PERMIT_EXPIRED")) {
            DialogHelper.createUnauthorizedToPlayDialog(getActivity()).show();
        } else if (errCode.equals("PURCHASE_FAIL")) {
            // TODO use correct message
            DialogHelper.createCannotPlayDialog(getActivity());
        } else if (errCode.equals("PURCHASE_TYPE_EXPIRED")) {
            DialogHelper.createCannotPlayDialog(getActivity());
        } else if (errCode.equals("PURCHASED_OTHER_FORMAT")) {
            DialogHelper.createCannotPlayDialog(getActivity());
        } else if (errCode.equals("PURCHASE_ON_OTHER_MACHINE")) {
            DialogHelper.createCannotPlayDialog(getActivity());
        } else if (errCode.equals("ALREADY_PURCHASED_BOTH_FORMAT")) {
            DialogHelper.createCannotPlayDialog(getActivity());
        } else if (errCode.equals("ALREADY_PURCHASED_ONE_OF_TWO_FORMAT")) {
            DialogHelper.createCannotPlayDialog(getActivity());
        } else if (errCode.equals("ALREADY_PURCHASED_ONLY_SINGLE_FORMAT")) {
            DialogHelper.createCannotPlayDialog(getActivity());
        } else if (errCode.equals("CONCURRENT_PURCHASE")) {
            handleConcurrentPlaying(error);
        } else if (errCode.equals("CONCURRENT_PLAYING")) {
            handleConcurrentPlaying(error);
        } else if (errCode.equals("GEO_CHECK_FAIL")) {
            handleGeoCheckFailed(error);
        } else if (errNum == -1004 || errNum == -1009) {
            handleNetworkDisconnection(error);
        } else {
            handleGenericError(error);
        }
    }

    private void handleFsaBinding(NMAFBasicCheckout.NMAFBasicCheckoutException error) {
        Intent intent = new Intent(getActivity(), FSABindingActivity.class);
        startActivityForResult(intent, RequestFsaBinding);
    }

    private void handleGenericError(final NMAFBasicCheckout.NMAFBasicCheckoutException error) {
        // show a generic message
        // TODO use the message provided by nowTV
        DialogHelper.createGeneralDialog(getActivity(), error.getErrorNumber()).show();
    }

    private void handleGeoCheckFailed(NMAFBasicCheckout.NMAFBasicCheckoutException error) {
        L.e(error);
        DialogHelper.createGeoDialog(getActivity()).show();
//        resolve(null, new RuntimeException("Not implemented Geo Check Failed handling yet"), false);
    }

    private void handleInvalidCheckoutPassword(NMAFBasicCheckout.NMAFBasicCheckoutException error) {

        if (getActivity() instanceof VEDollarPINActivity) {
            DialogHelper.createIncorrectPasswordDialog(getActivity()).show();
        } else {
            Judge.checkVE(this, node);
        }
    }

    private void handleLogin(NMAFBasicCheckout.NMAFBasicCheckoutException error) {
        Intent intent = new Intent(getActivity(), LoginActvitiy.class);
        startActivityForResult(intent, RequestLogin);
    }

    private void handleNetworkDisconnection(final NMAFBasicCheckout.NMAFBasicCheckoutException error) {
        // TODO use the message provided by nowTV
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.net_err) + error.getMessage())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resolve(null, error, false);
                    }
                })
                .show();
    }

    private void handleParentalLock(NMAFBasicCheckout.NMAFBasicCheckoutException error, boolean firstTime) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        bundle.putString(Constants.ARG_VIDEO_PIN, pin);
        bundle.putBoolean(Constants.ARG_IS_FIRSTLOCK, firstTime);
        NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_PARENT_CTRL + ":" + RequestParentalControl, bundle);
    }

    private void handleSubscription(NMAFBasicCheckout.NMAFBasicCheckoutException error) {
        DialogHelper.createSubscribeDialog(getActivity()).show();
        resolve(null, error, false);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isDownload() {
        if (getArguments() != null) {
            return getArguments().getBoolean(Constants.ARG_DOWNLOAD);
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestParentalControl:
                pin = data.getStringExtra(Constants.ARG_VIDEO_PIN);
            case RequestLogin:
            case RequestFsaBinding:
            case RequestDeviceRegistration:
                if (resultCode == Constants.SUCCESS_CODE) {
                    checkout();
                } else {
                    cancel();
                }
                break;
            case Constants.REQUEST_VE_CODE:
                if (resultCode == Constants.SUCCESS_CODE) {
                    if (node != null) {
                        if (node.isVE()) {
                            Judge.checkVE(this, node);
                            cancel();
                            break;
                        }
                    }
                } else if (resultCode == Constants.REQUEST_VE_SUCCESS_CODE) {
                    checkout();
                    break;
                }
            default:
                cancel();
        }
    }

    private void resolve(NMAFBasicCheckout.NMAFCheckoutData checkoutData, Throwable error, boolean cancelled) {
        if (resolved) {
            throw new RuntimeException(this.getClass().getSimpleName() + ".resolve can only be invoked once!");
        }
        resolved = true;

        dialog.dismiss();
        this.checkoutData = checkoutData;
        this.error = error;
        this.cancelled = cancelled;

        // callback
        if (callback != null) {
            callback.onCheckoutFinished(this);
        }

        // resolve / reject promise
        if (checkoutData != null) {
            deferredObject.resolve(checkoutData);
        } else if (error != null) {
            deferredObject.reject(error);
        } else {
            deferredObject.reject(new CancelledException());
        }
    }

    public CheckoutClient setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }


    public interface Callback {
        void onCheckoutFinished(CheckoutClient client);
    }
}
