package ng.prk.prkngandroid.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.TypefaceHelper;

public class IntroBoldButton extends Button {

    public IntroBoldButton(Context context) {
        this(context, null);
    }

    public IntroBoldButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public IntroBoldButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initFontFace(context);
    }

    private void initFontFace(Context context) {
        final Typeface face = TypefaceHelper.getTypeface(context, Const.TypeFaces.BOLD);

        setTypeface(face);
    }
}
