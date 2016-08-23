package com.mbv.pokket.threads.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.mbv.pokket.dao.CalendarEventDAO;
import com.mbv.pokket.dao.EducationDAO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arindamnath on 03/02/16.
 */
public class LoaderEvents extends AsyncTaskLoader<List<Object>> {

    private NetworkUtils networkUtils;
    private AppPreferences appPreferences;
    private JSONParser parser;
    private String decodedString;
    private Bundle params = new Bundle();
    private List<Object> calendarEventDAOs = new ArrayList<>();

    public LoaderEvents(Context context, Bundle params) {
        super(context);
        this.parser = new JSONParser();
        this.appPreferences = new AppPreferences(context);
        this.networkUtils = new NetworkUtils(context);
        this.params = params;
    }

    @Override
    public List<Object> loadInBackground() {
        if(networkUtils.isNetworkAvailable()) {
            try {
                HttpURLConnection httppost = networkUtils.getHttpURLConInstance(
                        String.format(URLConstants.EVENTS_URL, appPreferences.getUserId()), "GET", null, true);
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
                    JSONObject responseObj = (JSONObject) response.get("response");
                    JSONArray lendObj = (JSONArray) responseObj.get("LEND");
                    JSONArray borrowObj = (JSONArray) responseObj.get("BORROW");
                    if(lendObj != null) {
                        if (lendObj.size() > 0) {
                            calendarEventDAOs.add("Upcoming Payments");
                            for (int i = 0; i < lendObj.size(); i++) {
                                CalendarEventDAO calendarEventDAO = new CalendarEventDAO(getContext());
                                calendarEventDAO.parse(parser, (JSONObject) lendObj.get(i));
                                calendarEventDAOs.add(calendarEventDAO);
                            }
                        }
                    }
                    if(borrowObj != null) {
                        if (borrowObj.size() > 0) {
                            calendarEventDAOs.add("Upcoming Dues");
                            for (int i = 0; i < borrowObj.size(); i++) {
                                CalendarEventDAO calendarEventDAO = new CalendarEventDAO(getContext());
                                calendarEventDAO.parse(parser, (JSONObject) borrowObj.get(i));
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
