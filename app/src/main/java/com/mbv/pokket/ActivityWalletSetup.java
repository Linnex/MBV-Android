package com.mbv.pokket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.threads.tasks.AsyncTaskWallet;
import com.mbv.pokket.util.AppPreferences;

import org.json.simple.JSONObject;

/**
 * Created by arindamnath on 15/03/16.
 */
public class ActivityWalletSetup extends AppCompatActivity implements TextWatcher, ServerResponseListener {

    private AppPreferences appPreferences;
    private Button createWallet;
    private EditText password, rePassword;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_translate);
        setContentView(R.layout.activity_wallet_setup);

        appPreferences = new AppPreferences(this);
        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Wallet Setup");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        createWallet = (Button) findViewById(R.id.wallet_create_button);
        password = (EditText) findViewById(R.id.wallet_create_password_edittext);
        rePassword = (EditText) findViewById(R.id.wallet_create_reenter_password_edittext);

        rePassword.addTextChangedListener(this);

        createWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().trim().equalsIgnoreCase(rePassword.getText().toString().trim())) {
                    JSONObject data = new JSONObject();
                    data.put("hash", password.getText().toString().trim());
                    new AsyncTaskWallet(2, v.getContext(), ActivityWalletSetup.this)
                            .execute(new JSONObject[]{data});
                } else {
                    Toast.makeText(v.getContext(), "Password mismatch!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onSuccess(int threadId, Object object) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("app_event")
                .setAction("wallet_setup")
                .setLabel("New user wallet setup.")
                .build());
        appPreferences.setSignUpStep(3);
        startActivity(new Intent(ActivityWalletSetup.this, ActivityCompleteSignUp.class));
        finish();
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(password.getText().toString().trim().length() > 0
                && rePassword.getText().toString().trim().length() > 0) {
            createWallet.setEnabled(true);
        } else {
            createWallet.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
