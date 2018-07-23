package eu.wonderfulme.triptracker.utility;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionListener mConnectionListener;
    private Bundle mConnectionBundle;



    public GoogleApiHelper(Context context) {
        this.mContext = context;
        buildGoogleApiClient();
        connect();
    }

    public GoogleApiClient getGoogleApiClient() {
        return this.mGoogleApiClient;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.mConnectionListener = connectionListener;
        if (this.mConnectionListener != null && isConnected()) {
            connectionListener.onConnected(mConnectionBundle);
        }
    }

    public void connect() {
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
