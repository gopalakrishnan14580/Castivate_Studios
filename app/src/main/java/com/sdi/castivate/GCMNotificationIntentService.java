package com.sdi.castivate;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sdi.castivate.utils.DebugReportOnLocat;
import com.sdi.castivate.utils.Library;

public class GCMNotificationIntentService extends IntentService {

	String forImage = ""; 
	PendingIntent intent = null;
	public static final int NOTIFICATION_ID = 1;
	@SuppressWarnings("unused")
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";
	String messageSplit;

	String getRollId = "";

	String message = "";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification(this, "Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification(this, "Deleted messages on server: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

				for (int i = 0; i < 1; i++) {
					Log.i(TAG, "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				try {
					if (extras.get(Library.MESSAGE_KEY).toString() != null) {

						message = extras.get(Library.MESSAGE_KEY).toString();

						Log.i(TAG, "Received: " + extras.toString());
						// message#rollid#age#gender#ethencity#performance'
						// A new casting is
						// Added.433#Female#12-18#Caucasian#Actor

						if (message != null) {
							if (message.contains("#")) {
								

								String asr[] = message.split("\\.");
								// messageSplit = message.split("\\.")[0];
								sendNotification(this, message);
								// sendNotification(this,messageSplit);
								// displayMessage(context, messageSplit);
								// notifies user
								// generateNotification(context, messageSplit);
								String[] mmessage = asr[1].split("#");
								DebugReportOnLocat.ln("spilt message:" + mmessage);

								getRollId = mmessage[0];
							
								
							} else {
								
								sendNotification(this, message);
							}

						}

					}

				} catch (NullPointerException e) {
					DebugReportOnLocat.e(e);
				}

			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	Notification.Builder notification;
	String asr[] = null;

	Notification notificationTest;

	@SuppressWarnings("deprecation")
	private void sendNotification(Context context, String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		if (msg.contains("#")) {

			if (msg != null) {
				asr = msg.split("\\.");
				String[] mmessage = asr[1].split("#");
				DebugReportOnLocat.ln("spilt message:" + mmessage);

				getRollId = mmessage[0];
			}
		}
		else {
			
//			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			//mNotificationManager.cancel(id).
			
			forImage= "image";
			
		}

		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (message.contains("#")) {
//			notification = new Notification.Builder(icon, asr[0], when);
			notification = new Notification.Builder(getApplicationContext());

		} else {
//			notification = new Notification(icon, msg, when);
			notification = new Notification.Builder(getApplicationContext());
		}

		notificationTest = notification.getNotification();

		String title = context.getString(R.string.app_name);
		Intent notificationIntent = null;

		notificationIntent = new Intent(context, CastingScreen.class);

		if (getRollId.equals("")) {
			System.out.println("Empty RollId>>>>");

		} else {
			// Bundle b = new Bundle();
			// b.putString("rollID", getRollId);
			notificationIntent.putExtra("rollID", getRollId);
			// startActivity(launch);
			// sendBroadcast(intent2);
		}

		CastingScreen.isFavScreen = true;

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		if(forImage.equals("image")){
			//mNotificationManager.cancel(dsf);
			notificationIntent.putExtra("fromImage", "Image");
			//intent = PendingIntent.getActivity(context, (int) System.nanoTime(), notificationIntent, 0);
			
			//intent = PendingIntent.getActivity(context, (int) System.nanoTime(), notificationIntent, PendingIntent.FLAG_NO_CREATE);
			intent = PendingIntent.getActivity(context, (int) System.nanoTime(), notificationIntent, 0);
			
		}else{
			intent = PendingIntent.getActivity(context, (int) System.nanoTime(), notificationIntent, 0);
		}

		if (msg.contains("#")) {
//			notification.setLatestEventInfo(context, title, asr[0], intent);
			notification.setContentText(asr[0]);

			
		} else {
//			notification.setLatestEventInfo(context, title, msg, intent);
			notification.setContentText(msg);
		}

		notification.setContentTitle(title);
		notification.setAutoCancel(false);

//		notification.defaults |= Notification.DEFAULT_SOUND;
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify((int) System.nanoTime(), notificationTest);
		

	}
}
