package com.fernferret.customjersey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowJersey extends Activity {

	static final String PLAYER_NAME = "PLAYERNAME";
	static final String PLAYER_NUMBER = "PLAYERNUMBER";
	static final String IS_BLUE_JERSEY = "ISBLUEJERSEY";
	static final boolean DEFAULT_JERSEY_COLOR = true;
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
		mSettings = getPreferences(MODE_PRIVATE);

		// Load resources
		mRes = getResources();

		// Pull default values from XML and UI components
		mPlayerName = mSettings.getString(PLAYER_NAME, mRes.getString(R.string.start_name));
		mPlayerNumber = mSettings.getInt(PLAYER_NUMBER, Integer.parseInt(mRes.getString(R.string.start_number)));
		mIsBlueJersey = mSettings.getBoolean(IS_BLUE_JERSEY, DEFAULT_JERSEY_COLOR);

		updateJersey();

		// Set the Onclick action
		mEditButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent editIntent = new Intent(ShowJersey.this, EditJersey.class);
				editIntent.putExtra(PLAYER_NAME, mPlayerName);
				editIntent.putExtra(PLAYER_NUMBER, mPlayerNumber);
				editIntent.putExtra(IS_BLUE_JERSEY, mIsBlueJersey);
				startActivityForResult(editIntent, EDIT_JERSEY_REQUEST_CODE);
			}
		});
	}

	private void updateJersey() {
		mPlayerNameView.setText(mPlayerName);
		mPlayerNumberView.setText(mPlayerNumber + "");

		if (mIsBlueJersey) {
			mJerseyView.setImageDrawable(mRes.getDrawable(R.drawable.blue_jersey));
		} else {
			mJerseyView.setImageDrawable(mRes.getDrawable(R.drawable.red_jersey));
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

		// Commit the editor
		editor.commit();
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
		}
	}
}