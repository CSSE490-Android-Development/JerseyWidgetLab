package com.fernferret.customjersey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
	private int[] mHiddenJerseys;
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
		mSettings = getPreferences(MODE_PRIVATE);

		// Load resources
		mRes = getResources();

		// Pull default values from XML and UI components
		mPlayerName = mSettings.getString(PLAYER_NAME, mRes.getString(R.string.start_name));
		mPlayerNumber = mSettings.getInt(PLAYER_NUMBER, Integer.parseInt(mRes.getString(R.string.start_number)));
		mIsBlueJersey = mSettings.getBoolean(IS_BLUE_JERSEY, DEFAULT_JERSEY_COLOR);
		mHiddenJerseyIndex = mSettings.getInt(JERSEY_COLOR, DEFAULT_COLOR_INDEX);

		mHiddenJerseys = new int[]{R.drawable.red_jersey,R.drawable.orange_jersey,R.drawable.blue_jersey,R.drawable.green_jersey};
		
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
		
		mJerseyView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeColorSecret();
			}
		});
	}

	private void updateJersey() {
		mPlayerNameView.setText(mPlayerName);
		mPlayerNumberView.setText(mPlayerNumber + "");
		Log.e("SJ", "Index: " + mHiddenJerseyIndex);
		if (mHiddenJerseyIndex != -1) {
			mJerseyView.setImageDrawable(mRes.getDrawable(mHiddenJerseys[mHiddenJerseyIndex]));
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
		if(mHiddenJerseyIndex == -1) {
			if(mIsBlueJersey) {
				mHiddenJerseyIndex = 2;
			} else {
				mHiddenJerseyIndex = 0;
			}
		}
		mHiddenJerseyIndex++;
		if(mHiddenJerseyIndex >= mHiddenJerseys.length) {
			mHiddenJerseyIndex = 0;
		}
		
		mJerseyView.setImageDrawable(mRes.getDrawable(mHiddenJerseys[mHiddenJerseyIndex]));
	}
}
