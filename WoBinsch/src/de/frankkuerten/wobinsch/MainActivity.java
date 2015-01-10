package de.frankkuerten.wobinsch;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
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

public class MainActivity extends ActionBarActivity implements OnClickListener,
		OnItemSelectedListener {

	private OrtLauscher lauscher;
	private LocationManager locationManager;
	private boolean started = false;
	private Button button;
	private Spinner vehikelFeld;
	private Reise reise;

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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.alleAnzeigen) {
			reise = new Reise(this);
			reise.initFromDB();
			return true;
		}

		if (id == R.id.exportieren) {
			if (reise != null) {
				reise.dump2gpx();
			}
			return true;
		}

		if (id == R.id.loeschen) {
			if (reise != null && reise.hasGewaehlteTS()) {
				wirklichLoeschen();
			}
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
		if (view.getId() == R.id.vehikel && started) {
			stop();
			start();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Keine Aktion nötig

	}

	private Uri gibAlarm() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		if (alert == null) {
			// alert is null, using backup
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

			// I can't see this ever being null (as always have a default
			// notification) but just incase
			if (alert == null) {
				// alert backup is null, using 2nd backup
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}

	public void gibLauteNachricht() {
		// Notification Manager zitieren
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.anker)
				.setAutoCancel(true)
				.setContentTitle(getString(R.string.Alarm))
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
		if (reise != null){
			reise.refreshList();
		}
	}
	
	private void wirklichLoeschen(){
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(getString(R.string.wirklichLoeschen));
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton(getString(R.string.ja),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	reise.loescheGewaehlteTS();
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
