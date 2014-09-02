package com.MultimediaSeminar;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SharedMapsDetailActivity extends Activity {
	
	private ArrayList<Point> mapPoints = new ArrayList<Point>();
	
	private GoogleMap googleMap;
	private SupportMapFragment mapFrag;
	
	String name;
	String testName = "";
	String latitude = "";
	String longitude = "";
	
	// Progress Dialog
		private ProgressDialog pDialog;

		// JSON parser class
		JSONParser jsonParser = new JSONParser();
		
		// url to create new product
		private static String url_get_map = "http://134.184.113.5/multimediaseminar/get_map.php";

		// JSON Node names
		private static final String TAG_SUCCESS = "success";
		
		private static final String TAG_PRODUCT = "product";
		private static final String TAG_NAME = "name";
		private static final String TAG_LATITUDE = "latitude";
		private static final String TAG_lONGITUDE = "longitude";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shared_maps_detail);
		
		Bundle extras = getIntent().getExtras();
		name = extras.getString("name");
		
		new GetMapDetails().execute();
		
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shared_maps_detail, menu);
		return true;
	}
	
	protected void drawMap()
	{
		String[] latitudes = latitude.split(" ");
		String[] longitudes = longitude.split(" ");
		int j = 0;
		for(String coordinate : latitudes)
		{
			Point newPoint = new Point(Double.parseDouble(latitudes[j]), Double.parseDouble(longitudes[j]));
			mapPoints.add(newPoint);
			j++;
		}	
		
		for(int i=1; i<mapPoints.size(); i++)
		{
			Polyline line = googleMap.addPolyline(new PolylineOptions()
			.add(new LatLng(mapPoints.get(i-1).getLatitude(), mapPoints.get(i-1).getLongitute()), new LatLng(mapPoints.get(i).getLatitude(), mapPoints.get(i).getLongitute()))
        	.width(10)
        	.color(Color.BLUE));
		}
		goToStartpoint();
	}
	
	// could not be tested because of the complexity of the API
	protected void goToStartpoint()
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
	
	class GetMapDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("name", name));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_get_map, "GET", params);

						// check your log for json response
						Log.d("Single Map Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray productObj = json
									.getJSONArray(TAG_PRODUCT); // JSON Array
							
							// get first product object from JSON Array
							JSONObject product = productObj.getJSONObject(0);

							// display product data in EditText
							
							testName += product.getString(TAG_NAME);
							latitude = product.getString(TAG_LATITUDE);
							longitude = product.getString(TAG_lONGITUDE);
							

						}else{
							// product with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {

			drawMap();
		}
	}
	
//////////////////////////////////////////////////////////////
//Test procedures
/////////////////////////////////////////////////////////////
	protected void testDrawMap()
	{
		// Verify that latitude and longitude coordinates were read
		assertNotNull(latitude);
		assertNotNull(longitude);
		
		String[] latitudes = latitude.split(" ");
		String[] longitudes = longitude.split(" ");
		
		// Verify that there as as many latitude coordinates as longitude coordinates
		assertEquals(latitudes.length, longitudes.length);
		
		int j = 0;
		for(String coordinate : latitudes)
		{
			Point newPoint = new Point(Double.parseDouble(latitudes[j]), Double.parseDouble(longitudes[j]));
			
			// Verify that the new point has been creates
			assertNotNull(newPoint);
			
			mapPoints.add(newPoint);
			j++;
		}	
		
		// Verify that the list of points contains at least two points
		assertTrue(mapPoints.size() >= 2);
		
		Polyline previousLine = googleMap.addPolyline(new PolylineOptions()
		.add(new LatLng(mapPoints.get(0).getLatitude(), mapPoints.get(0).getLongitute()), new LatLng(mapPoints.get(1).getLatitude(), mapPoints.get(1).getLongitute()))
    	.width(10)
    	.color(Color.BLUE));
    	
    	// Verify that the list of points is not empty
    	assertNotNull(mapPoints);
		
		for(int i=1; i<mapPoints.size(); i++)
		{
			Polyline line = googleMap.addPolyline(new PolylineOptions()
			.add(new LatLng(mapPoints.get(i-1).getLatitude(), mapPoints.get(i-1).getLongitute()), new LatLng(mapPoints.get(i).getLatitude(), mapPoints.get(i).getLongitute()))
        	.width(10)
        	.color(Color.BLUE));
			
			assertNotNull(line);
			// Verify that the end of the last line is the begining of the new one
			assertEquals(line.getPoints().get(0), previousLine.getPoints().get(1));
		}
		//Clear the map after testing
		googleMap.clear();
	}

}
