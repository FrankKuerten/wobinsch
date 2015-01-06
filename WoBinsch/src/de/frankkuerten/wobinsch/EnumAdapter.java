package de.frankkuerten.wobinsch;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EnumAdapter<T> extends ArrayAdapter<T> {

	public EnumAdapter(Context context, int resource, int textViewResourceId,
			List<T> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public EnumAdapter(Context context, int resource, int textViewResourceId,
			T[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public EnumAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public EnumAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
	}

	public EnumAdapter(Context context, int resource, T[] objects) {
		super(context, resource, objects);
	}

	public EnumAdapter(Context context, int resource) {
		super(context, resource);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		changeText(view, position);
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getDropDownView(position, convertView, parent);
		changeText(view, position);
		return view;
	}

	private void changeText(View view, int position) {
		if (view != null && view instanceof TextView) {
			Enum e = (Enum) getItem(position);
			Resources res = getContext().getResources();
			int id = res.getIdentifier(
					e.getClass().getSimpleName() + '_' + e.name(), "string",
					getContext().getPackageName());
			if (id > 0) {
				String text = getContext().getString(id);
				((TextView) view).setText(text);
			}
		}
	}
}
