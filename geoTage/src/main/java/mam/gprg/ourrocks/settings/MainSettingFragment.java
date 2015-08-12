package mam.gprg.ourrocks.settings;

import java.util.HashMap;
import java.util.Map;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.detail.OurRocksInfoFragment;
import mam.gprg.ourrocks.detail.OurRocksInfoFragment.OurRocksInfoListener;
import mam.gprg.ourrocks.dialogs.ForgotPassword;
import mam.gprg.ourrocks.dialogs.ForgotPassword.ForgotPasswordListener;
import mam.gprg.ourrocks.settings.ChangePasswordFragment.ChangePasswordListener;
import mam.gprg.ourrocks.settings.ContactUsFragment.ContactUsListener;
import mam.gprg.ourrocks.settings.LoginFragment.WarningFragmentListener;
import mam.gprg.ourrocks.settings.ProfileFragment.RegisterFragmentListener;
import mam.gprg.ourrocks.settings.RegisteredFragment.RegisteredFragmentListener;
import mam.gprg.ourrocks.settings.SettingFragment.SettingFragmentListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class MainSettingFragment extends Fragment implements
		WarningFragmentListener, SettingFragmentListener,
		RegisterFragmentListener, RegisteredFragmentListener,
		ChangePasswordListener, ContactUsListener, OurRocksInfoListener,
		ForgotPasswordListener {

	GeoTage app;
	FragmentTransaction fTrans;
	SettingFragment mSettingFragment;
	LoginFragment mWarningFragment;
	ProfileFragment mProfileFragment;
	RegisteredFragment mRegisteredFragment;
	ChangePasswordFragment mChangePasswordFragment;
	ContactUsFragment mContactUsFragment;
	OurRocksInfoFragment mOurRocksInfoFragment;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		app = (GeoTage) activity.getApplication();
	}

	public SettingFragment getSettingFragment() {
		if (mSettingFragment == null) {
			mSettingFragment = new SettingFragment();
			mSettingFragment.setListener(this);
			mSettingFragment.setToggleListener(getActivity());
		}
		return mSettingFragment;
	}

	public LoginFragment getLoginFragment() {
		if (mWarningFragment == null) {
			mWarningFragment = new LoginFragment();
			mWarningFragment.setListener(this);
		}

		return mWarningFragment;
	}

	public ChangePasswordFragment getChangePasswordFragment() {
		if (mChangePasswordFragment == null) {
			mChangePasswordFragment = new ChangePasswordFragment();
			mChangePasswordFragment.setListener(this);
		}
		return mChangePasswordFragment;
	}

	public ProfileFragment getProfileFragment(int mode) {
		if (mProfileFragment == null) {
			mProfileFragment = new ProfileFragment();
			mProfileFragment.setListener(this);
			Bundle bundle = new Bundle();
			bundle.putInt(ProfileFragment.ARGS_MODE, mode);
			mProfileFragment.setArguments(bundle);
		}
		return mProfileFragment;
	}

	public RegisteredFragment getRegisteredFragment() {
		if (mRegisteredFragment == null) {
			mRegisteredFragment = new RegisteredFragment();
			mRegisteredFragment.setLisetner(this);
		}
		return mRegisteredFragment;
	}

	public ContactUsFragment getContactUsFragment() {
		if (mContactUsFragment == null) {
			mContactUsFragment = new ContactUsFragment();
			mContactUsFragment.setListener(this);
		}
		return mContactUsFragment;
	}

	public OurRocksInfoFragment getOurRocksInfoFragment(int mode) {
		if (mOurRocksInfoFragment == null) {
			mOurRocksInfoFragment = new OurRocksInfoFragment();
			Bundle args = new Bundle();
			args.putInt(OurRocksInfoFragment.ARGS_MODE, mode);
			mOurRocksInfoFragment.setArguments(args);
			mOurRocksInfoFragment.setListener(this);
		}
		return mOurRocksInfoFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.main_setting, null);
		FragmentTransaction fTrans = getFragmentManager().beginTransaction();
		if (app.isLoggedIn()) {
			fTrans.replace(R.id.main_setting_container, getSettingFragment());
		} else {
			fTrans.replace(R.id.main_setting_container, getLoginFragment());
		}
		fTrans.commit();
		return layout;
	}

	@Override
	public void onLogin() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getSettingFragment())
				.commit();
	}

	@Override
	public void onRegister() {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.main_setting_container,
						getProfileFragment(ProfileFragment.MODE_REGISTER))
				.commit();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onEdit() {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.main_setting_container,
						getProfileFragment(ProfileFragment.MODE_EDIT)).commit();
	}

	@Override
	public void onLogout() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getLoginFragment())
				.commit();
	}

	@Override
	public void onApplicationInfo(int info) {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.main_setting_container,
						getOurRocksInfoFragment(info)).commit();

	}

	@Override
	public void onRegistered() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getRegisteredFragment())
				.commit();
	}

	@Override
	public void onBackToMainSetting() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getLoginFragment())
				.commit();
	}

	@Override
	public void onSave() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getSettingFragment())
				.commit();

	}

	@Override
	public void onCancelRegister() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getLoginFragment())
				.commit();

	}

	@Override
	public void onCancelSave() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getSettingFragment())
				.commit();

	}

	@Override
	public void onChangePassword() {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.main_setting_container,
						getChangePasswordFragment()).commit();
	}

	@Override
	public void onCancelChangePassword() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getSettingFragment())
				.commit();
	}

	@Override
	public void onChangedPassword() {
		onLogout();
		app.shortToast(getActivity(), "Please login with your new password");
	}

	@Override
	public void onContactUs() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getContactUsFragment())
				.commit();
	}

	@Override
	public void onCancelContactUs() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getSettingFragment())
				.commit();

	}

	@Override
	public void onSendContactUsMessage() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getSettingFragment())
				.commit();
	}

	@Override
	public void onOurRocksInfoBack() {
		getFragmentManager().beginTransaction()
				.replace(R.id.main_setting_container, getSettingFragment())
				.commit();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		app.log(this, "tes");
	}

	@Override
	public void onForgotPassword() {
		ForgotPassword dialog = new ForgotPassword();
		dialog.setListener(this);
		dialog.show(getChildFragmentManager(), "forgot-password");
	}

	void sendInstruction(final String email) {
		Listener<String> sListener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
 				app.log(this, response);
  			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				app.log(this, error.toString());
			}
		};

		StringRequest request = new StringRequest(Method.POST,
				API.CLIENTS_FORGOT_PASSWORD, sListener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("email", email);
				return params;
			}
		};
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	@Override
	public void onResetPassword(String email) {
		sendInstruction(email);
		Toast.makeText(getActivity(),
				"We will send new password to your email, thank you",
				Toast.LENGTH_LONG).show();
	}
}
