package eu.wonderfulme.triptracker.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.utility.Utils;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        setTitle(R.string.action_privacy);

        TextView textView = findViewById(R.id.tv_privacy);
        try {
            InputStream inputStream = getAssets().open("privacy_policy.html");
            String html = Utils.toString(inputStream);
            inputStream.close();

            WebView webView = findViewById(R.id.wv_privacy);
            WebSettings settings = webView.getSettings();
            settings.setDefaultTextEncodingName("utf-8");
            webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        } catch (IOException e) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.privacy_failed_to_load);
        }
    }
}
