package com.mbv.pokket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * Created by arindamnath on 04/02/16.
 */
public class ContactActivity extends AppCompatActivity {

    private Spinner querySpinner, loanSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        querySpinner = (Spinner) findViewById(R.id.contact_query_type_spinner);
        loanSpinner = (Spinner) findViewById(R.id.contact_loan_id_spinner);

        querySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1) {
                    loanSpinner.setVisibility(View.VISIBLE);
                    findViewById(R.id.contact_loan_id_lable).setVisibility(View.VISIBLE);
                } else {
                    loanSpinner.setVisibility(View.GONE);
                    findViewById(R.id.contact_loan_id_lable).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
