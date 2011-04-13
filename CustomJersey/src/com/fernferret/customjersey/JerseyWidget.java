package com.fernferret.customjersey;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

public class JerseyWidget extends AppWidgetProvider {
	
	static final String UPDATE_WIDGET = "updatemywidget";
	private RemoteViews views = new RemoteViews("com.fernferret.customjersey", R.layout.widget_default);
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;
		
		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			
			Intent settingsIntent = new Intent(context, EditJersey.class);
			Intent appIntent = new Intent(context, ShowJersey.class);
			Intent refreshIntent = new Intent(context, JerseyWidget.class);
			refreshIntent.setAction(UPDATE_WIDGET);
			PendingIntent pendingSettingsIntent = PendingIntent.getActivity(context, 0, settingsIntent, 0);
			PendingIntent pendingAppIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
			PendingIntent pendingRefreshIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, 0);
			
			views.setOnClickPendingIntent(R.id.jersey, pendingAppIntent);
			views.setOnClickPendingIntent(R.id.settings, pendingSettingsIntent);
			views.setOnClickPendingIntent(R.id.refresh, pendingRefreshIntent);
			SharedPreferences settings = context.getSharedPreferences("ShowJersey", 1);
			Log.w("Jersey", "Number: " + settings.contains(ShowJersey.PLAYER_NUMBER));
			updateJerseyWidget(settings);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
	     if (intent.getAction().equals(UPDATE_WIDGET)) {
	    	 SharedPreferences settings = context.getSharedPreferences("ShowJersey", 1);
	    	 Log.w("Jersey", "Recieved update for widget request!");
	    	 
	    	 updateJerseyWidget(settings);
	    	 ComponentName jerseyUpdate = new ComponentName(context, JerseyWidget.class);  
             AppWidgetManager.getInstance(context).updateAppWidget(jerseyUpdate, views); 
	    	 
	     }
	     super.onReceive(context, intent);
	}
	
	private void updateJerseyWidget(SharedPreferences settings) {
		if(settings.contains(ShowJersey.PLAYER_NUMBER)) {
			Log.w("Jersey", "Number: " + settings.getInt(ShowJersey.PLAYER_NUMBER, 0));
			views.setTextViewText(R.id.widget_number, "" + settings.getInt(ShowJersey.PLAYER_NUMBER, 0));
		}
		if(settings.contains(ShowJersey.JERSEY_COLOR)) {
			int jerseyIndex = settings.getInt(ShowJersey.JERSEY_COLOR, 0);
			Log.w("Jersey", "Color Index(pre): " + jerseyIndex);
			if(jerseyIndex < 0 && settings.contains(ShowJersey.IS_BLUE_JERSEY)) {
				boolean blue_jersey = settings.getBoolean(ShowJersey.IS_BLUE_JERSEY, true);
				if(blue_jersey) {
					jerseyIndex = 2; // Blue Jersey color
				} else {
					jerseyIndex = 0;
				}
			}
			Log.w("Jersey", "Color Index: " + jerseyIndex);
			views.setImageViewResource(R.id.jersey, ShowJersey.JERSEY_ARRAY_MINI[jerseyIndex]);
			 
		}
	}

}
