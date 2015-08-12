package mam.gprg.ourrocks.userdatas;

import java.util.ArrayList;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.model.Rock;
import mam.gprg.ourrocks.model.db.RockTable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DraftActivity extends Activity implements OnItemClickListener {

	ListView lv;
	DraftRockAdapter adapter;
	RockTable rockTbl;
	GeoTage app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draft_activity);
		app = (GeoTage) getApplication();
		lv = (ListView) findViewById(R.id.draft_activity_lvDraft);
		adapter = new DraftRockAdapter(this, R.layout.draft_adapter);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		rockTbl = new RockTable(this);
		rockTbl.open();
		getDrafts();
	}

	void getDrafts() {
		ArrayList<Rock> rocks = rockTbl.readAll(app.getPref().getInt("user_id",
				0));
		if (rocks.size() > 0) {
			for (Rock r : rocks) {
				adapter.add(r);
			}
		}
	}

	public void goBack(View v) {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, AddPlaceActivity.class);
		intent.putExtra(AddPlaceActivity.DRAFT_ROCK_ID, adapter.getItem(arg2)
				.getId());
		intent.putExtra(AddPlaceActivity.FROM_WHERE,
				AddPlaceActivity.FROM_DRAFT);
		intent.putExtra(AddPlaceActivity.MODE, AddPlaceActivity.MODE_EDIT);
		startActivity(intent);
	}

	public void deleteDraft(int position, Rock draft){
		app.shortToast(this, "delete " + draft.getId());
		if(rockTbl.delete(draft.getId()) > 0) { 
			adapter.remove(draft);
			adapter.notifyDataSetChanged();
		}
	}
	class DraftRockAdapter extends ArrayAdapter<Rock> {

		Holder holder;
		OnClickListener onClickListener;

		GeoTage app;

		public DraftRockAdapter(Context context, int resource) {
			super(context, resource);
			app = (GeoTage) ((Activity) context).getApplication();

		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			if (v == null) {
				v = LayoutInflater.from(getContext()).inflate(
						R.layout.draft_adapter, null);
				holder = new Holder();
				holder.tvName = (TextView) v
						.findViewById(R.id.draft_adapter_tvName);
				holder.tvAddress = (TextView) v
						.findViewById(R.id.draft_adapter_tvAddress);
				holder.tvDescription = (TextView) v
						.findViewById(R.id.draft_adapter_tvDescription);
				holder.btnDelete = (Button) v
						.findViewById(R.id.draft_adapter_btnDelete);
				v.setTag(holder);
			}
			holder = (Holder) v.getTag();
			holder.tvName.setText(getItem(position).getName());
			holder.tvAddress.setText(getItem(position).getAddress());
			holder.tvDescription.setText(getItem(position).getDesc());
			onClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					switch (arg0.getId()) {
					case R.id.draft_adapter_tvName:
						break;
					case R.id.draft_adapter_tvAddress:
						break;
					case R.id.draft_adapter_tvDescription:
						break;
					case R.id.draft_adapter_btnDelete:
						AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
						builder.setTitle("Confirm");
						builder.setMessage("Are you sure to delete " + getItem(position).getName() + " ?");
						builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								deleteDraft(position, getItem(position));

							}
						});
						builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
							
							}
						});
						
						builder.show();
						break;
					}
				}
			};

			holder.tvName.setOnClickListener(onClickListener);
			holder.tvAddress.setOnClickListener(onClickListener);
			holder.tvDescription.setOnClickListener(onClickListener);
			holder.btnDelete.setOnClickListener(onClickListener);

			return v;
		}

		boolean showAction;

		public void showAction(boolean show) {
			this.showAction = show;
		}
	}

	class Holder {
		TextView tvName, tvAddress, tvDescription;
		Button btnDelete;
	}

}
