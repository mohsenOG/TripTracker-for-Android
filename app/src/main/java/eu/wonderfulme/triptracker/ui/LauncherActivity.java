package eu.wonderfulme.triptracker.ui;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import eu.wonderfulme.triptracker.R;

public class LauncherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String STATE_INSTANCE_GOOGLE_API_CLIENT = "STATE_INSTANCE_GOOGLE_API_CLIENT";
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        if (savedInstanceState != null) {
            savedInstanceState.getParcelable(STATE_INSTANCE_GOOGLE_API_CLIENT);
        }

        //Check api connection.
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_INSTANCE_GOOGLE_API_CLIENT, (Parcelable) mGoogleApiClient);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LauncherDialog dialog = new LauncherDialog(this);
        dialog.show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, getString(R.string.toast_google_client_no_connection), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.toast_google_client_no_connection), Toast.LENGTH_LONG).show();
        finish();
    }
}
