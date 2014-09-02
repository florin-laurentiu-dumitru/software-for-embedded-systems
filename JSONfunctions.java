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

import android.content.Context;
import android.net.ParseException;
import android.util.Log;
import android.widget.Toast;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JSONfunctions {
	
	public static JSONObject getJSONfromURL(String url) {
	    InputStream is = null;
	    String result = "";
	    JSONObject jArray = null;

	    // Download JSON data from URL
	    try {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(url);
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();

	    } catch (Exception e) {
	        Log.e("log_tag", "Error in http connection " + e.toString());
	    }

	    // Convert response to string
	    try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                is, "iso-8859-1"), 8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	        is.close();
	        result = sb.toString();
	    } catch (Exception e) {
	        Log.e("log_tag", "Error converting result " + e.toString());
	    }

	    try {

	        jArray = new JSONObject(result);
	    } catch (JSONException e) {
	        Log.e("log_tag", "Error parsing data " + e.toString());
	    }

	    return jArray;
	}
	
	public static void readNames(Context context, ArrayList<String> filenames)
	{
		JSONArray jArray = null;

		String result = null;

		StringBuilder sb = null;

		InputStream is = null;

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//http post
		try{
		     HttpClient httpclient = new DefaultHttpClient();

		     
		     HttpPost httppost = new HttpPost("http://134.184.113.5/multimediaSeminar/myFile.php");
		     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpclient.execute(httppost);
		     HttpEntity entity = response.getEntity();
		     is = entity.getContent();
		     }catch(Exception e){
		         Log.e("log_tag", "Error in http connection "+e.toString());
		    }
		//convert response to string
		try{
		      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       sb = new StringBuilder();
		       sb.append(reader.readLine() + "\n");

		       String line="0";
		       while ((line = reader.readLine()) != null) {
		                      sb.append(line + "\n");
		        }
		        is.close();
		        result=sb.toString();
		        }catch(Exception e){
		              Log.e("log_tag", "Error converting result "+e.toString());
		        }
//		Toast.makeText(getBaseContext(), "result: " + result ,Toast.LENGTH_LONG).show();

		String ct_name = "";
		try{
		      jArray = new JSONArray(result);
		      JSONObject json_data=null;
		      for(int i=0;i<jArray.length();i++){
		             json_data = jArray.getJSONObject(i);
//		             ct_name = json_data.getString("name") + " ";//here "Name" is the column name in database
		             ct_name = json_data.getString("name");
		             filenames.add(ct_name);
		         }
		      }
		      catch(JSONException e1){
		       Toast.makeText(context, "No Data Found " + e1.toString() ,Toast.LENGTH_LONG).show();
		      } catch (ParseException e1) {
		   e1.printStackTrace();
		 }
		
//		Toast.makeText(getBaseContext(), ct_name ,Toast.LENGTH_LONG).show();
		}	
	
//////////////////////////////////////////////////////////////
//Test procedures
/////////////////////////////////////////////////////////////
	public void testGetJSONfromURL(){
		
	}
	
	// Test if current device has uploaded successfully the personal map
	public void testReadNames(Context context){
		JSONArray jArray = null;
		String result = null;
		StringBuilder sb = null;
		InputStream is = null;
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try{
		     HttpClient httpclient = new DefaultHttpClient();
		     HttpPost httppost = new HttpPost("http://134.184.113.5/multimediaSeminar/myFile.php");
		     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpclient.execute(httppost);
		     HttpEntity entity = response.getEntity();
		     is = entity.getContent();
		     }catch(Exception e){
		         Log.e("log_tag", "Error in http connection "+e.toString());
		    }
		//convert response to string
		try{
		      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       sb = new StringBuilder();
		       sb.append(reader.readLine() + "\n");

		       String line="0";
		       while ((line = reader.readLine()) != null) {
		                      sb.append(line + "\n");
		        }
		        is.close();
		        result=sb.toString();
		        }catch(Exception e){
		              Log.e("log_tag", "Error converting result "+e.toString());
		        }
		String ct_name = "";
		
		boolean current_device_posted = false;
		
		try{
		      jArray = new JSONArray(result);
		      JSONObject json_data=null;
		      for(int i=0;i<jArray.length();i++){
		             json_data = jArray.getJSONObject(i);
//		             ct_name = json_data.getString("name") + " ";//here "Name" is the column name in database
		             ct_name = json_data.getString("name");	             
		             if(ct_name.split("-")[0].equals(android.os.Build.MODEL)) current_device_posted = true;	             
		         }
		      }
		      catch(JSONException e1){
		       Toast.makeText(context, "No Data Found " + e1.toString() ,Toast.LENGTH_LONG).show();
		      } catch (ParseException e1) {
		   e1.printStackTrace();
		 }
		// Verify that the Sql database contains at least one post from current device
		assertEquals(ct_name.split("-")[0], android.os.Build.MODEL);
	}
}
