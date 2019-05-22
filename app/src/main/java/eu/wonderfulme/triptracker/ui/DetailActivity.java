package eu.wonderfulme.triptracker.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationData;
import eu.wonderfulme.triptracker.tasks.ExportCsvAsyncTask;
import eu.wonderfulme.triptracker.utility.Utils;
import eu.wonderfulme.triptracker.viewmodel.DetailActivityViewModel;
import eu.wonderfulme.triptracker.viewmodel.DetailActivityViewModelFactory;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static eu.wonderfulme.triptracker.ui.MainActivity.INTENT_EXTRA_ITEM_KEY;
import static eu.wonderfulme.triptracker.ui.MainActivity.INTENT_EXTRA_ROUTE_NAME;


public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 103;

    @BindView(R.id.btn_detail_export) protected Button mExportButton;
    @BindView(R.id.btn_detail_remove) protected Button mRemoveButton;
    @BindView(R.id.detail_activity_layout) protected ConstraintLayout mConstraintLayout;
    @BindView(R.id.adView_detail_activity) protected AdView mBannerAdView;
    private GoogleMap mMap;
    private Snackbar mSnackBar;
    private SupportMapFragment mMapFragment;
    private InterstitialAd mInterstitialAd;
    private DetailActivityViewModel detailActivityViewModel;

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

        setTitle(incomingIntent.getStringExtra(INTENT_EXTRA_ROUTE_NAME));
        int itemKey = incomingIntent.getIntExtra(INTENT_EXTRA_ITEM_KEY, -1);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

        detailActivityViewModel = ViewModelProviders.of(this, new DetailActivityViewModelFactory(this.getApplication(), itemKey)).get(DetailActivityViewModel.class);
        detailActivityViewModel.getLocationData().observe(this, new Observer<List<LocationData>>() {
            @Override
            public void onChanged(List<LocationData> locationData) {
                mMapFragment.getMapAsync(DetailActivity.this);
            }
        });

        mSnackBar = Snackbar.make(mConstraintLayout, "", Snackbar.LENGTH_LONG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapFragment.onSaveInstanceState(outState);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapFragment.onViewStateRestored(savedInstanceState);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                    List<LocationData> locationData = detailActivityViewModel.getLocationData().getValue();
                    new ExportCsvAsyncTask(this, mSnackBar, locationData);
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
            List<LocationData> locationData = detailActivityViewModel.getLocationData().getValue();
            new ExportCsvAsyncTask(this, mSnackBar, locationData).execute();
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
                        detailActivityViewModel.deleteSingleItemKey(DetailActivity.this, mSnackBar);
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
        List<LocationData> locationData = detailActivityViewModel.getLocationData().getValue();
        if (mMap != null &&  locationData != null && !locationData.isEmpty()) {
            mMap.addPolyline(Utils.getPolyLine(locationData));
            // Adding Start/End marker on map
            BitmapDescriptor markerA = Utils.getMarkerIconFromDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.marker_letter_a, null));
            BitmapDescriptor markerB = Utils.getMarkerIconFromDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.marker_letter_b, null));
            mMap.addMarker(Utils.getMarker(locationData.get(0), getString(R.string.marker_start), markerA));
            mMap.addMarker(Utils.getMarker(locationData.get(locationData.size() - 1), getString(R.string.marker_end), markerB));
        }
    }

    @Override
    public void onMapLoaded() {
        List<LocationData> locationData = detailActivityViewModel.getLocationData().getValue();
        if (locationData != null && !locationData.isEmpty()) {
            mMap.animateCamera(Utils.getMapCameraBounds(locationData));
        }
    }

    private class MyAdListener extends AdListener {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            exportCsv();
        }
    }
}
