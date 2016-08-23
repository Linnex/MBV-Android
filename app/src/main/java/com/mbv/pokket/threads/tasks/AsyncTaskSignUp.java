package com.mbv.pokket.threads.tasks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mbv.pokket.R;
import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by arindamnath on 28/12/15.
 */
public class AsyncTaskSignUp extends AppTask {

    private static String SIGN_UP_URL = "authenticate/user/create";
    private JSONObject entryParams;
    private String decodedString;
    private String errorMessage;

    public AsyncTaskSignUp(int id, Context context, ServerResponseListener serverResponseListener){
        super(id, context, serverResponseListener);
    }

    @Override
    protected ServerEvents doInBackground(JSONObject... params) {
        if(getNetworkUtils().isNetworkAvailable()) {
            try {
                InstanceID instanceID = InstanceID.getInstance(getContext());
                String token = instanceID.getToken(getContext().getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(AppConstants.APP_TAG, "GCM Registration Token: " + token);
                entryParams = params[0];
                JSONObject dato = params[0];
                dato.put("accountType", "USER");
                dato.put("gcmId", token);
                dato.put("deviceData", new JSONObject(getDeviceInfo(getContext())));
                return getSignUp(dato);
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
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                getServerResponseListener().onFaliure(ServerEvents.FAILURE, errorMessage);
                break;
            case NO_NETWORK:
                getServerResponseListener().onFaliure(ServerEvents.NO_NETWORK, null);
                break;
        }
    }

    private ServerEvents getSignUp(JSONObject object) throws Exception{
        //Write the request data
        HttpURLConnection httppost = getNetworkUtils().getHttpURLConInstance(SIGN_UP_URL, "POST", "application/json", false);
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
            getAppPreferences().saveUserInfo(entryParams.get("firstName").toString() + " " +
                            ((entryParams.get("middleName") != null) ? entryParams.get("middleName").toString() + " " : "") +
                            entryParams.get("lastName").toString(),
                    entryParams.get("email").toString(),
                    entryParams.get("phoneNumber").toString(), null,
                    null, null, null, -1l, (Long) response.get("id"), null);
            getAppPreferences().setGCMId(entryParams.get("gcmId").toString());
            getAppPreferences().setUserToken(response.get("response").toString());
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }
}
