package mam.gprg.ourrocks.detail;

import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.settings.SettingFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class OurRocksInfoFragment extends Fragment implements OnClickListener {

	String[] titles = new String[] { "Term and Condition", "About" };
 
	public static String ARGS_MODE = "mode";
	int mode;
	OurRocksInfoListener listener;

	public interface OurRocksInfoListener {
		public void onOurRocksInfoBack();
	}

	public void setListener(OurRocksInfoListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mode = getArguments().getInt(ARGS_MODE);

		View layout = inflater.inflate(R.layout.ourrocks_info, null);
		((TextView) layout.findViewById(R.id.ourrocks_info_tvTitle))
				.setText(titles[mode]);
		((Button) layout.findViewById(R.id.ourrocks_info_btnBack))
				.setOnClickListener(this);
		WebView wv = (WebView) layout.findViewById(R.id.ourrocks_info_wvInfo);
		if(mode == SettingFragment.ABOUT)
			wv.loadUrl("file:///android_asset/about.html");
		else
			wv.loadUrl("file:///android_asset/toc.html");
		return layout;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		listener.onOurRocksInfoBack();
	}
}
