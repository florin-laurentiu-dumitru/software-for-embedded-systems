package com.MultimediaSeminar;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HistoryActivity extends Activity {

	 private ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		final XMLDatabase database = new XMLDatabase();
		ArrayList<String> filenames = database.getFilesNames(getApplicationContext());
		final ArrayList<String> fileNamesList = new ArrayList<String>();
		
		for(String filename : filenames)
		{
			if(filename.startsWith("Map "))
				fileNamesList.add(filename);
		}
		
		 lv = (ListView) findViewById(R.id.listViewHistory);

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
              int itemPosition     = position;
              
              // ListView Clicked item value
              String  itemValue    = (String) lv.getItemAtPosition(position);
            	 
            	 Intent intent = new Intent(view.getContext(), HistoryDetailActivity.class);
            	 intent.putExtra("file_name",itemValue);
            	 startActivityForResult(intent,0);
                 
             /*  // Show Alert 
               Toast.makeText(getApplicationContext(),
                 "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                 .show();*/
            
             }		
        }); 
         
         	lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	 
             @Override
             public boolean onItemLongClick(AdapterView<?> parent, View view,
                int position, long id) {              
            	 
              // ListView Clicked item index
              int itemPosition     = position;
              
              // ListView Clicked item value
              String  itemValue    = (String) lv.getItemAtPosition(position);
              
              // Show Alert 
              Toast.makeText(getApplicationContext(),
                "File named :"+itemValue+" was deleted ", Toast.LENGTH_LONG)
                .show();
              
              fileNamesList.remove(itemValue);
              arrayAdapter.notifyDataSetChanged();
            	 
              return database.deleteFile(getApplicationContext(), itemValue);
              
            	/* Intent intent = new Intent(view.getContext(), HistoryDetailActivity.class);
            	 intent.putExtra("file_name",itemValue);
            	 startActivityForResult(intent,0);*/
             }		
        });
         
         
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
	}

}
