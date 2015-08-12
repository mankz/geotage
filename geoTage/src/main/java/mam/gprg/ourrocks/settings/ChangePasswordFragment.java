package mam.gprg.ourrocks.settings;

import java.util.HashMap;
import java.util.Map;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class ChangePasswordFragment extends Fragment implements OnClickListener {

	ChangePasswordListener listener;

	GeoTage app;
	EditText etPassword, etNewPassword, etConfirmPassword;

	public interface ChangePasswordListener {
		public void onCancelChangePassword();

		public void onChangedPassword();
	}

	public void setListener(ChangePasswordListener listener) {
		this.listener = listener;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		app = (GeoTage) activity.getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.change_password, null);
		((Button) layout.findViewById(R.id.change_password_btnCancel))
				.setOnClickListener(this);
		((Button) layout.findViewById(R.id.change_password_btnSave))
				.setOnClickListener(this);
		etPassword = (EditText) layout
				.findViewById(R.id.change_password_etCurrPassword);
		etNewPassword = (EditText) layout
				.findViewById(R.id.change_password_etNewPassword);
		etConfirmPassword = (EditText) layout
				.findViewById(R.id.change_password_etConfirmPassword);
		return layout;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.change_password_btnCancel:
			listener.onCancelChangePassword();
			break;
		case R.id.change_password_btnSave:
			String password = etPassword.getText().toString();
			String newPassword = etNewPassword.getText().toString();
			String confirmPassword = etConfirmPassword.getText().toString();
			if (password.length() > 0 && newPassword.length() > 0
					&& confirmPassword.length() > 0) {
				if (!newPassword.equals(password)) {
					if (newPassword.equals(confirmPassword))
						changePassword(password, newPassword);
					else
						app.shortToast(getActivity(),
								"Confirm your new password");
				} else
					app.shortToast(getActivity(),
							"New password must be different");
			}

			// listener.onChangedPassword();
			break;
		}

	}

	public void changePassword(final String oldPassword,
			final String newPassword) {

		Listener<String> succeedlistener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				app.log(this, response);
				try {
					JSONObject obj = new JSONObject(response);
					app.shortToast(getActivity(), obj.getString("message"));
					if (obj.getInt("status") == 200) {
						clear();
						listener.onChangedPassword();
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

		StringRequest request = new StringRequest(Method.POST,
				API.CLIENTS_CHANGE_PASSWORD, succeedlistener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("user_id",
						String.valueOf(app.getPref().getInt("user_id", 0)));
				params.put("password", oldPassword);
				params.put("new_password", newPassword);
				return params;
			}
		};

		app.shortToast(getActivity(), "Change Password");
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	void clear() {
		etConfirmPassword.setText("");
		etNewPassword.setText("");
		etPassword.setText("");
	}
}
