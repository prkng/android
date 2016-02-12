package ng.prk.prkngandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.ui.view.RedSnackbar;
import ng.prk.prkngandroid.util.AnalyticsUtils;
import ng.prk.prkngandroid.util.AuthValidation;
import ng.prk.prkngandroid.util.EditTextUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class LoginSignUpActivity extends LoginEmailActivity implements
        View.OnFocusChangeListener,
        TextView.OnEditorActionListener {
    private static final String TAG = "LoginSignUpActivity";

    private MaterialEditText vName;

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginSignUpActivity.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vName = (MaterialEditText) findViewById(R.id.name);

        setListeners();
        addValidators();
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsUtils.sendActivityView(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == Const.RequestCodes.AUTH_LOGIN_EMAIL)
                && (resultCode == Activity.RESULT_OK)) {
            setResult(Activity.RESULT_OK);
            this.finish();
        }
    }

    private void setListeners() {
        vName.setOnEditorActionListener(this);

        findViewById(R.id.btn_login_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();
            }
        });
    }

    private void startLoginActivity() {
        final String email = EditTextUtils.getText(vEmail);
        startActivityForResult(LoginEmailActivity.newIntent(LoginSignUpActivity.this,
                        AuthValidation.isValidEmail(email) ? email : null),
                Const.RequestCodes.AUTH_LOGIN_EMAIL
        );
    }

    private void addValidators() {
        vName.addValidator(new METValidator(
                getResources().getString(R.string.auth_error_invalid_name_short)) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty && AuthValidation.isValidName(text.toString());
            }
        });
    }

    @Override
    protected void submitForm() {
        // Hide the keyboard
        EditTextUtils.hideKeyboard(this);

        if (vName.validate() && vEmail.validate() && vPassword.validate()) {
            final String name = EditTextUtils.getText(vName);
            final String email = EditTextUtils.getText(vEmail);
            final String password = EditTextUtils.getText(vPassword);

            new SignUpTask().execute(new SignUpUser(name, email, password));
        }
    }

    private void showSignUpErrorMessage() {
        RedSnackbar.make(findViewById(R.id.root_view),
                R.string.snackbar_sign_up_fail,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_login, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLoginActivity();
                    }
                })
                .show();
    }

    private class SignUpTask extends AsyncTask<SignUpUser, Void, LoginObject> {
        private PrkngApiError error = null;

        @Override
        protected LoginObject doInBackground(SignUpUser... params) {
            try {
                final SignUpUser signUpUser = params[0];

                if (signUpUser != null) {
                    return ApiClient
                            .registerUser(
                                    ApiClient.getService(),
                                    signUpUser.getName(),
                                    signUpUser.getEmail(),
                                    signUpUser.getPasswd());
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
                    if (error.isNotFound() || error.isConflict()) {
                        showSignUpErrorMessage();
                    } else {
                        error.showSnackbar(findViewById(R.id.root_view));
                    }
                } else {
                    PrkngPrefs
                            .getInstance(LoginSignUpActivity.this)
                            .setAuthUser(loginObject);

                    setResult(Activity.RESULT_OK);
                    finish();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    protected class SignUpUser extends LoginUser {
        public final String name;

        public SignUpUser(String name, String email, String passwd) {
            super(email, passwd);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
