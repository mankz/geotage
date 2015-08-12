package mam.gprg.ourrocks.detail;

import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.model.Rock;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RockLocationAct extends FragmentActivity implements
		OnClickListener {

	MapFragment mapFragment;
	GoogleMap map;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.rock_location_act);
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(
				R.id.rock_location_act_map);
		Rock rock = getIntent().getParcelableExtra("rock");
		Log.d("ROCK LOCATION", rock.getName() + " / " + rock.getLatitude()
				+ "/" + rock.getLongitude());
		if (mapFragment != null) {
			map = mapFragment.getMap();
			MarkerOptions opt = new MarkerOptions()
					.position(
							new LatLng(rock.getLatitude(), rock.getLongitude()))
					.title(rock.getName()).snippet(rock.getDesc());

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
			}

			map.addMarker(opt);
			map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(rock
					.getLatitude(), rock.getLongitude())));
		} else {
			Log.d("ROCK LOCATION", "map fragment null");
		}

		((Button) findViewById(R.id.rock_location_act_terrain))
				.setOnClickListener(this);
		((Button) findViewById(R.id.rock_location_act_satellite))
				.setOnClickListener(this);
		((Button) findViewById(R.id.rock_location_act_traffic))
				.setOnClickListener(this);

	}

	public void changeMapType(View arg0) {

	}

	public void goBack(View v) {
		this.finish();
	}

	@Override
	public void onClick(View arg0) {
		if (map != null) {
			switch (arg0.getId()) {
			case R.id.rock_location_act_traffic:
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				break;
			case R.id.rock_location_act_terrain:
				map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				break;
			case R.id.rock_location_act_satellite:
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				break;
			}
		} 
	}
}
