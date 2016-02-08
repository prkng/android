package ng.prk.prkngandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.util.AnalyticsUtils;
import ng.prk.prkngandroid.util.AuthValidation;
import ng.prk.prkngandroid.util.EditTextUtils;

public class LoginForgotPasswordActivity extends AppCompatActivity implements
        TextView.OnEditorActionListener {
    private static final String TAG = "ForgotPassActivity";

    private MaterialEditText vEmail;
    private ViewGroup vForm;
    private TextView vMessage;

    public static Intent newIntent(Context context, String email) {
        final Intent intent = new Intent(context, LoginForgotPasswordActivity.class);

        if (email != null && !email.isEmpty()) {
            final Bundle bundle = new Bundle();
            bundle.putString(Const.BundleKeys.EMAIL, email);
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        vEmail = (MaterialEditText) findViewById(R.id.email);
        vForm = (ViewGroup) findViewById(R.id.form_reset);
        vMessage = (TextView) findViewById(R.id.confirm_message);

        final String email = getIntent().getStringExtra(Const.BundleKeys.EMAIL);
        vEmail.setText(email);

        setListeners();
        addValidators();
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsUtils.sendScreenView(this, "LoginForgotPasswordActivity");
    }

    /**
     * Implements OnEditorActionListener
     * Validate fields on IME_ACTION_NEXT or IME_ACTION_DONE
     *
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            if (((MaterialEditText) v).validate()) {
                resetPassword();
            }
        }

        return false;
    }

    private void setListeners() {
        vEmail.setOnEditorActionListener(this);

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void addValidators() {
        vEmail.addValidator(new METValidator(
                getResources().getString(R.string.auth_error_invalid_email)) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty && AuthValidation.isValidEmail(text.toString());
            }
        });
    }

    private void resetPassword() {
        // Hide the keyboard
        EditTextUtils.hideKeyboard(this);

        if (vEmail.validate()) {
            final String email = EditTextUtils.getText(vEmail);
            new PasswordResetTask().execute(email);
        }
    }

    private class PasswordResetTask extends AsyncTask<String, Void, Void> {

        private PrkngApiError error = null;

        @Override
        protected Void doInBackground(String... params) {
            try {
                final String email = params[0];
                if (email != null) {
                    ApiClient.resetUserPassword(
                            ApiClient.getService(),
                            email);
                }
            } catch (PrkngApiError e) {
                error = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (error != null) {
                error.showSnackbar(vForm);
                return;
            }

            try {
                vForm.setVisibility(View.GONE);
                vMessage.setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
