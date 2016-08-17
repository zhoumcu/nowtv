package com.pccw.nowplayer.model;

import android.content.Intent;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.model.exceptions.LibraryException;
import com.pccw.nowplayer.model.node.BaseNode;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowtv.nmaf.npx.mynow.DataModels;
import com.pccw.nowtv.nmaf.npx.mynow.NPXMyNow;

import org.jdeferred.DonePipe;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kriz on 2016-06-18.
 */
public class WatchListClient {
    public static final String BROADCAST_WATCH_LIST_ON_LOAD = "com.pccwnow.player.watchlist.onload";

    private static WatchListClient instance;
    private Set<String> watchListKeys;

    private WatchListClient() {
    }

    public static WatchListClient getInstance() {
        if (instance == null) {
            synchronized (WatchListClient.class) {
                if (instance == null) {
                    instance = new WatchListClient();
                }
            }
        }
        return instance;
    }

    public org.jdeferred.Promise<Void, Throwable, Void> addToWatchList(final Node node) {

        if (node == null) return PromiseUtils.resolve(null);

        final DataModels.NPXMyNowAddWatchlistItemDataModel watchListItem = node.makeWatchListItem();
        if (watchListItem == null) return PromiseUtils.resolve(null);

        final DeferredObject<Void, Throwable, Void> deferred = new DeferredObject<>();

        List<DataModels.NPXMyNowAddWatchlistItemDataModel> list = new ArrayList<>();
        list.add(watchListItem);

        NPXMyNow.getSharedInstance().addWatchlistItems(list, new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowAddWatchlistItemOutputModel>() {
            @Override
            public void onRequestFailed(final int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXMyNowAddWatchlistItemOutputModel output) {
                deferred.resolve(null);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public boolean isInWatchList(BaseNode node) {
        if (node == null || watchListKeys == null) return false;

        for (String key : node.getWatchListKeys()) {
            if (watchListKeys.contains(key)) return true;
        }

        return false;
    }

    public org.jdeferred.Promise<List<Node>, Throwable, Float> loadWatchList() {

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        NPXMyNow.getSharedInstance().getWatchlistItem(new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowGetWatchlistItemOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new Exception("Server side error: " + i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXMyNowGetWatchlistItemOutputModel response) {
                List<Node> list = Node.createList(response == null ? null : response.response, null);
                Set<String> keys = new HashSet<>();

                for (Node node : list) {
                    keys.addAll(node.getWatchListKeys());
                }
                WatchListClient.this.watchListKeys = keys;

                PlayerApplication.getContext().sendBroadcast(new Intent(BROADCAST_WATCH_LIST_ON_LOAD));

                deferred.resolve(list);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public org.jdeferred.Promise<List<Node>, Throwable, Float> loadWatchListAndDetails() {
        return this.loadWatchList().then(new DonePipe<List<Node>, List<Node>, Throwable, Float>() {
            @Override
            public org.jdeferred.Promise<List<Node>, Throwable, Float> pipeDone(List<Node> result) {
                return CatalogClient.getInstance().loadDetails(result, true);
            }
        });
    }

    public org.jdeferred.Promise<Void, Throwable, Void> removeFromWatchList(final Node node) {

        if (node == null) return PromiseUtils.resolve(null);

        final DataModels.NPXMyNowRemoveWatchlistItemDataModel watchListItem = node.makeWatchListRemoveItem();
        if (watchListItem == null) return PromiseUtils.resolve(null);

        final DeferredObject<Void, Throwable, Void> deferred = new DeferredObject<>();

        List<DataModels.NPXMyNowRemoveWatchlistItemDataModel> list = new ArrayList<>();
        list.add(watchListItem);

        NPXMyNow.getSharedInstance().removeWatchlistItems(list, new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowAddWatchlistItemOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXMyNowAddWatchlistItemOutputModel npxMyNowAddWatchlistItemOutputModel) {
                deferred.resolve(null);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public org.jdeferred.Promise<Boolean, Throwable, Void> toggleWatchListItem(final Node node) {
        return PromiseUtils.ignoreError(isInWatchList(node) ? removeFromWatchList(node) : addToWatchList(node))
                .then(PromiseUtils.<Void, List<Node>, Throwable, Float>next(loadWatchList()))
                .then(PromiseUtils.<List<Node>, Boolean, Throwable, Void>resolveAs(isInWatchList(node)));
    }
}
