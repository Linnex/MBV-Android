package com.mbv.pokket.dao;

import android.support.v4.app.Fragment;

/**
 * Created by arindamnath on 05/01/16.
 */
public class PagerFragment {

    private Fragment fragment;

    private String name;

    public PagerFragment(Fragment fragment, String name) {
        this.fragment = fragment;
        this.name = name;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getName() {
        return name;
    }
}
