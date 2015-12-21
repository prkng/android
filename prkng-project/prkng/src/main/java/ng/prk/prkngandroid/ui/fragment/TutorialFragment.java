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
        final TutorialFragment fragment = new TutorialFragment();
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
//        final boolean isInitial = getArguments().getBoolean(Const.BundleKeys.IS_INITIAL_ONBOARDING, false);

        return inflater.inflate(getResLayout(page), container, false);
    }

    private int getResLayout(int page) {
        switch (page) {
            case Const.TutorialSections.ONE:
                return R.layout.fragment_tutorial_01;
            case Const.TutorialSections.TWO:
                return R.layout.fragment_tutorial_02;
            case Const.TutorialSections.THREE:
                return R.layout.fragment_tutorial_03;
            case Const.TutorialSections.FOUR:
                return R.layout.fragment_tutorial_04;
            case Const.TutorialSections.LAST:
                return R.layout.fragment_tutorial_transparent;
        }

        return 0;
    }
}
