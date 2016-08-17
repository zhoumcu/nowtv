package com.pccw.nowplayer.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.video.BasePlayer;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.model.node.BaseNode;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Is;
import com.pccw.nowplayer.utils.NetWorkUtil;
import com.pccw.nowplayer.utils.Pref;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowplayer.utils.gson.GsonUtil;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;
import com.pccw.nowtv.nmaf.mediaplayer.NMAFStreamDownloader;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by kriz on 2016-07-21.
 */
public class DownloadClient implements NMAFStreamDownloader.NMAFStreamDownloaderProgressUpdateListener {

    public static final String BROADCAST_DOWNLOAD_LIST_CHANGE = "com.pccw.nowplayer.BROADCAST_DOWNLOAD_LIST_CHANGE";
    public static final String BROADCAST_DOWNLOAD_PROGRESS_CHANGE = "com.pccw.nowplayer.BROADCAST_DOWNLOAD_PROGRESS_CHANGE";
    public static final String BROADCAST_DOWNLOAD_STATUS_CHANGE = "com.pccw.nowplayer.BROADCAST_DOWNLOAD_STATUS_CHANGE";

    private static DownloadClient instance;
    private List<NMAFStreamDownloader.NMAFStreamDownloadItem> mItems;
    private DownloadStatusTracker mStartingDownloadTracker;
    private Map<String, DownloadStatusTracker> mStatusTrackers;
    private String trackersMonitor = "trackersMonitor";

    private DownloadClient() {
        NetWorkUtil.registerConnectivityBroadcastReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onNetworkChanged(NetWorkUtil.isNetworkConnected(context));
            }
        });
    }

    public static DownloadClient getInstance() {
        if (instance == null) {
            synchronized (DownloadClient.class) {
                if (instance == null) instance = new DownloadClient();
            }
        }
        return instance;
    }

    public Promise addToDownloadList(final FragmentActivity context, final Node node) {

        // block invalid node
        final String nodeId = node == null ? null : node.getNodeId();
        if (node == null || Is.empty(nodeId) || !node.isDownloadable()) return PromiseUtils.nil();

        // download already initiated
        if (isDownloadInitiated(node) || mStartingDownloadTracker != null) return PromiseUtils.nil();

        // create a temp tracker for node
        mStartingDownloadTracker = new DownloadStatusTracker(this);
        mStartingDownloadTracker.setIdentifier(nodeId);
        mStartingDownloadTracker.setDownloadItem(null, false);
        node.setDownloadTracker(mStartingDownloadTracker);
        addTracker(mStartingDownloadTracker);
        mStartingDownloadTracker.beginTransition();

        return PromiseUtils.firstly().then(new DonePipe() {
            @Override
            public Promise pipeDone(Object result) {
                if (BasePlayer.nowPlaying != null && BasePlayer.nowPlaying.isVOD() && Is.equal(BasePlayer.nowPlaying.getNodeId(), nodeId)) {
                    return DialogHelper.createAlertDialogPromise(context, context.getString(R.string.sorry), context.getString(R.string.cannot_download_while_playing), context.getString(R.string.ok), false).then(new DonePipe() {
                        @Override
                        public Promise pipeDone(Object result) {
                            return PromiseUtils.reject(new Exception());
                        }
                    });
                }
                return PromiseUtils.nil();
            }
        }).then(new DonePipe() {
            @Override
            public Promise pipeDone(Object result) {
                // check mobile network usage
                return DialogHelper.createMobileNetworkAlertPromise(context);
            }
        }).then(new DonePipe() {
            @Override
            public Promise pipeDone(Object result) {
                // checkout program for size calculation
                return CheckoutClient.create(node, false).attachTo(context).begin();
            }
        }).then(new DonePipe<NMAFBasicCheckout.NMAFCheckoutData, Number, Throwable, Void>() {
            @Override
            public Promise<Number, Throwable, Void> pipeDone(NMAFBasicCheckout.NMAFCheckoutData result) {
                // calculate download size
                return calculateDownloadSize(result, nodeId);
            }
        }).then(new DonePipe() {
            @Override
            public Promise pipeDone(Object result) {
                // save node to disk
                return node.promiseSave();
            }
        }).then(new DonePipe() {
            @Override
            public Promise pipeDone(Object result) {
                if (isDownloading()) {

                    // download later

                    // update queue
                    List<String> ids = getQueuedIds();
                    if (ids == null) {
                        ids = new ArrayList<String>();
                    }
                    ids.add(nodeId);
                    setQueuedIds(ids);

                    // update trackers
                    mStartingDownloadTracker = null;
                    updateTrackerMap();

                    return PromiseUtils.nil();
                } else {

                    // start download immediately
                    return startDownload(context, node);
                }
            }
        }).fail(new FailCallback() {
            @Override
            public void onFail(Object result) {

                // update trackers
                mStartingDownloadTracker = null;
                updateTrackerMap();
            }
        }).always(new AlwaysCallback() {
            @Override
            public void onAlways(Promise.State state, Object resolved, Object rejected) {
                mStartingDownloadTracker = null;
                getTracker(node);

                Intent bc = new Intent(BROADCAST_DOWNLOAD_LIST_CHANGE);
                bc.putExtra("identifier", nodeId);
                PlayerApplication.getContext().sendBroadcast(bc);
            }
        });
    }

    public void addTracker(DownloadStatusTracker tracker) {

        if (tracker == null || tracker.getIdentifier() == null) return;

        synchronized (trackersMonitor) {
            if (mStatusTrackers == null) {
                mStatusTrackers = new HashMap<>();
            }
            mStatusTrackers.put(tracker.getIdentifier(), tracker);
        }
    }

    public Promise<Number, Throwable, Void> calculateDownloadSize(NMAFBasicCheckout.NMAFCheckoutData checkoutData, final String identifier) {
        final Deferred<Number, Throwable, Void> deferred = new DeferredObject<>();

        NMAFStreamDownloader.getSharedInstance().calculateDownloadSize(checkoutData, new NMAFStreamDownloader.CalculateDownloadSizeCallback() {
            @Override
            public void operationFailed(Throwable throwable) {
                deferred.reject(throwable);
            }

            @Override
            public void operationSuccessful(int size) {
                // TODO Shoule use long here

                // remember the download size
                Pref.getPref("download_size").putLong(identifier, size);

                // update the tracker
                getTracker(identifier);

                deferred.resolve(Long.valueOf(size));
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public void frameworkOnSetup() {
        NMAFStreamDownloader.getSharedInstance().setProgressListener(this);

        loadDownloadList();
    }

    public NMAFBasicCheckout.NMAFCheckoutData getCheckoutData(Node node) {
        if (node == null) return null;
        if (Is.empty(node.getNodeId())) return null;
        return NMAFStreamDownloader.getSharedInstance().getMediaPlayerConfiguration(node.getNodeId());
    }

    private NMAFStreamDownloader.NMAFStreamDownloadItem getDownloadingItem() {

        List<NMAFStreamDownloader.NMAFStreamDownloadItem> list = loadDownloadList();
        if (list == null) return null;

        for (NMAFStreamDownloader.NMAFStreamDownloadItem item : list) {
            if (!item.completionFlag && !item.pauseFlag && !item.errorFlag) {
                return item;
            }
        }
        return null;
    }

    public List<String> getQueuedIds() {
        List<String> ret = StringUtils.splitBySeparator(Pref.getPref().getString("downloadQueue"), ",", true);
        return ret;
    }

    public void setQueuedIds(List<String> ids) {
        String queued = null;
        if (ids != null && ids.size() > 0) {
            queued = StringUtils.join(ids, ",");
        }
        Pref.getPref().putString("downloadQueue", queued);
    }

    public List<Node> getQueuedList() {
        List<String> ids = getQueuedIds();
        List<Node> ret = new ArrayList<>();
        if (ids != null) for (String id : ids) {
            if (Is.empty(id)) continue;
            Node node = Node.load(id, NodeType.VODProgram, null);
            if (node != null) ret.add(node);
        }
        return ret;
    }

    public DownloadStatusTracker getTracker(String identifier) {
        DownloadStatusTracker tracker = mStatusTrackers != null ? mStatusTrackers.get(identifier) : null;
        tracker.setDownloadSize(Pref.getPref("download_size").getLong(identifier));
        return tracker;
    }

    public DownloadStatusTracker getTracker(BaseNode node) {
        if (node == null) return null;
        DownloadStatusTracker tracker = getTracker(node.getNodeId());
        node.setDownloadTracker(tracker);
        return tracker;
    }

    private boolean isDownloadInitiated(Node node) {

        if (node == null) return false;

        String nodeId = node.getNodeId();
        if (Is.empty(nodeId)) return false;

        // query the tracker for quick result
        if (mStatusTrackers != null) {
            DownloadStatusTracker tracker = mStatusTrackers.get(nodeId);
            if (tracker != null) {
                return tracker.getStatus() != null;
            }
        }

        // check with the download queue
        List<String> queued = getQueuedIds();
        if (queued != null) {
            if (queued.contains(nodeId)) return true;
        }

        // check with the download list
        List<NMAFStreamDownloader.NMAFStreamDownloadItem> list = loadDownloadList();
        if (list != null) for (NMAFStreamDownloader.NMAFStreamDownloadItem item : list) {
            if (Is.equal(item.identifier, nodeId)) {
                return true;
            }
        }

        return false;
    }

    private boolean isDownloading() {
        NMAFStreamDownloader.NMAFStreamDownloadItem item = getDownloadingItem();
        return item != null;
    }

    public boolean isMobileDataEnabled() {
        return NMAFStreamDownloader.getSharedInstance().isAllowCellularDownload();
    }

    public void setMobileDataEnabled(boolean enabled) {
        NMAFStreamDownloader.getSharedInstance().setAllowCellularDownload(enabled);
    }

    private List<NMAFStreamDownloader.NMAFStreamDownloadItem> loadDownloadList() {

        NMAFStreamDownloader.NMAFStreamDownloadItem[] items = NMAFStreamDownloader.getSharedInstance().getDownloadList();
        List<NMAFStreamDownloader.NMAFStreamDownloadItem> list = new ArrayList<>();
        if (items != null) for (NMAFStreamDownloader.NMAFStreamDownloadItem item : items) {
            list.add(item);
        }
        mItems = list;

        updateTrackerMap();

        return list;
    }

    public Promise<List<Node>, Throwable, Float> loadDownloadNodesWithDetails() {

        // restore nodes from download list
        List<Node> downloadNodes = new ArrayList<>();
        List<NMAFStreamDownloader.NMAFStreamDownloadItem> downloadItems = loadDownloadList();
        if (downloadItems != null) for (NMAFStreamDownloader.NMAFStreamDownloadItem item : downloadItems) {
            String json = item.customPayload;
            Node node = GsonUtil.fromJson(json, Node.class);
            if (node != null) downloadNodes.add(node);
        }

        final Map<String, DownloadStatusTracker> trackers = mStatusTrackers;

        // get queued nodes
        List<Node> queuedNodes = getQueuedList();

        // merge two list
        List<Node> nodes = new ArrayList<>();
        if (downloadNodes != null && downloadNodes.size() > 0) nodes.addAll(downloadNodes);
        if (queuedNodes != null && queuedNodes.size() > 0) nodes.addAll(queuedNodes);

        // load details for nodes
        return CatalogClient.getInstance().loadDetails(nodes, false).then(new DoneFilter<List<Node>, List<Node>>() {
            @Override
            public List<Node> filterDone(List<Node> result) {
                if (result != null) for (Node node : result) {
                    node.setDownloadTracker(trackers.get(node.getNodeId()));
                }
                return result;
            }
        });
    }

    public void onNetworkChanged(boolean online) {
        if (!online) {
            pauseAll_internal();
        }
    }

    public Promise pause(final Node node) {

        if (node == null) return PromiseUtils.nil();

        final String nodeId = node.getNodeId();
        DownloadStatusTracker tracker = getTracker(nodeId);
        if (tracker != null) tracker.beginTransition();

        return PromiseUtils.firstlyInBackground(new PromiseUtils.Function<Void, Void>() {
            @Override
            public Void invoke(Void input) throws Throwable {

                NMAFStreamDownloader.getSharedInstance().pauseDownload(nodeId);

                // refresh download list and trackers
                loadDownloadList();

                return null;
            }
        }).always(new AlwaysCallback<Void, Throwable>() {
            @Override
            public void onAlways(Promise.State state, Void resolved, Throwable rejected) {

                getTracker(node);

                PlayerApplication.getContext().sendBroadcast(new Intent(BROADCAST_DOWNLOAD_STATUS_CHANGE));
            }
        });
    }

    public Promise pauseAll() {
        Pref.getPref().putBool("downloadPaused", true);
        return pauseAll_internal();
    }

    public Promise pauseAll_internal() {

        // being transition
        if (mItems != null) for (NMAFStreamDownloader.NMAFStreamDownloadItem item : mItems) {
            DownloadStatusTracker tracker = getTracker(item.identifier);
            if (tracker != null) tracker.beginTransition();
        }

        return PromiseUtils.firstlyInBackground(new PromiseUtils.Function() {
            @Override
            public Object invoke(Object input) throws Throwable {

                if (mItems != null) for (NMAFStreamDownloader.NMAFStreamDownloadItem item : mItems) {
                    if (!item.completionFlag) {
                        NMAFStreamDownloader.getSharedInstance().pauseDownload(item.identifier);
                    }
                }
                return null;
            }
        }).always(new AlwaysCallback() {
            @Override
            public void onAlways(Promise.State state, Object resolved, Object rejected) {

                // refresh download list and trackers
                loadDownloadList();

                PlayerApplication.getContext().sendBroadcast(new Intent(BROADCAST_DOWNLOAD_STATUS_CHANGE));
            }
        });
    }

    public Promise processQueue() {

        if (Pref.getPref().getBool("downloadPaused")) {
            // globally paused
        } else if (isDownloading()) {
            // still downloading
        } else {

            // find paused download
            List<NMAFStreamDownloader.NMAFStreamDownloadItem> items = loadDownloadList();
            if (items != null) for (NMAFStreamDownloader.NMAFStreamDownloadItem item : items) {
                if (!item.completionFlag && item.pauseFlag) {
                    return resumeDownload(item.identifier);
                }
            }

            // get queued list
            List<String> queued = getQueuedIds();
            if (queued != null && queued.size() > 0) {

                // get next program
                String next = queued.get(0);
                Node node = Node.load(next, NodeType.VODProgram, null);

                // update the queue
                queued.remove(0);
                setQueuedIds(queued);

                // start the download
                VODClient.getInstance().loadProgramDetails(node).then(new DonePipe<Node, Object, Object, Object>() {
                    @Override
                    public Promise<Object, Object, Object> pipeDone(Node details) {
                        return startDownload(null, details); // TODO where to find a FragmentActivity?
                    }
                });
            }
        }
        return PromiseUtils.nil();
    }

    public Promise removeFromDownloadList(final List<Node> nodes) {

        if (nodes == null) return PromiseUtils.nil();

        final HashSet<String> downloadIdSet = new HashSet<>();
        for (Node node : nodes) {
            if (Is.notEmpty(node.getNodeId())) downloadIdSet.add(node.getNodeId());
        }
        if (downloadIdSet.isEmpty()) return PromiseUtils.nil();

        Promise task = PromiseUtils.firstlyInBackground(new PromiseUtils.Function<Void, Void>() {
            @Override
            public Void invoke(Void input) throws Throwable {

                // remove from queue
                List<String> queuedIds = getQueuedIds();
                if (queuedIds != null) {
                    Iterator<String> itr = queuedIds.iterator();
                    while (itr.hasNext()) {
                        String id = itr.next();
                        if (downloadIdSet.contains(id)) {
                            itr.remove();
                            ;
                        }
                    }
                }
                setQueuedIds(queuedIds);

                // pause downloads
                for (String id : downloadIdSet) {

                    // pause download if in progress
                    NMAFStreamDownloader.getSharedInstance().pauseDownload(id);

                    // remove completely from download list
                    NMAFStreamDownloader.getSharedInstance().removeDownload(id);

                    // update download status trackers
                    if (mStatusTrackers != null) {
                        DownloadStatusTracker tracker = mStatusTrackers.get(id);
                        if (tracker != null) {
                            tracker.setDownloadItem(null, false);
                        }
                    }
                }

                // update download status of nodes
                for (Node node : nodes) {
                    node.setDownloadTracker(null);
                }

                // refresh download list and trackers
                loadDownloadList();

                return null;
            }
        });

        return new AndroidDeferredObject(task);
    }

    public Promise resumeAll() {
        Pref.getPref().putBool("downloadPaused", false);
        processQueue();
        return PromiseUtils.nil();
    }

    public Promise resumeDownload(final String identifier) {

        if (Is.empty(identifier)) return PromiseUtils.nil();

        DownloadStatusTracker tracker = getTracker(identifier);
        if (tracker != null) tracker.beginTransition();

        NMAFStreamDownloader.getSharedInstance().pauseDownload(identifier);

        final Deferred deferred = new DeferredObject();

        NMAFStreamDownloader.getSharedInstance().resumeDownload(identifier, new NMAFBaseModule.ErrorCallback() {
            @Override
            public void operationComplete(Throwable throwable) {
                if (throwable == null) {

                    Pref.getPref().putBool("downloadPaused", false);

                    // refresh download list and trackers
                    loadDownloadList();

                    deferred.resolve(null);
                } else {
                    deferred.reject(throwable);
                }
            }
        });

        return new AndroidDeferredObject<>(deferred).then(new DonePipe() {
            @Override
            public Promise pipeDone(Object result) {

                PlayerApplication.getContext().sendBroadcast(new Intent(BROADCAST_DOWNLOAD_STATUS_CHANGE));

                return PromiseUtils.nil();
            }
        });
    }

    public Promise resumeDownload(final Node node) {
        if (node == null) return PromiseUtils.nil();
        return resumeDownload(node.getNodeId()).always(new AlwaysCallback() {
            @Override
            public void onAlways(Promise.State state, Object resolved, Object rejected) {
                getTracker(node);
            }
        });
    }

    private Promise startDownload(final FragmentActivity context, final Node node) {

        if (node == null || context == null || context.isFinishing()) return PromiseUtils.nil();

        final String nodeId = node.getNodeId();
        if (Is.empty(nodeId) || !node.isDownloadable()) return PromiseUtils.nil();

        final DownloadStatusTracker tracker = getTracker(nodeId);
        if (tracker != null) tracker.beginTransition();

        return PromiseUtils.firstly(new DonePipe<Void, NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void>() {
            @Override
            public Promise<NMAFBasicCheckout.NMAFCheckoutData, Throwable, Void> pipeDone(Void nil) {
                // checkout program for download
                return CheckoutClient.create(node, false).attachTo(context).begin();
            }
        }).then(new DonePipe<NMAFBasicCheckout.NMAFCheckoutData, Void, Throwable, Void>() {
            @Override
            public Promise<Void, Throwable, Void> pipeDone(NMAFBasicCheckout.NMAFCheckoutData result) {
                // start the download

                final Deferred<Void, Throwable, Void> deferred = new DeferredObject<>();

                NMAFStreamDownloader.getSharedInstance().addDownload(result, nodeId, node.getTitle(), node.getJsonString(), new NMAFBaseModule.ErrorCallback() {
                    @Override
                    public void operationComplete(Throwable throwable) {
                        if (throwable == null) {
                            deferred.resolve(null);
                        } else {
                            deferred.reject(throwable);
                        }
                    }
                });

                return deferred;
            }
        }).always(new AlwaysCallback<Void, Throwable>() {
            @Override
            public void onAlways(Promise.State state, Void resolved, Throwable rejected) {
                if (state == Promise.State.REJECTED) {
                    if (tracker == mStartingDownloadTracker) {
                        mStartingDownloadTracker = null;
                    }
                }

                // refresh download list and trackers
                loadDownloadList();
                getTracker(node);
                PlayerApplication.getContext().sendBroadcast(new Intent(BROADCAST_DOWNLOAD_STATUS_CHANGE));
            }
        });
    }

    @Override
    public void updateProgress(int progress, NMAFStreamDownloader.NMAFStreamDownloadItem item) {
        item.progress = progress;

        DownloadStatusTracker tracker = mStatusTrackers == null ? null : mStatusTrackers.get(item.identifier);
        if (tracker != null) {
            tracker.setProgress(progress);
        }

        Intent bcProgress = new Intent(BROADCAST_DOWNLOAD_PROGRESS_CHANGE);
        bcProgress.putExtra("identifier", item.identifier);
        PlayerApplication.getContext().sendBroadcast(bcProgress);

        if (progress == 100) {

            Intent bcStatus = new Intent(BROADCAST_DOWNLOAD_STATUS_CHANGE);
            bcStatus.putExtra("identifier", item.identifier);
            PlayerApplication.getContext().sendBroadcast(bcStatus);

            processQueue();
        }
    }

    public void updateTrackerMap() {

        synchronized (trackersMonitor) {
            // make a dict of status trackers, which are single instances per nodeId
            Map<String, DownloadStatusTracker> existingTrackers = mStatusTrackers;

            // from download list
            Map<String, DownloadStatusTracker> trackers = new HashMap<>();
            if (mItems != null) for (NMAFStreamDownloader.NMAFStreamDownloadItem item : mItems) {

                if (item == null || item.identifier == null) continue;

                // get existing tracker
                DownloadStatusTracker tracker = existingTrackers != null ? existingTrackers.get(item.identifier) : null;

                // create if not exists
                if (tracker == null) tracker = new DownloadStatusTracker(this);

                // update the status and progress
                tracker.setDownloadItem(item, true);

                // add to new tracker map
                trackers.put(item.identifier, tracker);
            }

            // from queue
            List<String> nodeIds = getQueuedIds();
            if (nodeIds != null) for (String nodeId : nodeIds) {

                if (Is.empty(nodeId)) continue;

                // get existing tracker
                DownloadStatusTracker tracker = existingTrackers != null ? existingTrackers.get(nodeId) : null;

                // create if not exists
                if (tracker == null) tracker = new DownloadStatusTracker(this);

                // update the status and progress
                tracker.setIdentifier(nodeId);
                tracker.setDownloadItem(null, true);

                // add to new tracker map
                trackers.put(nodeId, tracker);
            }

            // from starting download
            if (mStartingDownloadTracker != null) {
                trackers.put(mStartingDownloadTracker.getIdentifier(), mStartingDownloadTracker);
            }

            mStatusTrackers = trackers;
        }
    }
}
