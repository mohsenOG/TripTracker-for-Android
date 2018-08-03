package eu.wonderfulme.triptracker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationData;
import eu.wonderfulme.triptracker.database.LocationDbSingleton;
import eu.wonderfulme.triptracker.tasks.ExportAsyncTask;
import eu.wonderfulme.triptracker.tasks.RemoveAsyncTask;
import eu.wonderfulme.triptracker.utility.Utils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static eu.wonderfulme.triptracker.ui.MainActivity.INTENT_EXTRA_ITEM_KEY;
import static eu.wonderfulme.triptracker.ui.MainActivity.INTENT_EXTRA_ROUTE_NAME;


public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    static final String ACTION_ROUTE_REMOVED = "ACTION_ROUTE_REMOVED";
    private static final String SAVE_STATE_LOCATION_DATA_KEY = "SAVE_STATE_LOCATION_DATA_KEY";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 103;

    @BindView(R.id.btn_detail_export) protected Button mExportButton;
    @BindView(R.id.btn_detail_remove) protected Button mRemoveButton;
    @BindView(R.id.detail_activity_layout) protected ConstraintLayout mConstraintLayout;
    @BindView(R.id.adView_detail_activity) protected AdView mBannerAdView;
    private int mItemKey;
    private List<LocationData> mLocationData;
    private GoogleMap mMap;
    private Snackbar mSnackBar;
    private SupportMapFragment mMapFragment;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        // request ad
        AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_detail_export));
        mInterstitialAd.setAdListener(new MyAdListener());

        Intent incomingIntent = getIntent();
        if (incomingIntent == null) {
            throw new RuntimeException(this.toString() + " must receive Route header");
        }
        // Get location data
        setTitle(incomingIntent.getStringExtra(INTENT_EXTRA_ROUTE_NAME));
        mItemKey = incomingIntent.getIntExtra(INTENT_EXTRA_ITEM_KEY, -1);
        mSnackBar = Snackbar.make(mConstraintLayout, "", Snackbar.LENGTH_LONG);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        if (savedInstanceState == null) {
            new LocationDataQueryAsyncTask().execute();
            mMapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_STATE_LOCATION_DATA_KEY, (Serializable) mLocationData);
        mMapFragment.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //noinspection unchecked
        mLocationData = (List<LocationData>) savedInstanceState.getSerializable(SAVE_STATE_LOCATION_DATA_KEY);
        mMapFragment.onViewStateRestored(savedInstanceState);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                    new ExportAsyncTask(this, mSnackBar, mItemKey);
                    break;
            }
        } else {
            Snackbar.make(mConstraintLayout, getString(R.string.snackBar_write_external_storage_permission_denied), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void onExportClicked(View view) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
         exportCsv();
        }
    }

    private void exportCsv() {
        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            // Permission is not granted. ask user.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            //Permission is already granted.
            new ExportAsyncTask(this, mSnackBar, mItemKey).execute();
        }
    }

    public void onRemoveClicked(View view) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(getString(R.string.alert_remove_route_title))
                .setMessage(getString(R.string.alert_remove_route_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        new RemoveAsyncTask(DetailActivity.this, mSnackBar, mItemKey).execute();
                        Intent intent = new Intent();
                        intent.setAction(ACTION_ROUTE_REMOVED);
                        setResult(RESULT_OK, intent);
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
        if (mMap != null &&  mLocationData != null && !mLocationData.isEmpty()) {
            mMap.addPolyline(Utils.getPolyLine(mLocationData));
        }
    }

    @Override
    public void onMapLoaded() {
        if (mLocationData != null && !mLocationData.isEmpty()) {
            mMap.animateCamera(Utils.getMapCameraBounds(mLocationData));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LocationDataQueryAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mLocationData = LocationDbSingleton.getInstance(DetailActivity.this).locationDao().getDbData(mItemKey);
            return null;
        }
    }

    private class MyAdListener extends AdListener {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            exportCsv();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            exportCsv();
        }
    }
}
