package ng.prk.prkngandroid.util;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EditTextUtils {

    public static String getText(EditText edit) {
        Editable editable = edit.getText();
        if (editable != null) {
            return editable.toString();
        }

        return "";
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        try {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            // SongComments version:
            // inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        } catch (NullPointerException e) {
            // Keyboard is already hidden
            // e.printStackTrace();
        }
    }
}
