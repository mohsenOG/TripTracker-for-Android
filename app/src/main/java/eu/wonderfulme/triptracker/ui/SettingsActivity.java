package eu.wonderfulme.triptracker.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;


public class SettingsActivity extends AppCompatActivity implements SettingsFragment.PreferenceFragmentCallback{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onRecordPeriodChanged(String periodType) {
        if (!StringUtils.equals(periodType, getString(R.string.pref_settings_record_type_custom))) {
            int recordPeriod = UtilsSharedPref.recordPeriodFinder(this, periodType);
            UtilsSharedPref.setRecordPeriodToSharedPref(this, recordPeriod);
        } else {
            NumberPickerDialog numberPickerDialog = new NumberPickerDialog(this, 0, 180);
            numberPickerDialog.show();
        }
        Toast.makeText(this, periodType, Toast.LENGTH_LONG).show();
    }
}
