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
 * Created by arindamnath on 15/03/16.
 */
public class AsyncTaskAcceptLoan extends AppTask {

    private static String ACCEPT_LOAN_URL = "loans/user/%1$s/loan/%2$s/accept";

    private String decodedString;
    private String errorMessage;
    private Long loanId;

    public AsyncTaskAcceptLoan(int id, Context context, ServerResponseListener serverResponseListener) {
        super(id, context, serverResponseListener);
    }

    @Override
    protected ServerEvents doInBackground(JSONObject... params) {
        if(getNetworkUtils().isNetworkAvailable()) {
            try {
                JSONObject dato = params[0];
                loanId = (Long) dato.get("loanId");
                dato.put("userId", getAppPreferences().getUserId());
                dato.put("deviceData", new JSONObject(getDeviceInfo(getContext())));
                return acceptLoanLender(dato);
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

    private ServerEvents acceptLoanLender(JSONObject object) throws Exception{
        HttpURLConnection httppost = getNetworkUtils().getHttpURLConInstance(
                String.format(ACCEPT_LOAN_URL, getAppPreferences().getUserId(), loanId), "POST", "application/json", true);
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
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }
}
