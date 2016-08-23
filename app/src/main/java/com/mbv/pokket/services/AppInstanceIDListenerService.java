package com.mbv.pokket.services;

import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.threads.tasks.AsyncTaskUpdateGMCId;

import org.json.simple.JSONObject;

/**
 * Created by arindamnath on 06/04/16.
 */
public class AppInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        new AsyncTaskUpdateGMCId(1, getApplicationContext(), new ServerResponseListener() {
            @Override
            public void onSuccess(int threadId, Object object) {
                Log.i(AppConstants.APP_TAG, "Token refreshed successfully!");
            }

            @Override
            public void onFaliure(ServerEvents serverEvents, Object object) {

            }
        }).execute(new JSONObject[]{});
    }
}
