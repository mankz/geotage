package mam.gprg.ourrocks.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ErrorDialog extends DialogFragment {

	public static String ERROR_MESSAGE = "error_message";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		try {
			Bundle bundle = getArguments();
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(bundle.getString(ERROR_MESSAGE));
			builder.setNeutralButton("Close", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
			return builder.create();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}

	}

}
