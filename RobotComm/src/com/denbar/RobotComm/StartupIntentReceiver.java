package com.denbar.RobotComm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Gets the startup intent and calls the service that will register with our
 * XMPP and C2DM servers This exists as glue between the
 * "hey we just finished booting" code and the registration code
 *
 */

// also gets robot command intents

public class StartupIntentReceiver extends BroadcastReceiver {
	private final String TAG = "StartupIntentReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "in onReceive");

		Intent serviceIntent = new Intent();
		serviceIntent.setAction("com.denbar.RobotComm.RobotCommService");


		// from C2DM server
		String messageFromC2DM = intent.getStringExtra("robotCommand");
		if (messageFromC2DM != null)
		{
			serviceIntent.putExtra("C2DMmessage", messageFromC2DM);
			Log.d(TAG, "C2DM message received, forwarding to RobotCommService: " + messageFromC2DM);
		}
		// start up the service
		context.startService(serviceIntent);
	}
}
