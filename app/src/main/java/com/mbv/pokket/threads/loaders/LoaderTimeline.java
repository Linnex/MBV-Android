package com.mbv.pokket.threads.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.mbv.pokket.dao.TimelineDAO;
import com.mbv.pokket.dao.UserProfileDAO;
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
 * Created by arindamnath on 07/02/16.
 */
public class LoaderTimeline extends AsyncTaskLoader<UserProfileDAO> {

    private NetworkUtils networkUtils;
    private AppPreferences appPreferences;
    private JSONParser parser;
    private String decodedString;
    private Bundle params = new Bundle();
    private UserProfileDAO userProfileDAO;
    private List<TimelineDAO> timelineDAOs = new ArrayList<>();

    public LoaderTimeline(Context context, Bundle params) {
        super(context);
        this.parser = new JSONParser();
        this.appPreferences = new AppPreferences(context);
        this.networkUtils = new NetworkUtils(context);
        this.params = params;
    }

    @Override
    public UserProfileDAO loadInBackground() {
        if(networkUtils.isNetworkAvailable()) {
            try {
                userProfileDAO = new UserProfileDAO(getContext());
                HttpURLConnection httppost = networkUtils.getHttpURLConInstance(
                        String.format(URLConstants.USER_INFO_URL,
                                (params.getLong("userId", -1l) != -1l) ? params.getLong("userId") : appPreferences.getUserId()),
                        "GET", null, true);
                StringBuilder sb = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httppost.getInputStream()));
                while ((decodedString = in.readLine()) != null)
                    sb.append(decodedString);
                in.close();
                JSONObject response = (JSONObject) parser.parse(sb.toString());
                if(ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.SUCCESS) {
                    userProfileDAO.parse(parser, (JSONObject) response.get("response"));
                    getTimeLineDetails();
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_TAG, e.toString());
            }
            return userProfileDAO;
        } else {
            return null;
        }
    }

    private void getTimeLineDetails() throws Exception{
        HttpURLConnection httppost = networkUtils.getHttpURLConInstance(
                String.format(URLConstants.TIMELINE_URL,
                        (params.getLong("userId", -1l) != -1l) ? params.getLong("userId") : appPreferences.getUserId()),
                "GET", null, true);
        //Read the response data
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                httppost.getInputStream()));
        while ((decodedString = in.readLine()) != null)
            sb.append(decodedString);
        in.close();
        JSONObject response = (JSONObject) parser.parse(sb.toString());
        if(ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.SUCCESS) {
            JSONArray responseObj = (JSONArray) response.get("response");
            if (responseObj.size() > 0) {
                this.timelineDAOs.clear();
                for (int i = 0; i < responseObj.size(); i++) {
                    TimelineDAO timelineDAO = new TimelineDAO(getContext());
                    timelineDAO.parse(parser, (JSONObject) responseObj.get(i));
                    timelineDAOs.add(timelineDAO);
                }
            }
            userProfileDAO.setTimelineDAOList(timelineDAOs);
        }
    }
}
