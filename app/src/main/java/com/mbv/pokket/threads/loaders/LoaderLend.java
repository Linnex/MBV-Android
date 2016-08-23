package com.mbv.pokket.threads.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.mbv.pokket.dao.LendDAO;
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
 * Created by arindamnath on 18/01/16.
 */
public class LoaderLend extends AsyncTaskLoader<List<LendDAO>> {

    private NetworkUtils networkUtils;
    private AppPreferences appPreferences;
    private JSONParser parser;
    private String decodedString;
    private Bundle bundle = new Bundle();
    private List<LendDAO> lendDAOList = new ArrayList<>();

    public LoaderLend(Context context, Bundle bundle) {
        super(context);
        this.parser = new JSONParser();
        this.appPreferences = new AppPreferences(context);
        this.networkUtils = new NetworkUtils(context);
        this.bundle = bundle;
    }

    @Override
    public List<LendDAO> loadInBackground() {
        if(networkUtils.isNetworkAvailable()) {
            try {
                HttpURLConnection httppost = networkUtils.getHttpURLConInstance(
                        String.format(URLConstants.OPEN_LOANS_URL, appPreferences.getUserId()), "GET", null, true);
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
                    JSONArray responseObj = (JSONArray) response.get("response");
                    if (responseObj.size() > 0) {
                        lendDAOList.clear();
                        for (int i = 0; i < responseObj.size(); i++) {
                            LendDAO lendDAO = new LendDAO(getContext());
                            lendDAO.parse(parser, (JSONObject) responseObj.get(i));
                            lendDAOList.add(lendDAO);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_TAG, e.toString());
            }
            return lendDAOList;
        } else {
            return null;
        }
    }
}
