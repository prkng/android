package ng.prk.prkngandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

import ng.prk.prkngandroid.Const;
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

    protected MaterialEditText vEmail;
    protected MaterialEditText vPassword;

    public static Intent newIntent(Context context, String email) {
        final Intent intent = new Intent(context, LoginEmailActivity.class);

        if (email != null && !email.isEmpty()) {
            final Bundle bundle = new Bundle();
            bundle.putString(Const.BundleKeys.EMAIL, email);
            intent.putExtras(bundle);
        }

        return intent;
    }

    protected int getLayoutResource() {
        return R.layout.activity_email_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResource());

        vEmail = (MaterialEditText) findViewById(R.id.email);
        vPassword = (MaterialEditText) findViewById(R.id.password);

        final String email = getIntent().getStringExtra(Const.BundleKeys.EMAIL);
        if (email != null) {
            vEmail.setText(email);
        }

        setListeners();
        addValidators();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login_email, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            final String email = EditTextUtils.getText(vEmail);

            startActivity(LoginForgotPasswordActivity.newIntent(this,
                            AuthValidation.isValidEmail(email) ? email : null)
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            // return true for invalid value, to block action
            return !((MaterialEditText) v).validate();
        } else if (actionId == EditorInfo.IME_ACTION_GO) {
            if (((MaterialEditText) v).validate()) {
                submitForm();
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
                submitForm();
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

    protected void submitForm() {
        // Hide the keyboard
        EditTextUtils.hideKeyboard(this);

        if (vEmail.validate() && vPassword.validate()) {
            final String email = EditTextUtils.getText(vEmail);
            final String password = EditTextUtils.getText(vPassword);

            new LoginTask().execute(new LoginUser(email, password));
        }
    }

    private void showLoginErrorMessage() {
        Snackbar.make(findViewById(R.id.root_view),
                R.string.snackbar_login_fail,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vEmail.setText(null);
                        vEmail.requestFocus();
                    }
                })
                .show();
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
            }

            return null;
        }

        @Override
        protected void onPostExecute(LoginObject loginObject) {
            try {
                if (error != null) {
                    if (error.isUnauthorized()) {
                        showLoginErrorMessage();
                    } else {
                        error.showSnackbar(findViewById(R.id.root_view));
                    }
                } else {
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

    protected class LoginUser {
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
