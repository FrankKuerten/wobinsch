package de.frankkuerten.wobinsch;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
		TeilStrecke ts = (TeilStrecke)getItem(position);
		view.setId((int) getItemId(position));
		
		CheckBox gewaehltFeld = (CheckBox) view.findViewById(R.id.Gewaehlt);
		TextView vehikelFeld = (TextView) view.findViewById(R.id.Vehikel);
		TextView punkteFeld = (TextView) view.findViewById(R.id.MessPunkte);
		TextView datumVonFeld = (TextView) view.findViewById(R.id.DatumVon);
		TextView datumBisFeld = (TextView) view.findViewById(R.id.DatumBis);
		TextView laengeFeld = (TextView) view.findViewById(R.id.Gesamtlaenge);
		TextView labelLaenge = (TextView) view.findViewById(R.id.labelLaenge);
		TextView geschwFeld = (TextView) view.findViewById(R.id.SchnittGeschwindigkeit);
		TextView labelGeschw = (TextView) view.findViewById(R.id.labelGeschw);
		
		gewaehltFeld.setChecked(ts.isGewaehlt());
		vehikelFeld.setText(ts.getVehikel().toString());
		punkteFeld.setText(String.valueOf(ts.getPositionen().size()));
		datumVonFeld.setText(Position.df.format(ts.getDatumVon()));
		datumBisFeld.setText(Position.df.format(ts.getDatumBis()));
		laengeFeld.setText(String.valueOf(ts.getGesamtLaenge()));
		geschwFeld.setText(String.valueOf(ts.getSchnittGeschwindigkeit()));
		labelLaenge.setText(ts.getLabelLaenge());
		labelGeschw.setText(ts.getLabelGeschw());
	}

}
