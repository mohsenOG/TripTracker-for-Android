package eu.wonderfulme.triptracker.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.common.util.CollectionUtils;

import java.util.List;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.Utils;

public class LauncherDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private Button mSaveParkingButton;
    private Button mOpenAppButton;


    public LauncherDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_launcher);
        this.setCanceledOnTouchOutside(false);

        mSaveParkingButton = findViewById(R.id.btn_launcher_dialog_save_parking);
        mSaveParkingButton.setOnClickListener(this);
        // Check the location if it is valid show the restore button.
        List<String> parkingLocation = Utils.getParkingLocationFromSharedPref(mContext);
        if (!CollectionUtils.isEmpty(parkingLocation)) {
            mSaveParkingButton.setText(R.string.btn_launcher_dialog_restore_parking);
        }

        mOpenAppButton = findViewById(R.id.btn_launcher_dialog_open_app);
        mOpenAppButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_launcher_dialog_save_parking: {
                if (mSaveParkingButton.getText().toString().equals(mContext.getString(R.string.btn_launcher_dialog_save_parking))) {
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
                AppCompatActivity parent = (AppCompatActivity) mContext;
                parent.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppCompatActivity parent = (AppCompatActivity) mContext;
        parent.finish();
    }

    private void saveParkingLocation() {
        //TODO Check GPS and when not ON send user to setting.
        // If succeed run the LocationService with REQUEST_TYPE_SINGLE.
        // Write a class to do it so it can be used also in main activity buttons.
    }

    private void openParkingLocation() {
        //TODO Get location from shared pref and send as intent to google maps or any navi app.
        // Write a class to do it so it can be used also in main activity buttons.
    }
}
