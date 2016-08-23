package com.mbv.pokket.threads.tasks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mbv.pokket.dao.BankCodeDAO;
import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.dao.constants.URLConstants;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by arindamnath on 16/03/16.
 */
public class AsyncTaskIFSCCode extends AppTask {

    private String decodedString;
    private String errorMessage;
    private BankCodeDAO bankCodeDAO;

    public AsyncTaskIFSCCode(int id, Context context, ServerResponseListener serverResponseListener) {
        super(id, context, serverResponseListener);
    }

    @Override
    protected ServerEvents doInBackground(JSONObject... params) {
        if(getNetworkUtils().isNetworkAvailable()) {
            try {
                return getBankDetails(params[0]);
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
                getServerResponseListener().onSuccess(getId(), bankCodeDAO);
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

    private ServerEvents getBankDetails(JSONObject object) throws Exception{
        HttpURLConnection httppost = getNetworkUtils().getHttpURLConInstance(
                URLConstants.IFSC_CODE_URL + object.get("search").toString(), "GET", null, true);
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                httppost.getInputStream()));
        while ((decodedString = in.readLine()) != null)
            sb.append(decodedString);
        in.close();
        JSONObject response = (JSONObject) getParser().parse(sb.toString());
        if(ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.SUCCESS) {
            JSONArray banks = (JSONArray) response.get("response");
            if(banks.size() > 0) {
                bankCodeDAO = new BankCodeDAO(getContext());
                bankCodeDAO.parse(getParser(), (JSONObject) banks.get(0));
            }
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }
}
