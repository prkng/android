package ng.prk.prkngandroid.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.TypefaceHelper;

public class MapTabLayout extends TabLayout {

    private Typeface typeface;
    public MapTabLayout(Context context) {
        this(context, null);
    }

    public MapTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typeface = TypefaceHelper.getTypeface(context, Const.TypeFaces.REGULAR);
    }

    @Override
    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        super.addTab(tab, position, setSelected);

        final ViewGroup mainView = (ViewGroup) getChildAt(0);
        final ViewGroup tabView = (ViewGroup) mainView.getChildAt(position);

        int tabChildCount = tabView.getChildCount();
        for (int i = 0; i < tabChildCount; i++) {
            View tabViewChild = tabView.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                ((TextView) tabViewChild).setTypeface(typeface);
            }
        }
    }
}
