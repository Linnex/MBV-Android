package com.mbv.pokket;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.threads.tasks.AsyncTaskSignUp;
import com.mbv.pokket.util.AppPreferences;
import com.mbv.pokket.util.AppWebViewClient;
import com.mbv.pokket.util.ValidatorUtils;

import org.json.simple.JSONObject;

/**
 * Created by arindamnath on 25/03/16.
 */
public class ActivityUserSignUp extends AppCompatActivity implements ServerResponseListener {

    private EditText signUpName, signUpEmail, signUpMobile, signUpPassword;
    private Button signUpButton;
    private WebView termsWebView;

    private AppPreferences appPreferences;
    private ValidatorUtils validatorUtils;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        appPreferences = new AppPreferences(this);
        validatorUtils = new ValidatorUtils();

        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Sign Up");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        signUpName = (EditText) findViewById(R.id.sign_up_name);
        signUpEmail = (EditText) findViewById(R.id.sign_up_email);
        signUpMobile = (EditText) findViewById(R.id.sign_up_phone);
        signUpPassword = (EditText) findViewById(R.id.sign_up_password);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        termsWebView = (WebView) findViewById(R.id.sign_up_terms_webview);

        termsWebView.getSettings().setJavaScriptEnabled(true);
        termsWebView.getSettings().setSupportZoom(false);
        termsWebView.getSettings().setAppCacheEnabled(true);
        termsWebView.setWebViewClient(new AppWebViewClient(this));

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    findViewById(R.id.sign_up_container).setVisibility(View.GONE);
                    findViewById(R.id.sign_up_terms_container).setVisibility(View.VISIBLE);
                    termsWebView.loadUrl("https://s3-ap-southeast-1.amazonaws.com/mbv-pokket/terms-page/index.html");
                }
            }
        });

        findViewById(R.id.sign_up_agree_terms_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("button_click")
                        .setAction("terms_agree")
                        .setLabel("New user terms agreed")
                        .build());
                findViewById(R.id.sign_up_container).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_up_terms_container).setVisibility(View.GONE);

                JSONObject data = new JSONObject();
                String[] userName = signUpName.getText().toString().trim().split("\\s+");
                if (userName.length == 1) {
                    data.put("firstName", userName[0]);
                } else if (userName.length == 2) {
                    data.put("firstName", userName[0]);
                    data.put("lastName", userName[1]);
                } else if (userName.length > 2) {
                    data.put("firstName", userName[0]);
                    data.put("middleName", userName[1]);
                    data.put("lastName", userName[2]);
                }
                data.put("email", signUpEmail.getText().toString());
                data.put("phoneNumber", signUpMobile.getText().toString());
                data.put("hash", signUpPassword.getText().toString());

                new AsyncTaskSignUp(1, v.getContext(), ActivityUserSignUp.this)
                        .execute(new JSONObject[]{data});
            }
        });

        findViewById(R.id.sign_up_disagree_terms_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("button_click")
                        .setAction("terms_disagree")
                        .setLabel("New user terms disagreed")
                        .build());
                findViewById(R.id.sign_up_container).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_up_terms_container).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityUserSignUp.this, ActivityUserSignIn.class));
                finish();
            }
        });
    }

    @Override
    public void onSuccess(int threadId, Object object) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("app_event")
                .setAction("sign_up")
                .setLabel("New user sign up.")
                .build());
        appPreferences.setSignUpActive();
        appPreferences.setSignUpStep(1);
        startActivity(new Intent(this, ActivityMobileOTP.class));
        finish();
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ActivitySplash.class));
        finish();
    }

    public boolean validate() {
        boolean valid = true;
        String name = signUpName.getText().toString();
        String email = signUpEmail.getText().toString();
        String phone = signUpMobile.getText().toString();
        String password = signUpPassword.getText().toString();

        if (name.isEmpty() || !validatorUtils.validateFullname(signUpName.getText().toString())) {
            signUpName.setError("enter your full name");
            valid = false;
        } else {
            signUpName.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpEmail.setError("enter a valid email address");
            valid = false;
        } else {
            signUpEmail.setError(null);
        }

        if (phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches()) {
            signUpMobile.setError("enter a valid mobile number");
            valid = false;
        } else {
            signUpMobile.setError(null);
        }

        if (password.isEmpty()) {
            signUpPassword.setError("password cannot be empty");
            valid = false;
        } else {
            signUpPassword.setError(null);
        }
        return valid;
    }
}
