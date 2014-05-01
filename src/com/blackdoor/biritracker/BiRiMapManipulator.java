package com.blackdoor.biritracker;

import java.util.ArrayList;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class BiRiMapManipulator {

	public enum Role {
		FOLLOW, LEAD
	}

	private Role role;
	private GoogleMap map;
	private LatLng myloc_LL;
	private Location myloc_L;
	private final long LOCATION_REFRESH_TIME = 10000;
	private final float LOCATION_REFRESH_DISTANCE = 10000;
	private LatLng lastleaderloc;
	private CameraPosition campos;
	private ArrayList<Marker> leaderloclist = new ArrayList<Marker>();

	public BiRiMapManipulator(Role myrole, GoogleMap mymap) {
		role = myrole;
		map = mymap;
	}

	public void addAndmanageMarkers(LatLng newloc) {
		//FOLLOW manage
		if (role == Role.FOLLOW) {
			// keep the last 5 points around
			Marker marker = map.addMarker(new MarkerOptions()
					.position(newloc));
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
					.position(newloc));
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

		} else { 
			//LEADER camera 
				
		}
	}

	public void setLeaderLocation(double lat, double lng) {
		lastleaderloc = new LatLng(lat, lng);
	}

	public void setLeaderLocation(LatLng pos) {
		lastleaderloc = pos;
	}
}
