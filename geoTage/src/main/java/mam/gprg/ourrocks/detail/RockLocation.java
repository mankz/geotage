package mam.gprg.ourrocks.detail;

import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.model.Rock;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RockLocation extends Fragment implements OnLayoutChangeListener {

	// LatLng jogja = new LatLng(-7.8014, 110.3644);

	View layout;
	SupportMapFragment mapFragment;
	GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.rock_location_fragment, null);
		mapFragment = (SupportMapFragment) getChildFragmentManager()
				.findFragmentById(R.id.rock_location_map);
		layout.addOnLayoutChangeListener(this);
		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Rock rock = getArguments().getParcelable("rock");
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

			Log.d("ADD MARKER", " add marker here");
			map.addMarker(opt);
			map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(rock
					.getLatitude(), rock.getLongitude())));
		} else {
			Log.d("ROCK LOCATION", "map fragment null");
		}
	}

	@Override
	public void onLayoutChange(View arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8) {
		if (mapFragment != null) {
 			GoogleMap map = mapFragment.getMap();
			if (map != null) {
				// Do stuff
				Log.d("ROCK YEEE", "BISAAAAAAAAAAA");
			} else {

			}

		}
			
	}
}
