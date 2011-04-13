package com.fernferret.customjersey;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

public class ShowJersey extends Activity {
	
	static final String PLAYER_NAME = "PLAYERNAME";
	static final String PLAYER_NUMBER = "PLAYERNUMBER";
	static final String IS_BLUE_JERSEY = "ISBLUEJERSEY";
	static final String JERSEY_COLOR = "JERSEYCOLOR";
	static final boolean DEFAULT_JERSEY_COLOR = true;
	static final int DEFAULT_COLOR_INDEX = 0;
	static final int EDIT_JERSEY_REQUEST_CODE = 0;
	
	private Button mEditButton;
	private TextView mPlayerNameView;
	private TextView mPlayerNumberView;
	private ImageView mJerseyView;
	
	private SharedPreferences mSettings;
	private Resources mRes;
	
	private String mPlayerName;
	private int mPlayerNumber;
	private boolean mIsBlueJersey;
	public static final int[] JERSEY_ARRAY = new int[] { R.drawable.red_jersey, R.drawable.orange_jersey, R.drawable.blue_jersey, R.drawable.green_jersey };
	public static final int[] JERSEY_ARRAY_MINI = new int[] { R.drawable.red_jersey_widget, R.drawable.orange_jersey_widget, R.drawable.blue_jersey_widget, R.drawable.green_jersey_widget };
	private int mHiddenJerseyIndex = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Load the UI components
		mPlayerNameView = (TextView) findViewById(R.id.name);
		mPlayerNumberView = (TextView) findViewById(R.id.number);
		mEditButton = (Button) findViewById(R.id.edit_button);
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
		
		// Pull default values from XML and UI components
		mPlayerName = mSettings.getString(PLAYER_NAME, mRes.getString(R.string.start_name));
		mPlayerNumber = mSettings.getInt(PLAYER_NUMBER, Integer.parseInt(mRes.getString(R.string.start_number)));
		mIsBlueJersey = mSettings.getBoolean(IS_BLUE_JERSEY, DEFAULT_JERSEY_COLOR);
		mHiddenJerseyIndex = mSettings.getInt(JERSEY_COLOR, DEFAULT_COLOR_INDEX);
		
		
		
		updateJersey();
		
		
		// Set the Onclick action
		mEditButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				takeMeToTheEditPage();
			}
		});
		
		mJerseyView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeColorSecret();
			}
		});
	}
	
	private void takeMeToTheEditPage() {
		Intent editIntent = new Intent(ShowJersey.this, EditJersey.class);
		editIntent.putExtra(PLAYER_NAME, mPlayerName);
		editIntent.putExtra(PLAYER_NUMBER, mPlayerNumber);
		editIntent.putExtra(IS_BLUE_JERSEY, mIsBlueJersey);
		startActivityForResult(editIntent, EDIT_JERSEY_REQUEST_CODE);
	}
	
	private void updateJersey() {
		mPlayerNameView.setText(mPlayerName);
		mPlayerNumberView.setText(mPlayerNumber + "");
		Log.e("SJ", "Index: " + mHiddenJerseyIndex);
		if (mHiddenJerseyIndex != -1) {
			mJerseyView.setImageDrawable(mRes.getDrawable(JERSEY_ARRAY[mHiddenJerseyIndex]));
		} else if (mIsBlueJersey) {
			mJerseyView.setImageDrawable(mRes.getDrawable(R.drawable.blue_jersey));
		} else {
			mJerseyView.setImageDrawable(mRes.getDrawable(R.drawable.red_jersey));
		}
		
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
		SharedPreferences.Editor editor = this.mSettings.edit();
		
		// Put in the number of buttons
		editor.putString(PLAYER_NAME, mPlayerName);
		editor.putInt(PLAYER_NUMBER, mPlayerNumber);
		editor.putBoolean(IS_BLUE_JERSEY, mIsBlueJersey);
		editor.putInt(JERSEY_COLOR, mHiddenJerseyIndex);
		
		// Commit the editor
		editor.commit();
		
		// Refersh the AppWidget
//		Intent refreshIntent = new Intent(this, JerseyWidget.class);
//		refreshIntent.setAction(JerseyWidget.UPDATE_WIDGET);
//		PendingIntent pendingRefreshIntent = PendingIntent.getBroadcast(this, 0, refreshIntent, 0);
		
		Log.w("Jersey", "Updating widget");
//		Context c = this.getBaseContext();
//		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(c);
//		RemoteViews views = new RemoteViews("com.fernferret.customjersey", R.layout.widget_default);
//		ComponentName jerseyWidget = new ComponentName(c, JerseyWidget.class);
//		appWidgetManager.updateAppWidget(jerseyWidget, views);

	}
	@Override
	protected void onStop() {
		super.onStop();
		
//		RemoteViews views = new RemoteViews("com.fernferret.customjersey", R.layout.widget_default);
//		ComponentName jerseyUpdate = new ComponentName(getBaseContext(), JerseyWidget.class);  
//        AppWidgetManager.getInstance(getBaseContext()).updateAppWidget(jerseyUpdate, views);
		
		Intent refreshIntent = new Intent(this, JerseyWidget.class);
		refreshIntent.setAction(JerseyWidget.UPDATE_WIDGET);
		PendingIntent pendingRefreshIntent = PendingIntent.getBroadcast(this, 0, refreshIntent, 0);
		
		AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		//alarms.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingRefreshIntent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateJersey();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EDIT_JERSEY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			mPlayerName = data.getStringExtra(PLAYER_NAME);
			mPlayerNumber = data.getIntExtra(PLAYER_NUMBER, Integer.parseInt(mRes.getString(R.string.start_number)));
			mIsBlueJersey = data.getBooleanExtra(IS_BLUE_JERSEY, DEFAULT_JERSEY_COLOR);
			mHiddenJerseyIndex = -1;
		}
	}
	
	private void changeColorSecret() {
		if (mHiddenJerseyIndex == -1) {
			if (mIsBlueJersey) {
				mHiddenJerseyIndex = 2;
			} else {
				mHiddenJerseyIndex = 0;
			}
		}
		mHiddenJerseyIndex++;
		if (mHiddenJerseyIndex >= JERSEY_ARRAY.length) {
			mHiddenJerseyIndex = 0;
		}
		
		mJerseyView.setImageDrawable(mRes.getDrawable(JERSEY_ARRAY[mHiddenJerseyIndex]));
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
				takeMeToTheEditPage();
				break;
			
		}
		return true;
	}
}
