package com.pccw.nowplayer.model;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.activity.TVGuideChannelDetailActivity;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.exceptions.LibraryException;
import com.pccw.nowplayer.model.node.BaseNode;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;
import com.pccw.nowtv.nmaf.npx.catalog.NPXCatalog;

import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kriz on 2/7/2016.
 */
public class FavoriteChannelsClient {

    public static final String BROADCAST_FAVORITE_CHANNELS_ON_LOAD = "com.pccwnow.player.favorite.onload";
    private static FavoriteChannelsClient instance;
    private Set<Integer> favoriteChannelIds;

    private FavoriteChannelsClient() {
    }

    public static FavoriteChannelsClient getInstance() {
        if (instance == null) {
            synchronized (FavoriteChannelsClient.class) {
                if (instance == null) {
                    instance = new FavoriteChannelsClient();
                }
            }
        }
        return instance;
    }

    public Promise<Node, Throwable, Float> addFavoriteChannel(final Node node) {
        final DeferredObject<Node, Throwable, Float> deferred = new DeferredObject<>();
        NPXCatalog.getSharedInstance().setFavoriteChannel(String.valueOf(node.getChannelId()), true, new NPXCatalog.NPXCatalogCallback<DataModels.NPXAddFavoriteChannelOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new Exception("Server side error: " + i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXAddFavoriteChannelOutputModel npxAddFavoriteChannelOutputModel) {
                deferred.resolve(node);
            }
        });
        return deferred.then(new DonePipe<Node, List<Node>, Throwable, Float>() {
            @Override
            public Promise<List<Node>, Throwable, Float> pipeDone(Node result) {
                return loadFavoriteChannelList();
            }
        }).then(new DonePipe<List<Node>, Node, Throwable, Float>() {
            @Override
            public Promise<Node, Throwable, Float> pipeDone(List<Node> result) {
                return new AndroidDeferredObject(new DeferredObject<Node, Throwable, Float>().resolve(node));
            }
        });
    }

    public boolean isFavoriteChannel(BaseNode node) {

        if (node == null) return false;
        int chId = node.getChannelId();
        if (favoriteChannelIds != null && favoriteChannelIds.contains(Integer.valueOf(chId)))
            return true;
        return false;
    }

    public Promise<List<Node>, Throwable, Float> loadFavoriteChannelList() {

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        DeferredManager dm = new DefaultDeferredManager();
        dm.when(loadFavoriteList(), EPGClient.getInstance().loadChannelList()).then(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                List<Node> ret = new ArrayList<>();

                List<Integer> favChIds = (List<Integer>) result.get(0).getResult();
                List<Node> chList = (List<Node>) result.get(1).getResult();

                SparseArray<Node> chMap = new SparseArray<>();
                if (chList != null) for (Node ch : chList) {
                    chMap.put(ch.getChannelId(), ch);
                }

                if (favChIds != null) for (Integer chId : favChIds) {
                    Node ch = chMap.get(chId.intValue());
                    if (ch != null) ret.add(ch);
                }

                deferred.resolve(ret);
            }
        }).fail(new FailCallback<OneReject>() {
            @Override
            public void onFail(OneReject result) {
                deferred.reject(new Exception());
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public Promise<List<Integer>, Throwable, Float> loadFavoriteList() {

        final DeferredObject<List<Integer>, Throwable, Float> deferred = new DeferredObject<>();

        NPXCatalog.getSharedInstance().getFavoriteChannel(new NPXCatalog.NPXCatalogCallback<DataModels.NPXGetFavoriteChannelOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXGetFavoriteChannelOutputModel response) {

                List<Integer> channelIds = new ArrayList<>();

                if (response != null && response.response != null) {
                    for (DataModels.NPXGetFavoriteChannelResponseModel ch : response.response) {
                        if (ch.enabled) channelIds.add(TypeUtils.toInt(ch.channelId, 0));
                    }
                }

                favoriteChannelIds = new HashSet<>(channelIds);

                PlayerApplication.getContext().sendBroadcast(new Intent(BROADCAST_FAVORITE_CHANNELS_ON_LOAD));

                deferred.resolve(channelIds);
            }
        });
        return new AndroidDeferredObject<>(deferred);
    }

    public Promise<Node, Throwable, Float> removeFavoriteChannel(final Node node) {
        final DeferredObject<Node, Throwable, Float> deferred = new DeferredObject<>();
        NPXCatalog.getSharedInstance().removeFavoriteChannel(String.valueOf(node.getChannelId()), new NPXCatalog.NPXCatalogCallback<DataModels.NPXAddFavoriteChannelOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new Exception("Server side error: " + i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXAddFavoriteChannelOutputModel npxAddFavoriteChannelOutputModel) {
                deferred.resolve(node);
            }
        });
        return deferred.then(new DonePipe<Node, List<Node>, Throwable, Float>() {
            @Override
            public Promise<List<Node>, Throwable, Float> pipeDone(Node result) {
                return loadFavoriteChannelList();
            }
        }).then(new DonePipe<List<Node>, Node, Throwable, Float>() {
            @Override
            public Promise<Node, Throwable, Float> pipeDone(List<Node> result) {
                return new AndroidDeferredObject(new DeferredObject<Node, Throwable, Float>().resolve(node));
            }
        });
    }

    public Promise<Node, Throwable, Float> toggleFavoriteChannel(Context context, Node channel) {
        if (NowIDClient.getInstance().isLoggedIn()) {
            if (isFavoriteChannel(channel)) {
                return removeFavoriteChannel(channel);
            } else {
                return addFavoriteChannel(channel);
            }
        } else {
            NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_LOGIN);
            final DeferredObject<Node, Throwable, Float> deferred = new DeferredObject<>();
            return deferred.reject(new Exception("need login first"));
        }
    }
}
