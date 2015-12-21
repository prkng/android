package ng.prk.prkngandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.adapter.TutorialPagerAdapter;
import ng.prk.prkngandroid.util.CrossfadePageTransformer;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TutorialActivity";

    private final static int NUM_PAGES = Const.TutorialSections._COUNT - 1;

    private ViewPager mPager;
    private View vSkip;
    private View vNext;
    private View vDone;
    private LinearLayout vCircles;
    private boolean mIsOpaque = true;


    public static Intent newIntent(Context context, boolean isInitial) {
        final Intent intent = new Intent(context, TutorialActivity.class);

        final Bundle bundle = new Bundle();
        bundle.putBoolean(Const.BundleKeys.IS_INITIAL_ONBOARDING, isInitial);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);

        vSkip = findViewById(R.id.skip);
        vNext = findViewById(R.id.next);
        vDone = findViewById(R.id.done);

        vSkip.setOnClickListener(this);
        vNext.setOnClickListener(this);
        vDone.setOnClickListener(this);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new TutorialPagerAdapter(getSupportFragmentManager(), isInitial()));
        mPager.setPageTransformer(true, new CrossfadePageTransformer());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //See note above for why this is needed
                if (position == NUM_PAGES - 1 && positionOffset > 0) {
                    if (mIsOpaque) {
                        mPager.setBackgroundColor(Color.TRANSPARENT);
                        mIsOpaque = false;
                    }
                } else {
                    if (!mIsOpaque) {
                        mPager.setBackgroundColor(getResources().getColor(R.color.tutorial_background_opaque));
                        mIsOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);

                if (position < NUM_PAGES - 1) {
                    vSkip.setVisibility(View.VISIBLE);
                    vNext.setVisibility(View.VISIBLE);
                    vDone.setVisibility(View.GONE);
                } else if (position == NUM_PAGES - 1) {
                    vSkip.setVisibility(View.GONE);
                    vNext.setVisibility(View.GONE);
                    vDone.setVisibility(View.VISIBLE);
                } else if (position == NUM_PAGES) {
                    endTutorial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        vCircles = (LinearLayout) findViewById(R.id.circles);
        buildCircles(vCircles);
    }

    @Override
    public void onClick(View v) {
        if (v == vNext) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
        } else if (v == vSkip || v == vDone) {
            endTutorial();
        }
    }

    /**
     * Verify if app's first launch or started from About screen.
     *
     * @return false for About's product-tour
     */
    private boolean isInitial() {
        try {
            return getIntent().getBooleanExtra(Const.BundleKeys.IS_INITIAL_ONBOARDING, false);
        } catch (NullPointerException e) {
            return false;
        }
    }


    /**
     * The last fragment is transparent to enable the swipe-to-finish behaviour seen on
     * Google's apps. So our viewpager circle indicator needs to show NUM_PAGES - 1
     * Source: https://gist.github.com/fiskurgit
     */
    private void buildCircles(ViewGroup parent) {

        final float scale = getResources().getDisplayMetrics().density;
        final int padding = (int) (5 * scale + 0.5f);

        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.circle);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            parent.addView(circle);
        }

        setIndicator(0);
    }

    /**
     * Update current circle
     *
     * @param index
     */
    private void setIndicator(int index) {
        if (index >= NUM_PAGES) {
            return;
        }

        for (int i = 0; i < NUM_PAGES; i++) {
            ((ImageView) vCircles.getChildAt(i)).setImageResource(
                    i == index ? R.drawable.circle_selected : R.drawable.circle
            );
        }
    }

    /**
     * Fade-out on exit
     */
    private void endTutorial() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
