package ng.prk.prkngandroid.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.ui.fragment.AboutFragment;
import ng.prk.prkngandroid.util.AnalyticsUtils;

public class AboutActivity extends AppCompatActivity {

    private static final String SEND_INTENT_TYPE = "text/plain";

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

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsUtils.sendActivityView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            /**
             * Native sharing
             */
            final Bundle extras = new Bundle();
            extras.putString(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_app_subject));
            extras.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.share_app_body));

            final Intent sendIntent = new Intent();
            sendIntent.putExtras(extras);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType(SEND_INTENT_TYPE);
            startActivity(sendIntent);

            return true;
//        } else if (item.getItemId() == R.id.action_rate) {
//            /**
//             * Launch Playstore to rate app
//             */
//            final Intent viewIntent = new Intent(Intent.ACTION_VIEW);
//            viewIntent.setData(Uri.parse(getResources().getString(R.string.url_playstore)));
//            startActivity(viewIntent);
//
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
