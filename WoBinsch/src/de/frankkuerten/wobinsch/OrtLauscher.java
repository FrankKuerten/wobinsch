package de.frankkuerten.wobinsch;

import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class OrtLauscher implements LocationListener {

	private MainActivity activity;

	public OrtLauscher(MainActivity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void onLocationChanged(Location ort) {
		Position pos = new Position(ort, activity.getVehikel());
		DBDAOPosition dbdao = DBDAOPosition.instance(activity);
		dbdao.insert(pos);

		// Ankerwache
		List<Position> aktTS = dbdao.readNewestBlock();
		if (activity.getVehikel() == Vehikel.VA
				&& Reise.isAnkerketteLos(aktTS)) {
			activity.gibLauteNachricht();
		}
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Noch keine Verwendung
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Noch keine Verwendung
	}

	@Override
	public void onProviderDisabled(String provider) {
		// Noch keine Verwendung
	}
}
