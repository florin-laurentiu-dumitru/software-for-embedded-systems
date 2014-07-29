package com.MultimediaSeminar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class SelectorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selector);
		
		/*Toast.makeText(getApplicationContext(),
                "Device name :" + android.os.Build.MODEL , Toast.LENGTH_LONG)
                .show();*/
		
	}
	
	public void onClick_Map(View v)
	{
		Intent intent = new Intent(v.getContext(), MainActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.selector, menu);
		return true;
	}
	
	public void onClick_History(View v)
	{
		Intent intent = new Intent(v.getContext(), HistoryActivity.class);
		startActivityForResult(intent, 0);
	}
	
	public void onClick_SharedMaps(View v)
	{
		Intent intent = new Intent(v.getContext(), SharedMapsActivity.class);
		startActivityForResult(intent, 0);
	}

}
