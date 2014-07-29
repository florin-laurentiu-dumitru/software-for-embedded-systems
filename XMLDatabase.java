package com.MultimediaSeminar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Xml;

import android.content.ContextWrapper;

public class XMLDatabase {

	public String write(Context ctx, ArrayList<LatLng> coordinatesList) throws IllegalArgumentException, IllegalStateException, IOException
	{
		String filename = "Map " + getdateTime();

	    FileOutputStream fos;       

	    fos = ctx.openFileOutput(filename,Context.MODE_APPEND);


	    XmlSerializer serializer = Xml.newSerializer();
	    serializer.setOutput(fos, "UTF-8");
	    serializer.startDocument(null, Boolean.valueOf(true));
	    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

	    serializer.startTag(null, "root");

	    for(LatLng point : coordinatesList)
	    {

	        serializer.startTag(null, "point");
	        
//	        serializer.startTag(null, "latitude");

	        serializer.text(String.valueOf(point.latitude) + " " + String.valueOf(point.longitude));
	        
//	        serializer.endTag(null, "latitude");
	        
//	        serializer.startTag(null, "longitude");

//	        serializer.text(String.valueOf(point.longitude));
	        
//	        serializer.endTag(null, "longitude");

	        serializer.endTag(null, "point");
	    }
	    
	    serializer.endTag(null, "root");
	    
	     serializer.endDocument();

	     serializer.flush();

	     fos.close();
	     
	     return filename;
	}
	
	private String getdateTime()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime()).toString();
	}
	
	public ArrayList<String> read(Context context, String filename) throws IOException, ParserConfigurationException, SAXException
	{
			FileInputStream fis = null;
		    InputStreamReader isr = null;
		    
		    fis = context.openFileInput(filename);
		    isr = new InputStreamReader(fis);
		    char[] inputBuffer = new char[fis.available()];
		    isr.read(inputBuffer);
		    String data = new String(inputBuffer);
		    isr.close();
		    fis.close();

		    /*
		     * converting the String data to XML format
		     * so that the DOM parser understand it as an XML input.
		     */
		        InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));

		      /*  ArrayList<XmlData> xmlDataList = new ArrayList<XmlData>();

		    XmlData xmlDataObj;*/
		    DocumentBuilderFactory dbf;
		    DocumentBuilder db;
		    NodeList items = null;
		    Document dom;

		    dbf = DocumentBuilderFactory.newInstance();
		    db = dbf.newDocumentBuilder();
		    dom = db.parse(is);
		    // normalize the document
		    dom.getDocumentElement().normalize();

		    items = dom.getElementsByTagName("point");

//		    ArrayList<String> arr = new ArrayList<String>();
		    ArrayList<String> arr = new ArrayList<String>();

		    for (int i=0;i<items.getLength();i++){

//		        Node item = items.item(i); 
		        
//		        item.getFirstChild().getNodeValue();
		        
//		        arr.add(new Point(Double.parseDouble(item.getFirstChild().getNodeValue()), Double.parseDouble(item.getLastChild().getNodeValue())));
		        
//		        arr.add(item.getFirstChild().getNodeValue() + " and " + item.getLastChild().getNodeValue());

		        arr.add(items.item(i).getTextContent());

		    }    
		    
		    return arr;
	}
	
	public ArrayList<String> getFilesNames(Context context)
	{
		File dir = new File(context.getFilesDir().toString());
		ArrayList<String> names = new ArrayList<String>();

		// Optain list of files in the directory. 
		// listFiles() returns a list of File objects to each file found.
		File[] files = dir.listFiles();

		// Loop through all files
		for (File f : files ) {
			names.add(f.getName());
		   
		}
		return names;
	}
	
	public boolean deleteFile(Context context, String filename)
	{
		boolean deleted = false;
		File dir = new File(context.getFilesDir().toString());
		File[] files = dir.listFiles();
		for (File f : files ) {
			if(f.getName().equals(filename))
			{
				deleted = f.delete();
			}
		}
		return deleted;
	}
}
