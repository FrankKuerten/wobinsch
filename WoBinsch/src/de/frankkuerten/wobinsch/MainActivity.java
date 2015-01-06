package de.frankkuerten.wobinsch;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity implements OnClickListener,
		OnItemSelectedListener {

	private OrtLauscher lauscher;
	private LocationManager locationManager;
	private boolean started = false;
	private Button button;
	private Spinner vehikelFeld;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Location Manager vom System holen
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(this);

		vehikelFeld = (Spinner) findViewById(R.id.vehikel);

		// Spinner aus Enum füllen
		EnumAdapter<Vehikel> adapter = new EnumAdapter<Vehikel>(this,
				android.R.layout.simple_spinner_item, Vehikel.values());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Adapter verknüpfen
		vehikelFeld.setAdapter(adapter);
		vehikelFeld.setOnItemSelectedListener(this);

		lauscher = new OrtLauscher(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.alleExportieren) {
			Reise r = new Reise(this);
			r.initFromDB();
			r.dump2gpx();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if (started) {
			button.setText(R.string.button_start);
			stop();
		} else {
			button.setText(R.string.button_stop);
			start();
		}
		started = !started;
	}

	public void start() {
		lauscher.start();
		if (locationManager.getAllProviders().contains(
				LocationManager.GPS_PROVIDER)) {
			// Listener registrieren, alle n Millisekunden mit 2 Meter Abstand
			// Bescheid sagen
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, getVehikel().getInterval(),
					2, lauscher);
		}
	}

	public void stop() {
		// Lauscher beenden
		locationManager.removeUpdates(lauscher);
		lauscher.stop();
	}

	public Vehikel getVehikel() {
		return (Vehikel) vehikelFeld.getSelectedItem();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (started) {
			stop();
			start();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Keine Aktion nötig

	}

}
