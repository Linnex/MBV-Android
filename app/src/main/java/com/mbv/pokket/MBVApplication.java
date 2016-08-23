package com.mbv.pokket;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.util.AppPreferences;

/**
 * Created by arindamnath on 26/03/16.
 */
public class MBVApplication extends Application {

    private static final String PROPERTY_ID = "UA-75606741-1";

    private AppPreferences appPreferences;

    private Tracker tracker;

    private GoogleAnalytics analytics;

    public MBVApplication() {
        super();
    }

    synchronized public Tracker getTracker(String screenName) {
        if(tracker == null) {
            appPreferences = new AppPreferences(this);
            analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(PROPERTY_ID);
            tracker.setAppName(getString(R.string.app_name));
            tracker.setScreenName(screenName);
            tracker.setScreenResolution(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
            if (appPreferences.getUserId() != -1) {
                tracker.setClientId(appPreferences.getUserId().toString() + "_" + appPreferences.getUserEmail());
            }
            tracker.enableExceptionReporting(true);
            tracker.enableAutoActivityTracking(true);
        }
        return tracker;
    }
}
