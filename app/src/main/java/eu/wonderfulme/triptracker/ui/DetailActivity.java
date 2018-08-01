package eu.wonderfulme.triptracker.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationData;
import eu.wonderfulme.triptracker.database.LocationDbSingleton;
import eu.wonderfulme.triptracker.tasks.ExportAsyncTask;
import eu.wonderfulme.triptracker.tasks.RemoveAsyncTask;
import eu.wonderfulme.triptracker.utility.Utils;

import static eu.wonderfulme.triptracker.ui.MainActivity.INTENT_EXTRA_ITEM_KEY;
import static eu.wonderfulme.triptracker.ui.MainActivity.INTENT_EXTRA_ROUTE_NAME;


public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback{

    static final String ACTION_ROUTE_REMOVED = "ACTION_ROUTE_REMOVED";

    @BindView(R.id.btn_detail_export) protected Button mExportButton;
    @BindView(R.id.btn_detail_remove) protected Button mRemoveButton;
    @BindView(R.id.detail_activity_layout) protected ConstraintLayout mConstraintLayout;
    private int mItemKey;
    private List<LocationData> mLocationData;
    private GoogleMap mMap;
    private Snackbar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent incomingIntent = getIntent();
        if (incomingIntent == null) {
            throw new RuntimeException(this.toString() + " must receive Route header");
        }
        // Get location data
        setTitle(incomingIntent.getStringExtra(INTENT_EXTRA_ROUTE_NAME));
        mItemKey = incomingIntent.getIntExtra(INTENT_EXTRA_ITEM_KEY, -1);
        mSnackBar = Snackbar.make(mConstraintLayout, "", Snackbar.LENGTH_LONG);
        new LocationDataQueryAsyncTask().execute();
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO save state
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //TODO Restore state
    }

    public void onExportClicked(View view) {
        new ExportAsyncTask(this, mSnackBar, mItemKey).execute();
    }

    public void onRemoveClicked(View view) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(getString(R.string.remove_route))
                .setMessage(getString(R.string.remove_route_msg))
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
}
