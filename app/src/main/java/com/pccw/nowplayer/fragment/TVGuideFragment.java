package com.pccw.nowplayer.fragment;

import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.adapter.TVGuideChannelAdapter;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.model.CatalogClient;
import com.pccw.nowplayer.model.EPGClient;
import com.pccw.nowplayer.model.TVGuideModel;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Validations;
import com.pccw.nowplayer.widget.TimeLineView;

import org.jdeferred.DoneCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/12/2016.
 */
public class TVGuideFragment extends MainBaseFragment {

    final List<TVGuideModel.Genre> genreList = new ArrayList<TVGuideModel.Genre>();
    @Bind(R.id.channels)
    Spinner channels;
    @Bind(R.id.date)
    Spinner date;
    @Bind(R.id.time_line)
    TimeLineView timeLine;
    @Bind(R.id.time_table)
    RecyclerView timeTable;
    TVGuideModel tvGuideModel;
    private Handler handler;
    private LinearLayoutManager layoutManager;
    private TVGuideChannelAdapter listAdapter;
    private Runnable updateScheduleTask = new Runnable() {
        @Override
        public void run() {
            if (listAdapter != null) listAdapter.updateSchedule();
        }
    };

    private void bindViews() {
        if (!isAdded()) return;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.date, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        date.setAdapter(adapter);
        date.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<TVGuideModel.Genre> channelAdapter = new ArrayAdapter<TVGuideModel.Genre>(getContext(),
                R.layout.simple_spinner_dropdown_item, R.id.dropdown_view, genreList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView titleTv = (TextView) view.findViewById(R.id.dropdown_view);
                titleTv.setTextColor(getResources().getColor(R.color.now_grey));
                titleTv.setText(genreList.get(position).name);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView titleTv = (TextView) view.findViewById(R.id.dropdown_view);
                titleTv.setTextColor(getResources().getColor(R.color.white));
                titleTv.setText(genreList.get(position).name);
                return view;
            }
        };
        channelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        channels.setAdapter(channelAdapter);
        channels.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    timeLine.toggleShowArrow(true);
                    timeLine.setStartFromNow(true);
                } else {
                    timeLine.toggleShowArrow(false);
                    timeLine.setStartFromNow(false);
                }
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        channels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        timeLine.setOnScollListener(new TimeLineView.OnScollListener() {
            @Override
            public void onScroll(long currentX, long currentScrollTimeStampStartByDay) {
                if (listAdapter != null)
                    listAdapter.setTimestamp(timeLine.getCurrentTimeStampStartByDay() + getOffsetTs());
            }
        });
        CatalogClient.getInstance().laodEPGRecommendations().done(new DoneCallback<Set<String>>() {
            @Override
            public void onDone(Set<String> result) {
                loadCurrentPageData();
            }
        });
    }

    @Override
    public View createViews(LayoutInflater inflater, ViewGroup parentContainer) {
        View root = inflater.inflate(R.layout.fragment_tv_guide, parentContainer, false);
        ButterKnife.bind(this, root);
        initViews();
        loadData();
        return root;
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    private long getOffsetTs() {
        return timeLine.getTodayTs() + timeLine.onedayts * date.getSelectedItemPosition();
    }

    private void initViews() {
        layoutManager = RecycleViewManagerFactory.verticalList(getContext());
        timeTable.setLayoutManager(layoutManager);
        timeTable.addItemDecoration(RecycleViewManagerFactory.getNormalDecoration(getContext()));
        timeTable.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    loadCurrentPageData();
                }
            }
        });
        timeTable.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (listAdapter != null) listAdapter.updateSchedule();
            }
        });
        timeLine.setStartFromNow(true);
    }

    private void loadCurrentPageData() {
        getHandler().removeCallbacks(updateScheduleTask);
        getHandler().postDelayed(updateScheduleTask, 250);
    }

    private void loadData() {
        EPGClient.getInstance().loadChannelGenres().then(new DoneCallback<List<TVGuideModel.Genre>>() {
            @Override
            public void onDone(List<TVGuideModel.Genre> result) {
                if (!isAdded()) return;
                genreList.clear();
                if (result != null) genreList.addAll(result);
                bindViews();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentPageData();
    }

    @Override
    public ActionBar showActionBar() {
        return new ActionBar(false, getString(R.string.tv_guide), false);
    }

    private void updateList() {
        if (!isAdded()) return;
        Parcelable state = timeTable.getLayoutManager().onSaveInstanceState();
        List<Node> channelList = genreList.get(channels.getSelectedItemPosition()).channelList;
        if (Validations.isEmptyOrNull(channelList)) channelList = new ArrayList<Node>() {{
            if (genreList.size() > 0) {
                List<Node> list = genreList.get(0).channelList;
                if (list != null) addAll(list);
            }
        }};
        Collections.sort(channelList, new Comparator<Node>() {
            @Override
            public int compare(Node lhs, Node rhs) {
                if (!lhs.isFavorite() && rhs.isFavorite()) return 1;
                else if (lhs.isFavorite() && !rhs.isFavorite()) return -1;
                else {
                    return lhs.getChannelId() - rhs.getChannelId();
                }
            }
        });
        listAdapter = new TVGuideChannelAdapter(getContext(), layoutManager, channelList, timeLine.getCurrentTimeStampStartByDay() + getOffsetTs());
        timeTable.setAdapter(listAdapter);
        timeTable.getLayoutManager().onRestoreInstanceState(state);
        loadCurrentPageData();
    }
}
