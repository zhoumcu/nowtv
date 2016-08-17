package com.pccw.nowplayer.activity.search;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.gson.GsonUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/9/2016.
 */
public class SearchDetailActivity extends SearchBaseActivity {

    @Bind(R.id.title)
    TextView title;
    private Node searchResult;


    @Override
    protected void initIntentData() {
        super.initIntentData();
        searchResult = GsonUtil.fromJson(getIntent().getStringExtra(Constants.ARG_SEARCH_DETAIL), Node.class);
        searchVariable = getIntent().getStringExtra(Constants.ARG_SEARCH_VALUE);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_search_detail);
        ButterKnife.bind(this);
        list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(RecycleViewManagerFactory.verticalList(this));
        list.addItemDecoration(RecycleViewManagerFactory.getNormalDecoration(this));
        title.setText("\"" + searchVariable + "\"");
    }

    @Override
    protected void bindEvents() {
        showSearchList(searchResult, true);
    }

}
