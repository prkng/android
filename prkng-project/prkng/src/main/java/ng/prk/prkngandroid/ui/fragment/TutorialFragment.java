package ng.prk.prkngandroid.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;

public class TutorialFragment extends Fragment {

    private static final String TAG = "TutorialFragment";

    public static TutorialFragment newInstance(int page, boolean isInitial) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.BundleKeys.PAGE, page);
        bundle.putBoolean(Const.BundleKeys.IS_INITIAL_ONBOARDING, isInitial);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final int page = getArguments().getInt(Const.BundleKeys.PAGE, -1);
        final boolean isInitial = getArguments().getBoolean(Const.BundleKeys.IS_INITIAL_ONBOARDING, false);

        final View view = inflater.inflate(getResLayout(page), container, false);

        if (page == Const.TutorialSections.LOGIN) {
            setLoginListeners(view);

            // Hide the loginButton if launched from about (product tour)
//            view.findViewById(R.id.onboarding_login_btn)
//                    .setVisibility(isInitial ? View.VISIBLE : View.GONE);
        }

        return view;
    }

    private int getResLayout(int page) {
        if (page == Const.TutorialSections.ONE) {
            return R.layout.fragment_tutorial_01;
        } else if (page == Const.TutorialSections.TWO) {
            return R.layout.fragment_tutorial_02;
        } else if (page == Const.TutorialSections.THREE) {
            return R.layout.fragment_tutorial_03;
        } else if (page == Const.TutorialSections.FOUR) {
            return R.layout.fragment_tutorial_04;
        }

        return 0;
    }

    /**
     * Set the login button listeners, and a fade animation
     *
     * @param view
     */
    private void setLoginListeners(View view) {
        // Launch the login activity
//        view.findViewById(R.id.onboarding_login_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Activity activity = getActivity();
//
//                activity.setResult(Activity.RESULT_OK,
//                        LoginActivity.newIntent(getActivity()));
//
//                activity.finish();
//                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//            }
//        });
    }
}
