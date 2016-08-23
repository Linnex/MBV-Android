package com.mbv.pokket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.fragments.IntroScreenFragment;
import com.mbv.pokket.util.AppPreferences;

/**
 * Created by arindamnath on 26/12/15.
 */
public class ActivitySplash extends Activity implements View.OnClickListener {

    private Button splashSignIn, splashSignUp;

    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appPreferences = new AppPreferences(this);

        splashSignIn = (Button) findViewById(R.id.splash_sign_in_btn);
        splashSignUp = (Button) findViewById(R.id.splash_sign_up_btn);

        splashSignIn.setOnClickListener(this);
        splashSignUp.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (appPreferences.isUserLoggedIn()) {
                    startActivity(new Intent(ActivitySplash.this, MainActivity.class));
                    finish();
                } else if(appPreferences.isSignUpActive()) {
                    if(appPreferences.getSignUpStep() == 1) {
                        startActivity(new Intent(ActivitySplash.this, ActivityMobileOTP.class));
                        finish();
                    } else if (appPreferences.getSignUpStep() == 2) {
                        startActivity(new Intent(ActivitySplash.this, ActivityWalletSetup.class));
                        finish();
                    } else if(appPreferences.getSignUpStep() == 3) {
                        startActivity(new Intent(ActivitySplash.this, ActivityCompleteSignUp.class));
                        finish();
                    } else if(appPreferences.getSignUpStep() == 4) {
                        startActivity(new Intent(ActivitySplash.this, ActivityAppIntro.class));
                        finish();
                    }
                } else {
                    findViewById(R.id.splash_promo_container).setVisibility(View.VISIBLE);
                }
            }
        }, 2500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.splash_sign_in_btn:
                startActivity(new Intent(this, ActivityUserSignIn.class));
                finish();
                break;
            case R.id.splash_sign_up_btn:
                startActivity(new Intent(this, ActivityUserSignUp.class));
                finish();
                break;
        }
    }
}
