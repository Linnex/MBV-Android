package com.mbv.pokket;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mbv.pokket.adapters.AppPagerAdapter;
import com.mbv.pokket.dao.PagerFragment;
import com.mbv.pokket.fragments.BorrowFragment;
import com.mbv.pokket.fragments.DashboardEventsFragment;
import com.mbv.pokket.fragments.DashboardTimelineFragment;
import com.mbv.pokket.fragments.LendFragment;
import com.mbv.pokket.fragments.WalletFragment;
import com.mbv.pokket.util.AppPreferences;
import com.mbv.pokket.util.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppPreferences appPreferences;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private AppPagerAdapter appPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private TextView userName, userEmail;
    private ImageView userImage;
    private ArrayList<PagerFragment> pagerFragments = new ArrayList<>();
    private int navigationMenuId;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Home Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().setNewSession().build());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mViewPager = (ViewPager) findViewById(R.id.main_dashboard_container);
        tabLayout = (TabLayout) findViewById(R.id.main_dashboard_tabs);

        View nav_header = navigationView.getHeaderView(0);
        userImage = (ImageView) nav_header.findViewById(R.id.nav_header_user_image);
        userName = (TextView) nav_header.findViewById(R.id.nav_header_name_text);
        userEmail = (TextView) nav_header.findViewById(R.id.nav_header_email_text);

        pagerFragments.add(new PagerFragment(DashboardTimelineFragment.newInstance(), "Home"));
        pagerFragments.add(new PagerFragment(DashboardEventsFragment.newInstance(), "Events"));
        //pagerFragments.add(new PagerFragment(DashboardStatsFragment.newInstance(), "Stats"));
        appPagerAdapter = new AppPagerAdapter(getSupportFragmentManager(), false);
        appPagerAdapter.setData(pagerFragments);
        mViewPager.setAdapter(appPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_developer_board_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_event_white_24dp);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_timeline_white_24dp);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        appPreferences = new AppPreferences(this);
        //Hide menu based on user role
        switch (appPreferences.getUserRole()) {
            case BORROW:
                navigationView.getMenu().findItem(R.id.nav_lend).setVisible(false);
                break;
            case LEND:
                navigationView.getMenu().findItem(R.id.nav_borrow).setVisible(false);
                break;
        }

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationSelection(R.id.nav_dashboard);

        userName.setText(appPreferences.getUserFirstName() + " " + appPreferences.getUserMiddleName()
                + " " + appPreferences.getUserLastName());
        userEmail.setText(appPreferences.getUserEmail());
        if(appPreferences.getUserImage() != null) {
            Picasso.with(this)
                    .load(appPreferences.getUserImage())
                    .transform(new CropCircleTransformation())
                    .into(userImage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        navigationSelection(item.getItemId());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(navigationMenuId != R.id.nav_dashboard) {
            navigationSelection(R.id.nav_dashboard);
            navigationView.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
        } else {
            super.onBackPressed();
        }
    }

    private void navigationSelection(int id) {
        navigationMenuId = id;
        switch (id) {
            case R.id.nav_dashboard:
                mViewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.main_fragment_holder).setVisibility(View.GONE);
                break;
            case R.id.nav_lend:
                mViewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                findViewById(R.id.main_fragment_holder).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_holder, LendFragment.newInstance())
                        .commit();
                break;
            case R.id.nav_borrow:
                mViewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                findViewById(R.id.main_fragment_holder).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_holder, BorrowFragment.newInstance())
                        .commit();
                break;
            case R.id.nav_manage_wallet:
                mViewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                findViewById(R.id.main_fragment_holder).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_holder, WalletFragment.newInstance())
                        .commit();
                break;
            case R.id.nav_transaction_history:
                startActivity(new Intent(MainActivity.this, ActivityTransactionHistory.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, ActivityProfileSettings.class));
                break;
            case R.id.nav_contact:
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
                break;
            case R.id.nav_rate_us:
                //startActivity(new Intent(MainActivity.this, ContactActivity.class));
                break;
            case R.id.nav_logout:
                appPreferences.setLoggedOut();
                startActivity(new Intent(MainActivity.this, ActivitySplash.class));
                finish();
                break;
        }
    }
}
