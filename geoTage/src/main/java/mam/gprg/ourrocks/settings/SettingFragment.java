package mam.gprg.ourrocks.settings;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener {

	public static int TERM_AND_CONDITION = 0;
	public static int ABOUT = 1;

	public static String NOTIF_ME = "notif_me";
	public static String SHOW_COMPASS = "show_compass";

	SettingFragmentListener listener;
	ToggleSettingListener toggleListener;
	GeoTage app;

	public interface SettingFragmentListener {
		public void onEdit();

		public void onLogout();

		public void onChangePassword();

		public void onContactUs();

		public void onApplicationInfo(int info);

	}

	public interface ToggleSettingListener {
		public void onToggleNotif(boolean on);

		public void onToggleCompass(boolean on);
	}

	public void setListener(Object object) {
		listener = (SettingFragmentListener) object;
	}

	public void setToggleListener(Object object) {
		toggleListener = (ToggleSettingListener) object;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (GeoTage) getActivity().getApplication();
	}

	ToggleButton tbNotification, tbCompass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.setting_fragment, null);
		TextView tv = (TextView) layout
				.findViewById(R.id.setting_fragment_tvUsername);
		tv.setText(app.getPref().getString("email", "email"));
		((Button) layout.findViewById(R.id.setting_fragment_btnEdit))
				.setOnClickListener(this);
		((Button) layout.findViewById(R.id.setting_fragment_btnLogout))
				.setOnClickListener(this);
		((TextView) layout.findViewById(R.id.setting_fragment_changePassword))
				.setOnClickListener(this);
		((TextView) layout.findViewById(R.id.setting_fragment_contactUs))
				.setOnClickListener(this);
		((TextView) layout.findViewById(R.id.setting_fragment_toc))
				.setOnClickListener(this);
		((TextView) layout.findViewById(R.id.setting_fragment_about))
				.setOnClickListener(this);
		tbNotification = (ToggleButton) layout
				.findViewById(R.id.setting_fragment_toggleNotification);
		tbCompass = (ToggleButton) layout
				.findViewById(R.id.setting_fragment_toggleCompass);
		tbCompass.setChecked(app.getPref().getBoolean(
				SettingFragment.SHOW_COMPASS, false));
		tbNotification.setChecked(app.getPref()
				.getString(SettingFragment.NOTIF_ME, "").length() > 0);
		tbCompass.setChecked(app.getPref().getBoolean(
				SettingFragment.SHOW_COMPASS, false));
		tbNotification.setOnCheckedChangeListener(this);
		tbCompass.setOnCheckedChangeListener(this);
		return layout;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.setting_fragment_btnEdit:
			listener.onEdit();
			break;
		case R.id.setting_fragment_btnLogout:
			Editor edit = app.getPref().edit();
			edit.remove("user_id");
	 		edit.commit();
			tbCompass.setChecked(false);
			listener.onLogout();
			break;
		case R.id.setting_fragment_changePassword:
			listener.onChangePassword();
			break;
		case R.id.setting_fragment_contactUs:
			listener.onContactUs();
			break;
		case R.id.setting_fragment_toc:
			listener.onApplicationInfo(TERM_AND_CONDITION);
			break;
		case R.id.setting_fragment_about:
			listener.onApplicationInfo(ABOUT);
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch (arg0.getId()) {
		case R.id.setting_fragment_toggleNotification:
			toggleListener.onToggleNotif(arg1);
			break;
		case R.id.setting_fragment_toggleCompass:
			app.log(this, "toggle comass");
			toggleListener.onToggleCompass(arg1);
			break;
		}

	}
}
