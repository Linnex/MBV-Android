package com.mbv.pokket;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.adapters.TimelineRecyclerAdapter;
import com.mbv.pokket.dao.TimelineDAO;
import com.mbv.pokket.dao.UserProfileDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.threads.loaders.LoaderTimeline;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActivityViewUserProfile extends AppCompatActivity implements LoaderManager.LoaderCallbacks<UserProfileDAO> {

    private TimelineRecyclerAdapter timelineRecyclerAdapter;
    private Bundle queryData;
    private RecyclerView recyclerView;
    private ImageView userImage;
    private TextView userName, userDOB, userGender;
    private ProgressDialog progressDialog;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_translate);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queryData = new Bundle();
        queryData.putLong("userId", getIntent().getLongExtra("userId", -1l));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading user profile...");

        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket View Profile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        timelineRecyclerAdapter = new TimelineRecyclerAdapter(null);
        recyclerView = (RecyclerView) findViewById(R.id.user_profile_detials_timeline_list);
        userImage = (ImageView) findViewById(R.id.user_profile_image);
        userName = (TextView) findViewById(R.id.user_profile_name);
        userDOB = (TextView) findViewById(R.id.user_profile_dob);
        userGender = (TextView) findViewById(R.id.user_profile_gender);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(timelineRecyclerAdapter);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        getSupportLoaderManager()
                .initLoader(LoaderID.TIMELINE.getValue(), queryData, this).forceLoad();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<UserProfileDAO> onCreateLoader(int id, Bundle args) {
        progressDialog.show();
        return new LoaderTimeline(this, args);
    }

    @Override
    public void onLoadFinished(Loader<UserProfileDAO> loader, UserProfileDAO data) {
        progressDialog.dismiss();
        if (data != null) {
            userName.setText(data.getName());
            userDOB.setText("DOB : " + data.getDob());
            userGender.setText(data.getGender().toString());
            Picasso.with(this)
                    .load(data.getImageUrl())
                    .into(userImage);
            timelineRecyclerAdapter.setData(data.getTimelineDAOList());
        } else {
            timelineRecyclerAdapter.setData(new ArrayList<TimelineDAO>());
        }
    }

    @Override
    public void onLoaderReset(Loader<UserProfileDAO> loader) {
        progressDialog.dismiss();
    }
}
