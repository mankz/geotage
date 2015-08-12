package info.androidhive.imageslider;

import info.androidhive.imageslider.adapter.FullScreenImageAdapter;
import info.androidhive.imageslider.helper.TouchPager;
import info.androidhive.imageslider.helper.Utils;

import java.util.ArrayList;

import mam.gprg.ourrocks.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class FullScreenViewActivity extends Activity {

	private Utils utils;
	private FullScreenImageAdapter adapter;
	private TouchPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);

		viewPager = (TouchPager) findViewById(R.id.pager);

		utils = new Utils(getApplicationContext());

		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		ArrayList<String> paths = getIntent().getStringArrayListExtra("paths");

		// adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
		// utils.getFilePaths());
		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, paths);
		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
	}
}