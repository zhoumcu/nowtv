package com.pccw.nowplayer.activity.node;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.ToolBarBaseActivity;
import com.pccw.nowplayer.adapter.MyNowAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.ImageUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shuaikunwang on 22/7/16.
 */
public class EpisodeListActivity extends ToolBarBaseActivity {
    @Bind(R.id.recycle_view)
    RecyclerView recycleView;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_image)
    ImageView titleImage;
    private Node node;

    @Override
    protected void initIntentData() {
        super.initIntentData();
        node = (Node) getIntent().getSerializableExtra(Constants.ARG_NODE);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_node_grid);
        ButterKnife.bind(this);
        if (node != null) {
            ImageUtils.loadImage(titleImage, node);
            title.setText(node.getTitle());
            recycleView.setLayoutManager(RecycleViewManagerFactory.verticalList(this));
            recycleView.addItemDecoration(RecycleViewManagerFactory.getNormalDecoration(this));
            MyNowAdapter adapter = new MyNowAdapter(this, node.getEpisodes());
            recycleView.setAdapter(adapter);
        }
    }

    @Override
    protected void bindEvents() {

    }
}
