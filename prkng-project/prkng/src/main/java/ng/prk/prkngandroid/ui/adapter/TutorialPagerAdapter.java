package ng.prk.prkngandroid.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.ui.fragment.TutorialFragment;

public class TutorialPagerAdapter extends FragmentPagerAdapter {

    private final boolean isInitial;

    public TutorialPagerAdapter(FragmentManager fm, boolean isInitial) {
        super(fm);
        this.isInitial = isInitial;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getCount() {
        return Const.TutorialSections._COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return TutorialFragment.newInstance(position, isInitial);
    }
}
