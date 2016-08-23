package com.mbv.pokket.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.mbv.pokket.ActivityProfileSettings;
import com.mbv.pokket.R;

/**
 * Created by arindamnath on 01/04/16.
 */
public class DialogLenderEligibility {

    private Dialog dialog;
    private TextView userText;

    public DialogLenderEligibility(final Context context, boolean isLender) {
        dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_eligibility);

        userText = (TextView) dialog.findViewById(R.id.eligibility_text);
        if(isLender) {
            userText.setText("Sorry but you don't qualify for these services yet. May be try providing extra information and verify your bank account details.");
        } else {
            userText.setText("Sorry but we don't have enough information to process your request! May be try providing extra information and verify your bank account details.");
        }

        dialog.findViewById(R.id.eligibility_update_profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ActivityProfileSettings.class));
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
