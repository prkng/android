package ng.prk.prkngandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.util.AnalyticsUtils;

public class WebViewActivity extends AppCompatActivity {

    private final static String ASSETS_BASE_URL = "file:///android_asset/";
    private WebView mWebView;
    private ContentLoadingProgressBar vProgressBar;
    private String mPage;

    public static Intent newIntent(Context context, String page) {
        final Intent intent = new Intent(context, WebViewActivity.class);

        final Bundle extras = new Bundle();
        extras.putString(Const.BundleKeys.PAGE, page);
        intent.putExtras(extras);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPage = getIntent().getStringExtra(Const.BundleKeys.PAGE);

        setContentView(R.layout.activity_webview);

        setTitle(null);
        getSupportActionBar().setElevation(0);
//        getSupportActionBar().setBackgroundDrawable(
//                ContextCompat.getDrawable(this, R.drawable.bg_webview_header));


        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.setWebViewClient(new PrkngWebViewClient());

        vProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress);
//        vProgressBar.getIndeterminateDrawable()
//                .setColorFilter(getResources().getColor(R.color.color_background), PorterDuff.Mode.SRC_IN);

        loadPage(mPage);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsUtils.sendScreenView(this, "WebViewActivity " + mPage);
    }

    private void loadPage(String page) {

        mWebView.getSettings()
                .setCacheMode(WebSettings.LOAD_DEFAULT);

        if (Const.PrefsNames.FAQ.equals(page)) {
//            setTitle(R.string.activity_faq);
            mWebView.getSettings()
                    .setJavaScriptEnabled(true);
            mWebView.loadUrl(getString(R.string.url_faq));
        } else if (Const.PrefsNames.TERMS.equals(page)) {
//            setTitle(R.string.activity_terms);
            mWebView.loadUrl(getString(R.string.url_terms));
        } else if (Const.PrefsNames.PRIVACY.equals(page)) {
            mWebView.loadUrl(getString(R.string.url_privacy));
        }
    }

    private class PrkngWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            vProgressBar.hide();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            mWebView.loadUrl(getLocalAsset(mPage));
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && url.startsWith("http://")) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            } else {
                return false;
            }
        }
    }

    private String getLocalAsset(String page) {
        if (Const.PrefsNames.FAQ.equals(page)) {
            return getString(R.string.asset_faq);
        } else if (Const.PrefsNames.TERMS.equals(page)) {
            return getString(R.string.asset_terms);
        }

        return null;
    }
}
