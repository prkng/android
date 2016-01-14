package ng.prk.prkngandroid.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.util.TypefaceHelper;

public class IntroBoldTextView extends TextView {

    public IntroBoldTextView(Context context) {
        this(context, null);
    }

    public IntroBoldTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public IntroBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initFontFace(context);
    }

    private void initFontFace(Context context) {
        final Typeface face = TypefaceHelper.getTypeface(context, Const.TypeFaces.BOLD);

        setTypeface(face);
    }
}
