package com.mbv.pokket.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.mbv.pokket.R;

/**
 * Created by arindamnath on 02/04/16.
 */
public class DialogLoanSuccess {

    private Dialog dialog;

    public DialogLoanSuccess(final Activity activity) {
        dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_loan_success);

        dialog.findViewById(R.id.dialog_loan_success_continue_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
