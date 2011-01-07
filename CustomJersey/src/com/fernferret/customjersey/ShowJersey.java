package com.fernferret.customjersey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShowJersey extends Activity {

	static final String PLAYER_NAME = "PLAYERNAME";
	static final String PLAYER_NUMBER = "PLAYERNUMBER";
	static final int EDIT_JERSEY_REQUEST_CODE = 0;
	
	private Button mEditButton;
	private String mPlayerName;
	private String mPlayerNumber;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mEditButton = (Button) findViewById(R.id.edit_button);
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
}