package de.frankkuerten.wobinsch;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="trkseg")
public class TeilStrecke {
	
	@ElementList(inline=true)
	private List<Position> positionen = new Vector<Position>();
	private float gesamtLaenge;
	private float schnittGeschwindigkeit;
	private boolean gewaehlt = false;
	public List<Position> getPositionen() {
		return positionen;
	}
	public void setPositionen(List<Position> positionen) {
		this.positionen = positionen;
	}
	public float getGesamtLaenge() {
		return gesamtLaenge;
	}
	public void setGesamtLaenge(float gesamtLaenge) {
		this.gesamtLaenge = gesamtLaenge;
	}
	public float getSchnittGeschwindigkeit() {
		return schnittGeschwindigkeit;
	}
	public void setSchnittGeschwindigkeit(float schnittGeschwindigkeit) {
		this.schnittGeschwindigkeit = schnittGeschwindigkeit;
	}
	
	public boolean isGewaehlt() {
		return gewaehlt;
	}
	public void setGewaehlt(boolean gewaehlt) {
		this.gewaehlt = gewaehlt;
	}
	public void addPosition(Position pos){
		this.positionen.add(pos);
	}
	
	public Date getDatumVon(){
		if (positionen == null || positionen.isEmpty()){
			return null;
		}
		if (positionen.get(0) == null || positionen.get(0).getOrt() == null){
			return null;
		}
		return new Date(positionen.get(0).getOrt().getTime());
	}
	
	public Date getDatumBis(){
		if (positionen == null || positionen.isEmpty()){
			return null;
		}
		int bis = positionen.size() - 1;
		if (positionen.get(bis) == null || positionen.get(bis).getOrt() == null){
			return null;
		}
		return new Date(positionen.get(bis).getOrt().getTime());
	}
	
	public Vehikel getVehikel(){
		if (positionen == null || positionen.isEmpty()){
			return null;
		}
		if (positionen.get(0) == null){
			return null;
		}
		return positionen.get(0).getVehikel();
	}
}