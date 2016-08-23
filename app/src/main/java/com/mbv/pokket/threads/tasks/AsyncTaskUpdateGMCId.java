package com.mbv.pokket.threads.tasks;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mbv.pokket.R;
import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.dao.constants.URLConstants;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by arindamnath on 06/04/16.
 */
public class AsyncTaskUpdateGMCId extends AppTask {

    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String decodedString;
    private String errorMessage;

    public AsyncTaskUpdateGMCId(int id, Context context, ServerResponseListener serverResponseListener) {
        super(id, context, serverResponseListener);
    }

    @Override
    protected ServerEvents doInBackground(JSONObject... params) {
        if(getNetworkUtils().isNetworkAvailable()) {
            try {
                if(checkPlayServices()) {
                    InstanceID instanceID = InstanceID.getInstance(getContext());
                    String token = instanceID.getToken(getContext().getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.i(AppConstants.APP_TAG, "GCM Registration Token: " + token);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("gcmId", token);
                    return updateGMCId(jsonObject);
                } else {
                    errorMessage = "Google Play services not available.";
                    return ServerEvents.FAILURE;
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_TAG, e.getMessage());
                errorMessage = "Oops something went wrong!";
                return ServerEvents.FAILURE;
            }
        } else {
            errorMessage = "Oops! Unable to connect to the internet.";
            return ServerEvents.NO_NETWORK;
        }
    }

    @Override
    protected void onPostExecute(ServerEvents serverEvents) {
        super.onPostExecute(serverEvents);
        switch (serverEvents) {
            case SUCCESS:
                getServerResponseListener().onSuccess(getId(), null);
                break;
            case FAILURE:
                getServerResponseListener().onFaliure(ServerEvents.FAILURE, errorMessage);
                break;
            case NO_NETWORK:
                getServerResponseListener().onFaliure(ServerEvents.NO_NETWORK, null);
                break;
        }
    }

    private ServerEvents updateGMCId(JSONObject object) throws Exception {
        //Write the request data
        HttpURLConnection httppost = getNetworkUtils().getHttpURLConInstance(
                String.format(URLConstants.USER_PROFILE_UPDATE_GCM_URL, getAppPreferences().getUserId()), "POST", "application/json", true);
        DataOutputStream out = new DataOutputStream(httppost.getOutputStream());
        out.writeBytes(object.toString());
        out.flush();
        out.close();
        //Read the response data
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                httppost.getInputStream()));
        while ((decodedString = in.readLine()) != null)
            sb.append(decodedString);
        in.close();
        //Parse the incoming response
        JSONObject response = (JSONObject) getParser().parse(sb.toString());
        if(ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.SUCCESS) {
            errorMessage = response.get("message").toString();
            getAppPreferences().setGCMId(object.get("gcmId").toString());
            return ServerEvents.SUCCESS;
        } else {
            return ServerEvents.FAILURE;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                /*apiAvailability.getErrorDialog(getContext(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();*/
            } else {
                Log.e(AppConstants.APP_TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
