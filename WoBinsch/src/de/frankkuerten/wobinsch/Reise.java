package de.frankkuerten.wobinsch;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

@Root(name = "gpx")
public class Reise implements OnItemClickListener {

	private List<TeilStrecke> positionen = new Vector<TeilStrecke>();
	private MainActivity activity;
	private TeilStreckeListAdapter listAdapter;
	private DBDAOPosition dbdao;

	public Reise(MainActivity activity) {
		super();
		this.activity = activity;

		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listAdapter = new TeilStreckeListAdapter(positionen, inflater);

		ListView locList = (ListView) activity.findViewById(R.id.loc);
		locList.setAdapter(listAdapter);
		dbdao = DBDAOPosition.instance(activity);

		locList.setOnItemClickListener(this);
	}

	public void dump2gpx() {
		if (!this.positionen.isEmpty()) {
			Date jetzt = new Date();
			File fi = activity.getExternalFilesDir("");
			String name = Position.df.format(jetzt).replace(':', '-');
			File result = new File(fi + "/TRK-" + name + ".gpx");

			Serializer serializer = new Persister();
			try {
				serializer.write(this, result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void initFromDB() {
		positionen.clear();
		gruppiereTeilStrecken(dbdao.readAllPositions());
		listAdapter.notifyDataSetChanged();
	}

	private void gruppiereTeilStrecken(List<Position> posen) {
		Position posVorher = null;
		TeilStrecke ts = null;
		float[] entfernung = { 0 };

		for (Position pos : posen) {
			if (posVorher == null
			// oder Vehikel gewechselt
					|| pos.getVehikel() != posVorher.getVehikel()
					// oder 10 Minuten Unterbrechung
					|| pos.getOrt().getTime() - posVorher.getOrt().getTime() > 600000) {
				ts = new TeilStrecke();
				positionen.add(ts);
			} else {
				Location.distanceBetween(posVorher.getLat(),
						posVorher.getLon(), pos.getLat(), pos.getLon(),
						entfernung);
				if (entfernung.length > 0) {
					ts.setGesamtLaenge(ts.getGesamtLaenge() + entfernung[0]);
				}
			}
			if (ts.getGesamtLaenge() > 0) {
				long anfang = ts.getPositionen().get(0).getOrt().getTime();
				long ende = pos.getOrt().getTime();
				ts.setSchnittGeschwindigkeit(ts.getGesamtLaenge()
						/ ((ende - anfang) / 1000));
			}

			ts.addPosition(pos);
			posVorher = pos;
		}
	}

	public static boolean isAnkerketteLos(List<Position> aktTS) {
		// Ankerwache
		if (aktTS == null || aktTS.size() < 2
				|| aktTS.get(0).getVehikel() != Vehikel.VA) {
			return false;
		}
		Position erste = aktTS.get(0);
		Position letzte = aktTS.get(aktTS.size() - 1);
		float[] entfernung = { 0 };
		Location.distanceBetween(erste.getLat(),
				erste.getLon(), letzte.getLat(), letzte.getLon(),
				entfernung);
		// TODO: Magic number 5 Meter
		if (entfernung.length > 0 && entfernung[0] > 5) {
			return true;
		}
		return false;
	}

	public void loescheGewaehlteTS() {
		for (TeilStrecke ts : this.positionen) {
			if (ts.isGewaehlt()) {
				for (Position pos : ts.getPositionen()) {
					dbdao.delete(pos);
				}
			}
		}
		initFromDB();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		positionen.get(position).setGewaehlt(
				!positionen.get(position).isGewaehlt());
		listAdapter.notifyDataSetChanged();
	}

	@Path("trk")
	@ElementList(inline = true)
	public List<TeilStrecke> getGewaehlteTS() {
		List<TeilStrecke> erg = new Vector<TeilStrecke>();
		for (TeilStrecke ts : positionen) {
			if (ts.isGewaehlt()) {
				erg.add(ts);
			}
		}
		return erg;
	}

	@Path("trk")
	@ElementList(inline = true)
	public void setGewaehlteTS(List<TeilStrecke> pos) {
		positionen = pos;
	}
	
	public void refreshList(){
		if (listAdapter != null){
			listAdapter.notifyDataSetChanged();
		}
	}
}
