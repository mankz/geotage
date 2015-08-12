package mam.gprg.ourrocks.userdatas;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.adapter.RockAdapter;
import mam.gprg.ourrocks.adapter.RockAdapter.RockAdapterListener;
import mam.gprg.ourrocks.dialogs.ErrorDialog;
import mam.gprg.ourrocks.model.Rock;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class AddPlaceFragment extends Fragment implements OnClickListener,
		RockAdapterListener, OnItemClickListener, OnRefreshListener {
	View layout;
	GeoTage app;
	PullToRefreshListView lv;
	TextView tv;
	RockAdapter adapter;

	Button btnShowdraft;
	int currentPage, totalResult;
	boolean isLoading;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		app = (GeoTage) getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = LayoutInflater.from(getActivity()).inflate(
				R.layout.add_place_fragment, null);
		lv = (PullToRefreshListView) layout
				.findViewById(R.id.add_place_fragment_lv);
		tv = (TextView) layout.findViewById(R.id.add_place_fragment_tvLoadMore);
		adapter = new RockAdapter(getActivity(), R.layout.info_item);
		adapter.setListener(this);
		lv.setAdapter(adapter);
		lv.setOnRefreshListener(this);

		btnShowdraft = (Button) layout
				.findViewById(R.id.add_place_fragment_btnShowDraft);
		btnShowdraft.setOnClickListener(this);
		ImageButton ibAdd = (ImageButton) layout
				.findViewById(R.id.add_place_fragment_ibAdd);
		ibAdd.setOnClickListener(this);
		lv.setOnItemClickListener(this);
		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		app.isLoggedIn();
		getUserRocks(false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		app.shortToast(getActivity(), "DI FRAGMENT");
	}

	public static int ADD_PLACE = 300;

	@Override
	public void onClick(View arg0) {
		if (app.isLoggedIn()) {
			if (arg0.getId() == R.id.add_place_fragment_ibAdd) {
				Intent intent = new Intent(getActivity(),
						AddPlaceActivity.class);
				intent.putExtra(AddPlaceActivity.MODE,
						AddPlaceActivity.MODE_ADD);
				getActivity().startActivityForResult(intent, ADD_PLACE);
			} else {
				Intent intent = new Intent(getActivity(), DraftActivity.class);
				getActivity().startActivity(intent);
			}

		} else {
			ErrorDialog dialog = new ErrorDialog();
			Bundle args = new Bundle();
			args.putString(ErrorDialog.ERROR_MESSAGE, "You must login first");
			dialog.setArguments(args);
			dialog.show(getChildFragmentManager(), "error");
		}

	}

	void getUserRocks(final boolean onPullRefresh) {
		isLoading = true;
		if (currentPage > 0)
			tv.setVisibility(View.VISIBLE);
		Listener<JSONObject> listener = new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				app.log(this, response.toString());
				if (onPullRefresh)
					lv.onRefreshComplete();
				tv.setVisibility(View.GONE);
				isLoading = false;
				ResponseParser parser = new ResponseParser(response);
				if (parser.getStatusCode() == 200) {
					currentPage = parser.getCurrentPage();
					totalResult = parser.getTotalResult();
					for (int i = 0; i < parser.getDataArray().length(); i++) {
						try {
							adapter.add(Rock.Parse(parser.getDataArray()
									.getJSONObject(i)));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}

			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (onPullRefresh)
					lv.onRefreshComplete();
				isLoading = false;
				--currentPage;
				tv.setVisibility(View.GONE);
				error.printStackTrace();
			}
		};
		JsonObjectRequest request = new JsonObjectRequest(API.ROCKS_GET_BY_USER
				+ app.getPref().getInt("user_id", 0) + "/" + (++currentPage),
				null, listener, errorListener);
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	public void reload(boolean onPullRefresh) {
		if (adapter != null) {
			adapter.clear();
			currentPage = 0;
			getUserRocks(onPullRefresh);
		}
	}

	@Override
	public void onShowUser(Context ctx, int userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowRock(Context ctx, Rock rock) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDelete(Context ctx, final int rockId, String rockName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle("Confirm");
		builder.setMessage("Are you sure to delete " + rockName + " ?");
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				delete(rockId);

			}
		});
		builder.show();
	}

	@Override
	public void onEdit(Context ctx, int rockId) {
		app.editRock(ctx, rockId);
	}

	void delete(final int rockId) {
		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				app.log(this, response);
				try {
					JSONObject obj = new JSONObject(response);
					try {
						if (obj.getInt("status") == 200) {
							// reload(false);
							remove(rockId);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				app.log(this, error.toString());
			}
		};
		StringRequest request = new StringRequest(Method.DELETE,
				API.ROCK_DELETE + rockId, listener, errorListener);
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	void remove(int rockId) {
		for (int i = 0; i < adapter.getCount(); i++) {
			Rock rock = adapter.getItem(i);
			if (rock.getId() == rockId) {
				adapter.remove(rock);
				adapter.notifyDataSetChanged();
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		app.log(this, "this " + arg2);
		if (arg2 > 0)
			app.showRockPage(getActivity(), adapter.getItem(arg2 - 1));
	}

	@Override
	public void onRefresh() {
		reload(true);

	}

	@Override
	public void onLastPosition() {
		if (!isLoading && adapter.getCount() < totalResult) {
			getUserRocks(false);
		}

	}
}
