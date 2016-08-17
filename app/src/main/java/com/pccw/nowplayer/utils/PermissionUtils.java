package com.pccw.nowplayer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Swifty on 5 May, 2016.
 * this class is for Android 6.0
 */
public class PermissionUtils {
    public static final int PERMISSIONS_REQUEST_WRITE_CALL_LOG = 100;
    public static final int PERMISSIONS_REQUEST_READ_CALL_LOG = 101;
    public static final int PERMISSIONS_REQUEST_CALL = 102;
    public static final int PERMISSIONS_REQUEST_READ_CONTACT = 103;
    public static final int PERMISSIONS_REQUEST_WRITE_CONTACT = 104;

    /**
     * @param context
     * @return false means Permission has not been granted and will show permission dialog, true means permission has been granted.
     */
    public static boolean checkReadContactPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(context, Manifest.permission.READ_CONTACTS)) {
                if (context instanceof Activity)
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACT);
                return false;
            }
        }
        return true;
    }

    public static boolean checkWriteContactPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(context, Manifest.permission.WRITE_CONTACTS)) {
                if (context instanceof Activity)
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_CONTACTS},
                            PERMISSIONS_REQUEST_WRITE_CONTACT);
                return false;
            }
        }
        return true;
    }

    public static boolean checkReadCallLogPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(context, Manifest.permission.READ_CALL_LOG)) {
                if (context instanceof Activity)
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.READ_CALL_LOG},
                            PERMISSIONS_REQUEST_READ_CALL_LOG);
                return false;
            }
        }
        return true;
    }

    public static boolean checkWriteCallLogPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(context, Manifest.permission.WRITE_CALL_LOG)) {
                if (context instanceof Activity)
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_CALL_LOG},
                            PERMISSIONS_REQUEST_WRITE_CALL_LOG);
                return false;
            }
        }
        return true;
    }

    public static boolean checkCallPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(context, Manifest.permission.CALL_PHONE)) {
                if (context instanceof Activity)
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSIONS_REQUEST_CALL);
                return false;
            }
        }
        return true;
    }

    /**
     * @param fragment
     * @return false means Permission has not been granted and will show permission dialog, true means permission has been granted.
     */
    public static boolean checkReadContactPermission(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(fragment.getContext(), Manifest.permission.READ_CONTACTS)) {
                fragment.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACT);
                return false;
            }
        }
        return true;
    }

    public static boolean checkWriteContactPermission(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(fragment.getContext(), Manifest.permission.WRITE_CONTACTS)) {
                fragment.requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_WRITE_CONTACT);
                return false;
            }
        }
        return true;
    }

    public static boolean checkReadCallLogPermission(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(fragment.getContext(), Manifest.permission.READ_CALL_LOG)) {
                fragment.requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_CALL_LOG);
                return false;
            }
        }
        return true;
    }

    public static boolean checkWriteCallLogPermission(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(fragment.getContext(), Manifest.permission.WRITE_CALL_LOG)) {
                fragment.requestPermissions(new String[]{Manifest.permission.WRITE_CALL_LOG}, PERMISSIONS_REQUEST_WRITE_CALL_LOG);
                return false;
            }
        }
        return true;
    }

    public static boolean checkCallPermission(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(fragment.getContext(), Manifest.permission.CALL_PHONE)) {
                fragment.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL);
                return false;
            }
        }
        return true;
    }

    private static boolean hasPermission(Context context, String permission) {
        if (context == null || permission == null) return false;
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}