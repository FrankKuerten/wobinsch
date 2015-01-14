package de.frankkuerten.wobinsch;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class EinstellungenFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

}
