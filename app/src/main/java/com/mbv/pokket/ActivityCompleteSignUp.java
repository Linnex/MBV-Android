package com.mbv.pokket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.adapters.LocationAdapter;
import com.mbv.pokket.dao.GeoLocation;
import com.mbv.pokket.dao.enums.CurrentLocationType;
import com.mbv.pokket.dao.enums.Gender;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.dao.enums.MaritalStatus;
import com.mbv.pokket.dao.enums.ResidentialStatus;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.enums.WorkStatus;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.fragments.SelectDateFragment;
import com.mbv.pokket.threads.loaders.LoaderSearchLocation;
import com.mbv.pokket.threads.tasks.AsyncTaskUserProfileUpdate;
import com.mbv.pokket.util.AppPreferences;
import com.mbv.pokket.util.CropCircleTransformation;
import com.mbv.pokket.util.ValidatorUtils;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class ActivityCompleteSignUp extends AppCompatActivity
        implements TextWatcher, LoaderManager.LoaderCallbacks<List<GeoLocation>>,
        ServerResponseListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private AppPreferences appPreferences;
    private LocationAdapter locationAdapter;

    private TextView dobSeletion;
    private Button saveBtn;
    private EditText name, fatherName;
    private Spinner gender, residentialStatus, workStatus, maritalStatus;
    private AutoCompleteTextView homeStreetAddress, currentStreetAddress;
    private EditText homeCity, homeState, homePincode;
    private EditText currentCity, currentState, currentPincode;
    private CardView currentAddress;
    private ImageView userImage;

    private File userImageFile;
    private Bundle queryData;
    private Bitmap imageBitmap;

    private Long dob = System.currentTimeMillis();
    private boolean imageCaptured, isSameAddress;

    private Loader<List<GeoLocation>> homeAddressLoader, currentAddressLoader;
    private ValidatorUtils validatorUtils;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        appPreferences = new AppPreferences(this);
        validatorUtils = new ValidatorUtils();
        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Complete Signup");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        try {
            userImageFile = File.createTempFile("user_photo_" + System.currentTimeMillis(), ".jpg", getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        userImageFile.setWritable(true, false);

        saveBtn = (Button) findViewById(R.id.profile_update_save_btn);
        userImage = (ImageView) findViewById(R.id.profile_update_image);
        dobSeletion = (TextView) findViewById(R.id.profile_update_dob);
        gender = (Spinner) findViewById(R.id.profile_update_gender);
        residentialStatus = (Spinner) findViewById(R.id.profile_update_residential_status);
        workStatus = (Spinner) findViewById(R.id.profile_update_employment_status);
        maritalStatus = (Spinner) findViewById(R.id.profile_update_marital_status);
        name = (EditText) findViewById(R.id.profile_update_name);
        fatherName = (EditText) findViewById(R.id.profile_update_father_name);
        homeStreetAddress = (AutoCompleteTextView) findViewById(R.id.profile_update_home_street_address);
        homeCity = (EditText) findViewById(R.id.profile_update_home_city);
        homeState = (EditText) findViewById(R.id.profile_update_home_state);
        homePincode = (EditText) findViewById(R.id.profile_update_home_pincode);
        currentStreetAddress = (AutoCompleteTextView) findViewById(R.id.profile_update_current_street_address);
        currentCity = (EditText) findViewById(R.id.profile_update_current_city);
        currentState = (EditText) findViewById(R.id.profile_update_current_state);
        currentPincode = (EditText) findViewById(R.id.profile_update_current_pincode);
        currentAddress = (CardView) findViewById(R.id.profile_update_current_address_holder);

        homeCity.addTextChangedListener(this);
        homeState.addTextChangedListener(this);
        homePincode.addTextChangedListener(this);
        currentCity.addTextChangedListener(this);
        currentState.addTextChangedListener(this);
        currentPincode.addTextChangedListener(this);
        homeStreetAddress.addTextChangedListener(this);
        currentStreetAddress.addTextChangedListener(this);

        dobSeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment selectDateFragment = new SelectDateFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        dobSeletion.setText("DOB - " + day + " " +
                                getResources().getStringArray(R.array.months_array)[month] + " " + year);
                        final Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        dob = calendar.getTimeInMillis();
                    }
                };
                Bundle date = new Bundle();
                date.putLong("date", dob);
                selectDateFragment.setArguments(date);
                selectDateFragment.show(getSupportFragmentManager(), "dialog");
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(userImageFile));
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    JSONArray locationData = new JSONArray();
                    //Add home address
                    JSONObject homeAddress = new JSONObject();
                    homeAddress.put("address", homeStreetAddress.getText().toString());
                    homeAddress.put("city", homeCity.getText().toString());
                    homeAddress.put("state", homeState.getText().toString());
                    homeAddress.put("country", "India");
                    homeAddress.put("pincode", Long.parseLong(homePincode.getText().toString()));
                    homeAddress.put("type", CurrentLocationType.HOME.toString());
                    locationData.add(homeAddress);
                    //Add current address
                    JSONObject currentAddress = new JSONObject();
                    if (isSameAddress) {
                        currentAddress.put("address", homeStreetAddress.getText().toString());
                        currentAddress.put("city", homeCity.getText().toString());
                        currentAddress.put("state", homeState.getText().toString());
                        currentAddress.put("country", "India");
                        currentAddress.put("pincode", Long.parseLong(homePincode.getText().toString()));
                    } else {
                        currentAddress.put("address", currentStreetAddress.getText().toString());
                        currentAddress.put("city", currentCity.getText().toString());
                        currentAddress.put("state", currentState.getText().toString());
                        currentAddress.put("country", "India");
                        currentAddress.put("pincode", Long.parseLong(currentPincode.getText().toString()));
                    }
                    currentAddress.put("type", CurrentLocationType.CURRENT.toString());
                    locationData.add(currentAddress);

                    JSONObject data = new JSONObject();
                    String[] userName = name.getText().toString().trim().split("\\s+");
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
                    data.put("gender", Gender.valueOf(gender.getSelectedItemPosition()).toString());
                    data.put("maritalStatus", MaritalStatus.valueOf(maritalStatus.getSelectedItemPosition()).toString());
                    data.put("workStatus", WorkStatus.valueOf(workStatus.getSelectedItemPosition()).toString());
                    data.put("residentialStatus", ResidentialStatus.valueOf(residentialStatus.getSelectedItemPosition()).toString());
                    data.put("dob", dob);
                    data.put("fatherName", fatherName.getText().toString());
                    data.put("userLocationDatas", locationData); //Pair address info
                    new AsyncTaskUserProfileUpdate(1, v.getContext(), ActivityCompleteSignUp.this, imageBitmap)
                            .execute(new JSONObject[]{data});
                } else {
                    Toast.makeText(v.getContext(), "Field(s) missing!", Toast.LENGTH_LONG).show();
                }
            }
        });

        homeStreetAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (locationAdapter != null) {
                    homeStreetAddress.setText(locationAdapter.getItem(position).getAddress());
                    homeCity.setText(locationAdapter.getItem(position).getCity());
                    homeState.setText(locationAdapter.getItem(position).getState());
                }
            }
        });

        currentStreetAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (locationAdapter != null) {
                    currentStreetAddress.setText(locationAdapter.getItem(position).getAddress());
                    currentCity.setText(locationAdapter.getItem(position).getCity());
                    currentState.setText(locationAdapter.getItem(position).getState());
                }
            }
        });

        residentialStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == 2) {
                    isSameAddress = true;
                    findViewById(R.id.profile_update_current_address_holder).setVisibility(View.GONE);
                } else {
                    isSameAddress = false;
                    findViewById(R.id.profile_update_current_address_holder).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageCaptured = true;
            try {
                imageBitmap = BitmapFactory.decodeFile(userImageFile.getPath());
                Picasso.with(this)
                        .load(userImageFile)
                        .transform(new CropCircleTransformation())
                        .into(userImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        name.setText(appPreferences.getUserFirstName() + " " + appPreferences.getUserMiddleName()
                + " " + appPreferences.getUserLastName());
    }

    @Override
    public Loader<List<GeoLocation>> onCreateLoader(int id, Bundle args) {
        return new LoaderSearchLocation(this, queryData);
    }

    @Override
    public void onLoadFinished(Loader<List<GeoLocation>> loader, List<GeoLocation> data) {
        if(data != null) {
            locationAdapter = new LocationAdapter(this, R.layout.adapter_geo_location, data);
            homeStreetAddress.setAdapter(locationAdapter);
            currentStreetAddress.setAdapter(locationAdapter);
            locationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<GeoLocation>> loader) {

    }

    @Override
    public void onSuccess(int threadId, Object object) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("app_event")
                .setAction("sign_up_complete")
                .setLabel("New user profile updated.")
                .build());
        appPreferences.setSignUpStep(4);
        startActivity(new Intent(ActivityCompleteSignUp.this, ActivityAppIntro.class));
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
        if (homeStreetAddress.getText().toString().length() > 4) {
            if (homeStreetAddress.getText().toString().length() % 5 == 0) {
                queryData = new Bundle();
                queryData.putString("query", homeStreetAddress.getText().toString());
                if (homeAddressLoader == null) {
                    homeAddressLoader = getSupportLoaderManager().initLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ActivityCompleteSignUp.this);
                } else {
                    homeAddressLoader = getSupportLoaderManager().restartLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ActivityCompleteSignUp.this);
                }
                homeAddressLoader.forceLoad();
            }
        }

        if (currentStreetAddress.getText().toString().length() > 4) {
            if (currentStreetAddress.getText().toString().length() % 5 == 0) {
                queryData = new Bundle();
                queryData.putString("query", currentStreetAddress.getText().toString());
                if (currentAddressLoader == null) {
                    currentAddressLoader = getSupportLoaderManager().initLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ActivityCompleteSignUp.this);
                } else {
                    currentAddressLoader = getSupportLoaderManager().restartLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ActivityCompleteSignUp.this);
                }
                currentAddressLoader.forceLoad();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean validate() {
        boolean valid = true;
        if(name.getText().toString().isEmpty()
                || !validatorUtils.validateFullname(name.getText().toString())) {
            valid = false;
        }
        if(fatherName.getText().toString().isEmpty()) {
            valid = false;
        }
        if(!imageCaptured || imageBitmap == null) {
            valid = false;
        }
        if(homeStreetAddress.getText().toString().isEmpty()
                && homeCity.getText().toString().isEmpty()
                && homeState.getText().toString().isEmpty()
                && homePincode.getText().toString().isEmpty()) {
            valid = false;
        }
        if(!isSameAddress) {
            if(currentStreetAddress.getText().toString().isEmpty()
                    && currentCity.getText().toString().isEmpty()
                    && currentState.getText().toString().isEmpty()
                    && currentPincode.getText().toString().isEmpty()) {
                valid = false;
            }
        }
        return valid;
    }
}
