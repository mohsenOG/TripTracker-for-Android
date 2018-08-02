package eu.wonderfulme.triptracker.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.List;

import eu.wonderfulme.triptracker.App;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.location.SearchLocation;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

import static eu.wonderfulme.triptracker.location.LocationService.ACTION_PARKING_LOCATION_SAVED;
import static eu.wonderfulme.triptracker.location.SearchLocation.LOCATION_TYPE_SINGLE;

public class LauncherDialog extends Dialog implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION_LAUNCHER = 100;
    private Context mContext;
    private Button mSaveParkingButton;
    private Button mOpenAppButton;
    private ProgressBar mProgressBar;
    private TextView mDetailTextView;
    private AppCompatActivity mParentActivity;
    private AdView mAdView;

    private BroadcastReceiver mLocationServiceBroadcastReceiver;


    public LauncherDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        mParentActivity = (AppCompatActivity) mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_launcher);
        this.setCanceledOnTouchOutside(false);

        //Init Ad
        mAdView = findViewById(R.id.adView_launch_dialog);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // Init receiver
        mLocationServiceBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(mContext, mContext.getString(R.string.toast_parking_saved), Toast.LENGTH_SHORT).show();
                dismiss();
                mParentActivity.finish();
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_PARKING_LOCATION_SAVED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mLocationServiceBroadcastReceiver, filter);

        mSaveParkingButton = findViewById(R.id.btn_launcher_dialog_save_parking);
        mSaveParkingButton.setOnClickListener(this);
        // Check the location if it is valid show the restore button.
        List<String> parkingLocation = UtilsSharedPref.getParkingLocationFromSharedPref(mContext);
        if (!CollectionUtils.isEmpty(parkingLocation)) {
            mSaveParkingButton.setText(R.string.btn_restore_parking);
        }

        mOpenAppButton = findViewById(R.id.btn_launcher_dialog_open_app);
        mOpenAppButton.setOnClickListener(this);

        mProgressBar = findViewById(R.id.progressBar_dialog);
        mDetailTextView = findViewById(R.id.tv_dialog_support);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mLocationServiceBroadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_launcher_dialog_save_parking: {
                showHideProgressBar(true);
                if (mSaveParkingButton.getText().toString().equals(mContext.getString(R.string.btn_save_parking))) {
                    saveParkingLocation();
                } else {
                    openParkingLocation();
                }
                break;
            }
            case R.id.btn_launcher_dialog_open_app:
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
                dismiss();
                mParentActivity.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
        mParentActivity.finish();
    }

    private void saveParkingLocation() {
        // Check permission
        if (ContextCompat.checkSelfPermission(mParentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted. ask user.
            ActivityCompat.requestPermissions(mParentActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION_LAUNCHER);
        } else {
            //Permission is already granted.
            checkGPSAndStartService();
        }
    }

    private void checkGPSAndStartService() {
        SearchLocation searchLocation = new SearchLocation(mContext, LOCATION_TYPE_SINGLE);
        boolean isGpsOn = searchLocation.isGpsOn();
        if (App.getGoogleApiHelper().isConnected() && isGpsOn){
            searchLocation.startService();
        } else {
            showHideProgressBar(false);
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.gps_is_not_on))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // send user to location service setting.
                        mParentActivity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        Toast.makeText(mContext, mContext.getString(R.string.toast_gps_denied), Toast.LENGTH_LONG).show();
                        dismiss();
                        mParentActivity.finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void openParkingLocation() {
        List<String> parkingLocationList = UtilsSharedPref.getParkingLocationFromSharedPref(mContext);
        if (parkingLocationList != null) {
            String latitude = parkingLocationList.get(0);
            String longitude = parkingLocationList.get(1);
            String uri = "https://www.google.com/maps/dir/?api=1&origin=Your+location&destination=" + latitude + "," + longitude;
            Uri mapUri = Uri.parse(uri);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(mapIntent);
            } else {
                mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mContext.startActivity(mapIntent);
            }
            // Remove location from shared pref.
            UtilsSharedPref.setParkingLocationToSharedPref(mContext, null);
            dismiss();
            mParentActivity.finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION_LAUNCHER:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted.
                    checkGPSAndStartService();
                } else {
                    // permission denied. Disable the functionality.
                    Toast.makeText(mContext, mContext.getString(R.string.toast_location_permission_denied), Toast.LENGTH_LONG).show();
                    dismiss();
                    mParentActivity.finish();
                }
                break;
            default:
                break;
        }
    }

    private void showHideProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mDetailTextView.setVisibility(show ? View.GONE : View.VISIBLE);

    }
}
