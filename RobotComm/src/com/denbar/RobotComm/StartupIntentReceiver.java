package com.denbar.RobotComm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Gets the startup intent and calls the service that will register with
 * our XMPP and C2DM servers
 * This exists as glue between the "hey we just finished booting" code and the
 * registration code
 *
 */
public class StartupIntentReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent();
		serviceIntent.setAction("com.denbar.RobotComm.RobotCommService");
		// start up the service that calls for XMPPregistration
		context.startService(serviceIntent);

		Intent C2DMStartupIntent = new Intent();
		C2DMStartupIntent.setAction("com.denbar.RobotComm.C2DMRegistrationService");
		// start up the service that does C2DMregistration
		context.startService(C2DMStartupIntent);
	}
}