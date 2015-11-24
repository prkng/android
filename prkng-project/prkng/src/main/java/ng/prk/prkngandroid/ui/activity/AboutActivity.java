package ng.prk.prkngandroid.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.ui.fragment.AboutFragment;

public class AboutActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final Fragment fragment = AboutFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment, Const.FragmentTags.ABOUT)
                    .commit();
        }
    }
}
