package mam.gprg.ourrocks.adapter;

import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.model.Category;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter<Category> {

	Holder holder;

	public CategoryAdapter(Context context, int resource) {
		super(context, resource);

	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		if (v == null) {
			v = LayoutInflater.from(getContext()).inflate(R.layout.simple_text,
					null);
			holder = new Holder();
			holder.tv = (TextView) v.findViewById(R.id.simple_text_text);
			v.setTag(holder);
		}

		holder = (Holder) v.getTag();
		Category c = getItem(position);
		holder.tv.setText(c.getName());
		return v;
	}

	@Override
	public View getDropDownView(int position, View v, ViewGroup parent) {
		if (v == null) {
			v = LayoutInflater.from(getContext()).inflate(R.layout.simple_text,
					null);
			holder = new Holder();
			holder.tv = (TextView) v.findViewById(R.id.simple_text_text);
			v.setTag(holder);
		}

		holder = (Holder) v.getTag();
		Category c = getItem(position);
		holder.tv.setText(c.getName());
		return v;
	}

	class Holder {
		TextView tv;
	}

}
