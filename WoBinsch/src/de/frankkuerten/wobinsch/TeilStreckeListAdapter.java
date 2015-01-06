package de.frankkuerten.wobinsch;

import java.util.Date;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TeilStreckeListAdapter extends BaseAdapter {
	
	private List<TeilStrecke> teilStrecken;
	private LayoutInflater inflater;

	public TeilStreckeListAdapter(List<TeilStrecke> teilStrecken, LayoutInflater inflater) {
		super();
		this.teilStrecken = teilStrecken;
		this.inflater =  inflater;
	}

	@Override
	public int getCount() {
		return teilStrecken.size();
	}

	@Override
	public Object getItem(int position) {
		return teilStrecken.get(position);
	}

	@Override
	public long getItemId(int position) {
		TeilStrecke pos = teilStrecken.get(position);
		return pos.getPositionen().get(0).getOrt().getTime();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout itemView = (LinearLayout) inflater.inflate(R.layout.ts_zeile, parent, false);
		bindView(itemView, position);
		return itemView;
	}
	
	private void bindView(LinearLayout view, int position) {
		TeilStrecke pos = (TeilStrecke)getItem(position);
		view.setId((int) getItemId(position));
		TextView datumFeld = (TextView) view.findViewById(R.id.Datum);
		TextView geschwFeld = (TextView) view.findViewById(R.id.SchnittGeschwindigkeit);
		TextView laengeFeld = (TextView) view.findViewById(R.id.Gesamtlaenge);
		TextView vehikelFeld = (TextView) view.findViewById(R.id.Vehikel);
		
		datumFeld.setText(Position.df.format(new Date(pos.getPositionen().get(0).getOrt().getTime())));
		laengeFeld.setText("L:" + String.valueOf(pos.getGesamtLaenge()));
		vehikelFeld.setText("Vehikel:" + pos.getPositionen().get(0).getVehikel());
		geschwFeld.setText("Geschw.:" + String.valueOf(pos.getSchnittGeschwindigkeit()));
	}

}
