package com.pccw.nowplayer.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Is;
import com.pccw.nowplayer.utils.ListUtils;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.networking.WebTVAPIModels;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;
import com.pccw.nowtv.nmaf.npx.catalog.NPXCatalog;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;
import org.osito.androidpromise.deferred.Deferred;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kriz on 2016-06-16.
 */
public class VODClient {

    private static VODClient instance;

    private VODClient() {
    }

    public static VODClient getInstance() {
        if (instance == null) {
            synchronized (VODClient.class) {
                if (instance == null) {
                    instance = new VODClient();
                }
            }
        }
        return instance;
    }

    private boolean isSubscribed(DataModels.NPXGetVodCategoryNodeOutputModel response) {
        if (response == null) return false;

        if (response.productList != null) for (DataModels.NPXGetVodLandingListProductModel product : response.productList) {
            // look at the first product
            return product.isSubscribed;
        }

        // no product, look at the cats
        if (response.categoryList != null) for (DataModels.NPXGetVodLandingListListModel cat : response.categoryList) {
            // look at the first product
            if (cat == null) continue;
            if (cat.productList != null) for (DataModels.NPXGetVodLandingListProductModel product : cat.productList) {
                return product.isSubscribed;
            }
            // no product.. look at the sub-cats
            if (cat.categoryList != null) for (DataModels.NPXGetVodLandingListCategoryModel subCat : cat.categoryList) {
                return subCat.isSubscribed;
            }
        }
        // damn
        return false;
    }

    public org.osito.androidpromise.deferred.Promise<Node> loadLanding() {

        final Deferred deferred = Deferred.newDeferred();

        NPXCatalog.getSharedInstance().getVodLandingCatalogs(new NPXCatalog.NPXCatalogCallback<DataModels.NPXGetVodLandingPageOutputModel>() {

            @Override
            public void onRequestFailed(int i) {
                deferred.reject();
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXGetVodLandingPageOutputModel npxGetVodLandingPageOutputModel) {
                deferred.resolve(Node.create(npxGetVodLandingPageOutputModel, null));
            }
        });
        return deferred;
    }

    public Promise<Node, Throwable, Float> loadProgramDetails(final Node node) {
        if (node == null) return PromiseUtils.resolve(node);
        return loadProgramDetailsList(ListUtils.make(node)).then(new DoneFilter<List<Node>, Node>() {
            @Override
            public Node filterDone(List<Node> result) {
                return ListUtils.first(result);
            }
        });
    }

    public Promise<List<Node>, Throwable, Float> loadProgramDetailsList(List<Node> nodes) {

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        ArrayList<String> ids = new ArrayList<>();
        final Map<String, String> genreIdMap = new HashMap<>();
        if (nodes != null) for (Node node : nodes) {
            ids.add(node.getNodeId());
            genreIdMap.put(node.getNodeId(), node.getRecommendationGenreId()); // preserve the recommendation genre-ids
        }
        if (ids.size() == 0) return new AndroidDeferredObject<>(deferred.resolve(null));

        NPXCatalog.getSharedInstance().getVodProductDetailsU3(ids, new NPXCatalog.NPXCatalogCallback<DataModels.NPXGetVodProductDetailU3OutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new Exception("Server side error " + i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXGetVodProductDetailU3OutputModel output) {
                DataModels.NPXGetVodProductDetailU3DataModel[] products = (output != null && output.productDetailList != null && output.productDetailList.length > 0) ? output.productDetailList : null;
                List<Node> list = Node.createList(products, null);

                // pass on the recommendation genre-ids
                for (Node node : list) {
                    String genreId = genreIdMap.get(node.getNodeId());
                    node.setRecommendationGenreId(genreId);
                }

                deferred.resolve(list);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public Promise<List<Node>, Throwable, Float> loadProgramDetailsListWithVEDetails(List<Node> nodes) {

        return loadProgramDetailsList(nodes).then(new DonePipe<List<Node>, List<Node>, Throwable, Float>() {
            @Override
            public Promise<List<Node>, Throwable, Float> pipeDone(List<Node> result) {
                return loadVEDetailsList(result);
            }
        });
    }

    public Promise<Node, Throwable, Float> loadProgramOrSeriesDetailsWithVEDetails(final Node node) {

        if (node == null) return PromiseUtils.resolve(node);

        boolean isSeries = !TextUtils.isEmpty(node.getSeriesId());
        final String id = isSeries ? node.getSeriesId() : node.getNodeId();
        if (TextUtils.isEmpty(id)) return PromiseUtils.resolve(node);

        return new AndroidDeferredObject<>((isSeries ? loadSeriesDetails(node) : loadProgramDetails(node)).then(new DonePipe<Node, Node, Throwable, Float>() {
            @Override
            public Promise<Node, Throwable, Float> pipeDone(Node result) {
                if (result != null && result.isVE()) {
                    return loadVEDetails(result);
                } else {
                    return PromiseUtils.resolve(result);
                }
            }
        }));
    }

    public Promise<Node, Throwable, Float> loadSeriesDetails(final Node node) {
        return loadSeriesDetailsList(ListUtils.make(node)).then(new DoneFilter<List<Node>, Node>() {
            @Override
            public Node filterDone(List<Node> result) {
                return ListUtils.first(result);
            }
        });
    }

    public Promise<List<Node>, Throwable, Float> loadSeriesDetailsList(List<Node> nodes) {

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        ArrayList<String> ids = new ArrayList<>();
        final Map<String, String> genreIdMap = new HashMap<>();
        if (nodes != null) for (Node node : nodes) {
            ids.add(node.getSeriesId());
            genreIdMap.put(node.getNodeId(), node.getRecommendationGenreId()); // preserve the recommendation genre-ids
        }
        if (ids.size() == 0) return new AndroidDeferredObject<>(deferred.resolve(null));

        NPXCatalog.getSharedInstance().getVodSeriesDetailsU3(ids, new NPXCatalog.NPXCatalogCallback<DataModels.NPXGetVodSeriesDetailOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new Exception("Server side error " + i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXGetVodSeriesDetailOutputModel output) {
                DataModels.NPXGetVodSeriesDetailDataModel[] seriesList = (output != null && output.seriesDetailList != null && output.seriesDetailList.length > 0) ? output.seriesDetailList : null;
                List<Node> list = Node.createList(seriesList, null);

                // pass on the recommendation genre-ids
                for (Node node : list) {
                    String genreId = genreIdMap.get(node.getNodeId());
                    node.setRecommendationGenreId(genreId);
                }

                deferred.resolve(list);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public Promise<List<Node>, Throwable, Float> loadSeriesDetailsListWithVEDetails(List<Node> nodes) {
        return loadSeriesDetailsList(nodes).then(new DonePipe<List<Node>, List<Node>, Throwable, Float>() {
            @Override
            public Promise<List<Node>, Throwable, Float> pipeDone(List<Node> result) {
                return loadVEDetailsList(result);
            }
        });
    }

    public org.osito.androidpromise.deferred.Promise<List<Node>> loadSubNodes(final Node node) {
        if (node == null || !node.isVOD()) return PromiseUtils.promiseWithValue((List<Node>) null);
        if (node.isGroup() || !node.hasMore()) return PromiseUtils.promiseWithValue((List<Node>) node.getSubNodes());

        final Deferred deferred = Deferred.newDeferred();

        NPXCatalog.getSharedInstance().getVodCategoryNode(node.getNodeId(), new NPXCatalog.NPXCatalogCallback<DataModels.NPXGetVodCategoryNodeOutputModel>() {
            @Override
            public void onRequestFailed(int i) {

                deferred.reject();
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXGetVodCategoryNodeOutputModel output) {

                if (output != null && output.nodeInfo != null) {
                    // patch the fucking subId
                    if (!TextUtils.isEmpty(output.nodeInfo.nodeType)) {
                        node.setRecommendationSubGenreId(output.nodeInfo.subId);
                    }

                    // patch node type
                    if (Is.equal(output.nodeInfo.nodeType, "category3")) {
                        node.addTypeMask(NodeType.Premium | NodeType.Cat3);
                    }

                    // patch subscription flag
                    node.setSubscribed(isSubscribed(output));

                    // patch name
                    node.setTitle(output.nodeInfo.displayName);
                }

                // sort product list by `sortDisplayOrder`
                if (output != null && output.productList != null) Arrays.sort(output.productList, new Comparator<DataModels.NPXGetVodLandingListProductModel>() {
                    @Override
                    public int compare(DataModels.NPXGetVodLandingListProductModel lhs, DataModels.NPXGetVodLandingListProductModel rhs) {
                        if (lhs.sortDisplayOrder < rhs.sortDisplayOrder) return -1;
                        if (lhs.sortDisplayOrder > rhs.sortDisplayOrder) return 1;
                        return 0;
                    }
                });
                List<Node> prds = Node.createList(output.productList, node);

                // filter black-listed categories
                Set<String> blacklist = new HashSet<>();
                blacklist.add("My Playlist");
                blacklist.add("我的清單");

                ArrayList<DataModels.NPXGetVodLandingListListModel> rawCats = new ArrayList<>();
                if (output != null && output.categoryList != null) for (DataModels.NPXGetVodLandingListListModel cat : output.categoryList) {
                    if (!blacklist.contains(cat.displayName)) {
                        rawCats.add(cat);
                    }
                }
                List<Node> cats = Node.createList(rawCats.toArray(new DataModels.NPXGetVodLandingListListModel[rawCats.size()]), node);

                // patch node type mask
                if (node.isCat3Parent()) for (Node cat : cats) {
                    for (Node subCat : cat.getSubNodes(NodeType.Category)) {
                        subCat.removeTypeMask(NodeType.Category);
                        subCat.addTypeMask(NodeType.Cat3);
                    }
                }

                // compose the final node list
                List<Node> list = new ArrayList<Node>();
                if (prds != null) list.addAll(prds);
                if (cats != null) list.addAll(cats);
                deferred.resolve(list);
            }
        });
        return deferred;
    }

    public Promise<Node, Throwable, Float> loadVEDetails(final Node node) {

        final DeferredObject<Node, Throwable, Float> deferred = new DeferredObject<>();

        if (node == null || !node.isVE()) {
            return deferred.resolve(node);
        }

        String productId = null;
        String seriesId = node.getSeriesId();
        if (node.isSeries()) {
            Node episode = node.getFirstEpisode();
            if (episode != null) {
                productId = episode.getNodeId();
            }
            if (TextUtils.isEmpty(seriesId) || TextUtils.isEmpty(productId)) return deferred.resolve(node);
        } else if (node.isProgram()) {
            productId = node.getNodeId();
            if (TextUtils.isEmpty(productId)) return deferred.resolve(node);
        } else {
            return deferred.resolve(node);
        }

        if (seriesId != null) {

            NMAFBasicCheckout.getSharedInstance().getVEProductDetail(seriesId, productId, new NMAFBasicCheckout.GetVEProductDetailCallback() {
                @Override
                public void onGetVEProductDetailFailed(@NonNull Throwable throwable) {
                    deferred.reject(throwable);
                }

                @Override
                public void onGetVEProductDetailSuccess(@NonNull WebTVAPIModels.GetProductDetailOutputModel output) {
                    WebTVAPIModels.GetProductDetailOutputModel.ProductDetailModel veDetails = null;
                    if (output != null) veDetails = output.seriesDetail;
                    node.setVeDetails(veDetails);

                    Node series = null;
                    if (node.isSeries()) {
                        series = node;
                    } else {
                        Node parent = node.getParent();
                        if (parent != null && parent.isSeries()) {
                            series = parent;
                        }
                    }

                    if (series != null) for (Node ep : series.getEpisodes()) {
                        // let child-episodes get the same ve details
                        ep.setVeDetails(veDetails);
                    }

                    deferred.resolve(node);
                }
            });
        } else {

            NMAFBasicCheckout.getSharedInstance().getVEProductDetail(ListUtils.make(productId), new NMAFBasicCheckout.GetVEProductDetailCallback() {
                @Override
                public void onGetVEProductDetailFailed(@NonNull Throwable throwable) {
                    deferred.reject(throwable);
                }

                @Override
                public void onGetVEProductDetailSuccess(@NonNull WebTVAPIModels.GetProductDetailOutputModel output) {
                    WebTVAPIModels.GetProductDetailOutputModel.ProductDetailModel veDetails = null;
                    if (output != null && output.ProductDetail != null && output.ProductDetail.length > 0) veDetails = output.ProductDetail[0];
                    node.setVeDetails(veDetails);
                    deferred.resolve(node);
                }
            });
        }
        return new AndroidDeferredObject<>(deferred);
    }

    public Promise<List<Node>, Throwable, Float> loadVEDetailsList(final List<Node> nodes) {

        List<Promise> loaders = new ArrayList<>();
        if (nodes != null) for (Node node : nodes) {
            if (node.isVE()) loaders.add(loadVEDetails(node));
        }

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        PromiseUtils.when(loaders, true, false).always(new AlwaysCallback<MultipleResults, OneReject>() {
            @Override
            public void onAlways(Promise.State state, MultipleResults resolved, OneReject rejected) {
                if (state == Promise.State.RESOLVED) {
                    deferred.resolve(nodes);
                } else {
                    Object rejectObject = rejected == null ? null : rejected.getReject();
                    Throwable error = (rejectObject instanceof Throwable) ? (Throwable) rejectObject : null;
                    deferred.reject(error);
                }
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }
}
