/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mam.gprg.ourrocks.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.detail.RocksDetailActivity;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param extras Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle extras) {
        String message = extras.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */


        Log.d("EXTRAS DI receiver", extras.toString());
        String notifAction = extras.getString("notif_action");
        if (notifAction.equals("add_comment")) {
            Intent newCommentIntent = new Intent(notifAction);
            newCommentIntent.putExtras(extras);
            sendBroadcast(newCommentIntent);
            notify(extras);
        }
      //  sendNotification(message);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, RocksDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.rock_icon)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public void notify(Bundle extras) {
        final int rockId = Integer.parseInt(extras.getString("object_id"));
        final int senderId = Integer.parseInt(extras.getString("sender_id"));
        final String message = extras.getString("message");
        final String senderName = extras.getString("sender_name");
        final String senderData = extras.getString("sender_data");
        Log.d("GET NOFIR",
                senderId
                        + " / "
                        + getSharedPreferences("ourrocks",
                        Context.MODE_PRIVATE).getInt("user_id", 0));
        if (senderId != getSharedPreferences("ourrocks",
                Context.MODE_PRIVATE).getInt("user_id", 0)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    this);
            NotificationManager nm = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            builder.setOnlyAlertOnce(true);
            builder.setContentTitle("New Comment");
            builder.setContentInfo(senderData);
            builder.setContentText(senderData);
            builder.setTicker(message);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.geotage_logo);
            builder.setLargeIcon(BitmapFactory.decodeResource(
                    getResources(), R.drawable.geotage_logo));
            Intent intent = new Intent(this, RocksDetailActivity.class);
            intent.putExtra("rock_id", rockId);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pIntent);
            nm.notify(123, builder.build());
        }
    }}
