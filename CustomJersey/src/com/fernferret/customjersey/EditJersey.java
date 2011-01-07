package com.fernferret.customjersey;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class EditJersey extends Activity {
	
	private Button mOkButton;
	private Button mCancelButton;
	private ToggleButton mIsBlueJerseyButton;
	
	private EditText mName;
	private EditText mNumber;
	
	private Resources mRes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		
		mRes = getResources();
		
		mOkButton = (Button)findViewById(R.id.ok);
		mCancelButton = (Button)findViewById(R.id.cancel);
		mIsBlueJerseyButton = (ToggleButton)findViewById(R.id.jersey_toggle);
		
		mName = (EditText) findViewById(R.id.name_edit);
		mNumber = (EditText) findViewById(R.id.number_edit);
		
		mName.setText(getIntent().getStringExtra(ShowJersey.PLAYER_NAME));
		mNumber.setText(getIntent().getIntExtra(ShowJersey.PLAYER_NUMBER, Integer.parseInt(mRes.getString(R.string.start_number))) + "");
		
		mIsBlueJerseyButton.setChecked(!getIntent().getBooleanExtra(ShowJersey.IS_BLUE_JERSEY, ShowJersey.DEFAULT_JERSEY_COLOR));
		
		mOkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent result = new Intent();
				result.putExtra(ShowJersey.PLAYER_NAME, mName.getText() + "");
				String numberString = mNumber.getText() + "";
				if(numberString.length() > 0) {
					result.putExtra(ShowJersey.PLAYER_NUMBER, Integer.parseInt(numberString));
				}
				result.putExtra(ShowJersey.IS_BLUE_JERSEY, !mIsBlueJerseyButton.isChecked());
				setResult(Activity.RESULT_OK, result);
				finish();
			}
		});
		
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
