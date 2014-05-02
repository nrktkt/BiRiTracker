package com.blackdoor.biritracker;

import java.util.ArrayList;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class BiRiMapManipulator {

	public enum Role {
		FOLLOW, LEAD
	}

	private Role role;
	private GoogleMap map;
	private LatLng myloc_LL;
	private LatLng lastleaderloc;
	private CameraPosition campos;
	private LatLngBounds bounds;
	private int zoom;
	private ArrayList<Marker> leaderloclist = new ArrayList<Marker>();
	final private String custom_icon = "/BiRiTracker/res/drawable-mdpi/circle_icon.png";

	public BiRiMapManipulator(Role myrole, GoogleMap mymap) {
		role = myrole;
		map = mymap;
	}

	public void addAndmanageMarkers(LatLng newloc) {
		lastleaderloc = newloc;
		if (role == Role.FOLLOW) {
			// keep the last 5 points around
			MarkerOptions options = new MarkerOptions().position(newloc).icon(BitmapDescriptorFactory.fromFile(custom_icon));
			Marker marker = map.addMarker(options);
			if (leaderloclist.size() > 5) {
				leaderloclist.add(0, marker);
				leaderloclist.remove(4);
			} else {
				leaderloclist.add(0, marker);
			}
			// disappearing points to look cool
			float alpha = 1;
			for (int i = 0; i < leaderloclist.size(); i++) {
				leaderloclist.get(i).setAlpha(alpha);
				alpha -= .15;
			}
		} else { 
			// keep the last 5 points around
			Marker marker = map.addMarker(new MarkerOptions()
					.position(newloc).icon(BitmapDescriptorFactory.fromFile(custom_icon)));
			if (leaderloclist.size() > 5) {
				leaderloclist.add(0, marker);
				leaderloclist.remove(4);
			} else {
				leaderloclist.add(0, marker);
			}
			// disappearing points to look cool
			float alpha = 1;
			for (int i = 0; i < leaderloclist.size(); i++) {
				leaderloclist.get(i).setAlpha(alpha);
				alpha -= .15;
			}	
		}
	}
	
	
	private void positionCamera() {
		//FOLLOW camera
		if (role == Role.FOLLOW) {
			bounds = new LatLngBounds(myloc_LL, lastleaderloc);
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
		} else { 
			map.moveCamera(CameraUpdateFactory.newLatLng(lastleaderloc));	
		}
	}
	
	private void positionCamera(LatLng pos) {
		//FOLLOW camera
		if (role == Role.FOLLOW) {
			bounds = new LatLngBounds(pos, lastleaderloc);
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
		} else { 
			map.moveCamera(CameraUpdateFactory.newLatLng(pos));	
		}
	}
	
	public void setFollowerLocation(double lat, double lng) {
		myloc_LL = new LatLng(lat, lng);
	}

	public void setFollowerLocation(LatLng pos) {
		myloc_LL = pos;
	}
	
	public void setLeaderLocation(double lat, double lng) {
		lastleaderloc = new LatLng(lat, lng);
	}

	public void setLeaderLocation(LatLng pos) {
		lastleaderloc = pos;
	}
}
