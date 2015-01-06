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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

@Root(name="gpx")
public class Reise implements OnItemClickListener{

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
		dbdao = DBDAOPosition.instance(activity);
		
		locList.setOnItemClickListener(this);
	}

	public void addPosition(Position pos){
		dbdao.insert(pos);
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
	
	public void loescheGewaehlteTS(){
		for (TeilStrecke ts : this.positionen){
			if (ts.isGewaehlt()){
				for (Position pos : ts.getPositionen()){
					dbdao.delete(pos);
				}
			}
		}
		initFromDB();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		positionen.get(position).setGewaehlt(!positionen.get(position).isGewaehlt());
		listAdapter.notifyDataSetChanged();
	}
	
	@Path("trk")
	@ElementList(inline=true)
	public List<TeilStrecke> getGewaehlteTS(){
		List<TeilStrecke> erg = new Vector<TeilStrecke>();
		for (TeilStrecke ts : positionen){
			if (ts.isGewaehlt()){
				erg.add(ts);
			}
		}
		return erg;
	}
	
	@Path("trk")
	@ElementList(inline=true)
	public void setGewaehlteTS(List<TeilStrecke> pos){
		positionen = pos;
	}
}
