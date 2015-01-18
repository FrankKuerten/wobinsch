package de.frankkuerten.wobinsch;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="wpt")
public class WegPunkt extends Position {

	public WegPunkt(Position pos) {
		super();
		this.setOrt(pos.getOrt());
		this.setVehikel(pos.getVehikel());
	}

	@Element(name="name")
	public String getVehikelName(){
		if (getVehikel() == null){
			return null;
		}
		return getVehikel().toString();
	}
	
	@Element(name="name")
	public void setVehikelName(String name){
		// nix
	}

	@Override
	public float getSpeed() {
		return 0;
	}

	@Override
	public void setSpeed(float val) {
		// nix
	}
	
}
