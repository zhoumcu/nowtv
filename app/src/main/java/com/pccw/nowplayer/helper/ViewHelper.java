package com.pccw.nowplayer.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.constant.VideoTypeIndex;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.node.Node;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by swifty on 24/6/2016.
 */
public class ViewHelper {

    public static Bundle exceAction(Context context, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        if (bundle.getSerializable(Constants.ARG_LEAF_NODE_ARRAY) != null) {
            NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_ALL_NODE_PAGE, bundle);
        } else if (bundle.getSerializable(Constants.ARG_SUB_NODE_ARRAY) != null) {
            NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_SUB_NODES_PAGE, bundle);
        } else if (bundle.getSerializable(Constants.ARG_SUB_NODE_SINGLE) != null) {
            NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_ALL_NODE_PAGE, bundle);
        } else {
            NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_SUB_NODES_PAGE, bundle);
        }
        return bundle;
    }

    protected static Bundle generateLeafNode(Node node, ArrayList<Node> subNodes, @Nullable Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        //bundle.putSerializable(Constants.ARG_LEAF_NODE_ARRAY, subNodes);
        return bundle;
    }

    protected static Bundle generateSubNodeSingle(Node node, Node singleNode, @Nullable Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        // bundle.putSerializable(Constants.ARG_SUB_NODE_SINGLE, singleNode);
        return bundle;
    }

    public static void generateWatermarkText(TextView textView){

        String nowId = NowIDClient.getInstance().getNowId();
        if(nowId==null){
            textView.setText("");
        }else{
            if(nowId.length()>20){
               nowId = nowId.substring(0,17)+"...";
            }
            textView.setText(nowId);
        }
    }

    public static void generateGravity(TextView textView){
        Random random = new Random();
        int value = random.nextInt(4);
        int gravity = Gravity.LEFT;
        switch (value){
            case 0:
                gravity = Gravity.LEFT|Gravity.TOP;
                break;
            case 1:
                gravity = Gravity.LEFT|Gravity.BOTTOM;
                break;
            case 2:
                gravity = Gravity.RIGHT|Gravity.TOP;
                break;
            case 3:
                gravity = Gravity.RIGHT|Gravity.BOTTOM;
                break;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
        layoutParams.gravity = gravity;
        textView.setLayoutParams(layoutParams);

    }


    protected static Bundle generateSubNodeArray(Node node, ArrayList<Node> subNodes, @Nullable Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        //  bundle.putSerializable(Constants.ARG_SUB_NODE_ARRAY, subNodes);
        return bundle;
    }

    protected static Bundle generateNodeBundle(Node node, Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        bundle.putSerializable(Constants.ARG_NODE, node);
        return bundle;
    }

    public static boolean exceNodeAction(Context context,Node node) {
        if (NodeType.isType(node.getType(), NodeType.Cat3)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, node);
            if (node.getSubNodes(NodeType.Branch).size() == 1) {
                bundle.putSerializable(Constants.ARG_SUB_NODE_SINGLE, node.getSubNodes(NodeType.Branch).get(0));
            } else {
                bundle.putSerializable(Constants.ARG_SUB_NODE_ARRAY, node.getSubNodes(NodeType.Branch));
            }
            OnDemandViewHelper.exceAction(context, bundle);
            return true;
        } else if (NodeType.isType(node.getType(), NodeType.EPGChannel)) {
            if (TextUtils.equals(node.getNodeId(), String.valueOf(node.getChannelId()))) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.ARG_NODE, node);
                NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_TV_GUIDE_CHANNEL_DETAIL, bundle);
            } else {
                Bundle bundle = new Bundle();
                if (node.isChannel()) {
                    bundle.putSerializable(Constants.ARG_CHANNEL, node);
                    bundle.putSerializable(Constants.ARG_NODE, node.currentPlayingProgram);
                } else {
                    bundle.putSerializable(Constants.ARG_NODE, node);
                }
                NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_DETAIL + ":" + VideoTypeIndex.EPG, bundle);
            }
            return true;
        } else if (node.isVOD()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, node);
            NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_DETAIL + ":" + VideoTypeIndex.VOD, bundle);
            return true;
        } else if (node.isEPG()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ARG_NODE, node);
            NowPlayerLinkClient.getInstance().executeUrlAction(context, Constants.ACTION_VIDEO_DETAIL + ":" + VideoTypeIndex.EPG, bundle);
            return true;
        }
        return false;
    }
}
