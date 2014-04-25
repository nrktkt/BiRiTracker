package com.blackdoor.biritracker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BiRiMapFragment extends MapFragment {
	private GoogleMap map;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.map_fragment, null, false);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMyLocationEnabled(true);
		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(SOME_PLACE, 2));
//		map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
		return v;
	}

	public void zoomToLeader(LatLng latlng) {
		Log.d("BiRi", "latlngzoom: " + latlng);
		if (map == null) {
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		}
//		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 2));
//		map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
	}
}
