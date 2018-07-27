package eu.wonderfulme.triptracker.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import eu.wonderfulme.triptracker.R;

public class SettingsFragment extends PreferenceFragment {

    public interface PreferenceFragmentCallback {
        public void onRecordPeriodChanged(String periodType);
    }

    private PreferenceFragmentCallback mPreferenceFragmentCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Add custom container to the fragment layout.
        final ViewGroup innerContainer = view.findViewById(R.id.list_container);
        final View innerView = super.onCreateView(inflater, innerContainer, savedInstanceState);
        if (innerView != null) {
            innerContainer.addView(innerView);
        }
        addPreferencesFromResource(R.xml.pref_settings);
        Preference preference = getPreferenceScreen().findPreference(getString(R.string.pref_settings_record_type_key));
        preference.setOnPreferenceChangeListener(new PreferenceChangeListener());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PreferenceFragmentCallback) {
            mPreferenceFragmentCallback = (PreferenceFragmentCallback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PreferenceFragmentCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPreferenceFragmentCallback = null;
    }

    private class PreferenceChangeListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            mPreferenceFragmentCallback.onRecordPeriodChanged((String)newValue);
            return true;
        }
    }

}
