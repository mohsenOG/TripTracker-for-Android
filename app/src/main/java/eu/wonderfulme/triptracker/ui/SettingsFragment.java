package eu.wonderfulme.triptracker.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.wonderfulme.triptracker.R;

public class SettingsFragment extends PreferenceFragment {

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

        return view;
    }

}
