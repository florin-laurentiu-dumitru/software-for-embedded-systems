package com.MultimediaSeminar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SharedMapsActivity extends Activity {

	ArrayList<String> filenames = new ArrayList<String>();
	private ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shared_maps);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		JSONfunctions json = new JSONfunctions();
		json.readNames(getBaseContext(), filenames);
		
		final ArrayList<String> fileNamesList = new ArrayList<String>();
		
		for(String filename : filenames)
		{
				fileNamesList.add(filename);
		}
		
		 lv = (ListView) findViewById(R.id.listViewSharedMaps);


         // This is the array adapter, it takes the context of the activity as a 
         // first parameter, the type of list view as a second parameter and your 
         // array as a third parameter.
         final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                 this, 
                 android.R.layout.simple_list_item_1,
                 fileNamesList );

         lv.setAdapter(arrayAdapter); 
         
         lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	 
             @Override
             public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {              
            	 
              // ListView Clicked item index
              int itemPosition = position;
              
              // ListView Clicked item value
              String  itemValue    = (String) lv.getItemAtPosition(position);
            	 
            	 Intent intent = new Intent(view.getContext(), SharedMapsDetailActivity.class);
            	 intent.putExtra("name",itemValue);
            	 startActivityForResult(intent,0);
                 
             /*  // Show Alert 
               Toast.makeText(getApplicationContext(),
                 "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                 .show();*/
            
             }		
        }); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shared_maps, menu);
		return true;
	}
	
	
}


