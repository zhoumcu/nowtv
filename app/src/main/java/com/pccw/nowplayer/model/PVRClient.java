package com.pccw.nowplayer.model;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.exceptions.LibraryException;
import com.pccw.nowplayer.model.node.BaseNode;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.DateBuilder;
import com.pccw.nowplayer.utils.DeviceManager;
import com.pccw.nowplayer.utils.Is;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowtv.nmaf.npx.catalog.NPXCatalog;
import com.pccw.nowtv.nmaf.npx.mynow.DataModels;
import com.pccw.nowtv.nmaf.npx.mynow.NPXMyNow;

import org.jdeferred.Deferred;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kriz on 20/7/2016.
 */
public class PVRClient {
    private static PVRClient instance;
    private Set<String> pvrKeys;

    private PVRClient() {
    }

    public static Promise<String, Throwable, Void> askForRecordingOption(Context context) {

        final Deferred<String, Throwable, Void> deferred = new DeferredObject<>();

        CharSequence options[] = new String[3];
        options[0] = context.getString(R.string.this_episode_only);
        options[1] = context.getString(R.string.this_and_upcoming_episodes);
        options[2] = context.getString(R.string.cancel);

        new AlertDialog.Builder(context).setTitle(context.getString(R.string.single_episode_or_series)).setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    deferred.resolve("E");
                } else if (i == 1) {
                    deferred.resolve("S");
                } else {
                    deferred.resolve("C");
                }
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                deferred.resolve("C");
            }
        }).setCancelable(false).create().show();

        return new AndroidDeferredObject<>(deferred);
    }

    public static PVRClient getInstance() {
        if (instance == null) {
            synchronized (PVRClient.class) {
                if (instance == null) instance = new PVRClient();
            }
        }
        return instance;
    }

    public Promise<Boolean, Throwable, Float> addNodeToPVRList(Context ctx, final Node node) {
        if (node == null) return PromiseUtils.resolve(false);

        Object nodeData = node.getData();
        if (nodeData instanceof com.pccw.nowtv.nmaf.npx.catalog.DataModels.NPXEpgProgramDetailDataModel) return PromiseUtils.resolve(false);
        final com.pccw.nowtv.nmaf.npx.catalog.DataModels.NPXEpgProgramDetailDataModel program = (com.pccw.nowtv.nmaf.npx.catalog.DataModels.NPXEpgProgramDetailDataModel) nodeData;

        if (isInPVRList(node)) return PromiseUtils.resolve(true);

        final Boolean[] canceled = new Boolean[1];
        canceled[0] = false;

        Promise<String, Throwable, Void> firstly;
        if (node.isEpisodic()) {
            firstly = askForRecordingOption(ctx);
        } else {
            firstly = PromiseUtils.resolve("P");
        }

        return firstly.then(new DonePipe<String, Boolean, Throwable, Float>() {
            @Override
            public Promise<Boolean, Throwable, Float> pipeDone(String result) {
                if ("C".equals(result)) {
                    canceled[0] = true;
                    return PromiseUtils.resolve(false);
                } else {
                    return addProgramToPVRList(program, result);
                }
            }
        }).then(new DonePipe<Boolean, List<Node>, Throwable, Float>() {
            @Override
            public Promise<List<Node>, Throwable, Float> pipeDone(Boolean result) {
                if (canceled[0].booleanValue()) {
                    return PromiseUtils.resolve(null);
                } else {
                    return loadPVRList();
                }
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                // ignored
            }
        }).then(new DoneFilter<List<Node>, Boolean>() {
            @Override
            public Boolean filterDone(List<Node> result) {
                return isInPVRList(node);
            }
        });
    }

    private Promise<Boolean, Throwable, Float> addProgramToPVRList(com.pccw.nowtv.nmaf.npx.catalog.DataModels.NPXEpgProgramDetailDataModel program, String progType) {

        if (program == null) PromiseUtils.resolve(false);

        Device device = DeviceManager.getInstance().getConnectDevice();
        if (device == null || Is.empty(device.deviceId)) return PromiseUtils.resolve(false);

        final Deferred<Boolean, Throwable, Float> deferred = new DeferredObject<>();

        boolean isSeries = "S".equals(progType);
        boolean isSingleEpisode = "E".equals(progType);

        String stbId = device.deviceId;
        String channelId = "" + program.channelId;
        String channelType = "I";
        String channelEngName = program.channelEngName;
        String channelChiName = program.channelChiName;
        String schMode = null;
        String schDay = null;
        String recurTime = null;
        DateBuilder db = DateBuilder.create();
        String recStartDate = db.setTime(program.actualStartTime).formatEnglish("yyyyMMddHHmmss");
        String recEndDate = db.setTime(program.endTime).formatEnglish("yyyyMMddHHmmss");
        String contentId = program.cid;
        Integer episodeNum = program.episodeNum;
        String progEngName = program.engProgName;
        String progChiName = program.chiProgName;
        String progEngSynopsis = program.synopsis;
        String progChiSynopsis = program.chiSynopsis;
        String seriesId = (isSeries || isSingleEpisode) ? program.sid : null;
        String seriesEngName = (isSeries || isSingleEpisode) ? program.engSeriesName : null;
        String seriesChiName = (isSeries || isSingleEpisode) ? program.chiSeriesName : null;

        NPXMyNow.getSharedInstance().addPVRItem(stbId, channelId, channelType, channelEngName, channelChiName, progType, schMode, schDay, recurTime, recStartDate, recEndDate, contentId, episodeNum, progEngName, progChiName, progEngSynopsis, progChiSynopsis, seriesId, seriesEngName, seriesChiName, new NPXCatalog.NPXCatalogCallback<DataModels.NPXMyNowAddPVRItemOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXMyNowAddPVRItemOutputModel npxMyNowAddPVRItemOutputModel) {
                deferred.resolve(true);
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    public boolean isInPVRList(BaseNode node) {
        if (node == null) return false;
        if (!node.isEPG() || !node.isProgram()) return false;

        Set<String> keys = pvrKeys;
        if (keys == null) return false;

        if (node.isEpisodic()) {
            String sid = node.getSeriesId();
            if (Is.notEmpty(sid)) {
                if (keys.contains("S/" + sid)) return true;
            }
            if (keys.contains("E/" + node.getCid())) return true;
        } else {
            String key = "P/" + node.getCid() + "/" + DateBuilder.create().setTime(node.getStartTime()).formatEnglish("yyyyMMddHHmmss");
            if (keys.contains(key)) return true;
        }
        return false;
    }

    public Promise<List<Node>, Throwable, Float> loadPVRList() {

        Device device = DeviceManager.getInstance().getConnectDevice();
        if (device == null || Is.empty(device.deviceId)) return PromiseUtils.resolve(null);

        final Deferred<List<Node>, Throwable, Float> deferred = new DeferredObject<>();

        NPXMyNow.getSharedInstance().getPVRItems(device.deviceId, new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowGetPVRItemsOutputModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXMyNowGetPVRItemsOutputModel result) {
                pvrKeys = makeKeys(result);
                deferred.resolve(toNodes(result));
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }

    private Set<String> makeKeys(DataModels.NPXMyNowGetPVRItemsOutputModel result) {
        HashSet<String> ret = new HashSet<>();

        if (result != null && result.response != null) for (DataModels.NPXMyNowGetPVRItemsResponseModel item : result.response) {

            if ("S".equals(item.progType)) {
                if (Is.notEmpty(item.contentId)) {
                    // !!! progType = "E" --> will become "S" with contentId
                    String key = "E/" + item.contentId;
                    ret.add(key);
                } else if (Is.notEmpty(item.seriesId)) {
                    String key = "S/" + item.seriesId;
                    ret.add(key);
                }
            } else if ("P".equals(item.progType) && Is.notEmpty(item.contentId)) {
                String key = "P/" + item.contentId + "/" + item.recStartDate;
                ret.add(key);
            }
        }
        return ret;
    }

    private List<Node> toNodes(DataModels.NPXMyNowGetPVRItemsOutputModel result) {

        // convert objects to nodes
        List<Node> nodes = Node.createList(result.response, null);

        // create a series map by series id
        Map<String, Node> seriesMap = new HashMap<>();

        // create a final list
        List<Node> output = new ArrayList<>();
        for (Node node : nodes) {

            if (node.isSeries()) {
                Node series = seriesMap.get(node.getSeriesId());
                if (series == null) {
                    // add unique series node to output list and map
                    seriesMap.put(node.getSeriesId(), node);
                    output.add(node);
                }
            } else {
                Node series = Is.empty(node.getSeriesId()) ? null : seriesMap.get(node.getSeriesId());
                if (series != null) {
                    // if program belongs to a series, add to the series instead of the output list
                    series.addSubNode(node);
                } else {
                    // add single product to the output list
                    output.add(node);
                }
            }
        }

        Iterator<Node> itr = output.iterator();
        while (itr.hasNext()) {
            Node node = itr.next();
            if (node.isSeries()) {
                if (node.getPrograms().size() == 0) itr.remove();
                else {
                        Collections.sort(node.getPrograms(), new Comparator<Node>() {
                        @Override
                        public int compare(Node node, Node t1) {
                            return (int) (node.getStartTime().getTime() - t1.getStartTime().getTime());
                        }
                    });

                }
            }
        }
        return output;
    }
}
