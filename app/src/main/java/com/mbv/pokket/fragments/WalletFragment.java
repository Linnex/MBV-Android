package com.mbv.pokket.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.adapters.AppPagerAdapter;
import com.mbv.pokket.dao.PagerFragment;
import com.mbv.pokket.dao.WalletDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.threads.loaders.LoaderWalletDetails;

import java.util.ArrayList;

/**
 * Created by arindamnath on 12/01/16.
 */
public class WalletFragment extends Fragment implements LoaderManager.LoaderCallbacks<WalletDAO> {

    private View mViewHolder;
    private Bundle queryData;
    private TextView walletAmout;

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private AppPagerAdapter appPagerAdapter;
    private ArrayList<PagerFragment> pagerFragments = new ArrayList<>();

    private ProgressDialog progressDialog;

    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        appPagerAdapter = new AppPagerAdapter(getActivity().getSupportFragmentManager(), true);
        pagerFragments.add(new PagerFragment(WalletAccountsFragment.newInstance(), "Accounts"));
        pagerFragments.add(new PagerFragment(WalletTransactionsFragment.newInstance(), "Transactions"));
        appPagerAdapter.setData(pagerFragments);

        mViewHolder = inflater.inflate(R.layout.fragment_wallet, container, false);
        walletAmout = (TextView) mViewHolder.findViewById(R.id.wallet_amount);
        mViewPager = (ViewPager) mViewHolder.findViewById(R.id.wallet_pager);
        tabLayout = (TabLayout) mViewHolder.findViewById(R.id.wallet_tabs);

        mViewPager.setAdapter(appPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        queryData = new Bundle();
        getActivity().getSupportLoaderManager()
                .initLoader(LoaderID.WALLET.getValue(), queryData, WalletFragment.this).forceLoad();
        return mViewHolder;
    }

    @Override
    public Loader<WalletDAO> onCreateLoader(int id, Bundle args) {
        progressDialog.show();
        return new LoaderWalletDetails(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<WalletDAO> loader, WalletDAO data) {
        progressDialog.dismiss();
        if(data != null) {
            walletAmout.setText(data.getAmount());
        }
    }

    @Override
    public void onLoaderReset(Loader<WalletDAO> loader) {
        progressDialog.dismiss();
    }
}
