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
import com.mbv.pokket.ActivityEducationDetails;
import com.mbv.pokket.R;
import com.mbv.pokket.adapters.EducationListAdapter;
import com.mbv.pokket.dao.EducationDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.threads.loaders.LoaderUserEducation;

import java.util.List;

/**
 * Created by arindamnath on 27/12/15.
 */
public class UserEducationFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<EducationDAO>>{

    private View mViewHolder;
    private FloatingActionButton floatingActionButton;
    private EducationListAdapter educationListAdapter;
    private ListView educationalDetails;
    private PullRefreshLayout pullRefreshLayout;
    private Bundle queryData;

    public static UserEducationFragment newInstance() {
        UserEducationFragment fragment = new UserEducationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        educationListAdapter = new EducationListAdapter(getContext());

        mViewHolder = inflater.inflate(R.layout.fragment_details_list, container, false);
        floatingActionButton = (FloatingActionButton) mViewHolder.findViewById(R.id.details_add_button);
        pullRefreshLayout = (PullRefreshLayout) mViewHolder.findViewById(R.id.details_refresh_container);
        educationalDetails = (ListView) mViewHolder.findViewById(R.id.details_list);
        floatingActionButton.setVisibility(View.VISIBLE);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), ActivityEducationDetails.class), 1);
            }
        });

        pullRefreshLayout.setRefreshing(false);
        educationalDetails.setAdapter(educationListAdapter);

        mViewHolder.findViewById(R.id.education_empty_list)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queryData = new Bundle();
                        queryData.putString("query", "");
                        getActivity().getSupportLoaderManager()
                                .initLoader(LoaderID.EDUCATION.getValue(), queryData, UserEducationFragment.this).forceLoad();
                    }
                });

        educationalDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActivityEducationDetails.class);
                intent.putExtra("id", educationListAdapter.getItem(position).getId());
                intent.putExtra("instituteName", educationListAdapter.getItem(position).getInstituteName());
                intent.putExtra("degreeType", educationListAdapter.getItem(position).getEducationDegreeType().toString());
                intent.putExtra("degreeName", educationListAdapter.getItem(position).getDegreeName());
                intent.putExtra("degreeId", educationListAdapter.getItem(position).getDegreeId());
                intent.putExtra("description", educationListAdapter.getItem(position).getDescription());
                intent.putExtra("city", educationListAdapter.getItem(position).getCity());
                intent.putExtra("state", educationListAdapter.getItem(position).getState());
                intent.putExtra("pincode", educationListAdapter.getItem(position).getPincode());
                intent.putExtra("joinDate", educationListAdapter.getItem(position).getStartDateAsLong());
                intent.putExtra("passDate", educationListAdapter.getItem(position).getPassDateAsLong());
                intent.putExtra("imageUrl", educationListAdapter.getItem(position).getReportUrl());
                startActivity(intent);
            }
        });

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryData = new Bundle();
                queryData.putString("query", "");
                getActivity().getSupportLoaderManager()
                        .initLoader(LoaderID.EDUCATION.getValue(), queryData, UserEducationFragment.this).forceLoad();
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
                .initLoader(LoaderID.EDUCATION.getValue(), queryData, UserEducationFragment.this).forceLoad();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        queryData = new Bundle();
        queryData.putString("query", "");
        getActivity().getSupportLoaderManager()
                .initLoader(LoaderID.EDUCATION.getValue(), queryData, UserEducationFragment.this).forceLoad();
    }

    @Override
    public Loader<List<EducationDAO>> onCreateLoader(int id, Bundle args) {
        pullRefreshLayout.setRefreshing(true);
        return new LoaderUserEducation(getActivity(), null);
    }

    @Override
    public void onLoadFinished(Loader<List<EducationDAO>> loader, List<EducationDAO> data) {
        pullRefreshLayout.setRefreshing(false);
        if(data != null) {
            educationListAdapter.setData(data);
            educationalDetails.setEmptyView(mViewHolder.findViewById(R.id.education_empty_list));
        } else {
            educationalDetails.setEmptyView(mViewHolder.findViewById(R.id.details_no_connection));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<EducationDAO>> loader) {
        pullRefreshLayout.setRefreshing(false);
    }
}
