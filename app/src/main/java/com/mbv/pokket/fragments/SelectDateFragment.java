package com.mbv.pokket.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by arindamnath on 28/02/16.
 */
public class SelectDateFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int yy, mm, dd;
        Calendar calendar = Calendar.getInstance();
        try {
            if (savedInstanceState.getLong("date", -1l) != -1l) {
                calendar.setTimeInMillis(savedInstanceState.getLong("date", -1l));
            }
        } catch (Exception e) {

        }
        yy = calendar.get(Calendar.YEAR);
        mm = calendar.get(Calendar.MONTH);
        dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    @Override
    public void onDateSet(DatePicker view, int yy, int mm, int dd) {

    }
}
