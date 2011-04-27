package com.fernferret.customjersey;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditJersey extends Activity {
	
	private Button mOkButton;
	private Button mCancelButton;
	
	private EditText mName;
	private EditText mNumber;
	private Spinner mColorPicker;
	
	private Resources mRes;
	private SharedPreferences mSettings;
	
	private int mNumberValue;
	private int mColorIndexValue;
	private String mNameValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		mSettings = getSharedPreferences("ShowJersey", Activity.MODE_WORLD_READABLE);
		mRes = getResources();
		
		// Load variables from prefs
		mNameValue = mSettings.getString(ShowJersey.PLAYER_NAME, mRes.getString(R.string.start_name));
		mNumberValue = mSettings.getInt(ShowJersey.PLAYER_NUMBER, Integer.parseInt(mRes.getString(R.string.start_number)));
		mColorIndexValue = mSettings.getInt(ShowJersey.JERSEY_COLOR_INDEX, Integer.parseInt(mRes.getString(R.string.start_color_index)));
		
		// Load buttons from view
		mOkButton = (Button)findViewById(R.id.ok);
		mCancelButton = (Button)findViewById(R.id.cancel);
		
		// Load Textboxes from view
		mName = (EditText) findViewById(R.id.name_edit);
		mNumber = (EditText) findViewById(R.id.number_edit);
		
		// Load the color spinner
		mColorPicker = (Spinner) findViewById(R.id.jersey_color);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.color_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mColorPicker.setAdapter(adapter);
	    
	    mColorPicker.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				mColorIndexValue = pos;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Stop. Hammer Time.
				// We could also collaborate and listen if you prefer...
			}
		});
		
	    // Set all of our UI elements to the saved values
		mName.setText(mNameValue);
		mNumber.setText(mNumberValue + "");
		mColorPicker.setSelection(mColorIndexValue);
		
		// Set click listeners for the OK and Cancel buttons
		mOkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = mSettings.edit();
				
				String numberString = mNumber.getText() + "";
				
				if(numberString.length() > 0) {
					editor.putInt(ShowJersey.PLAYER_NUMBER, Integer.parseInt(numberString));
				} else {
					editor.putInt(ShowJersey.PLAYER_NUMBER, mNumberValue);
				}
				
				// Save the name and the color index
				editor.putString(ShowJersey.PLAYER_NAME, mName.getText().toString().toUpperCase());
				editor.putInt(ShowJersey.JERSEY_COLOR_INDEX, mColorIndexValue);
				
				// Commit the results to settings
				editor.commit();
				
				// Finish the activity
				finish();
			}
		});
		
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Finish the activity
				finish();
			}
		});
	}
}
