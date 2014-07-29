package com.MultimediaSeminar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.MapFragment;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

public class HistoryDetailActivity extends Activity {
	
	private GoogleMap googleMap;
	private SupportMapFragment mapFrag;
	
	private LocationManager locationManager;
	private String provider;
	
	private ArrayList<Point> mapPoints = new ArrayList<Point>();
	private final LatLng Bruxel = new LatLng(50.841286, 4.358824); 
	String value = "";
	
	JSONParser jsonParser = new JSONParser();
	
	// url to create new product
	private static String url_create_map = "http://134.184.113.5/multimediaseminar/create_map.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_detail);
		
//		String value = "";
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		   value = extras.getString("file_name");
		}
		
		/*Toast.makeText(getApplicationContext(),
                value , Toast.LENGTH_LONG)
                .show();*/
		
		XMLDatabase database = new XMLDatabase();
		ArrayList<String> points = new ArrayList<String>();
			
		try {
			points = database.read(getApplicationContext(), value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for(String p : points)
		{
			String[] parts = p.split(" ");
			Point newPoint = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
			mapPoints.add(newPoint);
		}
		
		/*Toast.makeText(getApplicationContext(),
				mapPoints.toString() , Toast.LENGTH_LONG)
                .show();*/
		
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		googleMap.setMyLocationEnabled(true);
		
		goToStartpoint();
		
		drawRoute();
	}
	
	private void goToStartpoint()
	{
		googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		Point startingPoint = mapPoints.get(0);
		Point endPoint = mapPoints.get(mapPoints.size() - 1);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(startingPoint.getLatitude(), startingPoint.getLongitute()), 16);
//		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(Bruxel, 16);
		googleMap.animateCamera(update);
		
		Marker startPerc = googleMap.addMarker(new MarkerOptions()
        .position(new LatLng(startingPoint.getLatitude(), startingPoint.getLongitute()))
        .title("Start")
        .snippet("starting point"));
		
		Marker endPerc = googleMap.addMarker(new MarkerOptions()
        .position(new LatLng(endPoint.getLatitude(), endPoint.getLongitute()))
        .title("End")
        .snippet("ending point"));
		
		
	}
	
	private void drawRoute()
	{
		for(int i=1; i<mapPoints.size(); i++)
		{
			Polyline line = googleMap.addPolyline(new PolylineOptions()
			.add(new LatLng(mapPoints.get(i-1).getLatitude(), mapPoints.get(i-1).getLongitute()), new LatLng(mapPoints.get(i).getLatitude(), mapPoints.get(i).getLongitute()))
        	.width(10)
        	.color(Color.BLUE));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history_detail, menu);
		return true;
	}
	
		public void onClick_Share(View v)
	{
		new CreateNewProduct().execute();
	}
		
		class CreateNewProduct extends AsyncTask<String, String, String> {

			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				/*pDialog = new ProgressDialog(NewProductActivity.this);
				pDialog.setMessage("Registering Map..");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();*/
			}

			/**
			 * Creating product
			 * */
			protected String doInBackground(String... args) {
				
				String name = android.os.Build.MODEL + " - " + value;
				String latitude = "";
				String longitude = ""; 
				
				latitude += mapPoints.get(0).getLatitude() + " ";
				longitude += mapPoints.get(0).getLongitute() + " ";
				
				for(int i=0; i<mapPoints.size(); i++)
				{
					if ((i+1) % 5 == 0)
					{
					latitude += mapPoints.get(i).getLatitude() + " ";
					longitude += mapPoints.get(i).getLongitute() + " ";
					}
				}
				
				latitude += mapPoints.get(mapPoints.size() - 1).getLatitude() + " ";
				longitude += mapPoints.get(mapPoints.size() - 1).getLongitute() + " ";


				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name", name));
				params.add(new BasicNameValuePair("latitude", latitude));
				params.add(new BasicNameValuePair("longitude", longitude));

				// getting JSON Object
				// Note that create product url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(url_create_map,
						"POST", params);
				
				// check log cat fro response
				Log.d("Create Response", json.toString());

				// check for success tag
				try {
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						
						// closing this screen
						finish();
					} else {
						/*Toast.makeText(getApplicationContext(), "An error has occurred, the map was not shared!",
				                Toast.LENGTH_LONG).show();*/
						// failed to create product
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return null;
			}

			/**
			 * After completing background task Dismiss the progress dialog
			 * **/
			protected void onPostExecute(String file_url) {
				// dismiss the dialog once done
//				pDialog.dismiss();
			}

		}

}
