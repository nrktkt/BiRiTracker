package com.blackdoor.biritracker;

import java.util.ArrayList;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class BiRiMapManipulator {
	
	public enum Role{
		FOLLOW, LEAD}
	private Role role;
	private GoogleMap map;
	private LatLng myloc_LL;
	private Location myloc_L;
	private LocationManager mLocationManager;
	private final long LOCATION_REFRESH_TIME = 10000;
	private final float LOCATION_REFRESH_DISTANCE = 10000;
	private LatLng leaderloc;
	private CameraPosition campos;
	private ArrayList<Marker> leaderloclist = new ArrayList<Marker>();
	
	public BiRiMapManipulator(Role myrole){
		role = myrole;
	}
	

	private void addAndmanageMarkers() {
		// keep the current and last ten points around
		Marker marker = map.addMarker(new MarkerOptions().position(leaderloc));
		if (leaderloclist.size() > 10) {
			leaderloclist.add(0, marker);
			leaderloclist.remove(9);
		} else {
			leaderloclist.add(0, marker);
		}
		// disappearing points to look cool
		float alpha = 1;
		for (int i = 0; i < leaderloclist.size(); i++) {
			leaderloclist.get(i).setAlpha(alpha);
			alpha -= .1;
		}
	}

	private void positionCamera() {
		// TODO figure out the camera stuff!!
	}
	private void setupLocListener() {
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE,
				mLocationListener);
	}

	public void setupMap() {
		setUpMapIfNeeded();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setMyLocationEnabled(true);

	}

	public void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			// Check if we were successful in obtaining the map.
			if (map != null) {
				// The Map is verified. It is now safe to manipulate the map.

			}
		}
	}
	private void setLeaderLocation(double lat, double lng) {
		//sometimes you know what youre doing. 
		//sometimes you know what youre doing but you just do it wrong. 
		//what's important is that we all learned something here. 
		leaderloc = new LatLng(lat, lng);
	}
}
