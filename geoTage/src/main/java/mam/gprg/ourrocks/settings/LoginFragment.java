package mam.gprg.ourrocks.settings;

import java.util.HashMap;
import java.util.Map;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.dialogs.ProgressDialog;
import mam.gprg.ourrocks.model.User;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class LoginFragment extends Fragment implements OnClickListener {

	GeoTage app;

	EditText etEmail, etPassword;

	public WarningFragmentListener listener;

	public interface WarningFragmentListener {
		void onLogin(); 
		void onRegister();
		void onForgotPassword();
	}

	public void setListener(Fragment fragment) {
		this.listener = (WarningFragmentListener) fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.login_fragment, null);
		etEmail = (EditText) layout.findViewById(R.id.login_fragment_etEmail);
		etPassword = (EditText) layout
				.findViewById(R.id.login_fragment_etPassword);

		Button btnLogin = (Button) layout
				.findViewById(R.id.warning_fragment_btnLogin);
		Button btnRegister = (Button) layout
				.findViewById(R.id.warning_fragment_btnRegister);
		TextView tvForgotPassword = (TextView) layout.findViewById(R.id.warning_fragment_tvForgotPassword);
		
		btnLogin.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
		tvForgotPassword.setOnClickListener(this);
		return layout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (GeoTage) getActivity().getApplication();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.warning_fragment_btnLogin:
			login();
			break;
		case R.id.warning_fragment_btnRegister:
			listener.onRegister();
			break;
		case R.id.warning_fragment_tvForgotPassword:
			listener.onForgotPassword();
			break;
		}
	}

	void login() {
		final ProgressDialog prog = app.addProgress(getFragmentManager(),
				"Logging in");
		Listener<String> sListener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				prog.dismiss();
				app.log(this, response);
				ResponseParser parser = new ResponseParser(response);
				if (parser.getStatusCode() == 200) {
					listener.onLogin();
					User user = User.Parse(parser.getDataObject());
					Editor editor = app.getPref().edit();
					editor.putInt("user_id", user.getId());
					editor.putString("email", user.getEmail());
					editor.commit();
					clear();
				} else
					app.shortToast(getActivity(), parser.getFirstError());

			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				prog.dismiss();
				// TODO Auto-generated method stub
				app.log(this, error.toString());

			}
		};
		StringRequest request = new StringRequest(Method.POST,
				API.CLIENTS_LOGIN, sListener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("email", etEmail.getText().toString());
				params.put("password", etPassword.getText().toString());
				return params;
			}
		};
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	void clear() {
		etEmail.setText("");
		etPassword.setText("");
	}
}
