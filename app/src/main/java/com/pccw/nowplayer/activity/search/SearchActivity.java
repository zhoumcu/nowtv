package com.pccw.nowplayer.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.SearchListAdapter;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.Group;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.SearchClient;
import com.pccw.nowplayer.model.SearchTable;
import com.pccw.nowplayer.model.db_orm.OrmController;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Validations;
import com.pccw.nowplayer.utils.gson.GsonUtil;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;
import com.pccw.nowtv.nmaf.npx.catalog.NPXCatalog;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 2016/3/15.
 */
public class SearchActivity extends SearchBaseActivity implements SearchListAdapter.SearchListener {
    @Bind(R.id.progress_lay)
    FrameLayout progressLay;
    List<SearchTable> searchTables;
    @Bind(R.id.search_view)
    MaterialSearchView searchView;
    @Bind(R.id.title)
    TextView title;
    private Node searchResult;
    private DataModels.NPXEpgTopSearchResponseModel topSearch;

    @Override
    protected void bindEvents() {
        searchView.setVoiceSearch(false);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    showDefaultList();
                } else {
                    query(newText, false);
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                progressLay.setVisibility(View.VISIBLE);
                query(query, true);
                return true;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewClosed() {

            }

            @Override
            public void onSearchViewShown() {
                searchView.setQuery(searchVariable, true);
            }
        });
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_serach);
        ButterKnife.bind(this);
        list = (RecyclerView) findViewById(R.id.list);
        progressLay.setVisibility(View.GONE);
        searchVariable = getIntent().getExtras().getString(Constants.ARG_SEARCH_VALUE, "");
        list.setLayoutManager(RecycleViewManagerFactory.verticalList(this));
        list.addItemDecoration(RecycleViewManagerFactory.getNormalDecoration(this));
        title.setText(getString(R.string.search));
        showDefaultList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.post(new Runnable() {
            @Override
            public void run() {
                searchView.showSearch(true);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDelete(int postion, SearchTable searchTable) {
        OrmController.delete(searchTable);
        showDefaultList();
    }

    @Override
    public void onDeleteAll() {
        OrmController.deleteAll(SearchTable.class);
        showDefaultList();
    }

    @Override
    public void onSearch(String query) {
        if (!searchView.isSearchOpen()) searchView.showSearch(true);
        searchView.setQuery(query, true);
    }

    private void query(String query, final boolean isFullSearch) {
        if (isFullSearch) {
            SearchTable searchTable = new SearchTable(query, System.currentTimeMillis(), NowIDClient.getInstance().getNowId());
            OrmController.save(searchTable);
        }
        this.searchValue = query;
        SearchClient.getInstance().search(query).then(new DoneCallback<Node>() {
            @Override
            public void onDone(Node result) {
                progressLay.setVisibility(View.GONE);
                SearchActivity.this.searchResult = result;
                if (isFullSearch) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.ARG_SEARCH_DETAIL, GsonUtil.toJson(result));
                    NowPlayerLinkClient.getInstance().executeUrlAction(SearchActivity.this, Constants.ACTION_SEARCH + ":" + searchValue, bundle);
                } else {
                    showSearchList(SearchActivity.this.searchResult, false);
                }
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Log.w(TAG, "Server side error: " + result);
            }
        });
    }

    private void showDefaultList() {
        if (topSearch == null) {
            NPXCatalog.getSharedInstance().epgTopSearchProgram(new NPXCatalog.NPXCatalogCallback<DataModels.NPXEpgTopSearchResponseModel>() {
                @Override
                public void onRequestFailed(int i) {
                    if (TextUtils.isEmpty(searchValue)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showPreSearchList();
                            }
                        });
                    }
                }

                @Override
                public void onRequestSuccessful(DataModels.NPXEpgTopSearchResponseModel npxEpgTopSearchResponseModel) {
                    if (TextUtils.isEmpty(searchValue)) {
                        topSearch = npxEpgTopSearchResponseModel;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showPreSearchList();
                            }
                        });
                    }
                }
            });
        }
        showPreSearchList();

    }

    private void showPreSearchList() {
        if (searchTables == null)
            searchTables = new ArrayList<>();
        searchTables.clear();
        searchTables.addAll(OrmController.listAll(SearchTable.class, "timestamp desc limit 6"));
        Map<Group, List<Object>> map = new TreeMap<>();
        if (!Validations.isEmptyOrNull(searchTables)) {
            Group group = new Group(getString(R.string.history));
            map.put(group, new ArrayList<Object>(searchTables));
        }
        if (topSearch != null && !Validations.isEmptyOrNull(topSearch.docs)) {
            map.put(new Group(getString(R.string.top_search)), Arrays.asList((Object[]) (topSearch.docs)));
        }
        searchListAdapter = new SearchListAdapter(map, this);
        searchListAdapter.setSearchListener(this);
        list.setAdapter(searchListAdapter);
    }
}
