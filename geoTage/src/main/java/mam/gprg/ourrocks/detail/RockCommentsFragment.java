package mam.gprg.ourrocks.detail;

import java.util.HashMap;
import java.util.Map;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.model.Comment;
import mam.gprg.ourrocks.model.Rock;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class RockCommentsFragment extends Fragment implements OnClickListener,
		OnItemClickListener, OnRefreshListener {

	GeoTage app;

	View layout;
	EditText etComment;
	Button btnSend;
	PullToRefreshListView lvComments;
	CommentAdapter adapter;

	Rock rock;

	boolean isLoading;
	int currentPage, totalResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (GeoTage) getActivity().getApplication();
		rock = getArguments().getParcelable("rock");
	}

	public void resetValue() {
	}

	public void setRock(Rock rock) {
		this.rock = rock;
		resetValue();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.rock_comments_fragment, null);
		lvComments = (PullToRefreshListView) layout.findViewById(R.id.rock_comments_lv);
		lvComments.setOnRefreshListener(this);
		adapter = new CommentAdapter(getActivity(), R.layout.comment_item);
		lvComments.setAdapter(adapter);
		etComment = (EditText) layout
				.findViewById(R.id.rock_comments_etComment);
		btnSend = (Button) layout.findViewById(R.id.rock_comment_btnSend);
		btnSend.setOnClickListener(this);
		lvComments.setOnItemClickListener(this);
		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getComments();
	}

	public interface CommentAdapterListener {
		public void onLastItem();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	class CommentAdapter extends ArrayAdapter<Comment> {

		Holder holder;

		public CommentAdapter(Context context, int resource) {
			super(context, resource);
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				holder = new Holder();
				v = LayoutInflater.from(getActivity()).inflate(
						R.layout.comment_item, null);
				holder.ivAvatar = (ImageView) v
						.findViewById(R.id.comment_item_avatar);
				holder.tvName = (TextView) v
						.findViewById(R.id.comment_item_tvName);
				holder.tvText = (TextView) v
						.findViewById(R.id.comment_item_tvMessage);
				holder.tvDate = (TextView) v.findViewById(R.id.comment_item_tvDate);
				v.setTag(holder);
			}

			final Comment c = getItem(position);
			holder = (Holder) v.getTag();
			String avatar = c.getUser().getAvatar();
			if (avatar != null
					&& (avatar.endsWith(".jpg") || avatar.endsWith(".png") || avatar
							.endsWith(".gif")))
				app.getUniversalImageLoader().displayImage(
						API.IMAGE_BASE_URL + avatar, holder.ivAvatar,
						app.getCircleDisplayOption(),
						app.getFirstAnimationDisplay());
			else
				holder.ivAvatar.setImageResource(R.drawable.avatar);

			holder.ivAvatar.setFocusable(false);
			holder.ivAvatar.setFocusableInTouchMode(false);
			holder.ivAvatar.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					app.showUserPage(getActivity(), c.getUserId());
				}
			});
			holder.tvName.setText(c.getUser().getName());
			holder.tvText.setText(c.getComment());
			holder.tvDate.setText(c.getDate());
			if (position == getCount() - 1) {
				onLastPosition();
			}
			return v;
		}

	}

	void onLastPosition() {
		if (!isLoading && adapter.getCount() < totalResult)
			getComments();
	}

	class Holder {
		ImageView ivAvatar;
		TextView tvName, tvText, tvDate;
	}

	void getComments() {  
		isLoading = true;
		Listener<JSONObject> listener = new Listener<JSONObject>() { 
			@Override
			public void onResponse(JSONObject response) {
				lvComments.onRefreshComplete();
				app.log(this, "response " + response.toString());
				isLoading = false;
				ResponseParser parser = new ResponseParser(response);
				if (parser.getStatusCode() == 200) {
					currentPage = parser.getCurrentPage();
					totalResult = parser.getTotalResult();
					for (int i = 0; i < parser.getDataArray().length(); i++) {
						try {
							adapter.add(Comment.Parse(parser.getDataArray()
									.getJSONObject(i)));
							adapter.notifyDataSetChanged();
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
				lvComments.onRefreshComplete();
				app.log(this, "error " + error.toString());
				error.printStackTrace();
				isLoading = false;
				--currentPage;

			}
		};
		
		JsonObjectRequest request = new JsonObjectRequest(API.COMMENTS_BY_ROCK
				+ rock.getId() + "/" + (++currentPage), null, listener,
				errorListener);
		app.log(this, "request " + request.toString());
		app.getQueue().add(request);
	}

	void addComment() {
		JSONObject params = new JSONObject();
		try {
			params.put("user_id", app.getPref().getInt("user_id", 0));
			params.put("rock_id", rock.getId());
			params.put("comment", etComment.getText().toString());
			params.put("status", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				btnSend.setText("Post");
				app.log(this, response);
				try {
					JSONObject obj = new JSONObject(response);
					ResponseParser parser = new ResponseParser(obj);
					if (parser.getStatusCode() == 200) {
						etComment.setText("");
						sendNotification();
reload();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				btnSend.setText("Post");
			}
		};

		StringRequest sRequest = new StringRequest(Method.POST,
				API.COMMENTS_ADD_POST, listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> maps = new HashMap<String, String>();
				maps.put("user_id",
						String.valueOf(app.getPref().getInt("user_id", 0)));
				maps.put("rock_id", String.valueOf(rock.getId()));
				maps.put("comment", etComment.getText().toString());
				maps.put("status", String.valueOf(1));
				maps.put("date", "0000-00-00");
				return maps;
			}
		};

		app.log(this, sRequest.getUrl());
		app.log(this, params.toString());
		btnSend.setText("Posting...");
		app.getQueue().add(sRequest);
	}

	@Override
	public void onClick(View arg0) {
		if (app.isLoggedIn())
			addComment();
		else
			app.showErrorDialog(getChildFragmentManager(),
					"You have to login first");

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		app.showUserPage(getActivity(), ((CommentAdapter) arg0.getAdapter())
//				.getItem(arg2).getUserId());
//		
	}

	void sendNotification() {
		StringRequest request = new StringRequest(Method.POST,
				API.CLIENTS_NEW_COMMENT_NOTIFICATION, new Listener<String>() {

					@Override
					public void onResponse(String response) { 
						app.log(this, response.toString());
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						app.log(this, error.toString());
						
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("user_id", String.valueOf(app.getUserId()));
				params.put("rock_id", String.valueOf(rock.getId()));
				return params;
			}
		};
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	void reload() {
		clear();
		getComments();
	}

	void clear() {
		adapter.clear();

		currentPage = 0;
		totalResult = 0;

	}

	@Override
	public void onRefresh() { 
		
		reload();
		
	}
}
