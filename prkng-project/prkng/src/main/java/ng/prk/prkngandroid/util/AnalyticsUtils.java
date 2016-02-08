package ng.prk.prkngandroid.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.PrkngApp;
import ng.prk.prkngandroid.ui.activity.AboutActivity;
import ng.prk.prkngandroid.ui.activity.CheckinActivity;
import ng.prk.prkngandroid.ui.activity.LoginActivity;
import ng.prk.prkngandroid.ui.activity.LoginEmailActivity;
import ng.prk.prkngandroid.ui.activity.LoginForgotPasswordActivity;
import ng.prk.prkngandroid.ui.activity.LoginSignUpActivity;
import ng.prk.prkngandroid.ui.activity.MainActivity;
import ng.prk.prkngandroid.ui.activity.SearchActivity;
import ng.prk.prkngandroid.ui.activity.SettingsActivity;
import ng.prk.prkngandroid.ui.activity.TutorialActivity;
import ng.prk.prkngandroid.ui.activity.WebViewActivity;
import ng.prk.prkngandroid.ui.fragment.LotInfoFragment;
import ng.prk.prkngandroid.ui.fragment.MainMapFragment;
import ng.prk.prkngandroid.ui.fragment.SpotInfoFragment;
import ng.prk.prkngandroid.ui.fragment.TutorialFragment;

public class AnalyticsUtils implements
        Const.AnalyticsScreens,
        Const.AnalyticsValues {
    private static final String TAG = "AnalyticsUtils";

    public static void sendActivityView(Activity activity) {
        if (activity instanceof AboutActivity) {
            sendScreenView(activity, ABOUT_ACTIVITY);
        } else if (activity instanceof CheckinActivity) {
            sendScreenView(activity, CHECKIN_ACTIVITY);
        } else if (activity instanceof LoginActivity) {
            sendScreenView(activity, LOGIN_ACTIVITY);
        } else if (activity instanceof LoginSignUpActivity) {
            sendScreenView(activity, LOGIN_SIGNUP_ACTIVITY);
        } else if (activity instanceof LoginEmailActivity) {
            sendScreenView(activity, LOGIN_EMAIL_ACTIVITY);
        } else if (activity instanceof LoginForgotPasswordActivity) {
            sendScreenView(activity, LOGIN_FORGOTPASSWORD_ACTIVITY);
        } else if (activity instanceof MainActivity) {
            sendScreenView(activity, MAIN_ACTIVITY);
        } else if (activity instanceof SearchActivity) {
            sendScreenView(activity, SEARCH_ACTIVITY);
        } else if (activity instanceof SettingsActivity) {
            sendScreenView(activity, SETTINGS_ACTIVITY);
        } else if (activity instanceof TutorialActivity) {
            sendScreenView(activity, TUTORIAL_ACTIVITY);
        }

    }

    public static void sendActivityView(Activity activity, String extra) {
        Log.v(TAG, "sendActivityView "
                + String.format("extra = %s", extra));
        if (activity instanceof WebViewActivity) {
            if (Const.PrefsNames.FAQ.equals(extra)) {
                sendScreenView(activity, FAQ_WEBVIEW_ACTIVITY);
            } else if (Const.PrefsNames.TERMS.equals(extra)) {
                sendScreenView(activity, TERMS_WEBVIEW_ACTIVITY);
            } else if (Const.PrefsNames.PRIVACY.equals(extra)) {
                sendScreenView(activity, PRIVACY_WEBVIEW_ACTIVITY);
            } else {
                sendScreenView(activity, WEBVIEW_ACTIVITY);
            }
        }
    }

    public static void sendFragmentView(Fragment fragment) {
        final Activity activity = fragment.getActivity();

        if (fragment instanceof MainMapFragment) {
            sendScreenView(activity, MAP_FRAGMENT);
        }
    }

    /**
     * Not used, identical call is sent from activity
     */
//    public static void sendFragmentView(PreferenceFragment fragment) {
//        final Activity activity = fragment.getActivity();
//        if (fragment instanceof AboutFragment) {
//            sendScreenView(fragment.getActivity(), ABOUT_FRAGMENT);
//        } else if (fragment instanceof SettingsFragment) {
//            sendScreenView(activity, SETTINGS_FRAGMENT);
//        }
//    }
    public static void sendFragmentView(Fragment fragment, int extra) {
        final Activity activity = fragment.getActivity();

        if (fragment instanceof TutorialFragment) {
            switch (extra) {
                case Const.TutorialSections.SPLASH:
                    sendScreenView(activity, TUTORIAL_LOGO);
                    break;
                case Const.TutorialSections.ONE:
                case Const.TutorialSections.TWO:
                case Const.TutorialSections.THREE:
                case Const.TutorialSections.FOUR:
                    sendScreenView(activity, TUTORIAL_PAGE + String.valueOf(extra));
                    break;
//            case Const.TutorialSections.TRANSPARENT:
//                break;
            }
        } else if (fragment instanceof MainMapFragment) {
            switch (extra) {
                case Const.MapSections.ON_STREET:
                    sendScreenView(activity, MAP_ON_STREET);
                    break;
                case Const.MapSections.OFF_STREET:
                    sendScreenView(activity, MAP_OFF_STREET);
                    break;
                case Const.MapSections.CARSHARE_SPOTS:
                    sendScreenView(activity, MAP_CARSHARE_SPOTS);
                    break;
                case Const.MapSections.CARSHARE_VEHICLES:
                    sendScreenView(activity, MAP_CARSHARE_VEHICLES);
                    break;
            }
        }
    }

    public static void sendFragmentView(Fragment fragment, String extra) {
        final Activity activity = fragment.getActivity();
        if (fragment instanceof LotInfoFragment) {
            Map<String, String> params = new HashMap<>();
            params.put(LOT_ID, extra);
            sendScreenView(activity, LOT_INFO_FRAGMENT, params);
        } else if (fragment instanceof SpotInfoFragment) {
            Map<String, String> params = new HashMap<>();
            params.put(SPOT_ID, extra);
            sendScreenView(activity, SPOT_INFO_FRAGMENT, params);
        }
    }

    private static void sendScreenView(Context context, String screenName) {
        sendScreenView(context, screenName, null);
    }

    private static void sendScreenView(Context context, String screenName, Map<String, String> params) {
        Log.v(TAG, "sendScreenView, " + screenName);
        try {
            final Tracker tracker = PrkngApp.getInstance(context).getAnalyticsTracker();
            tracker.setScreenName(screenName);
            tracker.send(params);

            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
