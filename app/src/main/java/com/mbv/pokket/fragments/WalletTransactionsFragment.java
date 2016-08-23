package com.mbv.pokket.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.mbv.pokket.R;
import com.mbv.pokket.adapters.WalletTransactionBaseAdapter;
import com.mbv.pokket.dao.WalletTransactionsDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.threads.loaders.LoaderWalletTransactions;

import java.util.List;

/**
 * Created by arindamnath on 15/03/16.
 */
public class WalletTransactionsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<WalletTransactionsDAO>>  {

    private Bundle queryData;

    private View mViewHolder;
    private ListView detailsList;
    private PullRefreshLayout pullRefreshLayout;
    private WalletTransactionBaseAdapter walletTransactionBaseAdapter;

    public static WalletTransactionsFragment newInstance() {
        WalletTransactionsFragment fragment = new WalletTransactionsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        walletTransactionBaseAdapter = new WalletTransactionBaseAdapter(getContext());

        mViewHolder = inflater.inflate(R.layout.fragment_details_list, container, false);
        detailsList = (ListView) mViewHolder.findViewById(R.id.details_list);
        pullRefreshLayout = (PullRefreshLayout) mViewHolder.findViewById(R.id.details_refresh_container);

        pullRefreshLayout.setRefreshing(false);
        detailsList.setAdapter(walletTransactionBaseAdapter);

        mViewHolder.findViewById(R.id.accounts_empty_list)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queryData = new Bundle();
                        getActivity().getSupportLoaderManager()
                                .initLoader(LoaderID.WALLET_TRANSACTIONS.getValue(), queryData, WalletTransactionsFragment.this).forceLoad();
                    }
                });

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryData = new Bundle();
                getActivity().getSupportLoaderManager()
                        .initLoader(LoaderID.WALLET_TRANSACTIONS.getValue(), queryData, WalletTransactionsFragment.this).forceLoad();
            }
        });

        queryData = new Bundle();
        getActivity().getSupportLoaderManager()
                .initLoader(LoaderID.WALLET_TRANSACTIONS.getValue(), queryData, WalletTransactionsFragment.this).forceLoad();
        return mViewHolder;
    }

    @Override
    public Loader<List<WalletTransactionsDAO>> onCreateLoader(int id, Bundle args) {
        pullRefreshLayout.setRefreshing(true);
        return new LoaderWalletTransactions(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<WalletTransactionsDAO>> loader, List<WalletTransactionsDAO> data) {
        pullRefreshLayout.setRefreshing(false);
        if(data != null) {
            walletTransactionBaseAdapter.setData(data);
            detailsList.setEmptyView(mViewHolder.findViewById(R.id.transactions_empty_list));
        } else {
            detailsList.setEmptyView(mViewHolder.findViewById(R.id.details_no_connection));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<WalletTransactionsDAO>> loader) {
        pullRefreshLayout.setRefreshing(false);
    }
}
