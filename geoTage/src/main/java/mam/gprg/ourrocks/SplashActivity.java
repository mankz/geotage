package mam.gprg.ourrocks;

import java.util.Timer;
import java.util.TimerTask;

import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.dialogs.ErrorDialog;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class SplashActivity extends FragmentActivity {

	GeoTage app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		app = (GeoTage) getApplication();
		
		getAllowClient();

	}
 
  	void getAllowClient() {

		Listener<JSONObject> listener = new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				ResponseParser parser = new ResponseParser(response);
				app.log(SplashActivity.this, response.toString());
				if (parser.getStatusCode() == API.STATUS_OK) {
					boolean allow = parser.getDataBoolean();
					if (allow) {
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {

							@Override
							public void run() {
								Intent intent = new Intent(SplashActivity.this,
										MainActivity.class);
								startActivity(intent);
								SplashActivity.this.finish();

							}
						}, 0);

					} else {
						ErrorDialog dialog = new ErrorDialog();
						Bundle bundle = new Bundle();
						bundle.putString(ErrorDialog.ERROR_MESSAGE,
								"You are not allowed to access this app!");
						dialog.setArguments(bundle);
						dialog.show(getSupportFragmentManager(), "error-dialog");
					}
				} 
			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
			}
		};
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				API.CLIENTS_ALLOW, null, listener, errorListener);
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}
}
