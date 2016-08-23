package com.mbv.pokket.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mbv.pokket.R;
import com.mbv.pokket.adapters.RepaymentListBaseAdapter;
import com.mbv.pokket.dao.RepaymentDAO;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.dialogs.DialogLenderEligibility;
import com.mbv.pokket.threads.tasks.AsyncTackApplyLoan;
import com.mbv.pokket.threads.tasks.AsyncTaskCheckEligibility;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by arindamnath on 12/01/16.
 */
public class BorrowFragment extends Fragment implements ServerResponseListener {

    private View listHead, listFooter;
    private View mViewHolder, selectorContainer, quoteContainer;
    private RepaymentListBaseAdapter repaymentListBaseAdapter;
    private List<RepaymentDAO> repaymentDAOList = new ArrayList<>();
    private Spinner timeSpinner;
    private EditText noteEditText, codeEditText;
    private TextView principalAmount, loanAmount, tenure, note, referralCode;
    private TextView footerTax, footerAppUsage, footerTotal;
    private ListView borrowDetails;
    private DialogLenderEligibility dialogLenderEligibility;
    private boolean isEligible;

    private String amount, period;

    public static BorrowFragment newInstance() {
        BorrowFragment fragment = new BorrowFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        repaymentListBaseAdapter = new RepaymentListBaseAdapter(getActivity());
        dialogLenderEligibility = new DialogLenderEligibility(getContext(), false);

        mViewHolder = inflater.inflate(R.layout.fragment_borrow, container, false);
        selectorContainer = mViewHolder.findViewById(R.id.borrow_selector_container);
        quoteContainer = mViewHolder.findViewById(R.id.borrow_quote_container);
        listHead = inflater.inflate(R.layout.content_borrow_header, null);
        listFooter = inflater.inflate(R.layout.content_payment_footer, null);

        borrowDetails = (ListView) mViewHolder.findViewById(R.id.borrow_repayment_list);
        principalAmount = (TextView) mViewHolder.findViewById(R.id.borrow_amount);
        timeSpinner = (Spinner) mViewHolder.findViewById(R.id.borrow_teneure);
        noteEditText = (EditText) mViewHolder.findViewById(R.id.borrow_user_note);
        codeEditText = (EditText) mViewHolder.findViewById(R.id.borrow_user_referral_code);
        loanAmount = (TextView) listHead.findViewById(R.id.content_borrow_requested_amount);
        tenure = (TextView) listHead.findViewById(R.id.content_borrow_requested_tenure);
        note = (TextView) listHead.findViewById(R.id.content_borrow_requested_note);
        referralCode = (TextView) listHead.findViewById(R.id.content_borrow_requested_referral_code);
        footerTax = (TextView) listFooter.findViewById(R.id.content_borrow_tax_amount);
        footerAppUsage  = (TextView) listFooter.findViewById(R.id.content_borrow_other_charges);
        footerTotal = (TextView) listFooter.findViewById(R.id.content_borrow_total_loan_amount);

        mViewHolder.findViewById(R.id.borrow_get_quote_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEligible) {
                    layoutToggle(false);
                    loanAmount.setText(principalAmount.getText());
                    tenure.setText(timeSpinner.getSelectedItem().toString());
                    if (noteEditText.getText().toString().trim().length() > 0) {
                        note.setText(noteEditText.getText().toString().trim());
                    } else {
                        note.setText(R.string.na);
                    }
                    if (codeEditText.getText().toString().trim().length() > 0) {
                        referralCode.setText(codeEditText.getText().toString().trim());
                    } else {
                        referralCode.setText(R.string.na);
                    }
                }
            }
        });

        listHead.findViewById(R.id.content_borrow_edit_quote_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutToggle(true);
                        noteEditText.getText().clear();
                    }
                });

        mViewHolder.findViewById(R.id.borrow_submit_loan_request)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject data = new JSONObject();
                        data.put("amount", amount);
                        data.put("period", period);
                        data.put("note", noteEditText.getText().toString());
                        new AsyncTackApplyLoan(1, v.getContext(), BorrowFragment.this)
                                .execute(new JSONObject[]{data});
                    }
                });

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repaymentDAOList.clear();
                RepaymentDAO repaymentDAO = new RepaymentDAO(getActivity());
                Calendar calendar = Calendar.getInstance();
                switch (position) {
                    case 0:
                        principalAmount.setText(getString(R.string.rupee) + "550");
                        footerTax.setText(getString(R.string.rupee) + "6.5");
                        footerAppUsage.setText(getString(R.string.rupee) + "43.5");
                        footerTotal.setText(getString(R.string.rupee) + "550");
                        amount = "550";
                        period = "1";
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                        repaymentDAO.setId(1l);
                        repaymentDAO.setAmount(550l);
                        repaymentDAO.setDate(calendar.getTimeInMillis());
                        break;
                    case 1:
                        principalAmount.setText(getString(R.string.rupee) + "590");
                        footerTax.setText(getString(R.string.rupee) + "11.7");
                        footerAppUsage.setText(getString(R.string.rupee) + "78.3");
                        footerTotal.setText(getString(R.string.rupee) + "590");
                        amount = "590";
                        period = "2";
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 2);
                        repaymentDAO.setId(1l);
                        repaymentDAO.setAmount(590l);
                        repaymentDAO.setDate(calendar.getTimeInMillis());
                        break;
                    case 2:
                        principalAmount.setText(getString(R.string.rupee) + "620");
                        footerTax.setText(getString(R.string.rupee) + "15.7");
                        footerAppUsage.setText(getString(R.string.rupee) + "104.3");
                        footerTotal.setText(getString(R.string.rupee) + "620");
                        amount = "620";
                        period = "3";
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 3);
                        repaymentDAO.setId(1l);
                        repaymentDAO.setAmount(620l);
                        repaymentDAO.setDate(calendar.getTimeInMillis());
                        break;
                }
                repaymentDAOList.add(repaymentDAO);
                repaymentListBaseAdapter.setData(repaymentDAOList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        borrowDetails.setAdapter(repaymentListBaseAdapter);
        borrowDetails.addHeaderView(listHead);
        borrowDetails.addFooterView(listFooter);

        new AsyncTaskCheckEligibility(1, getContext(), new ServerResponseListener() {
            @Override
            public void onSuccess(int threadId, Object object) {
                isEligible = true;
            }

            @Override
            public void onFaliure(ServerEvents serverEvents, Object object) {
                dialogLenderEligibility.show();
                Snackbar.make(mViewHolder, object.toString(), Snackbar.LENGTH_LONG).show();
            }
        }).execute(new JSONObject[0]);
        return mViewHolder;
    }

    @Override
    public void onResume() {
        super.onResume();
        layoutToggle(true);
    }

    private void layoutToggle(boolean isSelectorLayout) {
        if(isSelectorLayout) {
            selectorContainer.animate()
                    .setDuration(500)
                    .translationY(0);
            quoteContainer.animate()
                    .setDuration(750)
                    .translationY(quoteContainer.getHeight())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            quoteContainer.setVisibility(View.GONE);
                        }
                    });
        } else {
            selectorContainer.animate()
                    .setDuration(500)
                    .translationY(-selectorContainer.getHeight());
            quoteContainer.animate()
                    .setDuration(750)
                    .translationY(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            quoteContainer.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    @Override
    public void onSuccess(int threadId, Object object) {
        layoutToggle(true);
        noteEditText.getText().clear();
        Snackbar.make(mViewHolder, "Your loan request is now active.", Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }
}
