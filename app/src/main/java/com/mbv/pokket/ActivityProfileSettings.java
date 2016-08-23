package com.mbv.pokket;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.dao.PagerFragment;
import com.mbv.pokket.dao.enums.RoleType;
import com.mbv.pokket.fragments.UserEducationFragment;
import com.mbv.pokket.adapters.AppPagerAdapter;
import com.mbv.pokket.fragments.ProfileFragment;
import com.mbv.pokket.fragments.UserKYCFragment;
import com.mbv.pokket.util.AppPreferences;

import java.util.ArrayList;

public class ActivityProfileSettings extends AppCompatActivity {

    private AppPreferences appPreferences;
    private AppPagerAdapter appPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ArrayList<PagerFragment> pagerFragments = new ArrayList<>();

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_translate);
        setContentView(R.layout.activity_tabs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket My Profile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        appPreferences = new AppPreferences(this);
        appPagerAdapter = new AppPagerAdapter(getSupportFragmentManager(), true);

        RoleType role = appPreferences.getUserRole();
        pagerFragments.add(new PagerFragment(ProfileFragment.newInstance(1), "Basic Info"));
        if(role == RoleType.BORROW || role == RoleType.ALL) {
            pagerFragments.add(new PagerFragment(UserEducationFragment.newInstance(), "Education"));
        }
        pagerFragments.add(new PagerFragment(UserKYCFragment.newInstance(), "KYC"));

        appPagerAdapter.setData(pagerFragments);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(appPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
