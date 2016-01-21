package ng.prk.prkngandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.CheckinData;
import ng.prk.prkngandroid.ui.activity.CheckinActivity;
import ng.prk.prkngandroid.util.CalendarUtils;
import ng.prk.prkngandroid.util.PrkngPrefs;

public class CheckinInfoFragment extends Fragment implements View.OnClickListener {

    private TextView vTitle;
    private long checkinId;

    public static CheckinInfoFragment newInstance(long id) {
        final CheckinInfoFragment fragment = new CheckinInfoFragment();

//        final Bundle bundle = new Bundle();
//        bundle.putLong(Const.BundleKeys.CHECKIN_ID, id);
//        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_checkin_info, container, false);

        vTitle = (TextView) view.findViewById(R.id.title);

//        final long id = getArguments().getLong(Const.BundleKeys.CHECKIN_ID);

        final PrkngPrefs prefs = PrkngPrefs.getInstance(getActivity());
        final CheckinData checkin = prefs.getCheckinData();
        if (checkin == null) {
            return null;
        }


        fillCheckinData(checkin);

        view.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        startActivity(CheckinActivity.newIntent(getActivity()));
    }

    private void fillCheckinData(CheckinData checkin) {
        checkinId = checkin.getId();

        final long checkoutAt = checkin.getCheckoutAt();
        if (Long.valueOf(Const.UNKNOWN_VALUE).equals(checkoutAt)) {
            vTitle.setText(
                    String.format(getResources().getString(R.string.checkin_prefix),
                            checkin.getAddress()));
        } else {
            final long remaining = checkoutAt - System.currentTimeMillis();
            vTitle.setText(CalendarUtils.getTimeFromMillis(getActivity(), remaining));
        }
    }
}
