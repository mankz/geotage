package mam.gprg.ourrocks.map;

import java.util.ArrayList;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.model.Rock;
import mam.gprg.ourrocks.settings.SettingFragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RocksFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMarkerClickListener,
		OnMarkerDragListener, OnClickListener, OnMapClickListener,
		SensorEventListener, OnInfoWindowClickListener, OnMapReadyCallback {

	View layout;
	SupportMapFragment mapFragment;
	GoogleMap map;
	GeoTage app;
	//LocationClient client;
	GoogleApiClient client;
	Location loc; 

	ArrayList<Rock> rocks = new ArrayList<Rock>();
	ArrayList<String> markerIds = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		MapsInitializer.initialize(getActivity().getApplicationContext());

		client = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		app = (GeoTage) getActivity().getApplication();
		mSensorManager = (SensorManager) getActivity().getSystemService(
				getActivity().SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

	}

	boolean showCompass;

	public void showCompass(boolean show) {
		showCompass = show;
		image.setVisibility(showCompass ? View.VISIBLE : View.GONE);
		image.invalidate();
		image.requestLayout();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = LayoutInflater.from(getActivity()).inflate(
				R.layout.rocks_fragment, null);
		// our compass image
		image = (ImageView) layout.findViewById(R.id.imageViewCompass);
		showCompass = app.getPref().getBoolean(SettingFragment.SHOW_COMPASS,
				false);
		image.setVisibility(showCompass ? View.VISIBLE : View.GONE);

		// TextView that will tell the user what degree is he heading
		// initialize your android device sensor capabilities
  		GoogleMapOptions options = new GoogleMapOptions();
		options.compassEnabled(false);
		// options.camera(new CameraPosition(jogja, 13, 0, 0));
		mapFragment = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.rocks_fragment_map);
	//	map = mapFragment.getMap();
		mapFragment.getMapAsync(this);

//		if(map != null){
//			map.setMyLocationEnabled(true);
//			// map.setOnMapClickListener(this);
//			map.setOnMarkerClickListener(this);
//			// map.setOnMarkerDragListener(this);
//			map.setOnInfoWindowClickListener(this);
//			// mapFragment = SupportMapFragment.newInstance(options);
//			// getChildFragmentManager().beginTransaction()
//			// .add(R.id.rocks_fragment_map_container, mapFragment).commit();
//		}
 		Button btnNormal = (Button) layout
				.findViewById(R.id.rocks_fragment_btnNormal);
		Button btnTerrain = (Button) layout
				.findViewById(R.id.rocks_fragment_btnTerrain);
		Button btnSatellite = (Button) layout
				.findViewById(R.id.rocks_fragment_btnSattelite);
		btnNormal.setOnClickListener(this);
		btnTerrain.setOnClickListener(this);
		btnSatellite.setOnClickListener(this);
		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public void onStart() {
		super.onStart();
		client.connect();

	}

	@Override
	public void onStop() {
		super.onStop();
		client.disconnect();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		loc = LocationServices.FusedLocationApi.getLastLocation(
				client);

		if(map != null)
			map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(loc
					.getLatitude(), loc.getLongitude())));
//		LocationRequest request = new LocationRequest();
//		request.setSmallestDisplacement(1000);
//		client.requestLocationUpdates(request, this);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("CONNECT", "FAILED");
	}

	@Override
	public void onLocationChanged(Location loc) {
		map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(loc
				.getLatitude(), loc.getLongitude())));
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// Rock rock = rocks.get(markerIds.indexOf(marker.getId()));
		// app.showRockPage(getActivity(), rock.getId());

		marker.showInfoWindow();
		return false;
	}

	@Override
	public void onClick(View arg0) {
		if (map != null) {
			switch (arg0.getId()) {
			case R.id.rocks_fragment_btnNormal:
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				break;
			case R.id.rocks_fragment_btnTerrain:
				map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				break;
			case R.id.rocks_fragment_btnSattelite:
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				break; 
			}
		}

	}

	@Override
	public void onMapClick(LatLng point) {
		// app.shortToast(getActivity(), point.latitude + " / " +
		// point.longitude);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mAccelerometer) {
			System.arraycopy(event.values, 0, mLastAccelerometer, 0,
					event.values.length);
			mLastAccelerometerSet = true;
		} else if (event.sensor == mMagnetometer) {
			System.arraycopy(event.values, 0, mLastMagnetometer, 0,
					event.values.length);
			mLastMagnetometerSet = true;
		}
		if (mLastAccelerometerSet && mLastMagnetometerSet) {
			SensorManager.getRotationMatrix(mR, null, mLastAccelerometer,
					mLastMagnetometer);
			SensorManager.getOrientation(mR, mOrientation);
			float azimuthInRadians = mOrientation[0];
			float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
			RotateAnimation ra = new RotateAnimation(mCurrentDegree,
					-azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);

			ra.setDuration(250);

			ra.setFillAfter(true);

			image.startAnimation(ra);
			mCurrentDegree = -azimuthInDegress;
		}
	}

	// buat compass
	// define the display assembly compass picture
	private ImageView image;

	// device sensor manager
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mMagnetometer;
	private float[] mLastAccelerometer = new float[3];
	private float[] mLastMagnetometer = new float[3];
	private boolean mLastAccelerometerSet = false;
	private boolean mLastMagnetometerSet = false;
	private float[] mR = new float[9];
	private float[] mOrientation = new float[3];
	private float mCurrentDegree = 0f;

	@Override
	public void onResume() {
		super.onResume();

		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mMagnetometer,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onPause() {
		super.onPause();

		// to stop the listener and save battery
		mSensorManager.unregisterListener(this, mAccelerometer);
		mSensorManager.unregisterListener(this, mMagnetometer);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Rock r = rocks.get(0);
		for (Rock rock : rocks) {
			if (marker.getPosition().latitude == rock.getLatitude()
					&& marker.getPosition().longitude == rock.getLongitude()
					&& marker.getTitle().equals(rock.getName())
					&& marker.getSnippet().equals(rock.getDesc())) {
				r = rock;
				break;
			}
		}
		app.showRockPage(getActivity(), r);
	}

	public void add(Rock rock) {
		Log.d("ADD ROCK " + rock.getName(), "MAP NULL? " + (map == null ?  "YES":"NOT") );
		if (map != null) {

			rocks.add(rock);
			MarkerOptions opt = new MarkerOptions()
					.draggable(true)
					.position(
							new LatLng(rock.getLatitude(), rock.getLongitude()))
					.title(rock.getName()).snippet(rock.getDesc())
					.visible(true);

			if (rock.getCategory().getId() == 1) {
				opt.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

			} else if (rock.getCategory().getId() == 2) {
				opt.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

			} else if (rock.getCategory().getId() == 3) {
				opt.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

			} else if (rock.getCategory().getId() == 4) {
				opt.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

			} else if (rock.getCategory().getId() == 5) {
				opt.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			} else {
				opt.icon(BitmapDescriptorFactory.defaultMarker());
			}

			Marker marker = map.addMarker(opt);
			markerIds.add(marker.getId());
		}

	}

	public void clear() {
 		if (map != null) {
			rocks.clear();
			map.clear();
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		this.map = map;
		map.setMyLocationEnabled(true);
		// map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
		// map.setOnMarkerDragListener(this);
		map.setOnInfoWindowClickListener(this);
		// mapFragment = SupportMapFragment.newInstance(options);
		// getChildFragmentManager().beginTransaction()
		// .add(R.id.rocks_fragment_map_container, mapFragment).commit();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
