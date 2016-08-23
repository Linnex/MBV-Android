package com.mbv.pokket.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.mbv.pokket.ActivityBankAccountDetails;
import com.mbv.pokket.R;
import com.mbv.pokket.adapters.WalletAccountsBaseAdapter;
import com.mbv.pokket.dao.WalletAccountsDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.threads.loaders.LoaderWalletAccounts;

import java.util.List;

/**
 * Created by arindamnath on 15/03/16.
 */
public class WalletAccountsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<WalletAccountsDAO>>{

    private WalletAccountsBaseAdapter walletAccountsBaseAdapter;

    private Bundle queryData;
    private View mViewHolder;
    private PullRefreshLayout pullRefreshLayout;
    private ListView detailsList;
    private FloatingActionButton fab;

    public static WalletAccountsFragment newInstance() {
        WalletAccountsFragment fragment = new WalletAccountsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        walletAccountsBaseAdapter = new WalletAccountsBaseAdapter(getContext());

        mViewHolder = inflater.inflate(R.layout.fragment_details_list, container, false);
        detailsList = (ListView) mViewHolder.findViewById(R.id.details_list);
        fab = (FloatingActionButton) mViewHolder.findViewById(R.id.details_add_button);
        pullRefreshLayout = (PullRefreshLayout) mViewHolder.findViewById(R.id.details_refresh_container);

        fab.setVisibility(View.VISIBLE);
        pullRefreshLayout.setRefreshing(false);
        detailsList.setAdapter(walletAccountsBaseAdapter);

        mViewHolder.findViewById(R.id.accounts_empty_list)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queryData = new Bundle();
                        getActivity().getSupportLoaderManager()
                                .initLoader(LoaderID.WALLET_ACCOUNTS.getValue(), queryData, WalletAccountsFragment.this).forceLoad();
                    }
                });

        detailsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WalletAccountsDAO walletAccountsDAO = walletAccountsBaseAdapter.getItem(position);
                Intent walletAccounts = new Intent(getActivity(), ActivityBankAccountDetails.class);
                walletAccounts.putExtra("id", walletAccountsDAO.getId());
                walletAccounts.putExtra("account", walletAccountsDAO.getAccountId());
                walletAccounts.putExtra("primary", walletAccountsDAO.getIsPrimary());
                walletAccounts.putExtra("bankId", walletAccountsDAO.getBankCodeDAO().getId());
                walletAccounts.putExtra("ifsc", walletAccountsDAO.getBankCodeDAO().getIfscCode());
                walletAccounts.putExtra("bankName", walletAccountsDAO.getBankCodeDAO().getBankName());
                walletAccounts.putExtra("address", walletAccountsDAO.getBankCodeDAO().getBankAddress());
                walletAccounts.putExtra("city", walletAccountsDAO.getBankCodeDAO().getBankCity());
                walletAccounts.putExtra("district", walletAccountsDAO.getBankCodeDAO().getBankDistrict());
                walletAccounts.putExtra("state", walletAccountsDAO.getBankCodeDAO().getBankState());
                startActivity(walletAccounts);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityBankAccountDetails.class));
            }
        });

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryData = new Bundle();
                getActivity().getSupportLoaderManager()
                        .initLoader(LoaderID.WALLET_ACCOUNTS.getValue(), queryData, WalletAccountsFragment.this).forceLoad();
            }
        });

        queryData = new Bundle();
        getActivity().getSupportLoaderManager()
                .initLoader(LoaderID.WALLET_ACCOUNTS.getValue(), queryData, WalletAccountsFragment.this).forceLoad();
        return mViewHolder;
    }

    @Override
    public Loader<List<WalletAccountsDAO>> onCreateLoader(int id, Bundle args) {
        pullRefreshLayout.setRefreshing(true);
        return new LoaderWalletAccounts(getContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<WalletAccountsDAO>> loader, List<WalletAccountsDAO> data) {
        pullRefreshLayout.setRefreshing(false);
        if(data != null) {
            walletAccountsBaseAdapter.setData(data);
            detailsList.setEmptyView(mViewHolder.findViewById(R.id.accounts_empty_list));
        } else {
            detailsList.setEmptyView(mViewHolder.findViewById(R.id.details_no_connection));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<WalletAccountsDAO>> loader) {
        pullRefreshLayout.setRefreshing(false);
    }
}
