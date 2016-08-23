package com.mbv.pokket.util;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.mbv.pokket.R;

/**
 * Created by arindamnath on 04/01/16.
 */
public class CrossfadePageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();

        View backgroundView = page.findViewById(R.id.app_info_holder);
        View centerImg = page.findViewById(R.id.app_info_image);
        View text_head = page.findViewById(R.id.app_info_heading);
        View text_content = page.findViewById(R.id.app_info_desc);

        if (0 <= position && position < 1) {
            page.setTranslationX(pageWidth * -position);
        }
        if (-1 < position && position < 0) {
            page.setTranslationX(pageWidth * -position);
        }

        if (position <= -1.0f || position >= 1.0f) {

        } else if (position == 0.0f) {

        } else {
            if (backgroundView != null) {
                backgroundView.setAlpha(1.0f - Math.abs(position));
            }

            if(centerImg != null){
                centerImg.setTranslationX(pageWidth * position);
                centerImg.setAlpha(1.0f - Math.abs(position));
            }

            if (text_head != null) {
                text_head.setTranslationX(pageWidth * position);
                text_head.setAlpha(1.0f - Math.abs(position));
            }

            if (text_content != null) {
                text_content.setTranslationX(pageWidth * position);
                text_content.setAlpha(1.0f - Math.abs(position));
            }
        }
    }
}
