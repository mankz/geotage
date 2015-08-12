package mam.gprg.ourrocks.settings;

import mam.gprg.ourrocks.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ContactUsFragment extends Fragment implements OnClickListener {

	ContactUsListener listener;

	public interface ContactUsListener {
		public void onCancelContactUs();

		public void onSendContactUsMessage();
	}

	public void setListener(ContactUsListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.contact_us, null);

		((Button) layout.findViewById(R.id.contact_us_btnCancel))
				.setOnClickListener(this);
		((Button) layout.findViewById(R.id.contact_us_btnSend))
				.setOnClickListener(this);
		return layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contact_us_btnCancel:
			listener.onCancelContactUs();
			break;
		case R.id.contact_us_btnSend:
			listener.onSendContactUsMessage();
			break;
		}

	}
}
