package com.blackdoor.biritracker;

import java.io.Serializable;
//import blackdoor.util.*;
import android.provider.Settings;

public class Leader implements Serializable {
	private String name;
	private double latitude;
	private double longitude;
	private byte[] ID;
	/**
	 * @return the iD
	 */
	public byte[] getID() {
		return ID;
	}
	/**
	 * @param iD the iD to set
	 */
	public void GenerateID() {
		ID = Hash.getSHA1(Settings.Secure.ANDROID_ID.getBytes());
	}
	
	/**
	 * @param name
	 * @param latitude
	 * @param longitude
	 */
	public Leader(String name, double latitude, double longitude) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	/**
	 * @param name
	 * @param latitude
	 * @param longitude
	 */
	public Leader(String name) {
		super();
		this.name = name;
		this.latitude = 0;
		this.longitude = 0;
	}
	
	/**
	 * @param name
	 * @param latitude
	 * @param longitude
	 */
	public Leader() {
		super();
		this.name = "";
		this.latitude = 0;
		this.longitude = 0;
	}
	
	public void setLocation(double latitude, double longitude){
		this.latitude = 0;
		this.longitude = 0;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
}
