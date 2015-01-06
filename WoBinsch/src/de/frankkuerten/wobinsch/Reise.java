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
import android.view.LayoutInflater;
import android.widget.ListView;

@Root(name="gpx")
public class Reise {

	@Path("trk")
	@ElementList(inline=true)
	private List<TeilStrecke> positionen = new Vector<TeilStrecke>();
	private MainActivity activity;
	private TeilStreckeListAdapter listAdapter;
	private DBDAOPosition dbdao;

	public Reise(MainActivity activity) {
		super();
		this.activity = activity;
		
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listAdapter = new TeilStreckeListAdapter(positionen, inflater);
		
		ListView locList = (ListView) activity.findViewById(R.id.loc);
		locList.setAdapter(listAdapter);
		dbdao = new DBDAOPosition(activity);
	}

	public void addPosition(Position pos){
		dbdao.insert(pos);
//		positionen.add(pos);
//		listAdapter.notifyDataSetChanged();
	}
	
	public void clear(){
		this.positionen.clear();
	}
	
	public void dump2gpx(){
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
	
	public void initFromDB(){
		positionen.clear();
		positionen.addAll(dbdao.initFromDB());
		listAdapter.notifyDataSetChanged();
	}
}
