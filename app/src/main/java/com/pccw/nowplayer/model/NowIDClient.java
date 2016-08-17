package com.pccw.nowplayer.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pccw.nowplayer.utils.DeviceManager;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowtv.nmaf.networking.WebTVAPIModels;
import com.pccw.nowtv.nmaf.nowID.NMAFnowID;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DeferredManager;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kriz on 4/7/2016.
 */
public class NowIDClient {

    private static NowIDClient instance;
    private final List<DeferredObject> sessionDataLoadPromises = new ArrayList<>();
    private boolean sessionDataLoaded;

    private NowIDClient() {
    }

    public static NowIDClient getInstance() {
        if (instance == null) {
            synchronized (NowIDClient.class) {
                if (instance == null) {
                    instance = new NowIDClient();
                }
            }
        }
        return instance;
    }

    public long getDollarExpiry() {
        NMAFnowID api = NMAFnowID.getSharedInstance();
        if (api == null) return 0l;

        WebTVAPIModels.UpdateSessionOutputModel session = api.getUpdateSessionOutputModel();
        if (session == null) return 0l;

        return session.nowDollarExpiryTime;
    }

    public String getFsa() {
        NMAFnowID api = NMAFnowID.getSharedInstance();
        if (api == null) return null;
        return api.getFsa();
    }

    public float getNowDollarBalance() {
        NMAFnowID api = NMAFnowID.getSharedInstance();
        if (api == null) return 0.f;

        WebTVAPIModels.UpdateSessionOutputModel session = api.getUpdateSessionOutputModel();
        if (session == null) return 0.f;

        return session.nowDollar;
    }

    public String getNowId() {
        NMAFnowID api = NMAFnowID.getSharedInstance();
        if (api == null) return null;
        return api.getNowId();
    }

    public boolean isFSABound() {
        return !TextUtils.isEmpty(getFsa());
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getNowId());
    }

    public org.jdeferred.Promise<String, Throwable, Void> loadNowDollarTopUpUrl() {
        final DeferredObject<String, Throwable, Void> deferred = new DeferredObject<>();
        NMAFnowID.getSharedInstance().getNowDollarTopUpUrl(new NMAFnowID.GetNowDollarTopUpUrlCallback() {
            @Override
            public void onGetNowDollarTopUpUrlFailed(@NonNull Throwable throwable) {
                deferred.reject(throwable);
            }

            @Override
            public void onGetNowDollarTopUpUrlSuccess(@NonNull String s) {
                deferred.resolve(s);
            }
        });
        return new AndroidDeferredObject<>(deferred);
    }

    public Promise logout() {
        NMAFnowID.getSharedInstance().logout();
        DeviceManager.getInstance().clearLocalDevice();
        return updateSessionData();
    }

    private void onSessionDataLoaded() {
        sessionDataLoaded = true;

        ArrayList<DeferredObject> promises;
        synchronized (sessionDataLoadPromises) {
            promises = new ArrayList<>(sessionDataLoadPromises);
            sessionDataLoadPromises.clear();
        }

        for (DeferredObject p : promises) {
            p.resolve(null);
        }
    }

    public Promise onceSessionDataLoaded() {
        if (sessionDataLoaded) return PromiseUtils.resolve(null);
        DeferredObject deferred = new DeferredObject();
        synchronized (sessionDataLoadPromises) {
            sessionDataLoadPromises.add(deferred);
        }
        return new AndroidDeferredObject(deferred);
    }

    public Promise updateSessionData() {
        DeferredManager dm = new DefaultDeferredManager();
        Promise promise = dm.when(
                PromiseUtils.ignoreError(DeviceManager.getInstance().retrieveDevice()),
                PromiseUtils.ignoreError(FavoriteChannelsClient.getInstance().loadFavoriteChannelList()),
                PromiseUtils.ignoreError(WatchListClient.getInstance().loadWatchList())
        ).always(new AlwaysCallback() {
            @Override
            public void onAlways(Promise.State state, Object resolved, Object rejected) {
                onSessionDataLoaded();
            }
        });
        return new AndroidDeferredObject(promise);
    }
}
