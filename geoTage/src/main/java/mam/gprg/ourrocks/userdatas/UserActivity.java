package mam.gprg.ourrocks.userdatas;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.model.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class UserActivity extends Activity implements OnClickListener {

	GeoTage app;
	int userId;

	TextView tvName, tvDesc;
	ImageView ivAvatar;

	TextView tvBirthDay, tvEmail, tvPhone, tvAddress, tvCity, tvAbout, tvSex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (GeoTage) getApplication();
		userId = getIntent().getExtras().getInt("user_id");
		setContentView(R.layout.user);
		initView();
		getUserDetail();
	}

	public void goBack(View v) {
		finish();
	}

	void initView() {
		tvName = (TextView) findViewById(R.id.user_tvName);
		tvDesc = (TextView) findViewById(R.id.user_tvDesc);
		ivAvatar = (ImageView) findViewById(R.id.user_ivAvatar);
		tvBirthDay = (TextView) findViewById(R.id.user_tvDate);
		tvEmail = (TextView) findViewById(R.id.user_tvEmail);
		tvPhone = (TextView) findViewById(R.id.user_tvPhone);
		tvAddress = (TextView) findViewById(R.id.user_tvAddress);
		tvCity = (TextView) findViewById(R.id.user_tvCity);
		tvAbout = (TextView) findViewById(R.id.user_tvAbout);
		tvSex = (TextView) findViewById(R.id.user_tvSex);
		ivAvatar.setOnClickListener(this);

	}

	void getUserDetail() {
		Listener<String> slistener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				ResponseParser parser = new ResponseParser(response);
				if (parser.getStatusCode() == 200) {
					user = User.Parse(parser.getDataObject());
					setValues();
				}

			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		};
		StringRequest request = new StringRequest(API.USER_DETAIL_GET + userId,
				slistener, errorListener);
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	User user;

	void setValues() {
		String avatar = user.getAvatar();
		if (avatar != null
				&& (avatar.endsWith(".jpg") || avatar.endsWith(".png") || avatar
						.endsWith(".gif"))) {
			app.getUniversalImageLoader().displayImage(
					API.IMAGE_BASE_URL + avatar, ivAvatar,
					app.getCircleDisplayOption());

		}
		tvName.setText(user.getName());
		tvDesc.setText(user.getJob() + " at " + user.getCompany());
		tvBirthDay.setText(user.getBirthDate());
		tvEmail.setText(user.getEmail());
		tvPhone.setText(user.getPhone());
		if (!user.isShowPrivacy()) {
			tvEmail.setVisibility(View.GONE);
			tvPhone.setVisibility(View.GONE);
		} else {
			tvEmail.setVisibility(View.VISIBLE);
			tvPhone.setVisibility(View.VISIBLE);
		}
		tvAddress.setText(user.getAddress());
		tvCity.setText(user.getCity());
		tvAbout.setText(user.getAbout());
		tvSex.setText((user.getSex() == 0 ? "Male" : "Female"));
		if (user.getSex() == 0) {
			tvSex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.male, 0,
					0, 0);
		} else {
			tvSex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.female, 0,
					0, 0);

		}
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this, UserPhotoActivity.class);
		intent.putExtra("avatar", user.getAvatar());
		startActivity(intent);
	}
}
