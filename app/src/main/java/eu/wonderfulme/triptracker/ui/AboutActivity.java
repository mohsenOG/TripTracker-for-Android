package eu.wonderfulme.triptracker.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.wonderfulme.triptracker.R;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.tv_about_openCSV_website) TextView mOpenCsvWebsiteTextView;
    @BindView(R.id.tv_about_openCSV_license) TextView mOpenCsvLicenseTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.action_about);
        }

        mOpenCsvWebsiteTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mOpenCsvLicenseTextView.setMovementMethod(LinkMovementMethod.getInstance());

        //TODO Fill in all the libraries.
    }
}
