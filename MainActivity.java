package com.MultimediaSeminar;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LocationListener{
	private GoogleMap googleMap;
	private SupportMapFragment mapFrag;
	private final LatLng Bruxel = new LatLng(50.841286, 4.358824); 
	
	private LocationManager locationManager;
	private String provider;
	private Marker currentLocation;
	boolean tracking = false;
	
	ArrayList<LatLng> coordinatesList = new ArrayList<LatLng>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		googleMap.setMyLocationEnabled(true);
		goToCurrentLocation();
		/*setUpMapIfNeeded();
		if(googleMap != null){
			setUpMap();
			}*/
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick_Save(View v) throws IllegalArgumentException, IllegalStateException, IOException, ParseException, ParserConfigurationException, SAXException
	{
		XMLDatabase database = new XMLDatabase();
		
		//Test all methods of the XMLDatabase object
		testXMLDatabase(database);
		
		String filename = database.write(getApplicationContext(), coordinatesList);
		Toast.makeText(this, "Map saved with name: " + filename, Toast.LENGTH_LONG).show();
//		Toast.makeText(this, "Path: " + getApplicationContext().getFilesDir(), Toast.LENGTH_LONG).show();
	}
	
	public void onClick_CurrentLocation(View v)
	{		
		tracking = true;
//		googleMap.setMyLocationEnabled(true);
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledWiFi = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabledGPS) {
            Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        

        // Initialize the location fields
        if (location != null) {
            Toast.makeText(this, "Selected Provider " + provider,
                    Toast.LENGTH_SHORT).show();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 18);
    		googleMap.animateCamera(update);
    		initializeTracking(location);
            onLocationChanged(location);
        } else {
        	AlertDialog alt_bld = new AlertDialog.Builder(this).create();     
        	alt_bld.setMessage("Current location not found yet!");
        	alt_bld.setButton("Back", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) {dialog.cancel(); } }); 
        	alt_bld.setCancelable(false);
        	alt_bld.show();
        }

	}
	
	
	protected void goToCurrentLocation()
	{
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledWiFi = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabledGPS) {
            Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        

        // Initialize the location fields
        if (location != null) {
            Toast.makeText(this, "Selected Provider " + provider,
                    Toast.LENGTH_SHORT).show();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 18);
    		googleMap.animateCamera(update);
 //           onLocationChanged(location);
    		currentLocation = googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(location.getLatitude(),location.getLongitude()))
            .visible(false));
        } else {
        	AlertDialog alt_bld = new AlertDialog.Builder(this).create();     
        	alt_bld.setMessage("Current location not found yet!");
        	alt_bld.setButton("Back", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) {dialog.cancel(); } }); 
        	alt_bld.setCancelable(false);
        	alt_bld.show();
        }
	}
	
	public void onClick_Stop(View v)
	{
		tracking = false;
//		locationManager.removeUpdates(this);
		Marker endPerc = googleMap.addMarker(new MarkerOptions()
        .position(currentLocation.getPosition())
        .title("End")
        .snippet("ending point"));
	}
	
	private void initializeTracking(Location location)
	{
		double lat = location.getLatitude();
        double lng = location.getLongitude();
        Toast.makeText(this, "Location " + lat+","+lng,
                Toast.LENGTH_LONG).show();
        LatLng coordinate = new LatLng(lat, lng);
        Toast.makeText(this, "Location " + coordinate.latitude+","+coordinate.longitude,
                Toast.LENGTH_LONG).show();
        Marker startPerc = googleMap.addMarker(new MarkerOptions()
        .position(coordinate)
        .title("Start")
        .snippet("starting point"));
        
        coordinatesList.add(coordinate);
        
        currentLocation = googleMap.addMarker(new MarkerOptions()
        .position(coordinate)
        .title("Location")
        .snippet("Your current location")
        .visible(false));
        
	}

	@Override
	public void onLocationChanged(Location location) {
		double lat =  location.getLatitude();
        double lng = location.getLongitude();
//        Toast.makeText(this, "Location " + lat+","+lng,
//                Toast.LENGTH_LONG).show();
        LatLng coordinate = new LatLng(lat, lng);
//        Toast.makeText(this, "Location " + coordinate.latitude+","+coordinate.longitude,
//                Toast.LENGTH_LONG).show();
        if(tracking)
        {
        	Polyline line = googleMap.addPolyline(new PolylineOptions()
            .add(currentLocation.getPosition(), coordinate)
            .width(10)
            .color(Color.BLUE));
        	
        	coordinatesList.add(coordinate);
        	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(coordinate, 18);
    		googleMap.animateCamera(update);
        }
        currentLocation.setPosition(coordinate);	
	}

	@Override
	public void onProviderDisabled(String provider) {
		 Toast.makeText(this, "Enabled new provider " + provider,
	                Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	 /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
   //     locationManager.removeUpdates(this);
    }
	
    protected void testXMLDatabase(XMLDatabase localDatabase) throws IllegalArgumentException, IllegalStateException, IOException, ParseException, ParserConfigurationException, SAXException{
    	localDatabase.testGetdateTime();
    	localDatabase.testWrite(getApplicationContext());
    	localDatabase.testGetFilesNames(getApplicationContext());
    	localDatabase.testRead(getApplicationContext());
    	localDatabase.testDeleteFile(getApplicationContext());
    }

}
