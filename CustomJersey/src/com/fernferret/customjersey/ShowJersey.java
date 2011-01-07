package com.fernferret.customjersey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowJersey extends Activity {

	static final String PLAYER_NAME = "PLAYERNAME";
	static final String PLAYER_NUMBER = "PLAYERNUMBER";
	static final int EDIT_JERSEY_REQUEST_CODE = 0;
	
	private Button mEditButton;
	private TextView mPlayerNameView;
	private TextView mPlayerNumberView;
	
	private SharedPreferences mSettings;
	private Resources mRes;
	
	private String mPlayerName;
	private int mPlayerNumber;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Load the UI components
		mPlayerNameView = (TextView)findViewById(R.id.name);
		mPlayerNumberView = (TextView)findViewById(R.id.number);
		mEditButton = (Button) findViewById(R.id.edit_button);
		
		// Load values from saved prefs
		mSettings = getPreferences(MODE_PRIVATE);
		
		// Load resources
		mRes = getResources();
		
		// Pull default values from XML
		mPlayerName = mSettings.getString(PLAYER_NAME, mRes.getString(R.string.start_name));
		mPlayerNumber = mSettings.getInt(PLAYER_NUMBER, Integer.parseInt(mRes.getString(R.string.start_number)));
		
		// Set the Onclick action
		mEditButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent editIntent = new Intent(ShowJersey.this, EditJersey.class);
				editIntent.putExtra(PLAYER_NAME, mPlayerName);
				editIntent.putExtra(PLAYER_NUMBER, mPlayerNumber);
				startActivityForResult(editIntent, EDIT_JERSEY_REQUEST_CODE);
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Get editor for shared preferences
		SharedPreferences.Editor editor = this.mSettings.edit();

		// Put in the number of buttons
		editor.putString(PLAYER_NAME, mPlayerName);
		editor.putInt(PLAYER_NUMBER, mPlayerNumber);

		// Commit the editor
		editor.commit();
	}
	
	
}