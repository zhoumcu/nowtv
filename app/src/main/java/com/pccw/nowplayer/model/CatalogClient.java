package com.pccw.nowplayer.model;

import android.content.Intent;
import android.text.TextUtils;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.model.exceptions.LibraryException;
import com.pccw.nowplayer.model.node.BaseNode;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.L;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;
import com.pccw.nowtv.nmaf.npx.catalog.NPXCatalog;
import com.pccw.nowtv.nmaf.npx.recommendationEngine.NPXRecommendationEngine;

import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;
import org.jdeferred.multiple.OneResult;
import org.osito.androidpromise.deferred.Deferred;
import org.osito.androidpromise.deferred.Promise;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by kriz on 2016-06-17.
 */
public class CatalogClient {
    public static final String BROADCAST_EPG_RECOMMENDATIONS_ON_LOAD = "com.pccwnow.player.epg_recommendations.onload";

    private static CatalogClient instance;
    private Set<String> recommendedEPGProgramIds;

    private CatalogClient() {
    }

    public static CatalogClient getInstance() {
        if (instance == null) {
            synchronized (CatalogClient.class) {
                if (instance == null) {
                    instance = new CatalogClient();
                }
            }
        }
        return instance;
    }

    public boolean isRecommended(BaseNode node) {
        if (node == null) return false;
        if (node.isEPG()) {
            return (recommendedEPGProgramIds != null && recommendedEPGProgramIds.contains(node.getNodeId()));
        }
        return false;
    }

    public org.jdeferred.Promise<Set<String>, Throwable, Float> laodEPGRecommendations() {

        final DeferredObject<Set<String>, Throwable, Float> deferred = new DeferredObject<>();

        NPXRecommendationEngine.getSharedInstance().getTaEpgPreferenceRecommendation(new NPXCatalog.NPXCatalogCallback<com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTAGetTaEpgPreferenceRecommendationDataModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTAGetTaEpgPreferenceRecommendationDataModel response) {
                Set<String> programs = new HashSet<String>();
                if (response != null && response.epg_recommendation != null && response.epg_recommendation.recommendations != null) {
                    for (com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTAGetTaEpgPreferenceRecommendationRecommendationsModel program : response.epg_recommendation.recommendations) {
                        if (program.p_vimProgId != null) programs.add(program.p_vimProgId);
                    }
                }
                recommendedEPGProgramIds = programs;

                PlayerApplication.getContext().sendBroadcast(new Intent(BROADCAST_EPG_RECOMMENDATIONS_ON_LOAD));

                deferred.resolve(programs);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    /**
     * @return Recommendation list under My Now, Recommendations screen.
     */
    public org.jdeferred.Promise<List<Node>, Throwable, Float> laodMyNowRecommendations() {

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        NPXRecommendationEngine.getSharedInstance().getTaVodEpgPreferenceParellelRecommendation(new NPXCatalog.NPXCatalogCallback<com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTAGetTaVodEpgPreferenceParallelRecommendationDataModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTAGetTaVodEpgPreferenceParallelRecommendationDataModel response) {
                deferred.resolve(Node.createList(response == null ? null : response.recommendations, null));
            }
        });

        return deferred.then(new DonePipe<List<Node>, List<Node>, Throwable, Float>() {
            @Override
            public org.jdeferred.Promise<List<Node>, Throwable, Float> pipeDone(List<Node> result) {
                return loadDetails(result, false);
            }
        });
    }

    public org.jdeferred.Promise<List<Node>, Throwable, Float> loadDetails(List<Node> nodes, boolean withVEDetails) {

        if (nodes == null || nodes.size() == 0) return PromiseUtils.resolve(null);

        ArrayList<org.jdeferred.Promise> loaders = new ArrayList<>();
        ArrayList<Node> vodSingles = new ArrayList<>();
        ArrayList<Node> vodSeries = new ArrayList<>();

        for (Node node : nodes) {
            if (node.isEPGProgram()) {
                loaders.add(EPGClient.getInstance().loadProgramDetails(node));
            } else if (node.isVOD()) {
                if (node.isSeries()) {
                    vodSeries.add(node);
                } else if (node.isProgram()) {
                    vodSingles.add(node);
                }
            }
        }

        if (vodSingles.size() > 0) {
            if (withVEDetails) {
                loaders.add(VODClient.getInstance().loadProgramDetailsListWithVEDetails(vodSingles));
            } else {
                loaders.add(VODClient.getInstance().loadProgramDetailsList(vodSingles));
            }
        }
        if (vodSeries.size() > 0) {
            if (withVEDetails) {
                loaders.add(VODClient.getInstance().loadSeriesDetailsListWithVEDetails(vodSeries));
            } else {
                loaders.add(VODClient.getInstance().loadSeriesDetailsList(vodSeries));
            }
        }

        if (loaders.size() == 0) return PromiseUtils.resolve(null);

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        PromiseUtils.when(loaders, true, false).then(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                List<Node> ret = new ArrayList<>();
                Iterator<OneResult> itr = result.iterator();
                while (itr.hasNext()) {
                    OneResult res = itr.next();
                    Object obj = res.getResult();
                    if (obj instanceof List) {
                        List tmpNodes = (List) obj;
                        for (Object tmpObj : tmpNodes) {
                            if (tmpObj instanceof Node) {
                                ret.add((Node) tmpObj);
                            }
                        }
                    } else if (obj instanceof Node) {
                        ret.add((Node) obj);
                    }
                }
                deferred.resolve(ret);
            }
        }).fail(new FailCallback<OneReject>() {
            @Override
            public void onFail(OneReject result) {
                L.e(result);
                deferred.resolve(null);
            }
        });

        return new AndroidDeferredObject(deferred);
    }

    public Promise<List<Node>> loadLandingCatalog() {

        final Deferred deferred = Deferred.newDeferred();

        NPXCatalog.getSharedInstance().getLandingCatalogs(new NPXCatalog.NPXCatalogCallback<DataModels.NPXGetLandingDataOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject();
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXGetLandingDataOutputModel output) {
                reorderLandingCatalog(output);
                List<Node> nodes = Node.createList(output.cat, null);
                deferred.resolve(nodes);
            }
        });

        return deferred;
    }

    public org.jdeferred.Promise<List<Node>, Throwable, Float> loadRecommendation(final Node node) {

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        Node program = null;
        if (node == null || !node.isVOD()) {
            // not supported!
        } else if (node.isProgram()) {
            program = node;
        } else if (node.isSeries()) {
            program = node.getFirstEpisode();
        }
        if (program == null) return new AndroidDeferredObject<>(deferred.resolve(null));

        String genreId = node.getRecommendationGenreId();
        String subGenreId = node.getRecommendationSubGenreId();
        String programId = program.getNodeId();
        if ((TextUtils.isEmpty(genreId) && TextUtils.isEmpty(subGenreId)) || TextUtils.isEmpty(programId)) return new AndroidDeferredObject<>(deferred.resolve(null));

        if (!TextUtils.isEmpty(genreId)) {

            NPXRecommendationEngine.getSharedInstance().getTaVodCollaReContentParallelRecommendationByGenreId(genreId, programId, new NPXCatalog.NPXCatalogCallback<com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTATaVodCollaReContentParallelRecommendationByGenreIdDataModel>() {
                @Override
                public void onRequestFailed(int i) {
                    deferred.reject(new LibraryException(i));
                }

                @Override
                public void onRequestSuccessful(com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTATaVodCollaReContentParallelRecommendationByGenreIdDataModel response) {

                    List<Node> list = new ArrayList<>();

                    if (response != null && response.also_watched != null && response.also_watched.recommendations != null) {
                        List<Node> tmp = Node.createList(response.also_watched.recommendations, null);
                        if (tmp != null) list.addAll(tmp);
                    }
                    if (response != null && response.more_like_this != null && response.more_like_this.recommendations != null) {
                        List<Node> tmp = Node.createList(response.more_like_this.recommendations, null);
                        if (tmp != null) list.addAll(tmp);
                    }
                    deferred.resolve(list);
                }
            });
        } else if (!TextUtils.isEmpty(subGenreId)) {

            NPXRecommendationEngine.getSharedInstance().getTaVodCollaReContentParallelRecommendationBySubGenreId(subGenreId, programId, new NPXCatalog.NPXCatalogCallback<com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTATaVodCollaReContentParallelRecommendationByGenreIdDataModel>() {
                @Override
                public void onRequestFailed(int i) {
                    deferred.reject(new LibraryException(i));
                }

                @Override
                public void onRequestSuccessful(com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTATaVodCollaReContentParallelRecommendationByGenreIdDataModel response) {

                    List<Node> list = new ArrayList<>();

                    if (response != null && response.also_watched != null && response.also_watched.recommendations != null) {
                        List<Node> tmp = Node.createList(response.also_watched.recommendations, null);
                        if (tmp != null) list.addAll(tmp);
                    }
                    if (response != null && response.more_like_this != null && response.more_like_this.recommendations != null) {
                        List<Node> tmp = Node.createList(response.more_like_this.recommendations, null);
                        if (tmp != null) list.addAll(tmp);
                    }
                    deferred.resolve(list);
                }
            });
        } else {
            deferred.resolve(null);
        }

        return new AndroidDeferredObject<>(deferred.then(new DonePipe<List<Node>, List<Node>, Throwable, Float>() {
            @Override
            public org.jdeferred.Promise<List<Node>, Throwable, Float> pipeDone(List<Node> result) {
                return loadDetails(result, false);
            }
        }));
    }

    private void reorderLandingCatalog(DataModels.NPXGetLandingDataOutputModel landingCatalog) {
        if (landingCatalog == null || landingCatalog.cat == null || landingCatalog.cat.length <= 1) return;
        DataModels.NPXGetLandingDataCat tmp;
        for (int i = 0; i < landingCatalog.cat.length; i++) {
            if ((NodeType.BANNER.equals(landingCatalog.cat[i].name.toLowerCase()))) {
                tmp = landingCatalog.cat[0];
                landingCatalog.cat[0] = landingCatalog.cat[i];
                landingCatalog.cat[i] = tmp;
            }
        }
    }

    public org.jdeferred.Promise<Void, Throwable, Float> trackAction(BaseNode node, String action) {

        final DeferredObject<Void, Throwable, Float> deferred = new DeferredObject<>();

        if (TextUtils.isEmpty(action) || node == null || TextUtils.isEmpty(node.getNodeId())) return PromiseUtils.resolve(null);

        NPXRecommendationEngine.getSharedInstance().putVodTranx(action, node.getNodeId(), new NPXCatalog.NPXCatalogCallback<com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTAPutVodTranxOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(com.pccw.nowtv.nmaf.npx.recommendationEngine.DataModels.NPXTAPutVodTranxOutputModel npxtaPutVodTranxOutputModel) {
                deferred.resolve(null);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }
}
