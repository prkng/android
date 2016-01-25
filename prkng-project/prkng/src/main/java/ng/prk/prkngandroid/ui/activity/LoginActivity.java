package ng.prk.prkngandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.ui.activity.base.BaseActivity;
import ng.prk.prkngandroid.ui.view.RedSnackbar;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class LoginActivity extends BaseActivity implements
        View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private LoginButton vFacebookButton;
    private SignInButton vGoogleButton;
    private CallbackManager facebookCallbackManager;
    private SocialLoginTask mSocialLoginTask;
    private GoogleApiClient mGoogleApiClient;

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch Onboarding once only
        if (savedInstanceState == null && !isFirstClickFreeIntent(getIntent())) {
            if (PrkngPrefs.getInstance(this).isOnboarding()) {
                startActivityForResult(TutorialActivity.newIntent(this, true), Const.RequestCodes.ONBOARDING);
            }
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        vFacebookButton = (LoginButton) findViewById(R.id.btn_facebook);
        vGoogleButton = (SignInButton) findViewById(R.id.btn_google);

        setupListeners();

        setupFacebookLogin();
        setupGoogleLogin();

        logoutIfNecessary();
    }

    @Override
    protected boolean isLoginRequired() {
        return false;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == R.id.btn_google) {
            startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient),
                    Const.RequestCodes.AUTH_LOGIN_GOOGLE);
        } else if (id == R.id.btn_email) {
            startActivityForResult(LoginSignUpActivity.newIntent(this),
                    Const.RequestCodes.AUTH_LOGIN_EMAIL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult "
                + String.format("requestCode = %s, resultCode = %s", requestCode, resultCode));

        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == Const.RequestCodes.AUTH_LOGIN_EMAIL)
                && (resultCode == Activity.RESULT_OK)) {
            setResult(Activity.RESULT_OK);
            this.finish();
        } else if (requestCode == Const.RequestCodes.AUTH_LOGIN_FACEBOOK) {
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Const.RequestCodes.AUTH_LOGIN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else if (requestCode == Const.RequestCodes.AUTH_LOGIN_GOOGLE_RESOLVE) {
            Log.e(TAG, "onActivityResult() google resolve");
        }
    }

    private void logoutIfNecessary() {
        Log.v(TAG, "logoutIfNecessary");

        PrkngPrefs.getInstance(this).setAuthUser(null);
        LoginManager.getInstance().logOut();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.v(TAG, "isConnected");


        }
    }

    private void setupListeners() {
        findViewById(R.id.btn_google).setOnClickListener(this);
        findViewById(R.id.btn_email).setOnClickListener(this);
    }

    private void setupGoogleLogin() {
        vGoogleButton.setColorScheme(SignInButton.COLOR_LIGHT);

        final String serverClientId = getResources().getString(R.string.oauth_web_client_id);
        final GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestIdToken(serverClientId)
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(Bundle bundle) {
//                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                                new ResultCallback<Status>() {
//                                    @Override
//                                    public void onResult(Status status) {
//                                        Log.v(TAG, "onResult "
//                                                + String.format("status = %s", status));
//                                    }
//                                }
//                        );
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int i) {
//
//                    }
//                })
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.v(TAG, "onConnectionFailed");
                    }
                })
                .setAccountName(null)

                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        vGoogleButton.setScopes(gso.getScopeArray());
    }

    private void setupFacebookLogin() {
        facebookCallbackManager = CallbackManager.Factory.create();

        vFacebookButton.setReadPermissions(getResources().getString(R.string.facebook_permissions));
        vFacebookButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final String token = loginResult.getAccessToken().getToken();

                runSocialLoginTask(Const.ApiValues.OAUTH_TYPE_FACEBOOK, token);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                try {
                    final String message = String.format(
                            getResources().getString(R.string.snackbar_auth_error_facebook),
                            error.getMessage()
                    );
                    RedSnackbar.make(findViewById(R.id.root_view),
                            message,
                            Snackbar.LENGTH_LONG)
                            .show();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully
            final GoogleSignInAccount account = result.getSignInAccount();
            runSocialLoginTask(Const.ApiValues.OAUTH_TYPE_GOOGLEPLUS, account.getIdToken());
        } else if (result.getStatus().hasResolution()) {
            try {
                result.getStatus().startResolutionForResult(this, Const.RequestCodes.AUTH_LOGIN_GOOGLE_RESOLVE);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            final String message = String.format(
                    getResources().getString(R.string.snackbar_auth_error_google),
                    result.getStatus().getStatusMessage()
            );
            RedSnackbar.make(findViewById(R.id.root_view),
                    message,
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void runSocialLoginTask(String type, String token) {
        if (mSocialLoginTask == null) {
            mSocialLoginTask = new SocialLoginTask();
        }

        if (mSocialLoginTask.getStatus() != AsyncTask.Status.RUNNING) {
            mSocialLoginTask = new SocialLoginTask();
            mSocialLoginTask.execute(type, token);
        }
    }

    private class SocialLoginTask extends AsyncTask<String, Void, LoginObject> {
        private PrkngApiError error = null;

        @Override
        protected LoginObject doInBackground(String... params) {
            if (params != null && params.length == 2) {
                try {
                    final String type = params[0];
                    final String token = params[1];

                    if (token != null && !token.isEmpty()) {
                        return ApiClient
                                .loginSocial(
                                        ApiClient.getService(),
                                        token,
                                        type);
                    }
                } catch (PrkngApiError e) {
                    error = e;
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(LoginObject loginObject) {
            try {
                if (error != null) {
                    error.showSnackbar(findViewById(R.id.root_view));
                    // TODO handle API errors
                    error.printStackTrace();
                } else {
                    PrkngPrefs
                            .getInstance(LoginActivity.this)
                            .setAuthUser(loginObject);

                    if (loginObject != null) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
