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
public class AsyncTaskBankAccounts extends AppTask {

    private static final String CREATE_ACCOUNT_URL = "wallet/user/%1$s/add/bank/account";
    private static final String UPDATE_ACCOUNT_URL = "wallet/user/%1$s/update/bank/account/%2$s";

    private String decodedString;
    private String errorMessage;
    private boolean isUpdate;

    public AsyncTaskBankAccounts(int id, Context context, ServerResponseListener serverResponseListener, boolean isUpdate) {
        super(id, context, serverResponseListener);
        this.isUpdate = isUpdate;
    }

    @Override
    protected ServerEvents doInBackground(JSONObject... params) {
        if(getNetworkUtils().isNetworkAvailable()) {
            try {
                JSONObject dato = params[0];
                dato.put("userId", getAppPreferences().getUserId());
                dato.put("walletId", getAppPreferences().getWalletId());
                return pushBankAccountInfo(dato);
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
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
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

    private ServerEvents pushBankAccountInfo(JSONObject object) throws Exception{
        HttpURLConnection httppost;
        if(isUpdate) {
            httppost = getNetworkUtils().getHttpURLConInstance(
                    String.format(UPDATE_ACCOUNT_URL, getAppPreferences().getUserId(), getId()), "PUT", "application/json", true);
        } else {
            httppost = getNetworkUtils().getHttpURLConInstance(
                    String.format(CREATE_ACCOUNT_URL, getAppPreferences().getUserId()), "POST", "application/json", true);
        }
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
            errorMessage = response.get("message").toString();
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }
}
