package com.mbv.pokket;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.dao.BankCodeDAO;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.threads.tasks.AsyncTaskBankAccounts;
import com.mbv.pokket.threads.tasks.AsyncTaskIFSCCode;

import org.json.simple.JSONObject;

/**
 * Created by arindamnath on 15/03/16.
 */
public class ActivityBankAccountDetails extends AppCompatActivity
        implements ServerResponseListener {

    private AutoCompleteTextView ifscCode;
    private EditText bankAccount, transactionHash;
    private CheckBox isPrimary;
    private Button search;
    private TextView bankName, bankAddress, bankCity, bankDistrict, bankState;
    private Long bankId, id = 0l;
    private boolean isBankFound, isUpdate = false;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_translate);
        setContentView(R.layout.activity_bank_account_detials);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Bank Account Details");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        ifscCode = (AutoCompleteTextView) findViewById(R.id.bank_account_detials_ifsc_code);
        bankAccount  = (EditText) findViewById(R.id.bank_account_detials_account_num);
        transactionHash  = (EditText) findViewById(R.id.bank_account_detials_transaction_pass);
        search = (Button) findViewById(R.id.bank_account_detials_search_bank);
        isPrimary = (CheckBox) findViewById(R.id.bank_account_detials_is_primary);
        bankName = (TextView) findViewById(R.id.bank_account_detials_bank_name);
        bankAddress = (TextView) findViewById(R.id.bank_account_detials_bank_address);
        bankCity = (TextView) findViewById(R.id.bank_account_detials_bank_city);
        bankDistrict = (TextView) findViewById(R.id.bank_account_detials_bank_district);
        bankState = (TextView) findViewById(R.id.bank_account_detials_bank_state);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                data.put("search", ifscCode.getText().toString());
                new AsyncTaskIFSCCode(1, v.getContext(), ActivityBankAccountDetails.this)
                        .execute(new JSONObject[]{data});
            }
        });

        findViewById(R.id.bank_account_continue_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent().getLongExtra("id", -1l) != -1l) {
            id = getIntent().getLongExtra("id", -1l);
            bankId = getIntent().getLongExtra("bankId", -1l);
            ifscCode.setText(getIntent().getStringExtra("ifsc"));
            bankAccount.setText(getIntent().getStringExtra("account"));
            bankName.setText(getIntent().getStringExtra("bankName"));
            bankAddress.setText(getIntent().getStringExtra("address"));
            bankCity.setText(getIntent().getStringExtra("city"));
            bankDistrict.setText(getIntent().getStringExtra("district"));
            bankState.setText(getIntent().getStringExtra("state"));
            isPrimary.setChecked(getIntent().getBooleanExtra("primary", false));
            isBankFound = true;
            isUpdate = true;
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
                if(isBankFound
                        && bankAccount.getText().toString().length() > 0
                        && transactionHash.getText().toString().length() > 0) {
                    JSONObject data = new JSONObject();
                    data.put("hash", transactionHash.getText().toString());
                    data.put("bankAccount", bankAccount.getText().toString());
                    data.put("bankRefId", bankId);
                    data.put("isPrimary", isPrimary.isChecked());
                    new AsyncTaskBankAccounts(id.intValue(), this, this, isUpdate)
                            .execute(new JSONObject[]{data});
                } else {
                    Snackbar.make(bankState, "Information set not complete.", Snackbar.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(int threadId, Object object) {
        if(threadId == 1) {
            if(object != null) {
                isBankFound = true;
                BankCodeDAO bankCodeDAO = (BankCodeDAO) object;
                bankId = bankCodeDAO.getId();
                bankName.setText(bankCodeDAO.getBankName());
                bankAddress.setText(bankCodeDAO.getBankAddress());
                bankCity.setText(bankCodeDAO.getBankCity());
                bankDistrict.setText(bankCodeDAO.getBankDistrict());
                bankState.setText(bankCodeDAO.getBankState());
            }
        } else {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("app_event")
                    .setAction((isUpdate) ? "bank_details_update" : "bank_details_create")
                    .setLabel("Bank details " + ((isUpdate) ? "added." : "updated."))
                    .build());
            findViewById(R.id.bank_account_detials_bank_container).setVisibility(View.GONE);
            findViewById(R.id.bank_account_verfication_container).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }
}
