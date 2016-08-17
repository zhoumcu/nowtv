package com.pccw.nowplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;
import com.pccw.nowplayer.constant.VideoTypeIndex;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.link.NowPlayerLinkClient;
import com.pccw.nowplayer.model.EPGClient;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowplayer.utils.Validations;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Swifty on 5/14/2016.
 */
public class TVGuideChannelFragment extends BaseFragment {

    @Bind(R.id.channel_program_list)
    RecyclerView channelProgramList;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.no_data_text)
    TextView noDataText;
    private View root;
    private Node channel;
    private int date_offset;
    private LinearLayoutManager layoutManager;
    private ArrayList<Node> programs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channel = (Node) getArguments().getSerializable(Constants.ARG_NODE);
        date_offset = getArguments().getInt(Constants.ARG_DATE_OFFSET);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_tv_guide_channel, container, false);
        ButterKnife.bind(this, root);
        layoutManager = RecycleViewManagerFactory.verticalList(getContext());
        channelProgramList.setLayoutManager(layoutManager);
        channelProgramList.addItemDecoration(RecycleViewManagerFactory.getNormalDecoration(getContext()));
        progress.setVisibility(View.VISIBLE);

        EPGClient.getInstance().loadPrograms(new ArrayList<Node>() {{
            add(channel);
        }}, date_offset, date_offset).then(new DoneCallback<List<Node>>() {
            @Override
            public void onDone(List<Node> updatedChannels) {
                if (!isAdded()) return;
                progress.setVisibility(View.GONE);

                programs = channel.getPrograms();
                if (programs == null || programs.size() == 0) {
                    noDataText.setVisibility(View.VISIBLE);
                }
                ChannelProgramsAdapter channelProgramsAdapter = new ChannelProgramsAdapter(programs);
                channelProgramList.setAdapter(channelProgramsAdapter);
                channelProgramList.post(new Runnable() {
                    @Override
                    public void run() {
                        int livePos = getLiveProgramsPos();
                        if (livePos > -1) {
                            channelProgramList.scrollToPosition(caculateLivePos(livePos));
                        }

                    }
                });
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                if (!isAdded()) return;
                progress.setVisibility(View.GONE);
            }
        });

        return root;
    }

    private int caculateLivePos(int livePos) {
        if (Validations.isEmptyOrNull(programs)) return 0;
        int pos = livePos + getVisibleChannelCount() - 1;
        return Math.min(Math.max(0, pos), programs.size() - 1);
    }

    private int getLiveProgramsPos() {
        if (channel != null) {
            ArrayList<Node> programs = channel.getPrograms();
            if (programs != null) for (int i = 0; i < programs.size(); i++) {
                Node program = programs.get(i);
                if (program.isLive()) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class ChannelProgramsAdapter extends RecyclerView.Adapter<ChannelProgramsAdapter.ViewHolder> {

        private final List<Node> programs;

        public ChannelProgramsAdapter(List<Node> programs) {
            if (programs == null) {
                this.programs = new ArrayList<>();
            } else {
                this.programs = programs;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.view_tv_guide_channel_program_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Node program = programs.get(position);
            holder.start0.setText(program.getStartTimeText());
            holder.title0.setText(program.getTitle());
            changeStyle(holder, channel.isSubscribed());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.ARG_CHANNEL, channel);
                    bundle.putSerializable(Constants.ARG_NODE, program);
                    NowPlayerLinkClient.getInstance().executeUrlAction(getContext(), Constants.ACTION_VIDEO_DETAIL + ":" + VideoTypeIndex.EPG, bundle);
                }
            });
            if (program.isLive()) {
                holder.live.setVisibility(View.VISIBLE);
            } else {
                holder.live.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return programs == null ? 0 : programs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.start_0)
            TextView start0;
            @Bind(R.id.ic_0)
            ImageView ic0;
            @Bind(R.id.title_0)
            TextView title0;
            @Bind(R.id.live)
            TextView live;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        private void changeStyle(ViewHolder holder, boolean isSubscribed) {
            if (isSubscribed) {
                holder.start0.setTextColor(getResources().getColor(R.color.white));
                holder.title0.setTextColor(getResources().getColor(R.color.white));
            } else {
                holder.start0.setTextColor(getResources().getColor(R.color.now_grey));
                holder.title0.setTextColor(getResources().getColor(R.color.now_grey));
            }
        }
    }

    public int getVisibleChannelCount() {
        int s = layoutManager.findFirstVisibleItemPosition();
        int e = layoutManager.findLastVisibleItemPosition();
        return e - s + 1;
    }
}
