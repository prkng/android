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
import com.google.android.gms.common.api.Scope;

import java.util.Set;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.io.ApiClient;
import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.LoginObject;
import ng.prk.prkngandroid.ui.activity.base.BaseActivity;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class LoginActivity extends BaseActivity implements
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";

    private LoginButton vFacebookLoginButton;
    private SignInButton vGoogleLoginButton;
    private CallbackManager facebookCallbackManager;
    private GoogleLoginTask mGoogleLoginTask;
    private FacebookLoginTask mFacebookLoginTask;

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        logoutIfNecessary();

        setupListeners();

        setupFacebookLogin();
        setupGoogleLogin();
    }

    private void logoutIfNecessary() {
        PrkngPrefs.getInstance(this).setAuthUser(null);
        LoginManager.getInstance().logOut();
    }

    @Override
    protected boolean isLoginRequired() {
        return false;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_google) {
            Log.v(TAG, "btn_google");
            googleSignIn();
        } else if (id == R.id.btn_email) {
            startActivityForResult(LoginEmailActivity.newIntent(this), Const.RequestCodes.AUTH_LOGIN_EMAIL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        }
    }

    private void setupListeners() {
        findViewById(R.id.btn_google).setOnClickListener(this);
        findViewById(R.id.btn_email).setOnClickListener(this);
    }


    private GoogleApiClient mGoogleApiClient;

    private void setupGoogleLogin() {
        mGoogleLoginTask = new GoogleLoginTask();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//        final String serverClientId = getResources().getString(R.string.project_id);
        final String serverClientId = getResources().getString(R.string.web_oauth_client_id);
        Log.v(TAG, "serverClientId = " + serverClientId);
        final GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder()
//                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
//                .requestScopes(
////                        new Scope("https://www.googleapis.com/auth/plus.login"),
////                        new Scope("https://www.googleapis.com/auth/plus.me"),
////                        new Scope("https://www.googleapis.com/auth/userinfo.email"),
////                        new Scope("https://www.googleapis.com/auth/userinfo.profile"),
//                        new Scope("profile"),
//                        new Scope("email"),
//                        new Scope("openid")
//                )
                .requestIdToken(serverClientId)
//                .requestServerAuthCode(getResources().getString(R.string.server_client_id))
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        vGoogleLoginButton = (SignInButton) findViewById(R.id.btn_google);
        vGoogleLoginButton.setScopes(new Scope[]{new Scope("profile"),
                new Scope("email"),
                new Scope("openid")});

        Scope[] scopes = gso.getScopeArray();
        for (Scope scope : scopes) {
            Log.v(TAG, "scope = " + scope.toString());
        }

    }

    private void setupFacebookLogin() {
        mFacebookLoginTask = new FacebookLoginTask();
        facebookCallbackManager = CallbackManager.Factory.create();
        vFacebookLoginButton = (LoginButton) findViewById(R.id.btn_facebook);
        vFacebookLoginButton.setReadPermissions(getResources().getString(R.string.facebook_permissions));
        vFacebookLoginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final String token = loginResult.getAccessToken().getToken();

                if (mFacebookLoginTask.getStatus() != AsyncTask.Status.RUNNING) {
                    mFacebookLoginTask = new FacebookLoginTask();
                    mFacebookLoginTask.execute(token);
                }
            }

            @Override
            public void onCancel() {
                Log.v(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                try {
                    final String message = String.format(
                            getResources().getString(R.string.snackbar_auth_error_facebook),
                            error.getMessage()
                    );
                    Snackbar.make(findViewById(R.id.root_view),
                            message,
                            Snackbar.LENGTH_LONG)
                            .show();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "onConnectionFailed");
    }


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Const.RequestCodes.AUTH_LOGIN_GOOGLE);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

//            final String token = GoogleAuthUtil.getToken(mAppContext, mAccountName, AUTH_TOKEN_TYPE);

            final GoogleSignInAccount account = result.getSignInAccount();
            Log.v(TAG, "result = " + account.toString());
            Log.v(TAG, "result = {" +
                            " email: " + account.getEmail() +
                            ", name: " + account.getDisplayName() +
                            ", idToken: " + account.getIdToken() +
                            ", id: " + account.getId() +
                            ", serverAuthCode: " + account.getServerAuthCode() +
                            ", scopes: " + account.getGrantedScopes() +
                            ", photoUrl: " + account.getPhotoUrl() +
                            "}"
            );
            String message = "Token: " + account.getIdToken();
            Snackbar.make(findViewById(R.id.root_view),
                    message,
                    Snackbar.LENGTH_LONG)
                    .show();

            Set<Scope> scopes = account.getGrantedScopes();
            for (Scope scope : scopes) {
                Log.v(TAG, "scope = " + scope.toString());
            }

            if (mGoogleLoginTask.getStatus() != AsyncTask.Status.RUNNING) {
                mGoogleLoginTask = new GoogleLoginTask();
                mGoogleLoginTask.execute(account);
            }

        } else if (result.getStatus().hasResolution()) {
            Log.v(TAG, "hasResolution");
            try {
                result.getStatus().startResolutionForResult(this, 1234);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            String message = "status " + result.getStatus().getStatusCode();
            Snackbar.make(findViewById(R.id.root_view),
                    message,
                    Snackbar.LENGTH_LONG)
                    .show();
            // Signed out, show unauthenticated UI.
//            updateUI(false);
            Log.v(TAG, "status " + result.getStatus().getStatusCode());
        }
    }

    private class FacebookLoginTask extends AsyncTask<String, Void, LoginObject> {
        private PrkngApiError error = null;

        @Override
        protected LoginObject doInBackground(String... params) {
            try {
                final String token = params[0];

                if (token != null && !token.isEmpty()) {
                    return ApiClient
                            .loginFacebook(
                                    ApiClient.getServiceLog(),
                                    token);
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
                    error.printStackTrace();
                } else {
                    PrkngPrefs
                            .getInstance(LoginActivity.this)
                            .setAuthUser(loginObject);

                    setResult(Activity.RESULT_OK);
                    finish();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


    private class GoogleLoginTask extends AsyncTask<GoogleSignInAccount, Void, LoginObject> {
        private PrkngApiError error = null;

        @Override
        protected LoginObject doInBackground(GoogleSignInAccount... params) {
            try {
                final GoogleSignInAccount account = params[0];

                if (account != null) {
                    return ApiClient
                            .loginGoogleplus(
                                    ApiClient.getServiceLog(),
                                    account.getIdToken(),
                                    account.getDisplayName(),
                                    account.getPhotoUrl());
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
                    error.printStackTrace();
                } else {
                    PrkngPrefs
                            .getInstance(LoginActivity.this)
                            .setAuthUser(loginObject);

                    setResult(Activity.RESULT_OK);
                    finish();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
