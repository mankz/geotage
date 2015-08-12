package mam.gprg.ourrocks.receiver;

import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.detail.RocksDetailActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("RECEIVER", intent.getExtras().toString());
		// TODO Auto-generated method stub
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String messageType = gcm.getMessageType(intent);
		int userId = 0;
		if (intent.hasExtra("user_id"))
			userId = Integer.parseInt(intent.getStringExtra("user_id"));
		// Filter messages based on message type. It is likely that GCM will be
		// extended in the future
		// with new message types, so just ignore message types you're not
		// interested in, or that you
		// don't recognize.
		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			// It's an error.
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
				.equals(messageType)) {
			// Deleted messages on the server.
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
				.equals(messageType)) {
			Bundle extras = intent.getExtras();
			Log.d("EXTRAS DI receiver", extras.toString());
			String notifAction = extras.getString("notif_action");
			if (notifAction.equals("add_comment")) {
				Intent newCommentIntent = new Intent(notifAction);
				newCommentIntent.putExtras(intent.getExtras());
				context.sendBroadcast(newCommentIntent);
				notify(context, extras);
			}
		}

	}

	public void notify(Context mContext, Bundle extras) {
		final int rockId = Integer.parseInt(extras.getString("object_id"));
		final int senderId = Integer.parseInt(extras.getString("sender_id"));
		final String message = extras.getString("message");
		final String senderName = extras.getString("sender_name");
		final String senderData = extras.getString("sender_data");
		Log.d("GET NOFIR",
				senderId
						+ " / "
						+ mContext.getSharedPreferences("ourrocks",
								Context.MODE_PRIVATE).getInt("user_id", 0));
		if (senderId != mContext.getSharedPreferences("ourrocks",
				Context.MODE_PRIVATE).getInt("user_id", 0)) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					mContext);
			NotificationManager nm = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			builder.setOnlyAlertOnce(true);
			builder.setContentTitle("New Comment");
			builder.setContentInfo(senderData);
			builder.setContentText(senderData);
			builder.setTicker(message);
			builder.setAutoCancel(true);
			builder.setSmallIcon(R.drawable.geotage_logo);
			builder.setLargeIcon(BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.geotage_logo));
			Intent intent = new Intent(mContext, RocksDetailActivity.class);
			intent.putExtra("rock_id", rockId);
			PendingIntent pIntent = PendingIntent.getActivity(mContext, 0,
					intent, PendingIntent.FLAG_ONE_SHOT);
			builder.setContentIntent(pIntent);
			nm.notify(123, builder.build());
		}

	}
}