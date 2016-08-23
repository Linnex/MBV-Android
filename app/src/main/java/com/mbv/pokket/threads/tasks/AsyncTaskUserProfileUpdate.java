package com.mbv.pokket.threads.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mbv.pokket.R;
import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.dao.constants.URLConstants;
import com.mbv.pokket.dao.enums.CurrentLocationType;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by arindamnath on 28/02/16.
 */
public class AsyncTaskUserProfileUpdate extends AppTask {

    private JSONObject entryParams;
    private String decodedString;
    private String errorMessage;
    private Bitmap bitmap;

    public AsyncTaskUserProfileUpdate(int id, Context context, ServerResponseListener serverResponseListener,
                                      Bitmap bitmap){
        super(id, context, serverResponseListener);
        this.bitmap = bitmap;
    }

    @Override
    protected ServerEvents doInBackground(JSONObject... params) {
        if(getNetworkUtils().isNetworkAvailable()) {
            try {
                entryParams = params[0];
                JSONObject dato = params[0];
                if(getAppPreferences().getGCMId() == null) {
                    InstanceID instanceID = InstanceID.getInstance(getContext());
                    String token = instanceID.getToken(getContext().getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.i(AppConstants.APP_TAG, "GCM Registration Token: " + token);
                    dato.put("gcmId", token);
                }
                dato.put("phoneNumber", getAppPreferences().getUserPhone());
                return updateProfile(dato);
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
                getServerResponseListener().onFaliure(ServerEvents.FAILURE, null);
                break;
            case NO_NETWORK:
                getServerResponseListener().onFaliure(ServerEvents.NO_NETWORK, null);
                break;
        }
    }

    private ServerEvents updateProfile(JSONObject object) throws Exception{
        //Write the request data
        HttpURLConnection httppost = getNetworkUtils().getHttpURLConInstance(
                String.format(URLConstants.USER_PROFILE_UPDATE_URL, getAppPreferences().getUserId()), "PUT", "application/json", true);
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
            getAppPreferences().updateUserInfo(
                    entryParams.get("firstName").toString() + " " +
                            ((entryParams.get("middleName") != null) ? entryParams.get("middleName").toString() + " " : "") +
                            entryParams.get("lastName").toString(),
                    entryParams.get("gender").toString(),
                    entryParams.get("maritalStatus").toString(),
                    entryParams.get("workStatus").toString(),
                    entryParams.get("residentialStatus").toString(),
                    (Long) entryParams.get("dob"),
                    entryParams.get("fatherName").toString());

            JSONArray locations = (JSONArray) entryParams.get("userLocationDatas");
            for(int i = 0; i < locations.size(); i++) {
                JSONObject location = (JSONObject) locations.get(i);
                if (CurrentLocationType.valueOf(location.get("type").toString()) == CurrentLocationType.HOME) {
                    getAppPreferences().saveHomeAddress(
                            (Long) response.get("id"),
                            location.get("address").toString(),
                            location.get("city").toString(),
                            location.get("state").toString(),
                            location.get("country").toString(),
                            location.get("pincode").toString(),
                            location.get("type").toString());
                } else {
                    getAppPreferences().saveCurrentAddress((Long) response.get("id"),
                            location.get("address").toString(),
                            location.get("city").toString(),
                            location.get("state").toString(),
                            location.get("country").toString(),
                            location.get("pincode").toString(),
                            location.get("type").toString());
                }
            }

            if(bitmap != null) {
                return uploadImage(bitmap);
            }
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }

    private ServerEvents uploadImage(Bitmap bitmap) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

        MultipartEntityBuilder entityBuilder =  MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        InputStream imageStream = new ByteArrayInputStream(bos.toByteArray());
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getContext().getString(R.string.endpoint) +
                        String.format(URLConstants.USER_IMAGE_URL, getAppPreferences().getUserId()));
                httpPost.addHeader("X-AUTH-TOKEN", getAppPreferences().getUserToken());
                entityBuilder.addBinaryBody("file", imageStream, ContentType.create("image/jpeg"), "userImage.jpg");
        httpPost.setEntity(entityBuilder.build());
        HttpResponse httpResponse = httpClient.execute(httpPost);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        while ((decodedString = reader.readLine()) != null) {
            sb = sb.append(decodedString);
        }
        //Parse the incoming response
        JSONObject response = (JSONObject) getParser().parse(sb.toString());
        if(ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.SUCCESS) {
            getAppPreferences().saveUserImage(response.get("message").toString());
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }
}
