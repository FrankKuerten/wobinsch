package de.frankkuerten.wobinsch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.location.Location;

@Root(name="trkpt")
public class Position {
	private Location ort;
	private Vehikel vehikel;
	public static SimpleDateFormat df; 

	{
		df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public Position(Location ort, Vehikel vehikel) {
		super();
		this.ort = ort;
		this.vehikel = vehikel;
	}
	
	public Position(){
		super();
		this.ort = new Location("GPS");
	}

	@Element(name="time")
	public String getUTCzeit(){
		if (ort == null){
			return "";
		}
		return df.format(new Date(ort.getTime()));
	}
	@Element(name="time")
	public void setUTCzeit(String val){
		if (ort == null){
			ort = new Location("GPS");
		}
		try {
			ort.setTime(df.parse(val).getTime());
		} catch (ParseException e) {
			ort.setTime(new Date().getTime());
		}
	}

	public Location getOrt() {
		return ort;
	}

	public void setOrt(Location ort) {
		this.ort = ort;
	}
	
	public Vehikel getVehikel() {
		return vehikel;
	}

	public void setVehikel(Vehikel vehikel) {
		this.vehikel = vehikel;
	}

	@Attribute(name="lat")
	public double getLat(){
		if (ort != null){
			return ort.getLatitude();
		}
		return 0;
	}
	@Attribute(name="lat")
	public void setLat(double val){
		if (ort == null){
			ort = new Location("GPS");
		}
		ort.setLatitude(val);
	}
	
	@Attribute(name="lon")
	public double getLon(){
		if (ort != null){
			return ort.getLongitude();
		}
		return 0;
	}
	
	@Attribute(name="lon")
	public void setLon(double val){
		if (ort == null){
			ort = new Location("GPS");
		}
		ort.setLongitude(val);
	}
	
	@Element(name="speed",required=false)
	public float getSpeed(){
		if (ort != null){
			return ort.getSpeed();
		}
		return 0;
	}
	
	@Element(name="speed",required=false)
	public void setSpeed(float val){
		if (ort == null){
			ort = new Location("GPS");
		}
		ort.setSpeed(val);
	}
	
	@Override
	public String toString() {
		StringBuffer erg = new StringBuffer();
		if (ort != null) {
			erg.append("Am:").append(new Date(ort.getTime()).toString());
			erg.append(",Breite:").append(ort.getLatitude());
			erg.append(",Länge:").append(ort.getLongitude());
			erg.append(",Höhe:").append(ort.getAltitude());
			erg.append(",Vel.:").append(ort.getSpeed());
			erg.append(",Genau:").append(ort.getAccuracy());
		}
		return erg.toString();
	}

}
