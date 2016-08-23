package com.mbv.pokket.threads.tasks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by arindamnath on 12/03/16.
 */
public class AsyncTaskForgotPassword extends AppTask{

    private static String RECOVER_URL = "authenticate/recover";

    private String decodedString;
    private String errorMessage;

    public AsyncTaskForgotPassword(int id, Context context, ServerResponseListener serverResponseListener){
        super(id, context, serverResponseListener);
    }

    @Override
    protected ServerEvents doInBackground(JSONObject... params) {
        if(getNetworkUtils().isNetworkAvailable()) {
            try {
                JSONObject dato = params[0];
                return recoverPassword(dato);
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

    private ServerEvents recoverPassword(JSONObject object) throws Exception{
        HttpURLConnection httppost = getNetworkUtils().getHttpURLConInstance(RECOVER_URL, "POST", "application/json", false);
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
        Log.v("Pokket", sb.toString());
        JSONObject response = (JSONObject) getParser().parse(sb.toString());
        if(ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.SUCCESS) {
            getAppPreferences().setUserToken(response.get("message").toString());
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }
}
