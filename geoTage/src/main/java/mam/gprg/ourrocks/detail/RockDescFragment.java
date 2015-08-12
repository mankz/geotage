package mam.gprg.ourrocks.detail;

import info.androidhive.imageslider.FullScreenViewActivity;

import java.util.ArrayList;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.model.Rock;
import mam.gprg.ourrocks.model.RockPhoto;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class RockDescFragment extends Fragment implements OnItemClickListener,
		OnClickListener {

	GeoTage app;
	View layout;
	// GridView gridImages;

	TextView tvTitle, tvDesc, tvAddress, tvFormation, tvAddedBy;

	FrameLayout frame1, frame2, frame3;
	ImageView iv1, iv2, iv3;
	ProgressBar p1, p2, p3;
	TextView btnShowMore;
	Button btnShowLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (GeoTage) getActivity().getApplication();
	}

	public void setValue() { 
		app.log(this, rock.getLatitude() + " / " + rock.getLongitude());
		tvTitle.setText(rock.getName() + " - " + rock.getCategory().getName());
		tvDesc.setText(rock.getDesc());
		tvAddress.setText(rock.getAddress());
		tvFormation.setText(rock.getFormation());
		tvAddedBy.setText(rock.getUser().getName());

		ArrayList<RockPhoto> photos = rock.getPhotos();
		if (photos != null && photos.size() > 0) {
			app.getUniversalImageLoader().displayImage(
					API.IMAGE_BASE_URL + photos.get(0).getUrl(), iv1,
					app.getSimpleDisplayOption(),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							super.onLoadingStarted(imageUri, view);
							frame1.setVisibility(View.VISIBLE);
							p1.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							// holder.prog.setVisibility(View.GONE);
							super.onLoadingComplete(imageUri, view, loadedImage);
							p1.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							// TODO Auto-generated method stub
							super.onLoadingFailed(imageUri, view, failReason);
							p1.setVisibility(View.GONE);
						}
					});
		} else {
			frame1.setVisibility(View.GONE);
		}

		if (photos != null && photos.size() > 1) {
			app.getUniversalImageLoader().displayImage(
					API.IMAGE_BASE_URL + photos.get(1).getUrl(), iv2,
					app.getSimpleDisplayOption(),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							super.onLoadingStarted(imageUri, view);
							frame2.setVisibility(View.VISIBLE);
							p2.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							// holder.prog.setVisibility(View.GONE);
							super.onLoadingComplete(imageUri, view, loadedImage);
							p2.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							// TODO Auto-generated method stub
							super.onLoadingFailed(imageUri, view, failReason);
							p2.setVisibility(View.GONE);
						}
					});
		} else {
			frame2.setVisibility(View.GONE);
		}
		if (photos != null && photos.size() > 2) {
			app.getUniversalImageLoader().displayImage(
					API.IMAGE_BASE_URL + photos.get(2).getUrl(), iv3,
					app.getSimpleDisplayOption(),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							super.onLoadingStarted(imageUri, view);
							frame3.setVisibility(View.VISIBLE);
							p3.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							// holder.prog.setVisibility(View.GONE);
							super.onLoadingComplete(imageUri, view, loadedImage);
							p3.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							// TODO Auto-generated method stub
							super.onLoadingFailed(imageUri, view, failReason);
							p3.setVisibility(View.GONE);
						}
					});
		} else {
			frame3.setVisibility(View.INVISIBLE);
		}

		if (photos != null && photos.size() > 3)
			btnShowMore.setVisibility(View.VISIBLE);
		else
			btnShowMore.setVisibility(View.GONE);

		// if (rock.getPhotos() != null && rock.getPhotos().size() > 0) {
		// for (RockPhoto p : rock.getPhotos()) {
		// adapter.add(p);
		// adapter.notifyDataSetChanged();
		// }
		// }

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.rock_desc_fragment, null);
		tvTitle = (TextView) layout
				.findViewById(R.id.rock_desc_fragment_tvTitle);
		tvDesc = (TextView) layout.findViewById(R.id.rock_desc_fragment_tvDesc);
		tvAddress = (TextView) layout
				.findViewById(R.id.rock_desc_fragment_tvAddress);
		tvFormation = (TextView) layout
				.findViewById(R.id.rock_desc_fragment_tvFormation);
		tvAddedBy = (TextView) layout
				.findViewById(R.id.rock_desc_fragment_tvAddedBy);

		frame1 = (FrameLayout) layout.findViewById(R.id.rock_desc_frag_frame1);
		frame2 = (FrameLayout) layout.findViewById(R.id.rock_desc_frag_frame2);
		frame3 = (FrameLayout) layout.findViewById(R.id.rock_desc_frag_frame3);

		iv1 = (ImageView) layout.findViewById(R.id.rock_desc_fragment_iv1);
		iv2 = (ImageView) layout.findViewById(R.id.rock_desc_fragment_iv2);
		iv3 = (ImageView) layout.findViewById(R.id.rock_desc_fragment_iv3);
		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		iv3.setOnClickListener(this);
		p1 = (ProgressBar) layout.findViewById(R.id.rock_desc_fragment_prog1);
		p2 = (ProgressBar) layout.findViewById(R.id.rock_desc_fragment_prog2);
		p3 = (ProgressBar) layout.findViewById(R.id.rock_desc_fragment_prog3);

		btnShowMore = (TextView) layout
				.findViewById(R.id.rock_desc_frag_tvMoreImages);
		btnShowLocation = (Button) layout
				.findViewById(R.id.rock_desc_fragment_btnShowLocation);
		btnShowMore.setOnClickListener(this);
		btnShowLocation.setOnClickListener(this);
		// gridImages = (GridView)
		// layout.findViewById(R.id.rock_desc_gridImages);
		// adapter = new RockImageAdapter();
		// gridImages.setAdapter(adapter);
		// gridImages.setOnItemClickListener(this);

		return layout;
	}

	// RockImageAdapter adapter;

	public void setRock(Rock rock) {
		this.rock = rock;
		if (rock != null)
			setValue();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		rock = getArguments().getParcelable("rock");
		if (rock.getName() != null)
			setValue();

	}

	class RockImageAdapter extends BaseAdapter {

		RockImageHolder holder;
		BitmapFactory.Options options = new Options();
		ArrayList<RockPhoto> photos = new ArrayList<RockPhoto>();

		public RockImageAdapter() {
			options.inSampleSize = 2;
		}

		public void add(RockPhoto photo) {
			photos.add(photo);
		}

		@Override
		public int getCount() {
			return photos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return this.photos.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View v, ViewGroup arg2) {
			if (v == null) {
				v = LayoutInflater.from(getActivity()).inflate(
						R.layout.rock_image_adapter, null);
				holder = new RockImageHolder();
				holder.ivImg = (ImageView) v
						.findViewById(R.id.rock_image_adapter_iv);
				holder.tvCaption = (TextView) v
						.findViewById(R.id.rock_image_adapter_tv);
				holder.prog = (ProgressBar) v
						.findViewById(R.id.rock_image_adapter_progress);
				v.setTag(holder);
			}

			holder = (RockImageHolder) v.getTag();

			app.getUniversalImageLoader().displayImage(
					API.IMAGE_BASE_URL + photos.get(position).getUrl(),
					holder.ivImg, app.getSimpleDisplayOption(),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							super.onLoadingStarted(imageUri, view);
							// holder.prog.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							// holder.prog.setVisibility(View.GONE);
							super.onLoadingComplete(imageUri, view, loadedImage);
							photos.get(position).setDisplayed(true);
							notifyDataSetChanged();
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							// TODO Auto-generated method stub
							super.onLoadingFailed(imageUri, view, failReason);
							photos.get(position).setDisplayed(true);
							notifyDataSetChanged();
						}
					});

			holder.prog
					.setVisibility(photos.get(position).isDisplayed() ? View.GONE
							: View.VISIBLE);
			// holder.ivImg.setImageBitmap(BitmapFactory.decodeResource(
			// getResources(), this.photos.get(position).getUrl(), options));
			return v;
		}

	}

	class RockImageHolder {
		ImageView ivImg;
		ProgressBar prog;
		TextView tvCaption;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//
		// Intent intent = new Intent(getActivity(), ImagesActivity.class);
		// intent.putParcelableArrayListExtra("photos", rock.getPhotos());
		// startActivity(intent);
		onOpenImage(0);
	}

	Rock rock;

	public void onOpenImage(int position) {
		Intent intent = new Intent(getActivity(), FullScreenViewActivity.class);
		ArrayList<String> paths = new ArrayList<String>();
		for (int i = 0; i < rock.getPhotos().size(); i++) {
			paths.add(rock.getPhotos().get(i).getUrl());
			app.log(this, paths.get(i));
		}
		intent.putStringArrayListExtra("paths", paths);
		intent.putExtra("position", position);
		startActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.rock_desc_fragment_iv1:
			onOpenImage(0);
			break;
		case R.id.rock_desc_fragment_iv2:
			onOpenImage(1);
			break;
		case R.id.rock_desc_fragment_iv3:
			onOpenImage(2);
			break;
		case R.id.rock_desc_frag_tvMoreImages:
			onOpenImage(3);
			break;
		case R.id.rock_desc_fragment_btnShowLocation:
			Intent intent = new Intent(getActivity(), RockLocationAct.class);
			intent.putExtra("rock", rock);
			startActivity(intent);
			break;
		}

	}

}
