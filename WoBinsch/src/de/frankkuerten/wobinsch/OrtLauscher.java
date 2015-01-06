package de.frankkuerten.wobinsch;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class OrtLauscher implements LocationListener {

	private MainActivity activity;
	private Reise strecke;

	public OrtLauscher(MainActivity activity) {
		super();
		this.activity = activity;
		this.strecke = new Reise(activity);
	}

	@Override
	public void onLocationChanged(Location ort) {
		Position pos = new Position(ort, activity.getVehikel());
		strecke.addPosition(pos);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public MainActivity getCallBack() {
		return activity;
	}

	public void setCallBack(MainActivity callBack) {
		this.activity = callBack;
	}
}
