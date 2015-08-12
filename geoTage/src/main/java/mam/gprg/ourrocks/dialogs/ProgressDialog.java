package mam.gprg.ourrocks.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialog extends DialogFragment {

	public static String MESSAGE = "message";

	String message;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		message = getArguments().getString(MESSAGE);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(message + ", please wait ...");
		return builder.create();
	}
}
