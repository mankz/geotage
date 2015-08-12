package mam.gprg.ourrocks.detail;

import java.util.Arrays;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.model.Rock;
import mam.gprg.ourrocks.util.ViewPagerAdapter;

import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class RocksDetailActivity extends FragmentActivity implements
		OnPageChangeListener {

	GeoTage app;
	Context mContext;
	View layout;
	ViewPager mPager;
	ViewPagerAdapter adapter;

	int rockId;
	Rock rock;

	int[] menuIds = new int[] { R.id.rock_detail_ibInfo,
			R.id.rock_detail_ibComments };

	RockDescFragment mRockDesc;
	// RockLocation mRockLocation;
	RockCommentsFragment mRockComments;

	NewCommentReceiver receiver;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		app = (GeoTage) getApplication();
		mContext = this;
		layout = LayoutInflater.from(mContext).inflate(R.layout.rock_detail,
				null);
		setContentView(layout);
		mPager = (ViewPager) findViewById(R.id.main_viewpager);
		adapter = new ViewPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) layout.findViewById(R.id.rock_detail_viewpager);
		mPager.setAdapter(adapter);
		mPager.setOnPageChangeListener(this);
		mPager.setOffscreenPageLimit(3);
		// adapter.addFragment(new AddPlaceFragment());
		app.setActiveMenu(layout, menuIds, 0);

		rockId = getIntent().getIntExtra("rock_id", 0);

		rock = getIntent().getParcelableExtra("rock");
		if (rock == null) {
			rock = new Rock();
		}
		Bundle args = new Bundle();
		args.putParcelable("rock", rock);
		mRockDesc = new RockDescFragment();
		// mRockLocation = new RockLocation();
		mRockComments = new RockCommentsFragment();
		mRockDesc.setArguments(args);
		// mRockLocation.setArguments(args);
		mRockComments.setArguments(args);
		adapter.addFragment(mRockDesc);
		// adapter.addFragment(mRockLocation);
		adapter.addFragment(mRockComments);

		receiver = new NewCommentReceiver();
		getRockDetail();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter("add_comment"));
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	public void onMenuClick(View v) {
		if (!v.isSelected()) {
			mPager.setCurrentItem(Arrays.binarySearch(menuIds, v.getId()));
			// setActiveMenu(Arrays.binarySearch(menuIds, v.getId()));
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		app.setActiveMenu(layout, menuIds, arg0);
	}

	void getRockDetail() {
		Listener<JSONObject> listener = new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				app.log(this, response.toString());
				ResponseParser parser = new ResponseParser(response);
				if (parser.getStatusCode() == 200) {
					rock = Rock.Parse(parser.getDataObject());
					mRockDesc.setRock(rock);
					mRockComments.setRock(rock);
				}
			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		};
		JsonObjectRequest request = new JsonObjectRequest(API.ROCK_DETAIL_GET
				+ rockId, null, listener, errorListener);
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	class NewCommentReceiver extends BroadcastReceiver {

		String action = "add_comment";

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String act = arg1.getAction(); // isinya add_comment
			Bundle extras = arg1.getExtras();
			// Toast.makeText(mContext, act, Toast.LENGTH_SHORT).show();

			Log.d("ROCK ID", extras.getInt("rock_id") + " / " + rockId);
			if (act.equalsIgnoreCase(action)) {
				if (extras.getInt("object_id") == rockId) {
					NotificationManager nm = (NotificationManager) mContext
							.getSystemService(Context.NOTIFICATION_SERVICE);
					nm.cancel(123); 
			//		nm.cancelAll();
					mRockComments.reload();
				}
			}
		}

	}
}
