package mam.gprg.ourrocks.locations;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.adapter.RockAdapter;
import mam.gprg.ourrocks.adapter.RockAdapter.RockAdapterListener;
import mam.gprg.ourrocks.model.Rock;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class DirectoryFragment extends Fragment implements OnItemClickListener,
		RockAdapterListener, OnRefreshListener {

	GeoTage app;
	View layout;

	PullToRefreshListView lv;
	TextView tvLoadMore;
	RockAdapter adapter;

	public interface DirectoryFragmentListener {
		public void onAddRock(Rock rock);
 		public void onClear();
 	}

	DirectoryFragmentListener dirFragmentListener;
	
	public void setDirectoryFragmentListener(DirectoryFragmentListener listener){
		this.dirFragmentListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		app = (GeoTage) getActivity().getApplication();
		dirFragmentListener = (DirectoryFragmentListener) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = LayoutInflater.from(getActivity()).inflate(
				R.layout.directory_fragment, null);
		lv = (PullToRefreshListView) layout
				.findViewById(R.id.directory_fragment_list);
		lv.setOnRefreshListener(this);
		tvLoadMore = (TextView) layout
				.findViewById(R.id.directory_fragment_tvLoadMore);
		adapter = new RockAdapter(getActivity(), 0);
		adapter.setListener(this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);

		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getRocks(false);
	}

	void getRocks(final boolean viaRefresh) {
		isLoading = true;
		if (currentPage > 0)
			tvLoadMore.setVisibility(View.VISIBLE);
		Listener<JSONObject> listener = new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				if (viaRefresh)
					lv.onRefreshComplete();
				isLoading = false;
				tvLoadMore.setVisibility(View.GONE);
				app.log(this, response.toString());
				ResponseParser parser = new ResponseParser(response);
				if (parser.getStatusCode() == 200) {
					for (int i = 0; i < parser.getDataArray().length(); i++) {
						try {
							currentPage = parser.getCurrentPage();
							totalResult = parser.getTotalResult();
							Rock rock = Rock.Parse(parser.getDataArray()
									.getJSONObject(i));
							adapter.add(rock);
							dirFragmentListener.onAddRock(rock);
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
				if (viaRefresh)
					lv.onRefreshComplete();
				--currentPage;
				isLoading = false;
				tvLoadMore.setVisibility(View.GONE);
				error.printStackTrace();
			}
		};
		JsonObjectRequest request = new JsonObjectRequest(API.ROCKS_GET
				+ (++currentPage), null, listener, errorListener);
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	int currentPage = 0;
	int totalResult = 0;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		app.showRockPage(getActivity(), adapter.getItem(arg2 - 1));

	}

	public void reload(boolean viaRefresh) {
		if (adapter != null) {
			adapter.clear();
			currentPage = 0;
			dirFragmentListener.onClear();
			getRocks(viaRefresh);
		}
	}

	@Override
	public void onShowUser(Context ctx, int userId) {
		app.showUserPage((Activity) ctx, userId);
	}

	@Override
	public void onShowRock(Context ctx, Rock rock) {
		app.showRockPage((Activity) ctx, rock);
	}

	@Override
	public void onDelete(Context ctx, final int rockId, String rockName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage("Are you sure to delete " + rockName + " ?");
		builder.setNegativeButton("No", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		builder.setPositiveButton("Yes", new OnClickListener() {
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

	@Override
	public void onRefresh() {
		reload(true);
	}

	boolean isLoading = false;

	void delete(final int rockId) {

		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				app.log(this, response.toString());
				try {
					if (response.getInt("status") == 200) {
						// reload(false);
						remove(rockId);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		};

		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
			}
		};

		JsonObjectRequest request = new JsonObjectRequest(Method.DELETE,
				API.ROCK_DELETE + rockId, null, listener, errorListener);
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
	public void onLastPosition() {
		if (!isLoading && adapter.getCount() < totalResult) {
			getRocks(false);
		}
	}
}
