package com.fernferret.customjersey;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowJersey extends Activity {
	
	static final String PLAYER_NAME = "PLAYERNAME";
	static final String PLAYER_NUMBER = "PLAYERNUMBER";
	static final String IS_BLUE_JERSEY = "ISBLUEJERSEY";
	static final String JERSEY_COLOR = "JERSEYCOLOR";
	static final String JERSEY_COLOR_INDEX = "JERSEYCOLORINDEX";
	static final boolean DEFAULT_JERSEY_COLOR = true;
	static final int DEFAULT_COLOR_INDEX = 0;
	static final int EDIT_JERSEY_REQUEST_CODE = 0;
	
	private TextView mPlayerNameView;
	private TextView mPlayerNumberView;
	private ImageView mJerseyView;
	
	private SharedPreferences mSettings;
	private Resources mRes;
	
	private String mPlayerName;
	private int mPlayerNumber;
	public static final int[] JERSEY_ARRAY = new int[] { R.drawable.red_jersey, R.drawable.orange_jersey, R.drawable.blue_jersey, R.drawable.green_jersey };
	public static final int[] JERSEY_ARRAY_MINI = new int[] { R.drawable.red_jersey_widget, R.drawable.orange_jersey_widget, R.drawable.blue_jersey_widget, R.drawable.green_jersey_widget };
	
	private int mJerseyColorIndex = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Load the UI components
		mPlayerNameView = (TextView) findViewById(R.id.name);
		mPlayerNumberView = (TextView) findViewById(R.id.number);
		mJerseyView = (ImageView) findViewById(R.id.jersey);
		
		// Set custom fonts
		Typeface jerseyThick = Typeface.createFromAsset(getAssets(), "fonts/Jersey M54.ttf");
		Typeface jerseyThin = Typeface.createFromAsset(getAssets(), "fonts/sportsjersey.ttf");
		mPlayerNumberView.setTypeface(jerseyThick);
		mPlayerNameView.setTypeface(jerseyThin);
		
		// Load values from saved prefs
		mSettings = getSharedPreferences("ShowJersey", MODE_WORLD_READABLE);
		
		// Load resources
		mRes = getResources();
		
		getJerseyValuesFromPreferences();
		
		// Set the Onclick action for the Jersey
		mJerseyView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeColorSecret();
			}
		});
	}
	
	private void getJerseyValuesFromPreferences() {
		// Pull default values from XML and UI components
		mPlayerName = mSettings.getString(PLAYER_NAME, mRes.getString(R.string.start_name));
		mPlayerNumber = mSettings.getInt(PLAYER_NUMBER, Integer.parseInt(mRes.getString(R.string.start_number)));
		mJerseyColorIndex = mSettings.getInt(JERSEY_COLOR, DEFAULT_COLOR_INDEX);
		updateJersey();
	}
	
	private void updateJersey() {
		mPlayerNameView.setText(mPlayerName);
		// We need the extra ""s here because if we don't pass setText a string; 
		// It'll assume we've passed a resource ID
		mPlayerNumberView.setText(mPlayerNumber + "");
		Log.e("SJ", "Index: " + mJerseyColorIndex);
		// Set the Jersy Image to the saved jersey color
		mJerseyView.setImageDrawable(mRes.getDrawable(JERSEY_ARRAY[mJerseyColorIndex]));
		
		int widthThreshold;
		if (mRes.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			mPlayerNameView.setTextSize(mRes.getDimension(R.dimen.name_size_port));
			widthThreshold = 170;
		} else {
			mPlayerNameView.setTextSize(mRes.getDimension(R.dimen.name_size_land));
			widthThreshold = 120;
		}
		
		// Resize the name
		Rect bounds = new Rect();
		Paint textPaint = mPlayerNameView.getPaint();
		textPaint.getTextBounds(mPlayerName, 0, mPlayerName.length(), bounds);
		int textWidth = bounds.width();
		
		while (textWidth > widthThreshold) {
			mPlayerNameView.setTextSize(mPlayerNameView.getTextSize() - 1);
			bounds = new Rect();
			textPaint = mPlayerNameView.getPaint();
			textPaint.getTextBounds(mPlayerName, 0, mPlayerName.length(), bounds);
			textWidth = bounds.width();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Get editor for shared preferences
		
		// TODO: Why are we saving these values?
		// Ah, I think it was because of the old way of doing things
		SharedPreferences.Editor editor = this.mSettings.edit();
		
		// Save the values
		editor.putString(PLAYER_NAME, mPlayerName);
		editor.putInt(PLAYER_NUMBER, mPlayerNumber);
		editor.putInt(JERSEY_COLOR, mJerseyColorIndex);
		
		// Commit the editor
		editor.commit();
		
		Log.w("Jersey", "Updating widget");
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		// Create a pending intent for our JerseyWidget Factory
		Intent refreshIntent = new Intent(this, JerseyWidget.class);
		refreshIntent.setAction(JerseyWidget.UPDATE_WIDGET);
		PendingIntent pendingRefreshIntent = PendingIntent.getBroadcast(this, 0, refreshIntent, 0);
		
		// Set an alarm for now to refresh the widget(s)
		AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarms.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingRefreshIntent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getJerseyValuesFromPreferences();
	}
	
	private void changeColorSecret() {
		// Increment the JersyColorIndex
		mJerseyColorIndex++;
		
		// If it's greater than the number of jerseys, reset to 0
		if (mJerseyColorIndex >= JERSEY_ARRAY.length) {
			mJerseyColorIndex = 0;
		}
		
		// Refresh the ImageView
		mJerseyView.setImageDrawable(mRes.getDrawable(JERSEY_ARRAY[mJerseyColorIndex]));
	}
	
	/**
	 * Fired when the options menu is created
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	/**
	 * Fired when an options menu item is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_edit:
				Intent editIntent = new Intent(ShowJersey.this, EditJersey.class);
				startActivity(editIntent);
				break;
			
		}
		return true;
	}
}
