package com.mbv.pokket.fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mbv.pokket.MainActivity;
import com.mbv.pokket.R;
import com.mbv.pokket.dao.enums.RoleType;
import com.mbv.pokket.dao.enums.ServerEvents;
import com.mbv.pokket.dao.interfaces.ServerResponseListener;
import com.mbv.pokket.threads.tasks.AsyncTaskSaveUserRole;
import com.mbv.pokket.util.AppPreferences;

import org.json.simple.JSONObject;

/**
 * Created by arindamnath on 04/01/16.
 */
public class IntroScreenFragment extends Fragment implements ServerResponseListener{

    public final static String LAYOUT_ID = "layoutId";
    private View mViewHolder;
    private LinearLayout base;
    private ImageView image;
    private TextView primaryText, secondaryText;
    private AppPreferences appPreferences;

    public static IntroScreenFragment newInstance(int layoutId) {
        IntroScreenFragment pane = new IntroScreenFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(bundle);
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appPreferences = new AppPreferences(getActivity());

        mViewHolder = inflater.inflate(R.layout.fragment_app_info, container, false);
        base = (LinearLayout) mViewHolder.findViewById(R.id.app_info_holder);
        image = (ImageView) mViewHolder.findViewById(R.id.app_info_image);
        primaryText = (TextView) mViewHolder.findViewById(R.id.app_info_heading);
        secondaryText = (TextView) mViewHolder.findViewById(R.id.app_info_desc);

        switch (getArguments().getInt(LAYOUT_ID)) {
            case 0:
                image.setImageResource(R.drawable.ic_logo);
                primaryText.setText(getString(R.string.screen1_title));
                secondaryText.setText(getString(R.string.screen1_desc));
                break;
            case 1:
                image.setImageResource(R.drawable.ic_student);
                primaryText.setText(getString(R.string.screen2_title));
                secondaryText.setText(getString(R.string.screen2_desc));
                break;
            case 2:
                image.setImageResource(R.drawable.ic_lend);
                primaryText.setText(getString(R.string.screen3_title));
                secondaryText.setText(getString(R.string.screen3_desc));
                break;
            case 3:
                mViewHolder.findViewById(R.id.intro_info_holder).setVisibility(View.GONE);
                mViewHolder.findViewById(R.id.intro_selector_holder).setVisibility(View.VISIBLE);
                break;
        }

        if(getArguments().getInt(LAYOUT_ID) != 0
                || getArguments().getInt(LAYOUT_ID) != 3) {
            Palette.from(((BitmapDrawable) image.getDrawable()).getBitmap())
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch vibrantSwatch = null;
                            if(getArguments().getInt(LAYOUT_ID) == 1) {
                                vibrantSwatch = palette.getMutedSwatch();
                            } else {
                                vibrantSwatch = palette.getDarkVibrantSwatch();
                            }
                            if (vibrantSwatch != null) {
                                base.setBackgroundColor(vibrantSwatch.getRgb());
                            }
                        }
                    });
        }

        mViewHolder.findViewById(R.id.intro_borrower_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appPreferences.setUserRole(RoleType.BORROW.toString());
                new AsyncTaskSaveUserRole(1, getContext(), IntroScreenFragment.this).execute(new JSONObject[0]);
            }
        });

        mViewHolder.findViewById(R.id.intro_lender_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appPreferences.setUserRole(RoleType.LEND.toString());
                new AsyncTaskSaveUserRole(1, getContext(), IntroScreenFragment.this).execute(new JSONObject[0]);
            }
        });
        return mViewHolder;
    }

    @Override
    public void onSuccess(int threadId, Object object) {
        appPreferences.setSignUpStep(0);
        appPreferences.setSignUpComplete();
        appPreferences.setLoggedIn();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void onFaliure(ServerEvents serverEvents, Object object) {

    }
}
