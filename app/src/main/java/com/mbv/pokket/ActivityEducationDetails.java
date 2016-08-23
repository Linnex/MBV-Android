package com.mbv.pokket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.adapters.DegreeAdapter;
import com.mbv.pokket.dao.DegreeDAO;
import com.mbv.pokket.dao.enums.EducationDegreeType;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.fragments.SelectDateFragment;
import com.mbv.pokket.threads.loaders.LoaderDegreeName;
import com.mbv.pokket.threads.tasks.AsyncTaskEducation;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by arindamnath on 04/01/16.
 */
public class ActivityEducationDetails extends AppCompatActivity
        implements ServerResponseListener, LoaderManager.LoaderCallbacks<List<DegreeDAO>> {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText instituteName, description, score, city, state, pincode;
    private Spinner degreeType;
    private AutoCompleteTextView degreeName;
    private TextView joiningDate, passingDate;
    private FloatingActionButton captureImageButton;
    private Long joining = System.currentTimeMillis(), passing = System.currentTimeMillis(), degreeId = 1l;

    private ImageView marksheetImage;
    private File userMarksheetFile;
    private Bitmap imageBitmap;
    private boolean imageClicked, isUpdate;

    private DegreeAdapter degreeAdapter;

    private Bundle queryData;
    private Loader<List<DegreeDAO>> educationDegreeLoader;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_translate);
        setContentView(R.layout.activity_education_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Education Details");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        try {
            userMarksheetFile = File.createTempFile("marksheet_photo_" + System.currentTimeMillis(), ".jpg", getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        userMarksheetFile.setWritable(true, false);

        description = (EditText) findViewById(R.id.educational_details_description);
        instituteName = (EditText) findViewById(R.id.educational_details_institute_name);
        score = (EditText) findViewById(R.id.educational_details_score);
        city = (EditText) findViewById(R.id.educational_details_city);
        state = (EditText) findViewById(R.id.educational_details_state);
        pincode = (EditText) findViewById(R.id.educational_details_pincode);
        degreeName = (AutoCompleteTextView) findViewById(R.id.educational_details_degree_name);
        joiningDate = (TextView) findViewById(R.id.educational_details_year_of_joining);
        passingDate = (TextView) findViewById(R.id.educational_details_year_of_passing);
        marksheetImage = (ImageView) findViewById(R.id.add_educational_details_image);
        degreeType = (Spinner) findViewById(R.id.educational_details_degree);
        captureImageButton = (FloatingActionButton) findViewById(R.id.add_educational_details_capture_image);

        joiningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment selectDateFragment = new SelectDateFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        joiningDate.setText(
                                getResources().getStringArray(R.array.months_array)[month] + "  " + year);
                        final Calendar calendar = Calendar.getInstance();
                        calendar.set(year,month,day);
                        joining = calendar.getTimeInMillis();
                    }
                };
                Bundle date = new Bundle();
                date.putLong("date", joining);
                selectDateFragment.setArguments(date);
                selectDateFragment.show(getSupportFragmentManager(), "dialog");
            }
        });

        passingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment selectDateFragment = new SelectDateFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        passingDate.setText(
                                getResources().getStringArray(R.array.months_array)[month] + " " + year);
                        final Calendar calendar = Calendar.getInstance();
                        calendar.set(year,month,day);
                        passing = calendar.getTimeInMillis();
                    }
                };
                Bundle date = new Bundle();
                date.putLong("date", passing);
                selectDateFragment.setArguments(date);
                selectDateFragment.show(getSupportFragmentManager(), "dialog");
            }
        });

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(userMarksheetFile));
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        if(getIntent().getLongExtra("id", -1l) != -1l) {
            getSupportActionBar().setTitle("Update Education Information");
            instituteName.setText(getIntent().getStringExtra("instituteName"));
            degreeType.setSelection(EducationDegreeType.valueOf(getIntent().getStringExtra("degreeType")).ordinal());
            degreeId = getIntent().getLongExtra("degreeId", 1l);
            degreeName.setText(getIntent().getStringExtra("degreeName"));
            description.setText(getIntent().getStringExtra("description"));
            city.setText(getIntent().getStringExtra("city"));
            state.setText(getIntent().getStringExtra("state"));
            pincode.setText(String.valueOf(getIntent().getLongExtra("pincode", 1l)));
            joining = getIntent().getLongExtra("joinDate", -1l);
            passing = getIntent().getLongExtra("passDate", -1l);
            if (joining != -1l) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(joining);
                joiningDate.setText(
                        getResources().getStringArray(R.array.months_array)[calendar.get(Calendar.MONTH)]
                                + " " + calendar.get(Calendar.YEAR));
            }
            if (passing != -1l) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(passing);
                passingDate.setText(
                        getResources().getStringArray(R.array.months_array)[calendar.get(Calendar.MONTH)]
                                + " " + calendar.get(Calendar.YEAR));
            }
            if(getIntent().getStringExtra("imageUrl") != null) {
                imageClicked = true;
                Picasso.with(this)
                        .load(getIntent().getStringExtra("imageUrl"))
                        .into(marksheetImage);
            }
            isUpdate = true;
        }

        degreeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                queryData = new Bundle();
                queryData.putString("type", EducationDegreeType.valueOf(position).toString());
                if (educationDegreeLoader == null) {
                    educationDegreeLoader = getSupportLoaderManager().initLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ActivityEducationDetails.this);
                } else {
                    educationDegreeLoader = getSupportLoaderManager().restartLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ActivityEducationDetails.this);
                }
                educationDegreeLoader.forceLoad();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageClicked = true;
            try {
                imageBitmap = BitmapFactory.decodeFile(userMarksheetFile.getPath());
                Picasso.with(this)
                        .load(userMarksheetFile)
                        .into(marksheetImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return true;
            case R.id.menu_save:
                JSONObject data = new JSONObject();
                data.put("institutionName", instituteName.getText().toString());
                data.put("degreeType", EducationDegreeType.valueOf(degreeType.getSelectedItemPosition()).toString());
                data.put("degreeCategoryName", degreeName.getText().toString());
                data.put("description", description.getText().toString());
                data.put("startDate", joining);
                data.put("endDate", passing);
                data.put("city", city.getText().toString());
                data.put("state", state.getText().toString());
                if(score.getText().toString().length() > 0) {
                    data.put("score", Double.parseDouble(score.getText().toString()));
                }
                data.put("country", "India");
                data.put("pincode", pincode.getText().toString());
                new AsyncTaskEducation(Integer.parseInt(
                        String.valueOf(getIntent().getLongExtra("id", -1l))), this, this,
                        isUpdate, imageBitmap)
                        .execute(new JSONObject[]{data});
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(int threadId, Object object) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("app_event")
                .setAction((isUpdate) ? "education_details_update" : "education_details_create")
                .setLabel("Education details " + ((isUpdate) ? "added." : "updated."))
                .build());
        finish();
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }

    @Override
    public Loader<List<DegreeDAO>> onCreateLoader(int id, Bundle args) {
        return new LoaderDegreeName(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<DegreeDAO>> loader, List<DegreeDAO> data) {
        if(data != null) {
            degreeAdapter = new DegreeAdapter(this, R.layout.adapter_geo_location, data);
            degreeName.setAdapter(degreeAdapter);
            degreeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<DegreeDAO>> loader) {

    }
}
