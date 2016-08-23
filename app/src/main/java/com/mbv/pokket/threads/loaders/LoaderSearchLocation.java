package com.mbv.pokket.threads.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.mbv.pokket.dao.GeoLocation;
import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.util.NetworkUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arindamnath on 28/02/16.
 */
public class LoaderSearchLocation extends AsyncTaskLoader<List<GeoLocation>> {

    private final String mKey = "key=AIzaSyD84IcJ1ct9ntDpKHdUZmecDG564n2vi_w";
    private InputStream iStream = null;
    private HttpURLConnection urlConnection = null;
    private String queryURL, sensor, parameters, output, types, searchParam;
    private NetworkUtils networkUtil;
    private JSONParser jsonParser;
    private Bundle args;

    private List<GeoLocation> data = new ArrayList<>();

    public LoaderSearchLocation(Context context, Bundle args) {
        super(context);
        this.args = args;
        this.jsonParser = new JSONParser();
        this.networkUtil = new NetworkUtils(context);
    }

    @Override
    public List<GeoLocation> loadInBackground() {
        if(networkUtil.isNetworkAvailable()) {
            try {
                data.clear();
                JSONObject response = getLocationSearchDetails();
                if(response != null) {
                    JSONArray jsonArray = (JSONArray) response.get("predictions");
                    if(jsonArray.size() > 0) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            GeoLocation geoLocation = new GeoLocation(getContext());
                            geoLocation.parse(jsonParser, (JSONObject) jsonArray.get(i));
                            data.add(geoLocation);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_TAG, e.toString());
            }
            return data;
        } else {
            return null;
        }
    }

    private JSONObject getLocationSearchDetails() throws Exception {
        searchParam = "input=" + URLEncoder.encode(args.getString("query"), "utf-8");
        sensor = "sensor=false";
        types = "types=geocode";
        parameters = searchParam + "&" + types + "&" + sensor + "&" + mKey;
        output = "json";
        queryURL = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

        URL url = new URL(queryURL);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        iStream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String jsonData = sb.toString();
        br.close();
        JSONObject response = (JSONObject) jsonParser.parse(jsonData);
        if(response.get("status").toString().equalsIgnoreCase("OK")) {
            return response;
        } else {
            return null;
        }
    }
}
