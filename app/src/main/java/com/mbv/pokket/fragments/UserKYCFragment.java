package com.mbv.pokket.fragments;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.mbv.pokket.ActivityKYCDetails;
import com.mbv.pokket.R;
import com.mbv.pokket.adapters.KYCListAdapter;
import com.mbv.pokket.dao.UserKycDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.dao.enums.RoleType;
import com.mbv.pokket.threads.loaders.LoaderKYCDetails;
import com.mbv.pokket.util.AppPreferences;

import java.util.List;

/**
 * Created by arindamnath on 04/02/16.
 */
public class UserKYCFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<UserKycDAO>>{

    private View mViewHolder;
    private FloatingActionButton floatingActionButton;
    private KYCListAdapter kycListAdapter;
    private ListView kycDetails;
    private PullRefreshLayout pullRefreshLayout;
    private AppPreferences appPreferences;
    private boolean identity, address, student;
    private TextView identityText, addressText, studentText;
    private Bundle queryData;

    public static UserKYCFragment newInstance() {
        UserKYCFragment fragment = new UserKYCFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        kycListAdapter = new KYCListAdapter(getContext());
        appPreferences = new AppPreferences(getContext());

        mViewHolder = inflater.inflate(R.layout.fragment_details_list, container, false);
        floatingActionButton = (FloatingActionButton) mViewHolder.findViewById(R.id.details_add_button);
        pullRefreshLayout = (PullRefreshLayout) mViewHolder.findViewById(R.id.details_refresh_container);
        kycDetails = (ListView) mViewHolder.findViewById(R.id.details_list);
        identityText = (TextView) mViewHolder.findViewById(R.id.details_list_kyc_proof_identity);
        addressText = (TextView) mViewHolder.findViewById(R.id.details_list_kyc_proof_address);
        studentText = (TextView) mViewHolder.findViewById(R.id.details_list_kyc_proof_student);

        floatingActionButton.setVisibility(View.VISIBLE);
        mViewHolder.findViewById(R.id.details_list_kyc_status_holder).setVisibility(View.VISIBLE);
        if (appPreferences.getUserRole() == RoleType.LEND) {
            studentText.setVisibility(View.GONE);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), ActivityKYCDetails.class), 1);
            }
        });

        pullRefreshLayout.setRefreshing(false);
        kycDetails.setAdapter(kycListAdapter);

        mViewHolder.findViewById(R.id.kyc_empty_list)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queryData = new Bundle();
                        queryData.putString("query", "");
                        getActivity().getSupportLoaderManager()
                                .initLoader(LoaderID.KYC.getValue(), queryData, UserKYCFragment.this).forceLoad();
                    }
                });

        kycDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActivityKYCDetails.class);
                intent.putExtra("id", kycListAdapter.getItem(position).getId());
                intent.putExtra("kycId", kycListAdapter.getItem(position).getKycId());
                intent.putExtra("imageUrl", kycListAdapter.getItem(position).getImageUrl());
                intent.putExtra("type", kycListAdapter.getItem(position).getKycType().toString());
                startActivity(intent);
            }
        });

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryData = new Bundle();
                queryData.putString("query", "");
                getActivity().getSupportLoaderManager()
                        .initLoader(LoaderID.KYC.getValue(), queryData, UserKYCFragment.this).forceLoad();
            }
        });

        return mViewHolder;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryData = new Bundle();
        queryData.putString("query", "");
        getActivity().getSupportLoaderManager()
                .initLoader(LoaderID.KYC.getValue(), queryData, UserKYCFragment.this).forceLoad();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        queryData = new Bundle();
        queryData.putString("query", "");
        getActivity().getSupportLoaderManager()
                .initLoader(LoaderID.KYC.getValue(), queryData, UserKYCFragment.this).forceLoad();
    }

    @Override
    public Loader<List<UserKycDAO>> onCreateLoader(int id, Bundle args) {
        pullRefreshLayout.setRefreshing(true);
        return new LoaderKYCDetails(getActivity(), null);
    }

    @Override
    public void onLoadFinished(Loader<List<UserKycDAO>> loader, List<UserKycDAO> data) {
        pullRefreshLayout.setRefreshing(false);
        if(data != null) {
            for(UserKycDAO userKycDAO : data) {
                switch (userKycDAO.getKycType()) {
                    case PAN:
                        identity = true;
                        break;
                    case ADHAAR:
                        identity = true;
                        address = true;
                        break;
                    case VOTER_ID:
                        identity = true;
                        address = true;
                        break;
                    case PASSPORT:
                        identity = true;
                        address = true;
                        break;
                    case BANK:
                        identity = true;
                        address = true;
                        break;
                    case STUDENT_ID:
                        student = true;
                        break;
                }
            }
            if(identity) {
                identityText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
            if(address) {
                addressText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
            if(student) {
                studentText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
            kycListAdapter.setData(data);
            kycDetails.setEmptyView(mViewHolder.findViewById(R.id.kyc_empty_list));
        } else {
            kycDetails.setEmptyView(mViewHolder.findViewById(R.id.details_no_connection));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<UserKycDAO>> loader) {
        pullRefreshLayout.setRefreshing(false);
    }
}
