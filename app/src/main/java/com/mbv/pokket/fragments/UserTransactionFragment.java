package com.mbv.pokket.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.mbv.pokket.ActivityLoanDetails;
import com.mbv.pokket.R;
import com.mbv.pokket.adapters.TransactionHistoryAdapter;
import com.mbv.pokket.dao.CalendarEventDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.dao.enums.Status;
import com.mbv.pokket.threads.loaders.LoaderTransactionHistory;

import java.util.List;

/**
 * Created by arindamnath on 13/01/16.
 */
public class UserTransactionFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<CalendarEventDAO>> {

    public static final int OPEN = 1;
    public static final int CLOSE = 2;
    public static final int PENDING = 3;
    private static final String TYPE = "type";

    private View mViewHolder;
    private PullRefreshLayout pullRefreshLayout;
    private ListView listView;
    private TransactionHistoryAdapter transactionHistoryAdapter;

    public static UserTransactionFragment newInstance(int type) {
        UserTransactionFragment fragment = new UserTransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        transactionHistoryAdapter = new TransactionHistoryAdapter(getActivity());

        mViewHolder = inflater.inflate(R.layout.fragment_details_list, container, false);
        pullRefreshLayout = (PullRefreshLayout) mViewHolder.findViewById(R.id.details_refresh_container);
        listView = (ListView) mViewHolder.findViewById(R.id.details_list);
        listView.setAdapter(transactionHistoryAdapter);

        pullRefreshLayout.setRefreshing(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActivityLoanDetails.class);
                intent.putExtra("loanId", transactionHistoryAdapter.getItem(position).getId());
                startActivity(intent);
            }
        });

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendQuery();
            }
        });

        sendQuery();
        return mViewHolder;
    }

    private void sendQuery() {
        Bundle data = new Bundle();
        switch (getArguments().getInt(TYPE)) {
            case OPEN:
                data.putString("status", Status.ACTIVE.toString());
                getActivity().getSupportLoaderManager().initLoader(
                        LoaderID.OPEN_TRANSACTIONS.getValue(), data, this).forceLoad();
                break;
            case CLOSE:
                data.putString("status", Status.COMPLETED.toString());
                getActivity().getSupportLoaderManager().initLoader(
                        LoaderID.CLOSE_TRANSACTION.getValue(), data, this).forceLoad();
                break;
            case PENDING:
                data.putString("status", Status.PENDING.toString());
                getActivity().getSupportLoaderManager().initLoader(
                        LoaderID.PENDING_TRANSACTIONS.getValue(), data, this).forceLoad();
                break;
        }
    }

    @Override
    public Loader<List<CalendarEventDAO>> onCreateLoader(int id, Bundle args) {
        pullRefreshLayout.setRefreshing(true);
        return new LoaderTransactionHistory(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<CalendarEventDAO>> loader, List<CalendarEventDAO> data) {
        pullRefreshLayout.setRefreshing(false);
        if(data != null) {
            transactionHistoryAdapter.setData(data);
            listView.setEmptyView(mViewHolder.findViewById(R.id.details_empty_list));
        } else {
            listView.setEmptyView(mViewHolder.findViewById(R.id.details_no_connection));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CalendarEventDAO>> loader) {
        pullRefreshLayout.setRefreshing(false);
    }
}

