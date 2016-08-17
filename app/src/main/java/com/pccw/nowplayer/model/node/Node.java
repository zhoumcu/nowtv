package com.pccw.nowplayer.model.node;

import android.graphics.Bitmap;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.Predicate;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowplayer.utils.TraversalCallback;
import com.pccw.nowplayer.utils.TraversalInfo;
import com.pccw.nowplayer.utils.gson.GsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Swifty on 5/17/2016.
 */
public class Node extends BaseNode implements Serializable, Cloneable, Comparable<Node> {
    public static final String TAG = Node.class.getSimpleName();
    transient protected static final List<WrapperClass> wrappers = new ArrayList<WrapperClass>();

    static {
        // Register wrappers here. The more specific class must be registered before more generic class

        // product nodes
        registerWrapper(NPXGetLandingDataDataNode.class);
        registerWrapper(NPXEpgVodSearchDocsModelNode.class);
        registerWrapper(NPXGetVodSeriesDetailDataModelNode.class); // this must be before NPXGetVodMoreOptionVodModelNode
        registerWrapper(NPXGetVodMoreOptionVodModelNode.class);
        registerWrapper(NPXGetVodLandingListProductModelNode.class);
        registerWrapper(NPXGetRecommendationResultModelNode.class);
        registerWrapper(NPXGetLiveProgramsProgramModelNode.class);
        registerWrapper(NPXEpgChannelListChannelModelNode.class);
        registerWrapper(NPXEpgProgramDetailDataModelNode.class);
        registerWrapper(NPXGetVodMoreOptionEpgModelNode.class);
        registerWrapper(NPXEpgAutocompleteSearchDataModelNode.class);
        registerWrapper(NPXEpgAutocompleteSearchChannelModelNode.class);
        registerWrapper(NPXEpgAutocompleteSearchMoviesModelNode.class);
        registerWrapper(NPXEpgAutocompleteSearchOndemandModelNode.class);
        registerWrapper(NPXEpgAutocompleteSearchProgramModelNode.class);

        // branch nodes
        registerWrapper(NPXGetLandingDataCatNode.class);
        registerWrapper(NPXGetVodLandingPageOutputModelNode.class);
        registerWrapper(NPXGetVodLandingListListModelNode.class);
        registerWrapper(NPXGetVodLandingListCategoryModelNode.class);

        // my now items
        registerWrapper(NPXMyNowGetWatchlistItemResultModelNode.class);
        registerWrapper(NPXMyNowGetWatchlistItemOutputModelNode.class);
        registerWrapper(NPXMyNowAddWatchlistItemDataModelNode.class);
        registerWrapper(NPXMyNowGetPVRItemsResponseModelNode.class);
        registerWrapper(NPXMyNowGetSavedListDataModelNode.class);

        // recommendation items
        registerWrapper(NPXTAGetTaEpgPreferenceRecommendationRecommendationsModelNode.class);
        registerWrapper(NPXTAGetTaVodEpgPreferenceParallelRecommendationRecommendationsModelNode.class);
        registerWrapper(NPXTAGetTaVodPreferenceTopListBackupRecommendationRecommendationsModelNode.class);
    }

    transient public Node currentPlayingProgram;
    transient public Node nextPlayingProgram;
    transient protected Object data;
    transient protected Bitmap image;
    protected ArrayList<Node> subNodes;
    private List<Node> linkedEPG;
    private List<Node> linkedVOD;
    private List<Node> otherTimes;

    public static Node create(Object data, Node parent) {

        Node ret = null;
        if (data == null) return null;
        if (data instanceof Node) return (Node) data;

        Class wrpCls = WrapperClass.find(wrappers, data.getClass());
        if (wrpCls != null) {
            try {
                ret = (Node) wrpCls.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ret == null) {
            ret = new Node();
        }
        ret.setData(data, parent);
        return ret;
    }

    public static List<Node> createList(Object[] nodes, Node parent) {
        ArrayList<Node> ret = new ArrayList<>();
        if (nodes != null) for (Object node : nodes) {
            ret.add(create(node, parent));
        }
        return ret;
    }

    public static Node emptyNode() {
        return new Node();
    }

    public static Node load(String nodeId, long typeMask, Node parent) {
        // TODO
        return null;
    }

    public static void registerWrapper(Class cls) {
        if (cls != Node.class && Node.class.isAssignableFrom(cls)) {
            NodeWrapper wrapper = (NodeWrapper) cls.getAnnotation(NodeWrapper.class);
            if (wrapper != null && wrapper.underlyingClass() != null) {
                wrappers.add(new WrapperClass(wrapper.underlyingClass(), cls));
            }
        }
    }

    public Node addSubNode(Object object) {
        if (subNodes == null) subNodes = new ArrayList<>();

        if (object != null) {
            Node node = create(object, this);
            if (node == null) return null;
            node.setParent(this);
            subNodes.add(node);
            return node;
        }
        return null;
    }

    public List<Node> addSubNodes(Object... objects) {
        List<Node> ret = new ArrayList<>();
        if (objects != null) for (Object obj : objects) {
            Node node = addSubNode(obj);
            if (node != null) ret.add(node);
        }
        return ret;
    }

    @Override
    public int compareTo(Node another) {
        if (another == null) return 1;
        if (isChannel()) {
            return StringUtils.compare(getChannelCode(), another.getChannelCode());
        }
        return StringUtils.compare(getTitle(), another.getTitle());
    }

    public Node copy() {
        try {
            return (Node) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public void deepFirstTraverse(TraversalCallback<Node> callback) {

        if (callback == null) return;

        // init stack
        int level = 0;
        ArrayList<TraversalInfo<Node>> stack = new ArrayList<>();
        stack.add(new TraversalInfo<Node>(this, level));

        while (stack.size() > 0) {
            // pop one from stack
            TraversalInfo<Node> info = stack.remove(stack.size() - 1);
            callback.visit(info);

            // iterate over sub-items
            ArrayList<Node> subNodes = info.data.subNodes;
            if (subNodes != null) {
                for (int i = subNodes.size() - 1; i >= 0; i--) {
                    Node node = subNodes.get(i);
                    stack.add(new TraversalInfo<Node>(node, info.level + 1));
                }
            }
        }
    }

    public boolean findPlayingProgramsAt(long timestamp) {

        Node[] tmp = getPlayingProgramsAt(timestamp);
        Node newPlayingProgram = tmp[0];
        Node newNextPlayingProgram = tmp[1];

        boolean changed = (currentPlayingProgram != newPlayingProgram) || (nextPlayingProgram != newNextPlayingProgram);
        currentPlayingProgram = newPlayingProgram;
        nextPlayingProgram = newNextPlayingProgram;
        return changed;
    }

    public List<Node> getBonusVideo() {
        return getSubNodes(NodeType.BonusVideo);
    }

    public Object getData() {
        return data;
    }

    public List<Node> getEpisodes() {
        return getSubNodes(NodeType.Episode | NodeType.Episodic);
    }

    public Node getFirstEpisode() {
        // first episode is the last array object because the list is in descending order
        List<Node> list = getSubNodes(NodeType.Episode);
        if (list == null || list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }

    public String getJsonString() {
        return GsonUtil.toJson(this);
    }

    public List<Node> getLinkedEPG() {
        return linkedEPG;
    }

    public void setLinkedEPG(List<Node> linkedEPG) {
        this.linkedEPG = linkedEPG;
    }

    public List<Node> getLinkedVOD() {
        return linkedVOD;
    }

    public void setLinkedVOD(List<Node> linkedVOD) {
        this.linkedVOD = linkedVOD;
    }

    public List<Node> getOtherTimes() {
        return otherTimes;
    }

    public void setOtherTimes(List<Node> otherTimes) {
        this.otherTimes = otherTimes;
    }

    public Node getPlayingProgramAt(long timestamp) {
        return getPlayingProgramsAt(timestamp)[0];
    }

    public Node[] getPlayingProgramsAt(long timestamp) {

        Node[] ret = new Node[2];
        Node newPlayingProgram = null;
        Node newNextPlayingProgram = null;

        List<Node> programs = getPrograms();
        if (programs != null) for (int i = 0; i < programs.size(); i++) {
            Node program = programs.get(i);
            if (program.isLiveAt(timestamp)) {
                newPlayingProgram = program;
                if (i < programs.size() - 1)
                    newNextPlayingProgram = programs.get(i + 1);
                break;
            }
        }

        ret[0] = newPlayingProgram;
        ret[1] = newNextPlayingProgram;
        return ret;
    }

    public ArrayList<Node> getProducts() {
        return getSubNodes(NodeType.Product);
    }

    public ArrayList<Node> getPrograms() {
        return getSubNodes(NodeType.Program);
    }

    public ArrayList<Node> getSubNodes(final long type, boolean recursive) {
        final ArrayList<Node> ret = new ArrayList<Node>();
        if (!recursive) {
            if (subNodes != null) for (Node node : subNodes) {
                if (node.isType(type)) ret.add(node);
            }
        } else {
            deepFirstTraverse(new TraversalCallback<Node>() {
                @Override
                public void visit(TraversalInfo<Node> info) {
                    if (info.data == Node.this) return;
                    if (info.data.isType(type)) ret.add(info.data);
                }
            });
        }
        return ret;
    }

    public ArrayList<Node> getSubNodes(long type) {
        return getSubNodes(type, false);
    }

    public ArrayList<Node> getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(List<Node> subNodes) {
        this.subNodes = subNodes == null ? null : new ArrayList(subNodes);
    }

    public Node getTrailer() {
        ArrayList<Node> nodes = getSubNodes(NodeType.Trailer);
        if (nodes != null && nodes.size() > 0) {
            return nodes.get(0);
        }
        return null;
    }

    public void removeSubNodes(Predicate<Node> predicate) {
        if (subNodes == null || predicate == null) return;
        Iterator<Node> itr = subNodes.iterator();
        while (itr.hasNext()) {
            Node node = itr.next();
            if (predicate.test(node)) {
                itr.remove();
            }
        }
    }

    public void setData(Object data, Node parent) {
        this.data = data;
        setParent(parent);
    }

    protected void setPaymentType(String paymentType) {
        if ("PPV".equalsIgnoreCase(paymentType) || "PPS".equalsIgnoreCase(paymentType) || "PPE".equalsIgnoreCase(paymentType)) {
            type |= NodeType.VE;
        }
    }

    static class WrapperClass {
        Class underlyingClass;
        Class wrapperClass;

        WrapperClass(Class underlyingClass, Class wrapperClass) {
            this.underlyingClass = underlyingClass;
            this.wrapperClass = wrapperClass;
        }

        static Class find(List<WrapperClass> wrappers, Class targetClass) {
            if (wrappers == null) return null;
            for (WrapperClass wc : wrappers) {
                if (wc.underlyingClass.isAssignableFrom(targetClass)) return wc.wrapperClass;
            }
            return null;
        }
    }
}
