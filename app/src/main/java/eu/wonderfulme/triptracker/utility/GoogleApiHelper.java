package eu.wonderfulme.triptracker.utility;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * https://stackoverflow.com/a/33400346/6072457
 */
public class GoogleApiHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public interface ConnectionListener {
        void onConnectionFailed(@NonNull ConnectionResult connectionResult);
        void onConnectionSuspended(int i);
        void onConnected(Bundle bundle);
    }

    private final Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionListener mConnectionListener;
    private Bundle mConnectionBundle;



    public GoogleApiHelper(Context context) {
        this.mContext = context;
        buildGoogleApiClient();
        connect();
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.mConnectionListener = connectionListener;
        if (this.mConnectionListener != null && isConnected()) {
            connectionListener.onConnected(mConnectionBundle);
        }
    }

    private void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mConnectionBundle = bundle;
        if (mConnectionListener != null) {
            mConnectionListener.onConnected(bundle);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        if (mConnectionListener != null) {
            mConnectionListener.onConnectionSuspended(i);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mConnectionListener != null) {
            mConnectionListener.onConnectionFailed(connectionResult);
        }
    }
}
