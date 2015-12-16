package ng.prk.prkngandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.util.AuthValidation;
import ng.prk.prkngandroid.util.EditTextUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class LoginEmailActivity extends AppCompatActivity implements
        View.OnFocusChangeListener,
        TextView.OnEditorActionListener {
    private static final String TAG = "LoginEmailActivity";

    private MaterialEditText vEmail;
    private MaterialEditText vPassword;

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginEmailActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_email_login);

        vEmail = (MaterialEditText) findViewById(R.id.email);
        vPassword = (MaterialEditText) findViewById(R.id.password);

        setListeners();
        addValidators();
    }

    /**
     * Implements OnEditorActionListener
     * Validate fields on IME_ACTION_NEXT
     *
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Log.v(TAG, "onEditorAction");

        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            // return true for invalid value, to block action
            return !((MaterialEditText) v).validate();
        } else if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (((MaterialEditText) v).validate()) {
                submitAuthLoginEmail();
            }
        }

        return false;
    }

    /**
     * Implements OnFocusChangeListener
     * Validate fields on focus-loss
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.v(TAG, "onFocusChange");

        if (!hasFocus) {
            ((MaterialEditText) v).validate();
        }
    }

    private void setListeners() {
        vEmail.setOnEditorActionListener(this);
        vPassword.setOnEditorActionListener(this);

        vEmail.setFocusable(true);
        vPassword.setFocusable(true);

        vEmail.setOnFocusChangeListener(this);
        vPassword.setOnFocusChangeListener(this);

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAuthLoginEmail();
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

        vPassword.addValidator(new METValidator(
                getResources().getString(R.string.auth_error_invalid_password_short)) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty && AuthValidation.isValidPassword(text.toString());
            }
        });
    }

    private void submitAuthLoginEmail() {
        // Hide the keyboard
        EditTextUtils.hideKeyboard(this);

        if (vEmail.validate() && vPassword.validate()) {
            final String emailUsername = EditTextUtils.getText(vEmail);
            final String password = EditTextUtils.getText(vPassword);

            new LoginTask().execute(new LoginUser(emailUsername, password));
        }
    }

    private class LoginTask extends AsyncTask<LoginUser, Void, LoginObject> {

        private PrkngApiError error = null;

        @Override
        protected LoginObject doInBackground(LoginUser... params) {
            try {
                final LoginUser loginUser = params[0];

                if (loginUser != null) {
                    return ApiClient
                            .loginEmail(
                                    ApiClient.getServiceLog(),
                                    loginUser.getEmail(),
                                    loginUser.getPasswd());
                }
            } catch (PrkngApiError e) {
                error = e;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(LoginObject loginObject) {
            try {
                if (error != null) {
                    // TODO handle API errors
                    Log.e(TAG, "PrkngApiError");
                    error.printStackTrace();
                } else {
                    Log.v(TAG, "name = " + loginObject.getName() + " email = " + loginObject.getEmail());
//                    Log.v(TAG, "mApiKey = " + loginObject.getApikey());

                    PrkngPrefs
                            .getInstance(LoginEmailActivity.this)
                            .setAuthUser(loginObject);

                    setResult(Activity.RESULT_OK);
                    finish();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoginUser {
        public final String email;
        public final String passwd;

        public LoginUser(String email, String passwd) {
            this.email = email;
            this.passwd = passwd;
        }

        public String getEmail() {
            return email;
        }

        public String getPasswd() {
            return passwd;
        }
    }
}
