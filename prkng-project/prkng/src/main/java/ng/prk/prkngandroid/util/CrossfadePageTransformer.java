package ng.prk.prkngandroid.util;

import android.support.v4.view.ViewPager;
import android.view.View;

import ng.prk.prkngandroid.R;


public class CrossfadePageTransformer implements ViewPager.PageTransformer {
    /**
     * Source: http://stackoverflow.com/a/23526632/535915
     */
    @Override
    public void transformPage(View view, float position) {

        final int pageWidth = view.getWidth();
        final View vBackground = view.findViewById(R.id.tutorial_background);
        final View vImage = view.findViewById(R.id.tutorial_image);
        final View vText = view.findViewById(R.id.tutorial_text);

        if (Float.compare(position, -1.0f) < 0) { // [-Infinity,-1)
            // This page is way off-screen to the left
        } else if (Float.compare(position, 0.0f) <= 0) { // [-1,0]
            // This page is moving out to the left

            // Counteract the default swipe
            view.setTranslationX(pageWidth * -position);
            if (vBackground != null) {
                vBackground.setAlpha(1 + position);
            }
            if (vImage != null) {
                // Fade the image in
                vImage.setAlpha(1 + position);
            }
            if (vText != null) {
                // But swipe the contentView
                vText.setTranslationX(pageWidth * position);
            }
        } else if (Float.compare(position, 1.0f) <= 0) { // (0,1]
            // This page is moving in from the right

            // Counteract the default swipe
            view.setTranslationX(pageWidth * -position);
            if (vBackground != null) {
                vBackground.setAlpha(1 - position);
            }
            if (vImage != null) {
                // Fade the image out
                vImage.setAlpha(1 - position);
            }
            if (vText != null) {
                // But swipe the contentView
                vText.setTranslationX(pageWidth * position);
            }
        } else { // (1,+Infinity]
            // This page is way off-screen to the right
        }
    }
}
