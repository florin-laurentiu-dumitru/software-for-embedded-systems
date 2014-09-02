package com.MultimediaSeminar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.os.Bundle;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {

		// Making HTTP request
		try {
			
			// check for request method
			if(method == "POST"){
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				
			}else if(method == "GET"){
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}			
			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			
			Log.v("Json Response ", json);
			
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
//			jObj = new JSONObject("new");
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
	
	
//////////////////////////////////////////////////////////////
//Test procedures
/////////////////////////////////////////////////////////////
	
	public void testMakeHttpRequest(){
		new TestCreateNewProduct().execute();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		new TestGetMapDetails().execute();
	}
	
	class TestCreateNewProduct extends AsyncTask<String, String, String> {

		int success;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			
			String name = android.os.Build.MODEL + " - " + "Test-create-map";
			String latitude = "";
			String longitude = ""; 
			
			latitude += "40.720201058841496";
			longitude += "-74.07051086425781";

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("latitude", latitude));
			params.add(new BasicNameValuePair("longitude", longitude));

			JSONObject json = makeHttpRequest("http://134.184.113.5/multimediaseminar/create_map.php",
					"POST", params);
			
			Log.d("Create Response", json.toString());

			try {
				success = json.getInt("success");	
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// test if the data was written to MySql database
			assertEquals(success, 1);
			
			return null;
		}

	}
	
	class TestGetMapDetails extends AsyncTask<String, String, String> {
		int success;
		protected String doInBackground(String... params) {

			Thread t = new Thread(new Runnable() {
				public void run() {		
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("name", android.os.Build.MODEL + " - " + "Test-create-map"));

						JSONObject json = makeHttpRequest("http://134.184.113.5/multimediaseminar/get_map.php",
								"GET", params);

						Log.d("Single Map Details", json.toString());
						
						success = json.getInt("success");
						
						//test if the data was read to MySql database
						assertEquals(success, 1);
						
						if (success == 1) {
							// successfully received product details
							JSONArray productObj = json
									.getJSONArray("product");
							
							// get first product object from JSON Array
							JSONObject product = productObj.getJSONObject(0);

							// test if read data is the same as written data
							assertEquals("40.720201058841496",product.getString("latitude"));
							assertEquals("-74.07051086425781", product.getString("longitude"));
							

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
	}
}
