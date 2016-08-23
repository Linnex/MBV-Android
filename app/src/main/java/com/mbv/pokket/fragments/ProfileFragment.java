package com.mbv.pokket.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.mbv.pokket.R;
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
import com.mbv.pokket.threads.loaders.LoaderSearchLocation;
import com.mbv.pokket.threads.tasks.AsyncTaskUserProfileUpdate;
import com.mbv.pokket.util.AppPreferences;
import com.mbv.pokket.util.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
public class ProfileFragment extends Fragment
        implements TextWatcher, LoaderManager.LoaderCallbacks<List<GeoLocation>>,
        ServerResponseListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private View mViewHolder;
    private AppPreferences appPreferences;
    private LocationAdapter locationAdapter;

    private Button dobSeletion, saveBtn;
    private EditText name;
    private Spinner gender, residentialStatus, workStatus, maritalStatus;
    private AutoCompleteTextView homeStreetAddress, currentStreetAddress;
    private EditText homeCity, homeState, homePincode;
    private EditText currentCity, currentState, currentPincode;
    private CardView currentAddress;
    private ImageView userImage;

    private File userImageFile;
    private Bundle queryData;
    private Bitmap imageBitmap;
    private boolean isAddressSame;

    private Long dob = System.currentTimeMillis();

    private Loader<List<GeoLocation>> homeAddressLoader, currentAddressLoader;

    public static ProfileFragment newInstance(int type) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("redirectType", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appPreferences = new AppPreferences(getContext());
        try {
            userImageFile = File.createTempFile("user_photo_" + System.currentTimeMillis(), ".jpg",
                    getActivity().getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        userImageFile.setWritable(true, false);

        mViewHolder = inflater.inflate(R.layout.fragment_update_profile, container, false);
        saveBtn = (Button) mViewHolder.findViewById(R.id.profile_update_save_btn);
        userImage = (ImageView) mViewHolder.findViewById(R.id.profile_update_image);
        dobSeletion = (Button) mViewHolder.findViewById(R.id.profile_update_dob);
        gender = (Spinner) mViewHolder.findViewById(R.id.profile_update_gender);
        residentialStatus = (Spinner) mViewHolder.findViewById(R.id.profile_update_residential_status);
        workStatus = (Spinner) mViewHolder.findViewById(R.id.profile_update_employment_status);
        maritalStatus = (Spinner) mViewHolder.findViewById(R.id.profile_update_marital_status);
        name = (EditText) mViewHolder.findViewById(R.id.profile_update_name);
        homeStreetAddress = (AutoCompleteTextView) mViewHolder.findViewById(R.id.profile_update_home_street_address);
        homeCity = (EditText) mViewHolder.findViewById(R.id.profile_update_home_city);
        homeState = (EditText) mViewHolder.findViewById(R.id.profile_update_home_state);
        homePincode = (EditText) mViewHolder.findViewById(R.id.profile_update_home_pincode);
        currentStreetAddress = (AutoCompleteTextView) mViewHolder.findViewById(R.id.profile_update_current_street_address);
        currentCity = (EditText) mViewHolder.findViewById(R.id.profile_update_current_city);
        currentState = (EditText) mViewHolder.findViewById(R.id.profile_update_current_state);
        currentPincode = (EditText) mViewHolder.findViewById(R.id.profile_update_current_pincode);
        currentAddress = (CardView) mViewHolder.findViewById(R.id.profile_update_current_address_holder);

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
                selectDateFragment.show(getActivity().getSupportFragmentManager(), "dialog");
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(userImageFile));
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        mViewHolder.findViewById(R.id.profile_update_save_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        if (isAddressSame) {
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
                        data.put("fatherName", appPreferences.getUserFatherName());
                        data.put("userLocationDatas", locationData); //Pair address info
                        new AsyncTaskUserProfileUpdate(1, v.getContext(), ProfileFragment.this, imageBitmap)
                                .execute(new JSONObject[]{data});
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
                    isAddressSame = true;
                    mViewHolder.findViewById(R.id.profile_update_current_address_holder)
                            .setVisibility(View.GONE);
                } else {
                    isAddressSame = false;
                    mViewHolder.findViewById(R.id.profile_update_current_address_holder)
                            .setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return mViewHolder;
    }

    @Override
    public void onResume() {
        super.onResume();
        /** Init the values form local instance **/
        name.setText(appPreferences.getUserFirstName() + " " + appPreferences.getUserMiddleName()
                + " " + appPreferences.getUserLastName());
        if(appPreferences.getUserDOB() != -1l) {
            dob = appPreferences.getUserDOB();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(appPreferences.getUserDOB());
            dobSeletion.setText("DOB - " + calendar.get(Calendar.DATE) + " "
                    + getResources().getStringArray(R.array.months_array)[calendar.get(Calendar.MONTH)]
                    + " " + calendar.get(Calendar.YEAR));
        }
        if(appPreferences.getUserImage() != null) {
            Picasso.with(getActivity())
                    .load(appPreferences.getUserImage())
                    .transform(new CropCircleTransformation())
                    .into(userImage);
        }
        if(appPreferences.getUserGender() != null) {
            gender.setSelection(Gender.valueOf(appPreferences.getUserGender()).ordinal());
        }
        if(appPreferences.getUserMaritalStatus() != null) {
            maritalStatus.setSelection(MaritalStatus.valueOf(appPreferences.getUserMaritalStatus()).ordinal());
        }
        if(appPreferences.getUserWorkStatus() != null) {
            workStatus.setSelection(WorkStatus.valueOf(appPreferences.getUserWorkStatus()).ordinal());
        }
        if(appPreferences.getUserResidientialStatus() != null) {
            residentialStatus.setSelection(ResidentialStatus.valueOf(appPreferences.getUserResidientialStatus()).ordinal());
        }
        String [] homeAddress = appPreferences.getHomeAddress();
        if(homeAddress != null) {
            homeStreetAddress.setText(homeAddress[0]);
            homeCity.setText(homeAddress[1]);
            homeState.setText(homeAddress[2]);
            homePincode.setText(homeAddress[5]);
        }
        String[] currentAddress = appPreferences.getCurrentAddress();
        if(currentAddress != null){
            if (currentAddress[0].equalsIgnoreCase(homeAddress[0])) {
                isAddressSame = true;
                mViewHolder.findViewById(R.id.profile_update_current_address_holder)
                        .setVisibility(View.GONE);
            }
            currentStreetAddress.setText(currentAddress[0]);
            currentCity.setText(currentAddress[1]);
            currentState.setText(currentAddress[2]);
            currentPincode.setText(currentAddress[5]);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (homeStreetAddress.toString().length() > 4) {
            if (homeStreetAddress.toString().length() % 5 == 0) {
                queryData = new Bundle();
                queryData.putString("query", homeStreetAddress.toString());
                if (homeAddressLoader == null) {
                    homeAddressLoader = getActivity().getSupportLoaderManager().initLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ProfileFragment.this);
                } else {
                    homeAddressLoader = getActivity().getSupportLoaderManager().restartLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ProfileFragment.this);
                }
                homeAddressLoader.forceLoad();
            }
        }

        if (currentStreetAddress.toString().length() > 4) {
            if (currentStreetAddress.toString().length() % 5 == 0) {
                queryData = new Bundle();
                queryData.putString("query", currentStreetAddress.toString());
                if (currentAddressLoader == null) {
                    currentAddressLoader = getActivity().getSupportLoaderManager().initLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ProfileFragment.this);
                } else {
                    currentAddressLoader = getActivity().getSupportLoaderManager().restartLoader(
                            LoaderID.GEO_LOCATION.getValue(), queryData, ProfileFragment.this);
                }
                currentAddressLoader.forceLoad();
            }
        }

        if(name.getText().toString().length() > 0
                && homeStreetAddress.getText().toString().length() > 0
                && homeCity.getText().toString().length() > 0
                && homeState.getText().toString().length() > 0
                && homePincode.getText().toString().length() > 0) {
            if(isAddressSame) {
                saveBtn.setEnabled(true);
            } else if(currentStreetAddress.getText().toString().length() > 0
                    && currentCity.getText().toString().length() > 0
                    && currentState.getText().toString().length() > 0
                    && currentPincode.getText().toString().length() > 0) {
                saveBtn.setEnabled(true);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public Loader<List<GeoLocation>> onCreateLoader(int id, Bundle args) {
        return new LoaderSearchLocation(getContext(), queryData);
    }

    @Override
    public void onLoadFinished(Loader<List<GeoLocation>> loader, List<GeoLocation> data) {
        if(data != null) {
            locationAdapter = new LocationAdapter(getContext(), R.layout.adapter_geo_location, data);
            homeStreetAddress.setAdapter(locationAdapter);
            currentStreetAddress.setAdapter(locationAdapter);
            locationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<GeoLocation>> loader) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {
                imageBitmap = BitmapFactory.decodeFile(userImageFile.getPath());
                Picasso.with(getContext())
                        .load(userImageFile)
                        .transform(new CropCircleTransformation())
                        .into(userImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSuccess(int threadId, Object object) {
        Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }
}
