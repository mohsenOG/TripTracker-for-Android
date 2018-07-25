package eu.wonderfulme.triptracker.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.util.CollectionUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.wonderfulme.triptracker.App;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.searcher.SearchLocation;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

import static eu.wonderfulme.triptracker.searcher.SearchLocation.LOCATION_TYPE_SINGLE;
import static eu.wonderfulme.triptracker.ui.LauncherDialog.ACTION_PARKING_LOCATION_SAVED;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION_MAIN = 101;

    @BindView(R.id.btn_main_save_parking) protected Button mSaveParkingButton;
    @BindView(R.id.btn_main_remove_parking) protected Button mRemoveParkingButton;
    @BindView(R.id.recyclerView_routes) protected RecyclerView mRecyclerView;
    @BindView(R.id.constraintLayout_mainActivity) protected ConstraintLayout mConstraintLayout;
    @BindView(R.id.progressBar_main) protected ProgressBar mProgressBar;
    private BroadcastReceiver mLocationServiceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Check the location if it is valid show the restore button.
        List<String> parkingLocation = UtilsSharedPref.getParkingLocationFromSharedPref(this);
        if (!CollectionUtils.isEmpty(parkingLocation)) {
            mSaveParkingButton.setText(R.string.btn_restore_parking);
            mRemoveParkingButton.setEnabled(true);
        } else {
            mRemoveParkingButton.setEnabled(false);
        }

        mLocationServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showProgressBar(false);
                mSaveParkingButton.setText(R.string.btn_restore_parking);
                mRemoveParkingButton.setEnabled(true);
                Snackbar.make(mConstraintLayout, getString(R.string.toast_parking_saved), Snackbar.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ACTION_PARKING_LOCATION_SAVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationServiceReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationServiceReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION_MAIN:
                showProgressBar(false);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted.
                    checkGPSAndStartService();
                } else {
                    // permission denied. Disable the functionality.
                    Snackbar.make(mConstraintLayout, getString(R.string.toast_location_permission_denied), Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void onSaveParkingClicked(View v) {
        if (mSaveParkingButton.getText().toString().equals(getString(R.string.btn_save_parking))) {
            showProgressBar(true);
            saveParkingLocation();
        } else {
            openParkingLocation();
        }
    }

    public void onRemoveParkingClicked(View v) {
        UtilsSharedPref.setParkingLocationToSharedPref(this, null);
        mSaveParkingButton.setText(getString(R.string.btn_save_parking));
        mRemoveParkingButton.setEnabled(false);
        Snackbar.make(mConstraintLayout, getString(R.string.toast_parking_removed), Snackbar.LENGTH_SHORT).show();
    }

    public void onRecordClicked(View v) {

    }

    private void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void saveParkingLocation() {
        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted. ask user.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION_MAIN);
        } else {
            //Permission is already granted.
            checkGPSAndStartService();
        }
    }

    private void openParkingLocation() {
        List<String> parkingLocationList = UtilsSharedPref.getParkingLocationFromSharedPref(this);
        if (parkingLocationList != null) {
            String latitude = parkingLocationList.get(0);
            String longitude = parkingLocationList.get(1);
            String uri = "https://www.google.com/maps/dir/?api=1&origin=Your+location&destination=" + latitude + "," + longitude;
            Uri mapUri = Uri.parse(uri);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                startActivity(mapIntent);
            }
            // Remove location from shared pref.
            UtilsSharedPref.setParkingLocationToSharedPref(this, null);
            // Set button to parking save parking again.
            mSaveParkingButton.setText(R.string.btn_save_parking);
            mRemoveParkingButton.setEnabled(false);
        }
    }

    private void checkGPSAndStartService() {
        SearchLocation searchLocation = new SearchLocation(this, LOCATION_TYPE_SINGLE);
        boolean isGpsOn = searchLocation.isGpsOn();
        if (App.getGoogleApiHelper().isConnected() && isGpsOn){
            searchLocation.startService();
        } else {
            showProgressBar(false);
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.gps_is_not_on))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // send user to location service setting.
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        Toast.makeText(MainActivity.this, getString(R.string.toast_gps_denied), Toast.LENGTH_LONG).show();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
