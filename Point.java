package com.MultimediaSeminar;

public class Point {

	private double latitude;
	private double longitute;
	
	public Point(){}
	
	public Point(double latitude, double longitute)
	{
		this.setLatitude(latitude);
		this.setLongitute(longitute);
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitute() {
		return longitute;
	}
	public void setLongitute(double longitute) {
		this.longitute = longitute;
	}
	
	public String toString()
	{
		return String.valueOf(this.getLatitude()) + " " + String.valueOf(this.getLongitute());
	}
	
}
