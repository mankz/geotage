package mam.gprg.ourrocks.dialogs;

import mam.gprg.ourrocks.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPassword extends DialogFragment {

	ForgotPasswordListener listener;

	public interface ForgotPasswordListener {
		public void onResetPassword(String email);
	}

	public void setListener(ForgotPasswordListener listener) {
		this.listener = listener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.forgot_password_dialog, null);
		builder.setView(view);
		((Button) view.findViewById(R.id.forgot_password_btnReset))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						EditText et = (EditText) view
								.findViewById(R.id.forgot_password_etEmail);
						String email = et.getText().toString();
						if (email.length() > 0) {
							listener.onResetPassword(email);
							ForgotPassword.this.dismiss();
						}
					}
				});
		return builder.create();
	}

}
