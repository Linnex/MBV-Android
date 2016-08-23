package com.mbv.pokket;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mbv.pokket.adapters.ScreenSlideAdapter;
import com.mbv.pokket.util.CrossfadePageTransformer;

/**
 * Created by arindamnath on 04/01/16.
 */
public class ActivityAppIntro extends AppCompatActivity {

    private static final int TOTAL_PAGES = 5;

    private ImageButton btnSkip, btnNext;
    private ViewPager viewpager;
    private PagerAdapter pagerAdapter;
    private LinearLayout circles;
    private boolean isOpaque = true;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_translate);
        setContentView(R.layout.activity_app_intro);

        pagerAdapter = new ScreenSlideAdapter(getSupportFragmentManager(), TOTAL_PAGES);
        mTracker = ((MBVApplication) getApplication()).getTracker("mPokket Intro Activtiy");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        btnSkip = (ImageButton)findViewById(R.id.btn_previous);
        btnNext = (ImageButton)findViewById(R.id.btn_next);
        viewpager = (ViewPager) findViewById(R.id.pager);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItem(viewpager.getCurrentItem() - 1, true);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItem(viewpager.getCurrentItem() + 1, true);
            }
        });

        viewpager.setAdapter(pagerAdapter);
        viewpager.setPageTransformer(true, new CrossfadePageTransformer());
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == TOTAL_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        viewpager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        viewpager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if(position == 0) {
                    btnSkip.setVisibility(View.INVISIBLE);
                } else if (position == TOTAL_PAGES - 2) {
                    btnNext.setVisibility(View.INVISIBLE);
                } else if (position < TOTAL_PAGES - 2) {
                    btnSkip.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewpager != null) {
            viewpager.clearOnPageChangeListeners();
        }
    }

    private void buildCircles() {
        circles = (LinearLayout)findViewById(R.id.circles);
        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);
        for (int i = 0; i < TOTAL_PAGES - 1; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.mipmap.ic_page_indicator);
            circle.setLayoutParams(new ViewGroup.LayoutParams((int) (20 * scale), (int) (20 * scale)));
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }
        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < TOTAL_PAGES) {
            for (int i = 0; i < TOTAL_PAGES - 1; i++) {
                ImageView circle = (ImageView) circles.getChildAt(i);
                if (i == index) {
                    circle.setColorFilter(getResources().getColor(android.R.color.darker_gray));
                } else {
                    circle.setColorFilter(getResources().getColor(android.R.color.white));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (viewpager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
        }
    }
}
