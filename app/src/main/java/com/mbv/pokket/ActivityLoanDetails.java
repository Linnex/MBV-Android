package com.mbv.pokket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.adapters.RepaymentListBaseAdapter;
import com.mbv.pokket.dao.LoanDetailsDAO;
import com.mbv.pokket.dao.enums.LoaderID;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.enums.Status;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.dialogs.DialogLoanSuccess;
import com.mbv.pokket.threads.loaders.LoaderLoanDetail;
import com.mbv.pokket.threads.tasks.AsyncTaskAcceptLoan;
import com.mbv.pokket.threads.tasks.AsyncTaskApproveLenderRequest;

import org.json.simple.JSONObject;

/**
 * Created by arindamnath on 13/01/16.
 */
public class ActivityLoanDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<LoanDetailsDAO>, ServerResponseListener {

    private View listFooter;
    private RepaymentListBaseAdapter repaymentListBaseAdapter;
    private ListView listView;
    private ProgressDialog progressDialog;
    private Bundle queryData;
    private boolean lenderApprove = false;

    private TextView refId, borrowerName, lenderName, loanAmt, status, loanTenure, issuedDate;
    private TextView dibursementAmount, footerTax, footerLoyaltyFee, footerAppUsage, footerTotal;
    private FloatingActionButton approveLoan, repayLoan;

    private long borrowerId, lenderId, amount;

    private Tracker mTracker;
    private DialogLoanSuccess dialogLoanSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repaymentListBaseAdapter = new RepaymentListBaseAdapter(this);
        dialogLoanSuccess = new DialogLoanSuccess(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Content...");
        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Loan Details");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        listView = (ListView) findViewById(R.id.loan_details_list);
        borrowerName = (TextView) findViewById(R.id.loan_details_borrower);
        lenderName = (TextView) findViewById(R.id.loan_details_lender);
        loanAmt = (TextView) findViewById(R.id.loan_details_total_amount);
        status = (TextView) findViewById(R.id.loan_details_status);
        loanTenure = (TextView) findViewById(R.id.loan_details_tenure);
        issuedDate = (TextView) findViewById(R.id.loan_details_issue_date);
        refId = (TextView) findViewById(R.id.loan_details_ref_id);
        approveLoan = (FloatingActionButton) findViewById(R.id.loan_details_approve);
        repayLoan = (FloatingActionButton) findViewById(R.id.loan_details_repayment);

        listFooter = getLayoutInflater().inflate(R.layout.content_payment_footer, null);
        dibursementAmount = (TextView) listFooter.findViewById(R.id.content_borrow_disbursement_amount);
        footerTax = (TextView) listFooter.findViewById(R.id.content_borrow_tax_amount);
        footerLoyaltyFee = (TextView) listFooter.findViewById(R.id.content_borrow_loyalty_fee);
        footerAppUsage  = (TextView) listFooter.findViewById(R.id.content_borrow_other_charges);
        footerTotal = (TextView) listFooter.findViewById(R.id.content_borrow_total_loan_amount);

        borrowerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(ActivityLoanDetails.this, ActivityViewUserProfile.class);
                user.putExtra("userId", borrowerId);
                startActivity(user);
            }
        });

        lenderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lenderId > 0) {
                    Intent user = new Intent(ActivityLoanDetails.this, ActivityViewUserProfile.class);
                    user.putExtra("userId", lenderId);
                    startActivity(user);
                }
            }
        });

        approveLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lenderApprove) {
                    JSONObject data = new JSONObject();
                    data.put("loanId", getIntent().getLongExtra("loanId", -1l));
                    data.put("amount", amount);
                    data.put("note", "");
                    new AsyncTaskAcceptLoan(1, v.getContext(), ActivityLoanDetails.this)
                            .execute(new JSONObject[]{data});
                } else {
                    JSONObject data = new JSONObject();
                    data.put("loanId", getIntent().getLongExtra("loanId", -1l));
                    new AsyncTaskApproveLenderRequest(2, v.getContext(), ActivityLoanDetails.this)
                            .execute(new JSONObject[]{data});
                }
            }
        });

        repayLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        listView.setAdapter(repaymentListBaseAdapter);
        listView.addFooterView(listFooter);

        queryData = new Bundle();
        queryData.putLong("loanId", getIntent().getLongExtra("loanId", -1l));
        getSupportLoaderManager().initLoader(LoaderID.OPEN_TRANSACTIONS.getValue(), queryData, this).forceLoad();
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
    public Loader<LoanDetailsDAO> onCreateLoader(int id, Bundle args) {
        progressDialog.show();
        return new LoaderLoanDetail(this, args);
    }

    @Override
    public void onLoadFinished(Loader<LoanDetailsDAO> loader, LoanDetailsDAO data) {
        progressDialog.dismiss();
        if(data != null) {
            refId.setText(data.getLoanId());
            borrowerId = data.getBorrowerId();
            borrowerName.setText(data.getBorrowerName());
            if(data.getLenderName() != null) {
                lenderId = data.getLenderId();
                lenderName.setText(data.getLenderName());
            }
            amount = data.getLoanAmount();
            loanAmt.setText(data.getLoanAmt());
            status.setText(data.getStatus());
            loanTenure.setText(data.getLoanTenure());
            issuedDate.setText(data.getIssuedDate());
            repaymentListBaseAdapter.setData(data.getRepaymentDAO());
            footerTax.setText(data.getServiceTax());
            footerLoyaltyFee.setText(data.getInterest());
            footerAppUsage.setText(data.getAppUsageFee());
            if(data.getRoleType() != null) {
                switch (data.getRoleType()) {
                    case ALL:
                        break;
                    case BORROW:
                        footerTotal.setText(data.getLoanAmt());
                        if(Status.valueOf(data.getStatus()) == Status.PENDING) {
                            approveLoan.setVisibility(View.VISIBLE);
                        } else if(Status.valueOf(data.getStatus()) == Status.ACTIVE) {
                            repayLoan.setVisibility(View.VISIBLE);
                        }
                        break;
                    case LEND:
                        dibursementAmount.setText(data.getLoanAmt());
                        footerTotal.setText(data.getLenderTotalAmt());
                        listFooter.findViewById(R.id.content_borrow_loyalty_fee_holder)
                                .setVisibility(View.VISIBLE);
                        listFooter.findViewById(R.id.content_borrow_tax_holder)
                                .setVisibility(View.GONE);
                        listFooter.findViewById(R.id.content_borrow_other_holder)
                                .setVisibility(View.GONE);
                        break;
                }
            } else {
                dibursementAmount.setText(data.getLoanAmt());
                footerTotal.setText(data.getLenderTotalAmt());
                listFooter.findViewById(R.id.content_borrow_loyalty_fee_holder)
                        .setVisibility(View.VISIBLE);
                listFooter.findViewById(R.id.content_borrow_tax_holder)
                        .setVisibility(View.GONE);
                listFooter.findViewById(R.id.content_borrow_other_holder)
                        .setVisibility(View.GONE);
                approveLoan.setVisibility(View.VISIBLE);
                lenderApprove = true;
                /*new ShowcaseView.Builder(this)
                        .setTarget(new ViewTarget(approveLoan))
                        .withMaterialShowcase()
                        .setStyle(R.style.CustomShowcaseTheme)
                        .setContentTitle("Approving Requests")
                        .setContentText("Click on the button to approve a borrow request.")
                        .hideOnTouchOutside()
                        .build();*/
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<LoanDetailsDAO> loader) {

    }

    @Override
    public void onSuccess(int threadId, Object object) {
        if(threadId == 1) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("app_event")
                    .setAction("loan_accept")
                    .setLabel("Lender accepted loan")
                    .build());
        } else if (threadId == 2) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("app_event")
                    .setAction("loan_borrower_accept")
                    .setLabel("Borrower accepted lender request.")
                    .build());
        }
        dialogLoanSuccess.show();
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {
        Snackbar.make(refId, object.toString(), Snackbar.LENGTH_LONG).show();
    }
}
