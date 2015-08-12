package mam.gprg.ourrocks.userdatas;

import java.text.DecimalFormat;

import mam.gprg.ourrocks.R;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SetLocationActivity extends FragmentActivity implements
		LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
		OnMapClickListener, OnMarkerDragListener {

	GoogleApiClient client;

	SupportMapFragment mapFragment;

	TextView tvLocation;

	Location location;
	double latitude, longitude;

	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_location);

		latitude = getIntent().getDoubleExtra("latitude", 0);
		longitude = getIntent().getDoubleExtra("longitude", 0);

		tvLocation = (TextView) findViewById(R.id.set_location_tvLocation);
		mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.set_location_map);
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);
		map.setOnMapClickListener(this);
		map.setOnMarkerDragListener(this);
		client = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		if (latitude != 0 && longitude != 0) {
			setLocation(latitude, longitude);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		client.connect();
	}

	public void ok(View v) {
		Intent data = new Intent();
		data.putExtra("latitude", latitude);
		data.putExtra("longitude", longitude);
		setResult(RESULT_OK, data);
		finish();
	}

	public void cancel(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}

	void setLocation(double latitude, double longitude) {
		map.clear();
		DecimalFormat df = new DecimalFormat("0.0000000");
		tvLocation.setText("Location " + df.format(latitude) + ":"
				+ df.format(longitude));
		map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,
				longitude)));
		MarkerOptions opt = new MarkerOptions().draggable(true)
				.position(new LatLng(latitude, longitude)).title("Location")
				.snippet("New Location Here").visible(true);
		opt.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		Marker marker = map.addMarker(opt);

	}

	@Override
	public void onLocationChanged(Location location) {
		// tvLocation.setText("Location " + location.getLatitude() + ":"
		// + location.getLongitude());

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (latitude == 0 && longitude == 0) {
			location =LocationServices.FusedLocationApi.getLastLocation(
					client);
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				DecimalFormat df = new DecimalFormat("0.0000000");
				tvLocation.setText("Location : " + df.format(latitude) + " / "
						+ df.format(longitude));
				map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
						latitude, longitude)));
			}
		} else {

		}

		// LocationRequest request = new LocationRequest();
		// client.requestLocationUpdates(request, SetLocationActivity.this);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}



	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapClick(LatLng point) {
		latitude = point.latitude;
		longitude = point.longitude;
		setLocation(latitude, longitude);

	}

	public void goBack(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		latitude = marker.getPosition().latitude;
		longitude = marker.getPosition().longitude;
		setLocation(latitude, longitude);

	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub

	}

}
