package com.mbv.pokket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.dao.enums.KYCType;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.threads.tasks.AsyncTaskAddKYCInfo;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by arindamnath on 29/02/16.
 */
public class ActivityKYCDetails extends AppCompatActivity implements ServerResponseListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Spinner kycType;
    private EditText kycId;
    private TextView address, student, identity;
    private FloatingActionButton captureImage;
    private ImageView kycImage;
    private File userKYCFile;
    private Bitmap imageBitmap;
    private Boolean imageClicked = false, isUpdate = false;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_translate);
        setContentView(R.layout.activity_kyc_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket KYC Details");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        try {
            userKYCFile = File.createTempFile("kyc_photo_" + System.currentTimeMillis(), ".jpg", getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        userKYCFile.setWritable(true, false);

        kycType = (Spinner) findViewById(R.id.kyc_details_type);
        kycId = (EditText) findViewById(R.id.kyc_details_id);
        address = (TextView) findViewById(R.id.kyc_detials_proof_address);
        student = (TextView) findViewById(R.id.kyc_detials_proof_student);
        identity = (TextView) findViewById(R.id.kyc_detials_proof_identity);
        kycImage = (ImageView) findViewById(R.id.kyc_details_image);
        captureImage = (FloatingActionButton) findViewById(R.id.kyc_details_take_image);

        kycType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        kycId.setHint("Enter your PAN Card Id");
                        address.setVisibility(View.GONE);
                        identity.setVisibility(View.VISIBLE);
                        student.setVisibility(View.GONE);
                        break;
                    case 1:
                        kycId.setHint("Enter your Adhaar Id");
                        address.setVisibility(View.VISIBLE);
                        identity.setVisibility(View.VISIBLE);
                        student.setVisibility(View.GONE);
                        break;
                    case 2:
                        kycId.setHint("Enter your Passport Id");
                        address.setVisibility(View.VISIBLE);
                        identity.setVisibility(View.VISIBLE);
                        student.setVisibility(View.GONE);
                        break;
                    case 3:
                        kycId.setHint("Enter your Voter Card Id");
                        address.setVisibility(View.VISIBLE);
                        identity.setVisibility(View.VISIBLE);
                        student.setVisibility(View.GONE);
                        break;
                    case 4:
                        kycId.setHint("Enter your Bank Account number");
                        address.setVisibility(View.VISIBLE);
                        identity.setVisibility(View.VISIBLE);
                        student.setVisibility(View.GONE);
                        break;
                    case 5:
                        kycId.setHint("Enter your Student Id");
                        address.setVisibility(View.GONE);
                        identity.setVisibility(View.GONE);
                        student.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(userKYCFile));
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        if(getIntent().getLongExtra("id", -1l) != -1l) {
            getSupportActionBar().setTitle("Update KYC Information");
            kycId.setText(getIntent().getStringExtra("kycId"));
            kycType.setSelection(KYCType.valueOf(getIntent().getStringExtra("type")).ordinal());
            kycType.setEnabled(false);
            if(getIntent().getStringExtra("imageUrl").length() > 0) {
                Picasso.with(this)
                        .load(getIntent().getStringExtra("imageUrl"))
                        .into(kycImage);
            }
            isUpdate = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageClicked = true;
            try {
                imageBitmap = BitmapFactory.decodeFile(userKYCFile.getPath());
                Picasso.with(this)
                        .load(userKYCFile)
                        .into(kycImage);
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
                if(imageClicked) {
                    if (kycId.getText().toString().length() > 0) {
                        JSONObject data = new JSONObject();
                        data.put("kycId", kycId.getText().toString());
                        data.put("type", kycType.getSelectedItemPosition());
                        data.put("imageUrl", "");
                        new AsyncTaskAddKYCInfo(Integer.parseInt(
                                String.valueOf(getIntent().getLongExtra("id", -1l))), this, this,
                                isUpdate, imageBitmap)
                                .execute(new JSONObject[]{data});
                    } else {
                        Snackbar.make(kycId, "Please enter the ID", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(kycId, "Please provide an image of the KYC type.", Snackbar.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSuccess( int threadId, Object object) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("app_event")
                .setAction((isUpdate) ? "kyc_details_update" : "kyc_details_create")
                .setLabel("KYC details " + ((isUpdate) ? "added." : "updated."))
                .build());
        finish();
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }
}
