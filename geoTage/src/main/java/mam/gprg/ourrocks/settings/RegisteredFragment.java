package mam.gprg.ourrocks.settings;

import mam.gprg.ourrocks.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class RegisteredFragment extends Fragment {

	RegisteredFragmentListener listener;

	public interface RegisteredFragmentListener {
		public void onBackToMainSetting();
	}

	public void setLisetner(RegisteredFragmentListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.registered_fragment, null);
		((Button) layout.findViewById(R.id.registered_btnBack))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						listener.onBackToMainSetting();
					}
				});
		return layout;
	}
}
