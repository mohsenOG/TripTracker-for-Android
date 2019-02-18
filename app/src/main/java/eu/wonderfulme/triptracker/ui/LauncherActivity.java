package eu.wonderfulme.triptracker.ui;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import eu.wonderfulme.triptracker.App;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.utility.GoogleApiHelper;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setTitle("");

        //Check api connection.
        App.getGoogleApiHelper().setConnectionListener(new GoogleApiHelper.ConnectionListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                showErrorToast();
            }

            @Override
            public void onConnectionSuspended(int i) {
                showErrorToast();
            }

            @Override
            public void onConnected(Bundle bundle) {
                LauncherDialog dialog = new LauncherDialog(LauncherActivity.this);
                dialog.show();
            }
        });
    }

    private void showErrorToast() {
        Toast.makeText(LauncherActivity.this, getString(R.string.toast_google_client_no_connection), Toast.LENGTH_LONG).show();
        finish();
    }

}
