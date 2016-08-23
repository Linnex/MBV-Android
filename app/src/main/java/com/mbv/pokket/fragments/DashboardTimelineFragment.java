package com.mbv.pokket.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.mbv.pokket.R;
import com.mbv.pokket.adapters.TimelineRecyclerAdapter;
import com.mbv.pokket.dao.UserProfileDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.threads.loaders.LoaderTimeline;

/**
 * Created by arindamnath on 06/02/16.
 */
public class DashboardTimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<UserProfileDAO>{

    private View mViewHolder;
    private TimelineRecyclerAdapter timelineRecyclerAdapter;
    private PullRefreshLayout pullRefreshLayout;
    private Bundle queryData;
    private RecyclerView recyclerView;

    public static DashboardTimelineFragment newInstance() {
        DashboardTimelineFragment fragment = new DashboardTimelineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        timelineRecyclerAdapter = new TimelineRecyclerAdapter(getActivity());
        mViewHolder = inflater.inflate(R.layout.fragment_timeline, container, false);
        pullRefreshLayout = (PullRefreshLayout) mViewHolder.findViewById(R.id.timeline_refresh_container);
        recyclerView = (RecyclerView) mViewHolder.findViewById(R.id.dashboard_timeline_list);
        pullRefreshLayout.setRefreshing(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(timelineRecyclerAdapter);

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryData = new Bundle();
                getActivity().getSupportLoaderManager()
                        .initLoader(LoaderID.TIMELINE.getValue(), queryData, DashboardTimelineFragment.this).forceLoad();
            }
        });

        mViewHolder.findViewById(R.id.timeline_empty_list)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queryData = new Bundle();
                        getActivity().getSupportLoaderManager()
                                .initLoader(LoaderID.TIMELINE.getValue(), queryData, DashboardTimelineFragment.this).forceLoad();
                    }
                });

        queryData = new Bundle();
        getActivity().getSupportLoaderManager()
                .initLoader(LoaderID.TIMELINE.getValue(), queryData, DashboardTimelineFragment.this).forceLoad();
        return mViewHolder;
    }

    @Override
    public Loader<UserProfileDAO> onCreateLoader(int id, Bundle args) {
        pullRefreshLayout.setRefreshing(true);
        return new LoaderTimeline(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<UserProfileDAO> loader, UserProfileDAO data) {
        pullRefreshLayout.setRefreshing(false);
        if(data != null) {
            mViewHolder.findViewById(R.id.timeline_empty_list).setVisibility(View.GONE);
            timelineRecyclerAdapter.setData(data.getTimelineDAOList());
        } else {
            mViewHolder.findViewById(R.id.timeline_empty_list).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<UserProfileDAO> loader) {
        pullRefreshLayout.setRefreshing(false);
    }
}
