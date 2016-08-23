package com.mbv.pokket.threads.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.mbv.pokket.dao.LoanDetailsDAO;
import com.mbv.pokket.dao.RepaymentDAO;
import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.dao.constants.URLConstants;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.util.AppPreferences;
import com.mbv.pokket.util.NetworkUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 04/02/16.
 */
public class LoaderLoanDetail extends AsyncTaskLoader<LoanDetailsDAO> {

    private NetworkUtils networkUtils;
    private AppPreferences appPreferences;
    private JSONParser parser;
    private String decodedString;
    private Bundle bundle = new Bundle();
    private LoanDetailsDAO loanDetailsDAO;
    private List<RepaymentDAO> repaymentDAOList = new ArrayList<>();


    public LoaderLoanDetail(Context context, Bundle bundle) {
        super(context);
        this.parser = new JSONParser();
        this.appPreferences = new AppPreferences(context);
        this.networkUtils = new NetworkUtils(context);
        this.bundle = bundle;
    }

    @Override
    public LoanDetailsDAO loadInBackground() {
        if(networkUtils.isNetworkAvailable()) {
            try {
                HttpURLConnection httppost = networkUtils.getHttpURLConInstance(
                        String.format(URLConstants.LOAN_DETAILS_URL, appPreferences.getUserId(), bundle.getLong("loanId")),
                        "GET", null, true);
                //Read the response data
                StringBuilder sb = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httppost.getInputStream()));
                while ((decodedString = in.readLine()) != null)
                    sb.append(decodedString);
                in.close();
                //Parse the incoming response
                JSONObject response = (JSONObject) parser.parse(sb.toString());
                if(ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.SUCCESS) {
                    JSONObject responseObj = (JSONObject) response.get("response");
                    loanDetailsDAO = new LoanDetailsDAO(getContext());
                    loanDetailsDAO.parse(parser, responseObj);
                    if(responseObj.get("loanInstallmentData") != null) {
                        JSONArray installments = (JSONArray) responseObj.get("loanInstallmentData");
                        if (installments.size() > 0) {
                            repaymentDAOList.clear();
                            for (int i = 0; i < installments.size(); i++) {
                                RepaymentDAO repaymentDAO = new RepaymentDAO(getContext());
                                repaymentDAO.parse(parser, (JSONObject) installments.get(i));
                                repaymentDAOList.add(repaymentDAO);
                            }
                        }
                        loanDetailsDAO.setRepaymentDAO(repaymentDAOList);
                    }
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_TAG, e.toString());
            }
            return loanDetailsDAO;
        } else {
            return null;
        }
    }
}
