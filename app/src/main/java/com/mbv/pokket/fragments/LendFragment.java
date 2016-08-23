package com.mbv.pokket.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.mbv.pokket.ActivityLoanDetails;
import com.mbv.pokket.R;
import com.mbv.pokket.adapters.LendListBaseAdapter;
import com.mbv.pokket.dao.LendDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.dialogs.DialogLenderEligibility;
import com.mbv.pokket.threads.loaders.LoaderLend;
import com.mbv.pokket.threads.tasks.AsyncTaskCheckEligibility;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by arindamnath on 12/01/16.
 */
public class LendFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<LendDAO>> {

    private View mViewHolder;
    private LendListBaseAdapter lendListBaseAdapter;
    private ListView listView;
    private CardView filterOptions;
    private PullRefreshLayout pullRefreshLayout;
    private DialogLenderEligibility dialogLenderEligibility;
    private boolean isEligible;

    public static LendFragment newInstance() {
        LendFragment fragment = new LendFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        lendListBaseAdapter = new LendListBaseAdapter(getActivity());

        mViewHolder = inflater.inflate(R.layout.fragment_lend, container, false);
        dialogLenderEligibility = new DialogLenderEligibility(getContext(), true);

        listView = (ListView) mViewHolder.findViewById(R.id.lend_request_list);
        pullRefreshLayout = (PullRefreshLayout) mViewHolder.findViewById(R.id.lend_refresh_container);
        filterOptions = (CardView) mViewHolder.findViewById(R.id.lend_filter_option_container);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent loanDetails = new Intent(getActivity(), ActivityLoanDetails.class);
                loanDetails.putExtra("loanId", lendListBaseAdapter.getItem(position).getId());
                startActivity(loanDetails);
            }
        });

        mViewHolder.findViewById(R.id.lend_filters)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleDrawer(true);
                    }
                });

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isEligible) {
                    Bundle data = new Bundle();
                    data.putString("query", "");
                    getActivity().getSupportLoaderManager().initLoader(
                            LoaderID.AVAILABLE_BORROWER.getValue(), data, LendFragment.this).forceLoad();
                }
            }
        });

        mViewHolder.findViewById(R.id.lend_close_filter_options)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleDrawer(false);
                    }
                });

        pullRefreshLayout.setRefreshing(false);
        listView.setAdapter(lendListBaseAdapter);
        toggleDrawer(false);

        new AsyncTaskCheckEligibility(1, getContext(), new ServerResponseListener() {
            @Override
            public void onSuccess(int threadId, Object object) {
                isEligible = true;
                Bundle data = new Bundle();
                data.putString("query", "");
                getActivity().getSupportLoaderManager().initLoader(
                        LoaderID.AVAILABLE_BORROWER.getValue(), data, LendFragment.this).forceLoad();
            }

            @Override
            public void onFaliure(ServerEvents serverEvents, Object object) {
                dialogLenderEligibility.show();
                listView.setEmptyView(mViewHolder.findViewById(R.id.lend_empty_list));
                Snackbar.make(mViewHolder, object.toString(), Snackbar.LENGTH_LONG).show();
            }
        }).execute(new JSONObject[0]);

        return mViewHolder;
    }

    @Override
    public Loader<List<LendDAO>> onCreateLoader(int id, Bundle args) {
        pullRefreshLayout.setRefreshing(true);
        return new LoaderLend(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<LendDAO>> loader, List<LendDAO> data) {
        pullRefreshLayout.setRefreshing(false);
        if(data != null) {
            lendListBaseAdapter.setData(data);
            listView.setEmptyView(mViewHolder.findViewById(R.id.lend_empty_list));
        } else {
            listView.setEmptyView(mViewHolder.findViewById(R.id.lend_no_connection));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<LendDAO>> loader) {
        pullRefreshLayout.setRefreshing(false);
    }

    private void toggleDrawer(boolean open) {
        if(open) {
            filterOptions.animate()
                    .setDuration(500)
                    .translationY(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            filterOptions.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            filterOptions.animate()
                    .setDuration(500)
                    .translationY(filterOptions.getHeight())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            filterOptions.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
