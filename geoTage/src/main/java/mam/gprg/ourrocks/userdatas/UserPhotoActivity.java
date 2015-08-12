package mam.gprg.ourrocks.userdatas;

import info.androidhive.imageslider.helper.TouchImageView;
import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import android.app.Activity;
import android.os.Bundle;

public class UserPhotoActivity extends Activity {

	GeoTage app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (GeoTage) getApplication();
		setContentView(R.layout.user_photo);
		TouchImageView iv = (TouchImageView) findViewById(R.id.user_photo_imgDisplay);
		String url = getIntent().getStringExtra("avatar");
		app.getUniversalImageLoader().displayImage(url, iv,
				app.getSimpleDisplayOption(), app.getFirstAnimationDisplay());

	}

}
