package com.mbv.pokket.threads.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.mbv.pokket.R;
import com.mbv.pokket.dao.constants.AppConstants;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
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
public class AsyncTaskEducation extends AppTask {

    private static final String UPDATE_LOCATION_URL = "education/user/%1$s/update/institute/%2$s";
    private static final String ADD_LOCATION_URL = "education/user/%1$s/create";
    private static String MARKSHEET_IMAGE_URL = "education/user/%1$s/add/institute/%2$s/marksheet";

    private Long educationId = -1l;
    private String decodedString;
    private String errorMessage;
    private boolean isUpdate;
    private Bitmap bitmap;

    public AsyncTaskEducation(int id, Context context, ServerResponseListener serverResponseListener, boolean isUpdate,
                              Bitmap bitmap){
        super(id, context, serverResponseListener);
        this.isUpdate = isUpdate;
        this.bitmap = bitmap;
    }

    @Override
    protected ServerEvents doInBackground(JSONObject... params) {
        if(getNetworkUtils().isNetworkAvailable()) {
            try {
                JSONObject dato = params[0];
                return postEducationData(dato);
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
                getServerResponseListener().onSuccess(getId(), educationId);
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

    private ServerEvents postEducationData(JSONObject object) throws Exception{
        //Write the request data
        HttpURLConnection httppost;
        if (isUpdate) {
            httppost = getNetworkUtils().getHttpURLConInstance(
                    String.format(UPDATE_LOCATION_URL, getAppPreferences().getUserId(),
                            String.valueOf(getId())), "PUT", "application/json", true);
        } else {
            httppost = getNetworkUtils().getHttpURLConInstance(
                    String.format(ADD_LOCATION_URL, getAppPreferences().getUserId()),
                    "POST", "application/json", true);
        }
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
        if(ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.SUCCESS ||
                ServerEvents.valueOf(response.get("status").toString()) == ServerEvents.UPDATED) {
            educationId = (Long) response.get("id");
            if(bitmap != null && educationId != -1) {
                return uploadImage(bitmap);
            }
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.SUCCESS;
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
                        String.format(MARKSHEET_IMAGE_URL, getAppPreferences().getUserId(), educationId));
                httpPost.addHeader("X-AUTH-TOKEN", getAppPreferences().getUserToken());
                entityBuilder.addBinaryBody("file", imageStream, ContentType.create("image/jpeg"), "marksheetImg.jpg");
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
            return ServerEvents.SUCCESS;
        } else {
            errorMessage = response.get("message").toString();
            return ServerEvents.FAILURE;
        }
    }
}
