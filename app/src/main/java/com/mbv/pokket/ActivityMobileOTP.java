package com.mbv.pokket;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.threads.tasks.AsyncTaskVerification;
import com.mbv.pokket.util.AppPreferences;
import com.mbv.pokket.util.SMSReceiver;

import org.json.simple.JSONObject;

/**
 * Created by arindamnath on 04/01/16.
 */
public class ActivityMobileOTP extends AppCompatActivity implements TextWatcher, ServerResponseListener, OnPermissionCallback {

    private AppPreferences appPreferences;
    private Button verfiyOTP;
    private TextView generateOTP;
    private EditText otpCode;
    private BroadcastReceiver broadcastReceiver;
    private Tracker mTracker;
    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_translate);
        setContentView(R.layout.activity_mobile_otp);

        appPreferences = new AppPreferences(this);
        permissionHelper = PermissionHelper.getInstance(this);
        permissionHelper.setForceAccepting(true).request(Manifest.permission.READ_SMS);
        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Mobile OTP");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        verfiyOTP = (Button) findViewById(R.id.otp_verfiy_button);
        generateOTP = (TextView) findViewById(R.id.otp_regenerate_button);
        otpCode = (EditText) findViewById(R.id.otp_code_edittext);

        otpCode.addTextChangedListener(this);

        verfiyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                data.put("otp", otpCode.getText().toString().trim());
                new AsyncTaskVerification(2, v.getContext(), ActivityMobileOTP.this)
                        .execute(new JSONObject[]{data});
            }
        });

        generateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("button_click")
                        .setAction("mobile_otp_requested")
                        .setLabel("New otp requested.")
                        .build());
                JSONObject data = new JSONObject();
                data.put("otp", "generate");
                new AsyncTaskVerification(1, v.getContext(), ActivityMobileOTP.this)
                        .execute(new JSONObject[]{data});
            }
        });

        broadcastReceiver =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                String message = b.getString("otp");
                otpCode.setText(message);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("app_event")
                        .setAction("mobile_otp_auto_detected")
                        .setLabel("OTP detected : " + message)
                        .build());
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(SMSReceiver.OTP_BROADCAST_TAG));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(otpCode.getText().toString().trim().length() > 0) {
            verfiyOTP.setEnabled(true);
        } else {
            verfiyOTP.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onSuccess(int threadId, Object object) {
        if(threadId == 2) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("app_event")
                    .setAction("mobile_otp")
                    .setLabel("New user otp validity success.")
                    .build());
            appPreferences.setSignUpStep(2);
            startActivity(new Intent(ActivityMobileOTP.this, ActivityWalletSetup.class));
            finish();
        }
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }

    @Override
    public void onPermissionGranted(String[] permissionName) {

    }

    @Override
    public void onPermissionDeclined(String[] permissionName) {

    }

    @Override
    public void onPermissionPreGranted(String permissionsName) {

    }

    @Override
    public void onPermissionNeedExplanation(String permissionName) {

    }

    @Override
    public void onPermissionReallyDeclined(String permissionName) {

    }

    @Override
    public void onNoPermissionNeeded() {

    }
}
