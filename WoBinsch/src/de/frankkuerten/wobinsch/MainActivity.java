package de.frankkuerten.wobinsch;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener,
		OnItemSelectedListener {

	private OrtLauscher lauscher;
	private LocationManager locationManager;
	private boolean started = false;
	private Button button;
	private Spinner vehikelFeld;
	private Reise reise;
	private int toastDuration = Toast.LENGTH_SHORT;
	private static final int RESULT_SETTINGS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean ausgewaehlt = reise != null && reise.hasGewaehlteTS();
		menu.findItem(R.id.exportieren).setEnabled(ausgewaehlt);
		menu.findItem(R.id.exportieren).setVisible(ausgewaehlt);
		// menu.findItem(R.id.exportieren).getIcon().setAlpha(ausgewaehlt?255:130);
		menu.findItem(R.id.loeschen).setEnabled(ausgewaehlt);
		menu.findItem(R.id.loeschen).setVisible(ausgewaehlt);
		// menu.findItem(R.id.loeschen).getIcon().setAlpha(ausgewaehlt?255:130);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
		case R.id.alleAnzeigen:
			reise = new Reise(this);
			reise.initFromDB();
			invalidateOptionsMenu();
			return true;

		case R.id.exportieren:
			if (reise != null && reise.hasGewaehlteTS()) {
				reise.dump2gpx();
				showToast(getString(R.string.msg_dateiExportiert));
			}
			return true;

		case R.id.loeschen:
			if (reise != null && reise.hasGewaehlteTS()) {
				wirklichLoeschen();
			}
			return true;

		case R.id.einstellungen:
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, EinstellungenActivity.class);
			startActivityForResult(intent, 0);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button:
			if (started) {
				button.setText(R.string.button_start);
				stop();
			} else {
				button.setText(R.string.button_stop);
				start();
			}
			started = !started;
			break;
		}
	}

	public void start() {
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
	}

	public Vehikel getVehikel() {
		return (Vehikel) vehikelFeld.getSelectedItem();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == R.id.vehikel && started) {
			stop();
			start();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Keine Aktion nötig

	}

	private void showToast(String text) {
		Toast.makeText(this.getApplicationContext(), text, toastDuration)
				.show();
	}

	private Uri gibAlarm() {
		// Signalton aus Preferences
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String signal = settings.getString("signalAnkerwache", "");
		return Uri.parse(signal);
	}

	public void gibLauteNachricht() {
		// Notification Manager zitieren
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				getApplicationContext()).setSmallIcon(R.drawable.anker)
				.setAutoCancel(true).setContentTitle(getString(R.string.Alarm))
				.setContentText(getString(R.string.AnkerwacheNachricht))
				.setSound(gibAlarm()); // This sets the sound to play

		// Nachricht anzeigen
		notificationManager.notify(0, mBuilder.build());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (started) {
			button.setText(R.string.button_stop);
		} else {
			button.setText(R.string.button_start);
		}
		if (reise != null) {
			reise.refreshList();
		}
	}

	private void wirklichLoeschen() {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setMessage(getString(R.string.wirklichLoeschen));
		alertBuilder.setCancelable(true);
		alertBuilder.setPositiveButton(getString(R.string.ja),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						reise.loescheGewaehlteTS();
						invalidateOptionsMenu();
						dialog.cancel();
					}
				});
		alertBuilder.setNegativeButton(getString(R.string.nein),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert = alertBuilder.create();
		alert.show();
	}

}
