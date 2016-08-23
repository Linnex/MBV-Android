package com.mbv.pokket.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.mbv.pokket.ActivityLoanDetails;
import com.mbv.pokket.R;
import com.mbv.pokket.adapters.EventsListBaseAdapter;
import com.mbv.pokket.dao.CalendarEventDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.threads.loaders.LoaderEvents;

import java.util.List;

/**
 * Created by arindamnath on 12/01/16.
 */
public class DashboardEventsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Object>> {

    private View mViewHolder;
    private PullRefreshLayout pullRefreshLayout;
    private ListView listView;
    private Bundle queryData;
    private EventsListBaseAdapter eventsListBaseAdapter;

    public static DashboardEventsFragment newInstance() {
        DashboardEventsFragment fragment = new DashboardEventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        eventsListBaseAdapter = new EventsListBaseAdapter(getActivity());

        mViewHolder = inflater.inflate(R.layout.fragment_details_list, container, false);

        pullRefreshLayout = (PullRefreshLayout) mViewHolder.findViewById(R.id.details_refresh_container);
        listView = (ListView) mViewHolder.findViewById(R.id.details_list);
        listView.setAdapter(eventsListBaseAdapter);
        pullRefreshLayout.setRefreshing(false);

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryData = new Bundle();
                queryData.putString("query", "");
                getActivity().getSupportLoaderManager()
                        .initLoader(LoaderID.EVENTS.getValue(), queryData, DashboardEventsFragment.this).forceLoad();
            }
        });

        mViewHolder.findViewById(R.id.events_empty_list)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queryData = new Bundle();
                        queryData.putString("query", "");
                        getActivity().getSupportLoaderManager()
                                .initLoader(LoaderID.EVENTS.getValue(), queryData, DashboardEventsFragment.this).forceLoad();
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActivityLoanDetails.class);
                intent.putExtra("loanId", ((CalendarEventDAO) eventsListBaseAdapter.getItem(position)).getId());
                startActivity(intent);
            }
        });

        queryData = new Bundle();
        queryData.putString("query", "");
        getActivity().getSupportLoaderManager().initLoader(LoaderID.EVENTS.getValue(), queryData, this).forceLoad();

        return mViewHolder;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        pullRefreshLayout.setRefreshing(true);
        return new LoaderEvents(getActivity(), null);
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        pullRefreshLayout.setRefreshing(false);
        if(data != null) {
            eventsListBaseAdapter.setData(data);
            listView.setEmptyView(mViewHolder.findViewById(R.id.events_empty_list));
        } else {
            listView.setEmptyView(mViewHolder.findViewById(R.id.details_no_connection));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        pullRefreshLayout.setRefreshing(false);
    }
}
