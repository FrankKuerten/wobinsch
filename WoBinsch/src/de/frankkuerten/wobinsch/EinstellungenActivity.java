package de.frankkuerten.wobinsch;

import android.app.Activity;
import android.os.Bundle;

public class EinstellungenActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new EinstellungenFragment())
				.commit();
	}

}
