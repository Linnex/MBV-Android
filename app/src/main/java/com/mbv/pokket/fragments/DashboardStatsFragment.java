package com.mbv.pokket.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.mbv.pokket.R;
import com.mbv.pokket.ActivityTransactionHistory;
import com.mbv.pokket.util.AppWebViewClient;
import com.mbv.pokket.util.GraphUtil;

/**
 * Created by arindamnath on 06/02/16.
 */
public class DashboardStatsFragment extends Fragment {

    private View mViewHolder;
    private Spinner summaryTypeSpinner;
    private TextView summaryValue1, summaryValue2, summaryValue3, summaryInterest, summaryDefault;
    private TextView summaryName1, summaryName2, summaryName3, summaryInterestName, summaryDefaultName;
    private TextView transactionAmount, transactionCredit, transactionDebit;
    private ArcProgress transactionRatio;
    private WebView chartsWebView;
    private ProgressBar webViewProgress;
    private GraphUtil graphUtil;

    public static DashboardStatsFragment newInstance() {
        DashboardStatsFragment fragment = new DashboardStatsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewHolder = inflater.inflate(R.layout.fragment_statistics, container, false);
        summaryTypeSpinner = (Spinner) mViewHolder.findViewById(R.id.dashboard_stat_type_spinner);
        summaryValue1 = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_number_1);
        summaryValue2 = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_number_2);
        summaryValue3 = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_number_3);
        summaryInterest = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_roi_value);
        summaryDefault = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_default_value);
        summaryName1 = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_name_1);
        summaryName2 = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_name_2);
        summaryName3 = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_name_3);
        summaryInterestName = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_roi);
        summaryDefaultName = (TextView) mViewHolder.findViewById(R.id.dashboard_summary_default);
        transactionAmount = (TextView) mViewHolder.findViewById(R.id.dashboard_transaction_amount);
        transactionCredit = (TextView) mViewHolder.findViewById(R.id.dashboard_credit);
        transactionDebit = (TextView) mViewHolder.findViewById(R.id.dashboard_debit);
        transactionRatio = (ArcProgress) mViewHolder.findViewById(R.id.dashboard_arc_progress);
        chartsWebView = (WebView) mViewHolder.findViewById(R.id.dashboard_graph_holder);
        webViewProgress = (ProgressBar) mViewHolder.findViewById(R.id.dashboard_graph_loader);

        graphUtil = new GraphUtil();
        chartsWebView.setWebViewClient(new AppWebViewClient(getContext()));
        chartsWebView.getSettings().setJavaScriptEnabled(true);

        transactionRatio.setMax(1200);
        transactionAmount.setText(getString(R.string.rupee) + "1200");
        transactionCredit.setText(getString(R.string.rupee) + "500");
        transactionDebit.setText(getString(R.string.rupee) + "700");

        chartsWebView.loadDataWithBaseURL("file:///android_asset/", graphUtil.getGraphHTML(), "text/html", "UTF-8", "");

        mViewHolder.findViewById(R.id.dashboard_view_transaction_history)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ActivityTransactionHistory.class));
                    }
                });

        summaryTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateLable(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return mViewHolder;
    }

    private void updateLable(int pos) {
        switch (pos) {
            case 0:
                summaryName1.setText("Total Loans");
                summaryName2.setText("Lent");
                summaryName3.setText("Borrowed");
                summaryDefaultName.setText("Default Ratio");
                transactionCredit.setTextColor(getResources().getColor(R.color.colorBase));
                transactionDebit.setTextColor(getResources().getColor(R.color.colorBase));
                break;
            case 1:
                summaryName1.setText("Total Lent");
                summaryName2.setText("Open");
                summaryName3.setText("Closed");
                summaryDefaultName.setText("Defaulters");
                transactionCredit.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                transactionDebit.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                transactionRatio.setProgress(700);
                break;
            case 2:
                summaryName1.setText("Total Borrowed");
                summaryName2.setText("Open");
                summaryName3.setText("Closed");
                summaryDefaultName.setText("Defaulted");
                transactionCredit.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                transactionDebit.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                transactionRatio.setProgress(500);
                break;
        }
    }
}
