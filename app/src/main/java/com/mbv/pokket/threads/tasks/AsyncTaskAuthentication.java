package com.mbv.pokket.threads.tasks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.dao.constants.URLConstants;
import com.mbv.pokket.dao.enums.CurrentLocationType;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by arindamnath on 28/12/15.
 */
public class AsyncTaskAuthentication extends AppTask {

    private String decodedString;
    private String errorMessage;

    public AsyncTaskAuthentication(int id, Context context, ServerResponseListener serverResponseListener){
        super(id, context, serverResponseListener);
    }

    @Override
    protected ServerEvents doInBackground(JSONObject... params) {
        if(getNetworkUtils().isNetworkAvailable()) {
            try {
                JSONObject dato = params[0];
                dato.put("deviceData", new JSONObject(getDeviceInfo(getContext())));
                return getLogin(dato);
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

    private ServerEvents getLogin(JSONObject object) throws Exception{
        HttpURLConnection httppost = getNetworkUtils().getHttpURLConInstance(
                URLConstants.AUTH_URL, "POST", "application/json", false);
        DataOutputStream out = new DataOutputStream(httppost.getOutputStream());
        out.writeBytes(object.toString());
        out.flush();
        out.close();
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                httppost.getInputStream()));
        while ((decodedString = in.readLine()) != null)
            sb.append(decodedString);
        in.close();
        JSONObject response = (JSONObject) getParser().parse(sb.toString());
        if(ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.SUCCESS) {
            getAppPreferences().setUserToken(response.get("response").toString());
            return getUserInfo((Long) response.get("id"));
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }

    private ServerEvents getUserInfo(Long userId) throws Exception{
        //Write the request data
        HttpURLConnection httppost = getNetworkUtils().getHttpURLConInstance(
                String.format(URLConstants.USER_INFO_URL, userId), "GET", null, true);
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
            JSONObject responseObj = (JSONObject) response.get("response");
            getAppPreferences().saveUserInfo(
                    responseObj.get("name").toString(),
                    responseObj.get("email").toString(),
                    responseObj.get("phoneNumber").toString(),
                    responseObj.get("gender").toString(),
                    responseObj.get("maritalStatus").toString(),
                    responseObj.get("workStatus").toString(),
                    responseObj.get("residentialStatus").toString(),
                    (Long) responseObj.get("dob"),
                    (Long) responseObj.get("id"),
                    responseObj.get("fatherName").toString());
            getAppPreferences().saveUserImage(responseObj.get("userImage").toString());
            getAppPreferences().setUserRole(responseObj.get("roleType").toString());
            if(responseObj.get("gcmId") != null) {
                getAppPreferences().setGCMId(responseObj.get("gcmId").toString());
            }
            if(responseObj.get("userLocationDatas") != null) {
                JSONArray locations = (JSONArray) responseObj.get("userLocationDatas");
                for(int i = 0; i < locations.size(); i++) {
                    JSONObject location = (JSONObject) locations.get(i);
                    if(CurrentLocationType.valueOf(location.get("type").toString()) == CurrentLocationType.HOME) {
                        getAppPreferences().saveHomeAddress(
                                (Long) location.get("id"),
                                location.get("address").toString(),
                                location.get("city").toString(),
                                location.get("state").toString(),
                                location.get("country").toString(),
                                location.get("pincode").toString(),
                                location.get("type").toString());
                    } else {
                        getAppPreferences().saveCurrentAddress(
                                (Long) location.get("id"),
                                location.get("address").toString(),
                                location.get("city").toString(),
                                location.get("state").toString(),
                                location.get("country").toString(),
                                location.get("pincode").toString(),
                                location.get("type").toString());
                    }
                }
            }
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }
}
