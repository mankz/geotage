package mam.gprg.ourrocks.services;

import mam.gprg.ourrocks.receiver.GCMReceiver;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GCMService extends Service {

	NewCommentReceiver receiver;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		receiver = new NewCommentReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(new String("add_comment"));
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	class NewCommentReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String action = arg1.getAction();
			if (action.equals("add_comment")) {
				Bundle extras = arg1.getExtras();
				Log.d("GET COMMENT NOTIF", extras.toString());
			} else {
			}

		}

	}

	public static String SERVER_KEY = "AIzaSyD3EjYxtgzSvAhN7XMaBOtz-85lxnLVYrw";
}
