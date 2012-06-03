package com.denbar.RobotComm;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RobotCommWidget extends AppWidgetProvider {

	private static final String LOG = "RobotCommWidget";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(LOG, " in onUpdate");
		// Get all ids
		ComponentName thisWidget = new ComponentName(context,
				RobotCommWidget.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		// Build the intent to call the service and update the widget
		Intent intent = new Intent(context.getApplicationContext(),
				UpdateWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
		context.startService(intent);
	}
}