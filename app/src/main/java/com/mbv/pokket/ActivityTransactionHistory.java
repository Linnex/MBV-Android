package com.mbv.pokket;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.adapters.AppPagerAdapter;
import com.mbv.pokket.dao.PagerFragment;
import com.mbv.pokket.fragments.UserTransactionFragment;

import java.util.ArrayList;

public class ActivityTransactionHistory extends AppCompatActivity {

    private ViewPager mViewPager;
    private AppPagerAdapter appPagerAdapter;
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

        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Transaction History");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        pagerFragments.add(new PagerFragment(UserTransactionFragment.newInstance(UserTransactionFragment.PENDING), "Pending"));
        pagerFragments.add(new PagerFragment(UserTransactionFragment.newInstance(UserTransactionFragment.OPEN), "Active"));
        pagerFragments.add(new PagerFragment(UserTransactionFragment.newInstance(UserTransactionFragment.CLOSE), "Complete"));

        appPagerAdapter = new AppPagerAdapter(getSupportFragmentManager(), true);
        appPagerAdapter.setData(pagerFragments);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(appPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
