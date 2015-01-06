package de.frankkuerten.wobinsch;

import java.util.List;
import java.util.Vector;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;

@Root(name="trkseg")
public class TeilStrecke {
	
	@ElementList(inline=true)
	private List<Position> positionen = new Vector<Position>();
	@Transient
	private float gesamtLaenge;
	@Transient
	private float schnittGeschwindigkeit;
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
	public void addPosition(Position pos){
		this.positionen.add(pos);
	}
}
