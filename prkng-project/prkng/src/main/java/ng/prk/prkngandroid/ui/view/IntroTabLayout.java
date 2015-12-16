package ng.prk.prkngandroid.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.TypefaceHelper;

public class IntroTabLayout extends TabLayout {

    private Typeface typeface;
    public IntroTabLayout(Context context) {
        this(context, null);
    }

    public IntroTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IntroTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
Log.v("IntroTabLayout", "constructor");
        typeface = TypefaceHelper.getTypeface(context, Const.TypeFaces.BOOK);
    }

    @Override
    public void addTab(Tab tab) {
        super.addTab(tab);
        Log.v("addTab", "constructor");

        ViewGroup mainView = (ViewGroup) getChildAt(0);
        ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());

        int tabChildCount = tabView.getChildCount();
        for (int i = 0; i < tabChildCount; i++) {
            View tabViewChild = tabView.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                ((TextView) tabViewChild).setTypeface(typeface);
            }
        }
    }
}
