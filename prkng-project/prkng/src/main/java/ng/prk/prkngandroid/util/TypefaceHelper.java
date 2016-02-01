package ng.prk.prkngandroid.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;

public class TypefaceHelper implements Const.TypeFaces {
    private static final String TAG = "TypefaceHelper";

    private static LruCache<String, Typeface> sTypefaceCache =
            new LruCache<String, Typeface>(_COUNT);

    public static Toolbar setTitle(Context context, Toolbar toolbar, CharSequence title) {
        if (toolbar == null) {
            return null;
        }

        toolbar.setTitle(title);
        try {
            final Typeface typeface = getTypeface(context, BOLD);
            final int childCount = toolbar.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View v = toolbar.getChildAt(i);
                if (v instanceof TextView) {
                    ((TextView) v).setTypeface(typeface);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toolbar;
    }

    public static Toolbar setTitle(Context context, Toolbar toolbar, int title) {
        return setTitle(context, toolbar, context.getString(title));
    }

    public static Typeface getTypeface(Context context, String typefaceName) {
        final String typefaceCode = typefaceName.replace("fonts/", "");
        Typeface typeface = sTypefaceCache.get(typefaceCode);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(),
                    typefaceName);

            // Cache the loaded Typeface
            sTypefaceCache.put(typefaceCode, typeface);
        }

        return typeface;
    }
}
