package com.pccw.nowplayer.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.exceptions.LibraryException;
import com.pccw.nowplayer.model.node.BaseNode;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.DateUtils;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;
import com.pccw.nowtv.nmaf.npx.catalog.NPXCatalog;

import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kriz on 2016-06-18.
 */
public class EPGClient {

    private static EPGClient instance;
    private SparseArray<Node> channelMap;
    private Set<Integer> subscribedChannels;

    private EPGClient() {
    }

    public static EPGClient getInstance() {
        if (instance == null) {
            synchronized (EPGClient.class) {
                if (instance == null) {
                    instance = new EPGClient();
                }
            }
        }
        return instance;
    }

    public Node getChannel(int id) {
        if (channelMap == null) return null;
        return channelMap.get(id);
    }

    public List<Node> getDetailedChannelList(List<Node> simpleChannels) {

        List<Node> ret = new ArrayList<>();
        SparseArray<Node> channelMap = this.channelMap;

        if (simpleChannels != null && channelMap != null) for (Node ch : simpleChannels) {
            int id = ch.getChannelId();
            Node detailedChannel = channelMap.get(id);
            if (detailedChannel != null) ret.add(detailedChannel);
        }

        return ret;
    }

    public boolean isSubscribed(BaseNode node) {
        return subscribedChannels != null && node != null && subscribedChannels.contains(node.getChannelId());
    }

    public Promise<List<TVGuideModel.Genre>, Throwable, Void> loadChannelGenres() {

        final DeferredObject<List<TVGuideModel.Genre>, Throwable, Void> deferred = new DeferredObject<>();

        DeferredManager dm = new DefaultDeferredManager();
        dm.when(loadChannelList(), loadGenreList()).then(new DoneCallback<MultipleResults>() {

            @Override
            public void onDone(MultipleResults result) {
                final List<Node> channels = (List<Node>) result.get(0).getResult();
                final DataModels.NPXEpgGetChannelGenreGenresModel[] genres = (DataModels.NPXEpgGetChannelGenreGenresModel[]) result.get(1).getResult();

                // make a channel id to channel map
                SparseArray<Node> chMap = new SparseArray<>();
                if (channels != null) for (Node ch : channels) {
                    int chId = TypeUtils.toInt(ch.getChannelId(), 0);
                    if (chId != 0) chMap.put(chId, ch);
                }

                List<TVGuideModel.Genre> output = new ArrayList<TVGuideModel.Genre>();
                final Context ctx = PlayerApplication.getContext();

                // make the "All Channels" virtual genre
                output.add(new TVGuideModel.Genre() {{
                    id = "__all";
                    key = "__all";
                    name = ctx.getString(R.string.all_channels);
                    channelList = new ArrayList<Node>() {{
                        if (channels != null) addAll(channels);
                    }};
                }});

                // make the "Subscribed Channels" virtual genre
                output.add(new TVGuideModel.Genre() {{
                    id = "__subscribed";
                    key = "__subscribed";
                    name = ctx.getString(R.string.subscribed_channels);
                    channelList = new ArrayList<Node>() {{
                        if (channels != null) for (Node ch : channels) {
                            if (ch.isSubscribed()) add(ch);
                        }
                    }};
                }});

                // make the "Watch On Device Channels" virtual genre
                output.add(new TVGuideModel.Genre() {{
                    id = "__on_device";
                    key = "__on_device";
                    name = ctx.getString(R.string.watch_on_device_channels);
                    channelList = new ArrayList<Node>() {{
                        if (channels != null) for (Node ch : channels) {
                            if (ch.isOnApp()) add(ch);
                        }
                    }};
                }});

                // convert genres to our model
                if (genres != null)
                    for (DataModels.NPXEpgGetChannelGenreGenresModel genre : genres) {
                        TVGuideModel.Genre g = TVGuideModel.Genre.create(genre, chMap);
                        if (g != null) output.add(g);
                    }

                deferred.resolve(output);
            }
        }).fail(new FailCallback<OneReject>() {
            @Override
            public void onFail(OneReject result) {
                deferred.reject((Throwable) result.getReject());
            }
        });

        return new AndroidDeferredObject(deferred);
    }

    public Promise<List<Node>, Throwable, Void> loadChannelList() {

        final DeferredObject<List<Node>, Throwable, Void> deferred = new DeferredObject<>();

        NPXCatalog.getSharedInstance().epgChannelList(new NPXCatalog.NPXCatalogCallback<DataModels.NPXEpgChannelListDataModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new Exception("Server side error: " + i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXEpgChannelListDataModel output) {

                SparseArray<Node> channelMap = new SparseArray<>();
                Set<Integer> subscribed = new HashSet<>();
                List<Node> channels = new ArrayList<Node>();

                if (output != null && output.channel != null) {
                    for (Map.Entry<String, Object> e : output.channel.entrySet()) {
                        Object obj = e.getValue();
                        if (obj instanceof DataModels.NPXEpgChannelListChannelModel) {
                            DataModels.NPXEpgChannelListChannelModel channel = (DataModels.NPXEpgChannelListChannelModel) obj;
                            Node channelNode = Node.create(channel, null);
                            channels.add(channelNode);
                            channelMap.put(channelNode.getChannelId(), channelNode);

                            if (channel.isSubscribed) subscribed.add(TypeUtils.toInt(channel.id, 0));
                        }
                    }
                }
                EPGClient.this.subscribedChannels = subscribed;
                EPGClient.this.channelMap = channelMap;
                deferred.resolve(channels);
            }
        });

        return new AndroidDeferredObject(deferred);
    }

    public Promise<DataModels.NPXEpgGetChannelGenreGenresModel[], Throwable, Void> loadGenreList() {

        final Deferred<DataModels.NPXEpgGetChannelGenreGenresModel[], Throwable, Void> deferred = new DeferredObject<>();

        NPXCatalog.getSharedInstance().epgGetChannelGenre(new NPXCatalog.NPXCatalogCallback<DataModels.NPXEpgGetChannelGenreOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new Exception("Server side error: " + i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXEpgGetChannelGenreOutputModel output) {
                deferred.resolve(output == null ? null : output.genres);
            }
        });

        return new AndroidDeferredObject(deferred);
    }

    public Promise<Node, Throwable, Float> loadLinkedProducts(final Node node) {

        if (node == null || TextUtils.isEmpty(node.getNodeId())) return PromiseUtils.resolve(node);

        String type = null;
        if (node.isVOD()) {
            if (node.isProgram()) {
                type = NPXCatalog.NPXGetVodMoreOptionsTypeProduct;
            } else if (node.isSeries()) {
                type = NPXCatalog.NPXGetVodMoreOptionsTypeSeries;
            }
        } else if (node.isEPG()) {
            if (node.isNPVR()) {
                if (node.isProgram()) {
                    type = NPXCatalog.NPXGetVodMoreOptionsTypeNpvr;
                } else if (node.isSeries()) {
                    type = NPXCatalog.NPXGetVodMoreOptionsTypeNpvrSeries;
                }
            } else {
                if (node.isProgram()) {
                    type = NPXCatalog.NPXGetVodMoreOptionsTypeEpg;
                } else if (node.isSeries()) {
                    type = NPXCatalog.NPXGetVodMoreOptionsTypeEpgSeries;
                }
            }
        }
        if (TextUtils.isEmpty(type)) return PromiseUtils.resolve(node);

        final DeferredObject<Node, Throwable, Float> deferred = new DeferredObject<>();

        NPXCatalog.getSharedInstance().getVodMoreOptions(node.getNodeId(), type, new NPXCatalog.NPXCatalogCallback<DataModels.NPXGetVodMoreOptionResultModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXGetVodMoreOptionResultModel response) {
                List<Node> epg = Node.createList(response.epg, node);
                List<Node> vod = Node.createList(response.vod, node);
                node.setLinkedEPG(epg);
                node.setLinkedVOD(vod);
                deferred.resolve(node);
            }
        });
        return new AndroidDeferredObject<>(deferred);
    }

    public Promise<Node, Throwable, Float> loadLiveProgram(final Node channel) {

        if (channel == null || channel.getChannelId() <= 0) return PromiseUtils.resolve(null);

        return this.loadPrograms(new ArrayList<Node>() {{
            add(channel);
        }}, 0, 0).then(new DoneFilter<List<Node>, Node>() {
            @Override
            public Node filterDone(List<Node> result) {
                Node liveProgram = channel.getPlayingProgramAt(System.currentTimeMillis());
                return liveProgram;
            }
        }).then(new DonePipe<Node, Node, Throwable, Float>() {
            @Override
            public Promise<Node, Throwable, Float> pipeDone(Node result) {
                // load the program details
                return loadProgramDetails(result);
            }
        });
    }

    public Promise<Node, Throwable, Float> loadOtherTimes(final Node node) {

        String programId = node == null ? null : node.getNodeId();
        if (TextUtils.isEmpty(programId)) return PromiseUtils.resolve(node);

        final DeferredObject<Node, Throwable, Float> deferred = new DeferredObject<>();

        NPXCatalog.getSharedInstance().getEpgProgramOtherTime(programId, new NPXCatalog.NPXCatalogCallback<DataModels.NPXGetEpgProgramOtherTimeOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXGetEpgProgramOtherTimeOutputModel response) {
                List<Node> list = Node.createList(response.epgProgram, null);
                Collections.sort(list, new Comparator<Node>() {
                    @Override
                    public int compare(Node lhs, Node rhs) {
                        if (lhs == rhs) return 0;
                        if (lhs == null) return -1;
                        if (rhs == null) return 1;
                        return DateUtils.compare(lhs.getStartTime(), rhs.getStartTime());
                    }
                });
                node.setOtherTimes(list);
                deferred.resolve(node);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public Promise<Node, Throwable, Float> loadProgramDetails(final Node node) {

        if (node == null || !node.isEPG() || TextUtils.isEmpty(node.getNodeId())) return PromiseUtils.resolve(node);

        final DeferredObject<Node, Throwable, Float> deferred = new DeferredObject<>();

        NPXCatalog.getSharedInstance().getEpgProgramDetail(node.getNodeId(), new NPXCatalog.NPXCatalogCallback<DataModels.NPXEpgProgramDetailDataModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new Exception("Server side error: " + i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXEpgProgramDetailDataModel response) {
                deferred.resolve(Node.create(response, node.getParent()));
            }
        });

        return new AndroidDeferredObject(deferred);
    }

    public Promise<List<Node>, Throwable, Float> loadPrograms(final List<Node> channels, final int startDay, final int endDay) {

        final DeferredObject<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        final SparseArray<Node> chMap = new SparseArray<>();
        List<String> chIds = new ArrayList<>();
        if (channels != null) for (Node ch : channels) {
            if (ch.getChannelId() != 0) {
                chMap.put(ch.getChannelId(), ch);
                chIds.add(String.valueOf(ch.getChannelId()));
            }
        }

        if (chIds.size() < 1) {
            return new AndroidDeferredObject(deferred.resolve(null));
        }

        NPXCatalog.getSharedInstance().getEpgDetails(chIds, startDay, endDay, new NPXCatalog.NPXCatalogCallback<DataModels.NPXGetEpgDetailsOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new Exception("Server side error: " + i));
            }

            @Override
            public void onRequestSuccessful(final DataModels.NPXGetEpgDetailsOutputModel npxGetEpgDetailsOutputModel) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        List<Node> updatedChannels = new ArrayList<Node>();
                        for (DataModels.NPXGetEpgDetailsDetailModel detail : npxGetEpgDetailsOutputModel.epgDetail) {
                            Node ch = chMap.get(TypeUtils.toInt(detail.channelId, 0));
                            if (ch != null) {
                                ch.setSubNodes(null);

                                // Fix server side repeated program bug
                                String lastKey = null;
                                if (detail.programs != null)
                                    for (DataModels.NPXGetLiveProgramsProgramModel program : detail.programs) {
                                        if (!TextUtils.equals(lastKey, program.key)) {
                                            ch.addSubNode(program);
                                            lastKey = program.key;
                                        }
                                    }
                                updatedChannels.add(ch);
                            }
                        }
                        deferred.resolve(updatedChannels);
                    }
                });
            }
        });

        return new AndroidDeferredObject(deferred);
    }
}
