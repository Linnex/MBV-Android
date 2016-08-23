package com.mbv.pokket.threads.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.mbv.pokket.dao.CalendarEventDAO;
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
 * Created by arindamnath on 21/03/16.
 */
public class LoaderTransactionHistory extends AsyncTaskLoader<List<CalendarEventDAO>> {

    private NetworkUtils networkUtils;
    private AppPreferences appPreferences;
    private JSONParser parser;
    private String decodedString;
    private Bundle params = new Bundle();
    private List<CalendarEventDAO> calendarEventDAOs = new ArrayList<>();

    public LoaderTransactionHistory(Context context, Bundle params) {
        super(context);
        this.parser = new JSONParser();
        this.appPreferences = new AppPreferences(context);
        this.networkUtils = new NetworkUtils(context);
        this.params = params;
    }

    @Override
    public List<CalendarEventDAO> loadInBackground() {
        if(networkUtils.isNetworkAvailable()) {
            try {
                HttpURLConnection httppost = networkUtils.getHttpURLConInstance(
                        String.format(URLConstants.LOAN_TRANSACTION_HISTORY, appPreferences.getUserId()) +
                        params.getString("status"), "GET", null, true);
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
                    this.calendarEventDAOs.clear();
                    JSONArray responseObj = (JSONArray) response.get("response");
                    if(responseObj != null) {
                        if (responseObj.size() > 0) {
                            for (int i = 0; i < responseObj.size(); i++) {
                                CalendarEventDAO calendarEventDAO = new CalendarEventDAO(getContext());
                                calendarEventDAO.parse(parser, (JSONObject) responseObj.get(i));
                                calendarEventDAOs.add(calendarEventDAO);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_TAG, e.toString());
            }
            return calendarEventDAOs;
        } else {
            return null;
        }
    }
}
