package ng.prk.prkngandroid.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.TypefaceHelper;

public class IntroRegularButton extends Button {

    public IntroRegularButton(Context context) {
        this(context, null);
    }

    public IntroRegularButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public IntroRegularButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initFontFace(context);
    }

    private void initFontFace(Context context) {
        final Typeface face = TypefaceHelper.getTypeface(context, Const.TypeFaces.REGULAR);

        setTypeface(face);
    }
}
