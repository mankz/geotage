package mam.gprg.ourrocks.adapter;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.model.Rock;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RockAdapter extends ArrayAdapter<Rock> {

	Holder holder;
	OnClickListener onClickListener;

	GeoTage app;

	public interface RockAdapterListener {
		public void onShowUser(Context ctx, int userId);

		public void onShowRock(Context ctx, Rock rock);

		public void onDelete(Context ctx, int rockId, String rockName);

		public void onEdit(Context ctx, int rockId);

		public void onLastPosition();
	}

	RockAdapterListener listener;

	public RockAdapter(Context context, int resource) {
		super(context, resource);
		app = (GeoTage) ((Activity) context).getApplication();

	}

	public void setListener(RockAdapterListener listener) {
		this.listener = listener;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		if (v == null) {
			v = LayoutInflater.from(getContext()).inflate(R.layout.info_item,
					null);
			holder = new Holder();
			holder.v = v.findViewById(R.id.info_item_view);
			holder.ivAvatar = (ImageView) v.findViewById(R.id.info_item_avatar);
			holder.tvName = (TextView) v.findViewById(R.id.info_item_tvName);
			holder.tvRockCategory = (TextView) v
					.findViewById(R.id.info_item_tvRocks);
			holder.tvAddress = (TextView) v
					.findViewById(R.id.info_item_tvAddress);
			holder.tvDescription = (TextView) v
					.findViewById(R.id.info_item_tvDescription);
			holder.tvDate = (TextView) v.findViewById(R.id.info_item_tvDate);
			holder.panelAction = v.findViewById(R.id.info_item_panelAction);
			holder.btnEdit = (Button) v.findViewById(R.id.info_item_btnEdit);
			holder.btnDelete = (Button) v
					.findViewById(R.id.info_item_btnDelete);
			v.setTag(holder);
		}
		holder = (Holder) v.getTag();
		String avatar = getItem(position).getUser().getAvatar();
		app.log(this, avatar);
		if (avatar != null
				&& (avatar.endsWith(".jpg") || avatar.endsWith(".png") || avatar
						.endsWith(".gif")))
			((GeoTage) getContext().getApplicationContext())
					.getUniversalImageLoader().displayImage(
							API.IMAGE_BASE_URL + avatar, holder.ivAvatar,
							app.getCircleDisplayOption(),
							app.getFirstAnimationDisplay());
		else
			holder.ivAvatar.setImageResource(R.drawable.avatar);
		holder.tvName.setText(getItem(position).getName());
		holder.tvRockCategory
				.setText(getItem(position).getCategory().getName());
		holder.tvAddress.setText(getItem(position).getAddress());
		holder.tvDescription.setText(getItem(position).getDesc());
		holder.tvDate.setText("at " + getItem(position).getDate());
		holder.panelAction
				.setVisibility(app.getPref().getInt("user_id", 0) == getItem(
						position).getUserId() ? View.VISIBLE : View.GONE);
		onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.info_item_view:
					// listener.onShowRock(getContext(),
					// getItem(position).getId());
					break;
				case R.id.info_item_avatar:
					if (listener != null)
						listener.onShowUser(getContext(), getItem(position)
								.getUserId());
					break;
				case R.id.info_item_tvName:
					// if (listener != null)
					// listener.onShowRock(getContext(), getItem(position)
					// .getId());
					break;
				case R.id.info_item_tvRocks:
					// if (listener != null)
					// listener.onShowRock(getContext(), getItem(position)
					// .getId());
					break;
				case R.id.info_item_tvAddress:
					// if (listener != null)
					// listener.onShowRock(getContext(), getItem(position)
					// .getId());
					break;
				case R.id.info_item_tvDescription:
					// if (listener != null)
					// listener.onShowRock(getContext(), getItem(position)
					// .getId());
					break;
				case R.id.info_item_btnEdit:
					if (listener != null)
						listener.onEdit(getContext(), getItem(position).getId());
					break;
				case R.id.info_item_btnDelete:
					if (listener != null)
						listener.onDelete(getContext(), getItem(position)
								.getId(), getItem(position).getName());
					break;
				}
			}
		};

		holder.ivAvatar.setOnClickListener(onClickListener);
		holder.tvName.setOnClickListener(onClickListener);
		holder.tvRockCategory.setOnClickListener(onClickListener);
		holder.tvAddress.setOnClickListener(onClickListener);
		holder.tvDescription.setOnClickListener(onClickListener);
		holder.btnEdit.setOnClickListener(onClickListener);
		holder.btnDelete.setOnClickListener(onClickListener);

		if (position == getCount() - 1)
			listener.onLastPosition();
		return v;
	}

	boolean showAction;

	public void showAction(boolean show) {
		this.showAction = show;
	}

	class Holder {
		View v;
		ImageView ivAvatar;
		TextView tvName, tvRockCategory, tvAddress, tvDescription, tvDate;
		View panelAction;
		Button btnEdit, btnDelete;
	}
}
