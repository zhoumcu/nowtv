package com.pccw.nowplayer.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.mynow.LoginActvitiy;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.exceptions.CancelledException;
import com.pccw.nowplayer.service.ConfigService;
import com.pccw.nowplayer.utils.IntentUtil;
import com.pccw.nowplayer.utils.NetWorkUtil;
import com.pccw.nowplayer.utils.Pref;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowplayer.utils.TypeUtils;

import org.jdeferred.Deferred;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DeferredObject;

import java.io.IOException;

/**
 * Created by Swifty on 5/2/2016.
 */
public class DialogHelper extends ViewHelper {

    private static Dialog loading;

    public static Promise<Void, Throwable, Void> createAlertDialogPromise(Context context, String title, String message, String okButtonText, boolean cancelable) {

        final Deferred<Void, Throwable, Void> deferred = new DeferredObject<>();

        createDialogBuilder(context).setTitle(title).setMessage(message).setPositiveButton(okButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deferred.resolve(null);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                deferred.reject(new CancelledException());
            }
        }).setCancelable(cancelable).create().show();

        return new AndroidDeferredObject<>(deferred);
    }

    public static void createCannotPlayDialog(Context context) {
        DialogHelper.createOneButtonAlertDialog(context, context.getString(R.string.sorry), context.getString(R.string.cannot_be_played));
    }

    public static Dialog createConcurrentDialog(final Activity context) {
        Dialog dialog = createDialogBuilder(context).setMessage(context.getString(R.string.general_err)).setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        return dialog;
    }

    public static AlertDialog createConnectionLostDialog(final Activity context) {
        return new AlertDialog.Builder(context).setMessage(R.string.connection_lost)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public static AlertDialog createDecodeBusyDialog(final Activity context) {
        return new AlertDialog.Builder(context).setTitle(R.string.sorry).setMessage(R.string.decode_busy)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public static AlertDialog.Builder createDialogBuilder(Context context) {
        return new AlertDialog.Builder(context);
    }

    public static AlertDialog createEndOfProgramDialog(final Activity context) {
        return new AlertDialog.Builder(context).setTitle(R.string.end_of_program).setMessage(R.string.end_of_the_program)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.finish();
                    }
                }).create();
    }

    public static void createErrorNetWorkDialog(Context context) {
        createOneButtonAlertDialog(context, context.getString(R.string.connect_lost), context.getString(R.string.connect_lost_content), context.getString(R.string.ok));
    }

    public static Dialog createGeneralDialog(final Activity context, int code) {
        Dialog dialog = createDialogBuilder(context).setTitle(R.string.sorry).setMessage(String.format(context.getString(R.string.general_err), code + "")).setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        return dialog;
    }

    public static Dialog createGeoDialog(final Activity context) {
        Dialog dialog = createDialogBuilder(context).setTitle(R.string.sorry).setMessage(R.string.geo_err).setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.finish();
                dialog.dismiss();
            }
        }).create();
        return dialog;
    }

    public static Dialog createIncorrectPasswordDialog(final Activity context) {
        Dialog dialog = createDialogBuilder(context).setMessage(R.string.incorrect_password).setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        return dialog;
    }

    public static void createInputDialog(Context context, String string, final inputDialogCallBack inputDialogCallBack) {
        createInputDialog(context, string, null, inputDialogCallBack);
    }

    public static void createInputDialog(Context context, String string, String defaultText, final inputDialogCallBack inputDialogCallBack) {
        final EditText et = new EditText(context);
        et.setText(defaultText);
        et.setWidth(TypeUtils.dpToPx(context, 150));
        et.setSingleLine(true);
        et.setTextColor(context.getResources().getColor(R.color.themeColor));
        new AlertDialog.Builder(context)
                .setTitle(string)
                .setCancelable(false)
                .setView(et)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (inputDialogCallBack != null)
                            inputDialogCallBack.inputTextConfirm(dialog, input, which);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (inputDialogCallBack != null)
                            inputDialogCallBack.inputTextCancel(dialog);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static Promise<String, Throwable, Void> createInputDialogPromise(Context context, String title, String defaultText) {

        final DeferredObject<String, Throwable, Void> deferred = new DeferredObject<>();

        createInputDialog(context, title, defaultText, new inputDialogCallBack() {
            @Override
            public void inputTextCancel(DialogInterface dialog) {
                deferred.reject(new CancelledException());
            }

            @Override
            public void inputTextConfirm(DialogInterface dialog, String inputText, int which) {
                deferred.resolve(inputText);
            }
        });

        return new AndroidDeferredObject(deferred);
    }

    public static Dialog createInvalidPassportDialog(final Activity context) {
        Dialog dialog = createDialogBuilder(context).setMessage(R.string.invalid_identity).setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        return dialog;
    }

    public static AlertDialog createLoginDialog(final Activity context) {
        return new AlertDialog.Builder(context).setTitle(R.string.please_login).setMessage(R.string.need_login)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LoginActvitiy.class);
                        context.startActivityForResult(intent, Constants.REQUEST_CODE);
                    }
                })
                .setNegativeButton(R.string.cancel, null).create();
    }

    /**
     * Press "Continue" to continue playback. If not close in 1min, stop playback
     *
     * @param context
     * @return
     */
    public static AlertDialog createLongPlayPromptDialog(final Activity context) {
        return new AlertDialog.Builder(context).setTitle(R.string.overtime_playing_alert).setMessage(R.string.long_play_prompt)
                .setPositiveButton(R.string.alert_continue, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public static Promise<Boolean, Throwable, Void> createMobileNetworkAlertPromise(final Context context) {
        if (!NetWorkUtil.isWIFI(context) && !new ConfigService(context).isMobileDataEnabled()) {
            return DialogHelper.createUnderMobileAlert(context).then(new DonePipe<Boolean, Boolean, Throwable, Void>() {
                @Override
                public Promise<Boolean, Throwable, Void> pipeDone(Boolean result) {
                    return PromiseUtils.reject((Throwable) new Exception());
                }
            });
        }
        return PromiseUtils.nil();
    }

    public static AlertDialog createNotSupportHDDDialog(final Activity context) {
        return new AlertDialog.Builder(context).setTitle(R.string.sorry).setMessage(R.string.not_support_hdd)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public static void createOneButtonAlertDialog(Context context, String content) {
        createOneButtonAlertDialog(context, null, content);
    }

    public static void createOneButtonAlertDialog(Context context, String title, String content) {
        createOneButtonAlertDialog(context, title, content, context.getString(R.string.back));
    }

    public static void createOneButtonAlertDialog(Context context, String title, String content, String button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(content);
        builder.setNegativeButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public static AlertDialog createOnlyScreencastDialog(final Activity context) {
        return new AlertDialog.Builder(context).setTitle(R.string.sorry).setMessage(R.string.only_screen_cast)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public static Dialog createParentalActivatedDialog(final Activity context) {

        String msg = context.getString(R.string.parental_alert_msg) + "\n" + context.getString(R.string.parental_alert_attention);

        Dialog dialog = createDialogBuilder(context).setTitle(R.string.parental_activated).setMessage(msg).setPositiveButton(context.getString(R.string.play), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.setResult(Constants.SUCCESS_CODE);
                dialog.dismiss();
                context.finish();

            }
        }).create();
        return dialog;
    }

    public static Dialog createProgressDialog(Context context, String text) {
        ProgressDialog dialog = ProgressDialog.show(context, null, text, false);
        return dialog;
    }

    public static Dialog createProgressDialog(Context context, String title, String text) {
        ProgressDialog dialog = ProgressDialog.show(context, title, text, false);
        return dialog;
    }

    public static Dialog createProgressDialog(Context context, String title, String text, boolean cancleable) {
        ProgressDialog dialog = ProgressDialog.show(context, title, text, cancleable);
        return dialog;
    }

    public static Dialog createRequstFailDialog(final Activity context) {
        Dialog dialog = createDialogBuilder(context).setMessage(context.getString(R.string.request_fail)).setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.finish();
                dialog.dismiss();
            }
        }).create();
        return dialog;
    }

    public static void createStreamQualityDialog(final Activity context) {
        if (Pref.getPref().getBool(Constants.PREF_SHOW_STREAM_QUALITY, true)) {
            Pref.getPref().putBool(Constants.PREF_SHOW_STREAM_QUALITY, false);
            new AlertDialog.Builder(context).setMessage(R.string.stream_quality_alert)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    public static Dialog createSubscribeDialog(final Context context) {
        Dialog dialog = createDialogBuilder(context).setTitle(Html.fromHtml(context.getString(R.string.content_not_subscribe))).setMessage(String.format(context.getString(R.string.not_subscribe_content), context.getString(R.string.subscribe_number))).setPositiveButton(context.getString(R.string.call_now), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                IntentUtil.call(context, context.getString(R.string.subscribe_number));
                dialog.dismiss();
            }
        }).setNegativeButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        }).create();
        return dialog;
    }

    public static void createTAndCDialog(final Context context, DialogInterface.OnClickListener declineAction, DialogInterface.OnClickListener accept) {
        try {
            String s = StringUtils.is2String(context.getAssets().open(context.getString(R.string.TandCPath)));
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.t_and_c)).setMessage(s);
            builder.setPositiveButton(context.getString(R.string.accept), accept)
                    .setNegativeButton(context.getString(R.string.decline), declineAction);
            builder.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Dialog createTwoButtonDialog(Context context, String message, DialogInterface.OnClickListener postionButton, DialogInterface.OnClickListener negativeButton) {
        return createTwoButtonDialog(context, null, message, postionButton, negativeButton);
    }

    public static Dialog createTwoButtonDialog(Context context, String title, String message, DialogInterface.OnClickListener postionButton, DialogInterface.OnClickListener negativeButton) {
        Dialog dialog = createDialogBuilder(context).setTitle(title).setMessage(message).setPositiveButton(context.getString(R.string.reconnect), postionButton).setNegativeButton(context.getString(R.string.exit), negativeButton).setCancelable(false).create();
        return dialog;
    }

    public static AlertDialog createUnauthorizedToPlayDialog(final Activity context) {
        return new AlertDialog.Builder(context).setTitle(R.string.sorry).setMessage(R.string.unauthorized_to_play)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.finish();
                    }
                }).create();
    }

    public static Promise<Boolean, Void, Void> createUnderMobileAlert(final Context context) {

        final Deferred<Boolean, Void, Void> deferred = new DeferredObject<>();

        Dialog dialog = createDialogBuilder(context).setMessage(R.string.setting_network_data_please)
                .setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deferred.resolve(Boolean.valueOf(false));
                    }
                }).setNegativeButton(context.getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_SETTING);
                        deferred.resolve(Boolean.valueOf(true));
                    }
                }).create();
        dialog.show();

        return new AndroidDeferredObject<>(deferred);
    }

    public static AlertDialog createWatchlistFullDialog(final Activity context) {
        return new AlertDialog.Builder(context).setTitle(R.string.sorry).setMessage(R.string.watchlist_is_full)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public static Dialog generateProgressLayer(Context context) {

        if (loading != null) {
            loading.dismiss();
        }
        loading = new Dialog(context, R.style.theme_dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.view_dialog_layer, null);
        layout.setBackgroundResource(R.color.transparent);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.setContentView(layout, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loading.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                // TODO Auto-generated method stub
                loading.dismiss();
            }
        });
        loading.show();
        return loading;

    }

    public static View generateProgressLayer(Context context, FrameLayout frameLayout) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.view_dialog_layer, null);
        frameLayout.addView(layout);
        return layout;
    }

    public static void removeProgressLayer(Context context, FrameLayout frameLayout) {

        View view = frameLayout.findViewById(R.id.ll_progress_layer);
        if (view != null) {
            frameLayout.removeView(view);
        }
    }

    public static void requestPassport(Context context, final String[] options, final inputDialogCallBack callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null)
                    callback.inputTextConfirm(dialog, options[which], which);
            }
        });
        builder.show();
    }

    public static void requestResume(Context context, String title, final inputDialogCallBack callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        final String[] options = {context.getString(R.string.last_viewed_scene), context.getString(R.string.play_from_beginning)};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null)
                    callback.inputTextConfirm(dialog, options[which], which);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null)
                    callback.inputTextCancel(dialog);
            }
        });
        builder.show();
    }

    public static Dialog wifiToMobileNetworkAlert(Activity context, DialogInterface.OnClickListener onClickListener) {
        Dialog dialog = createDialogBuilder(context).setTitle(R.string.attention).setMessage(R.string.wifiToMobileNetwork).setPositiveButton(context.getString(R.string.ok), onClickListener).create();
        dialog.show();
        return dialog;
    }

    public interface inputDialogCallBack {
        void inputTextCancel(DialogInterface dialog);

        void inputTextConfirm(DialogInterface dialog, String inputText, int which);
    }
}
