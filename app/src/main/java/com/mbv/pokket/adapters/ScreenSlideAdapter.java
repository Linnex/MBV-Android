package com.mbv.pokket.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mbv.pokket.fragments.IntroScreenFragment;

/**
 * Created by arindamnath on 04/01/16.
 */
public class ScreenSlideAdapter extends FragmentStatePagerAdapter {

    private int numPages;

    public ScreenSlideAdapter(FragmentManager fm, int numPages) {
        super(fm);
        this.numPages = numPages;
    }

    @Override
    public Fragment getItem(int position) {
        return new IntroScreenFragment().newInstance(position);
    }

    @Override
    public int getCount() {
        return numPages;
    }
}