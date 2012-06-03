package com.denbar.RobotComm;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	private static final String LOG = "UpdatewidgetService";
	private String _XMPPstatus, _bluetoothStatus, _sentToRobot, _sentToServer;
	private AppWidgetManager appWidgetManager;
	private RemoteViews remoteViews;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG, "in onCreate");
		appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());
		remoteViews = new RemoteViews(this.getApplicationContext()
				.getPackageName(), R.layout.widget_layout);

	}

	@Override
	public void onStart(Intent intent, int startId) {

		ComponentName thisWidget = new ComponentName(getApplicationContext(),
				RobotCommWidget.class);
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);

		_XMPPstatus = intent.getStringExtra("XMPP");
		_bluetoothStatus = intent.getStringExtra("bluetooth");
		_sentToServer = intent.getStringExtra("sentToServer");
		_sentToRobot = intent.getStringExtra("sentToRobot");

		for (int widgetId : allWidgetIds2) {
			if (_XMPPstatus != null)
				remoteViews.setTextViewText(R.id.XMPP, _XMPPstatus);
			if (_bluetoothStatus != null)
				remoteViews.setTextViewText(R.id.bluetooth, _bluetoothStatus);
			if (_sentToServer != null)
				remoteViews.setTextViewText(R.id.sentToServer, _sentToServer);
			if (_sentToRobot != null)
				remoteViews.setTextViewText(R.id.sentToRobot, _sentToRobot);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);

			/*
			 * // Register an onClickListener. to use this, set up a broadcast
			 * listener in RobotCommService Intent clickIntent = new
			 * Intent(this.getApplicationContext(), RobotCommService.class);
			 * //Intent clickIntent = new Intent();
			 * clickIntent.setAction("com.denbar.RobotComm.UpdateWidgetService.OPEN"
			 * ); //clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * clickIntent.putExtra("widgetCalling","showActivity");
			 *
			 * PendingIntent pendingIntent = PendingIntent.getBroadcast(
			 * getApplicationContext(), 0, clickIntent,
			 * PendingIntent.FLAG_UPDATE_CURRENT);
			 * remoteViews.setOnClickPendingIntent(R.id.btnOpen, pendingIntent);
			 * appWidgetManager.updateAppWidget(widgetId, remoteViews);
			 */
		}
		stopSelf();
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
