package com.mbv.pokket;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.threads.tasks.AsyncTaskAuthentication;
import com.mbv.pokket.threads.tasks.AsyncTaskForgotPassword;
import com.mbv.pokket.util.AppPreferences;
import com.mbv.pokket.util.ValidatorUtils;

import org.json.simple.JSONObject;

/**
 * Created by arindamnath on 25/03/16.
 */
public class ActivityUserSignIn extends AppCompatActivity implements ServerResponseListener {

    private Button signInButton, recoverPasswordButton;
    private EditText signInEmail, signInPassword, forgotPasswordEmail;
    private TextView forgotPassworButton;

    private AppPreferences appPreferences;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        appPreferences = new AppPreferences(this);
        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Sign In");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        signInButton = (Button) findViewById(R.id.sign_in_button);
        forgotPassworButton = (TextView) findViewById(R.id.forgot_password_button);
        recoverPasswordButton = (Button) findViewById(R.id.recover_password_button);
        signInEmail = (EditText) findViewById(R.id.sign_in_email);
        signInPassword = (EditText) findViewById(R.id.sign_in_password);
        forgotPasswordEmail = (EditText) findViewById(R.id.forgot_password_email);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    JSONObject data = new JSONObject();
                    data.put("email", signInEmail.getText().toString());
                    data.put("password", signInPassword.getText().toString());
                    new AsyncTaskAuthentication(1, v.getContext(), ActivityUserSignIn.this)
                            .execute(new JSONObject[]{data});
                }
            }
        });

        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityUserSignIn.this, ActivityUserSignUp.class));
                finish();
            }
        });

        forgotPassworButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.sign_in_container).setVisibility(View.GONE);
                findViewById(R.id.forgot_password_container).setVisibility(View.VISIBLE);
            }
        });

        recoverPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateRecoverPassword()) {
                    JSONObject data = new JSONObject();
                    data.put("email", forgotPasswordEmail.getText().toString());
                    new AsyncTaskForgotPassword(3, v.getContext(), ActivityUserSignIn.this)
                            .execute(new JSONObject[]{data});
                }
            }
        });
    }

    @Override
    public void onSuccess(int threadId, Object object) {
        switch (threadId) {
            case 1:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("app_event")
                        .setAction("sign_in")
                        .setLabel("Sign In")
                        .build());
                appPreferences.setLoggedIn();
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case 2:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("app_event")
                        .setAction("password_recovery")
                        .setLabel("Password Recovery")
                        .build());
                findViewById(R.id.sign_in_container).setVisibility(View.VISIBLE);
                findViewById(R.id.forgot_password_container).setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.sign_in_container).getVisibility() == View.VISIBLE) {
            startActivity(new Intent(this, ActivitySplash.class));
            finish();
        } else {
            findViewById(R.id.sign_in_container).setVisibility(View.VISIBLE);
            findViewById(R.id.forgot_password_container).setVisibility(View.GONE);
        }
    }

    public boolean validate() {
        boolean valid = true;
        String email = signInEmail.getText().toString();
        String password = signInPassword.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signInEmail.setError("enter a valid email address");
            valid = false;
        } else {
            signInEmail.setError(null);
        }

        if (password.isEmpty()) {
            signInPassword.setError("password cannot be empty");
            valid = false;
        } else {
            signInPassword.setError(null);
        }
        return valid;
    }

    public boolean validateRecoverPassword() {
        boolean valid = true;
        String email = forgotPasswordEmail.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            forgotPasswordEmail.setError("enter a valid email address");
            valid = false;
        } else {
            forgotPasswordEmail.setError(null);
        }
        return valid;
    }
}
