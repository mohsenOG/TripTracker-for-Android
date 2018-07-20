package eu.wonderfulme.triptracker.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import eu.wonderfulme.triptracker.R;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //TODO add googleApiClient here and try to connect to it.
        // if failed show a dialog that google services is a must.
        //Otherwise call launcher dialog.

        LauncherDialog dialog = new LauncherDialog(this);
        dialog.show();

    }
}
