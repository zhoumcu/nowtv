package com.pccw.nowplayer.model;

import com.pccw.nowplayer.model.exceptions.LibraryException;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ListUtils;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowtv.nmaf.npx.catalog.NPXCatalog;
import com.pccw.nowtv.nmaf.npx.mynow.DataModels;
import com.pccw.nowtv.nmaf.npx.mynow.NPXMyNow;

import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kriz on 4/7/2016.
 */
public class MyNowClient {

    private static MyNowClient instance;

    private MyNowClient() {
    }

    public static MyNowClient getInstance() {
        if (instance == null) {
            synchronized (MyNowClient.class) {
                if (instance == null) {
                    instance = new MyNowClient();
                }
            }
        }
        return instance;
    }

    public Promise<Boolean, Throwable, Float> addHistory(final Node node) {

        // firstly, prepare the program to add to history
        return PromiseUtils.firstly(new DonePipe<Void, Node, Throwable, Float>() {
            @Override
            public Promise<Node, Throwable, Float> pipeDone(Void result) {
                if (node.isEPG() && !node.isProgram() && node.getChannelId() != 0) {
                    return EPGClient.getInstance().loadLiveProgram(node);
                }
                return PromiseUtils.resolve(node);
            }
        }).then(new DonePipe<Node, Boolean, Throwable, Float>() {
            @Override
            public Promise<Boolean, Throwable, Float> pipeDone(Node program) {

                final DeferredObject<Boolean, Throwable, Float> deferred = new DeferredObject<>();

                if (program != null) {

                    DataModels.NPXMyNowAddWatchlistItemDataModel model = program.getHistoryModel();

                    if (model == null) {
                        deferred.resolve(false);
                    } else {

                        NPXMyNow.getSharedInstance().addHistoryItems(ListUtils.make(model), new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowAddWatchlistItemOutputModel>() {
                            @Override
                            public void onRequestFailed(int i) {
                                deferred.reject(new LibraryException(i));
                            }

                            @Override
                            public void onRequestSuccessful(DataModels.NPXMyNowAddWatchlistItemOutputModel response) {
                                deferred.resolve(true);
                            }
                        });
                    }
                } else {
                    deferred.resolve(false);
                }

                return new AndroidDeferredObject<>(deferred);
            }
        });
    }

    public Promise<Void, Throwable, Float> clearHistory() {

        return loadHistory().then(new DonePipe<DataModels.NPXMyNowAddWatchlistItemDataModel[], Void, Throwable, Float>() {
            @Override
            public Promise<Void, Throwable, Float> pipeDone(DataModels.NPXMyNowAddWatchlistItemDataModel[] list) {

                final DeferredObject<Void, Throwable, Float> deferred = new DeferredObject<>();

                // convert object type
                List<DataModels.NPXMyNowRemoveWatchlistItemDataModel> items = new ArrayList<>();
                if (list != null) for (DataModels.NPXMyNowAddWatchlistItemDataModel obj : list) {
                    items.add(new DataModels.NPXMyNowRemoveWatchlistItemDataModel(obj.itemId, obj.itemType));
                }

                // call remove
                NPXMyNow.getSharedInstance().removeHistoryItems(items, new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowAddWatchlistItemOutputModel>() {
                    @Override
                    public void onRequestFailed(int i) {
                        deferred.reject(new LibraryException(i));
                    }

                    @Override
                    public void onRequestSuccessful(DataModels.NPXMyNowAddWatchlistItemOutputModel result) {
                        deferred.resolve(null);
                    }
                });

                return new AndroidDeferredObject<>(deferred);
            }
        });
    }

    public Promise<DataModels.NPXMyNowAddWatchlistItemDataModel[], Throwable, Float> loadHistory() {

        final DeferredObject<DataModels.NPXMyNowAddWatchlistItemDataModel[], Throwable, Float> deferred = new DeferredObject<>();

        NPXMyNow.getSharedInstance().getHistoryItem(new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowGetWatchlistItemOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXMyNowGetWatchlistItemOutputModel response) {
                deferred.resolve(response == null ? null : response.response);
            }
        });

        return deferred;
    }


    /**
     * @return History list under My Now.
     */
    public org.jdeferred.Promise<List<Node>, Throwable, Float> loadHistoryWithDetails() {

        return loadHistory().then(new DonePipe<DataModels.NPXMyNowAddWatchlistItemDataModel[], List<Node>, Throwable, Float>() {
            @Override
            public Promise<List<Node>, Throwable, Float> pipeDone(DataModels.NPXMyNowAddWatchlistItemDataModel[] result) {
                List<Node> list = Node.createList(result, null);
                return CatalogClient.getInstance().loadDetails(list, true);
            }
        });
    }

    /**
     * @return Saved list under My Now.
     */
    public org.jdeferred.Promise<List<Node>, Throwable, Float> loadSavedList() {

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        NPXMyNow.getSharedInstance().getSavedList(new NPXCatalog.NPXCatalogCallback<DataModels.NPXMyNowGetSavedListResponseModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXMyNowGetSavedListResponseModel response) {
                List<Node> list = null;
                if (response != null && response.products != null && response.products.result != null) {
                    list = Node.createList(response.products.result, null);
                }
                deferred.resolve(list);
            }
        });

        return deferred.then(new DonePipe<List<Node>, List<Node>, Throwable, Float>() {
            @Override
            public org.jdeferred.Promise<List<Node>, Throwable, Float> pipeDone(List<Node> result) {
                return CatalogClient.getInstance().loadDetails(result, false);
            }
        });
    }
}
