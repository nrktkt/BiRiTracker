package com.blackdoor.biritracker;

import com.blackdoor.biritracker.Status.Operation;

import java.util.Timer;
import java.util.TimerTask;

//import blackdoor.util.Watch;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BiRiLeader extends Activity {
	private MapFragment mMapFragment;
	private GoogleMap mMap;
	private boolean tracking;
	TextView serverStatusTextView;
	Button trackingButton;
	EditText leaderName;

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.obj.equals("checkServerStatus")){
				checkServerStatus();
			}else if(msg.obj.equals("updateLocation")){
				updateLocation();
			}
		}
	};

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_bi_ri_leader);
			addMapFragment();
			tracking = false;
		}
		protected void onStart(){
			super.onStart();
			serverStatusTextView = (TextView) findViewById(R.id.serverStatusTextView);
			trackingButton = (Button) findViewById(R.id.trackingToggleButton);
			leaderName = (EditText) findViewById(R.id.leaderName);
			setUpMapIfNeeded();
			mMap.setMyLocationEnabled(true);
			startTimers();
		}
		private void startTimers() {
			Timer serverStatusTimer = new Timer("serverStatus");
			serverStatusTimer.schedule(new CheckServerStatus(this), 0, 30000);
			Timer locationUpdateTimer = new Timer("locationUpdate");
			locationUpdateTimer.schedule(new UpdateLocation(this), 0, 60000);
		}

		private class CheckServerStatus extends TimerTask{
			private BiRiLeader callingClass;

			/**
			 * @param callingClass
			 */
			public CheckServerStatus(BiRiLeader callingClass) {
				super();
				this.callingClass = callingClass;
			}

			@Override
			public void run() {
				Message msg = new Message();
				msg.obj = "checkServerStatus";
				mHandler.sendMessage(msg);
			}

		}
		private class UpdateLocation extends TimerTask{
			private BiRiLeader callingClass;

			/**
			 * @param callingClass
			 */
			public UpdateLocation(BiRiLeader callingClass) {
				super();
				this.callingClass = callingClass;
			}
			@Override
			public void run() {
				Message msg = new Message();
				msg.obj = "updateLocation";
				mHandler.sendMessage(msg);
			}

		}

		public void toggleTracking(View view){
			tracking = !tracking;
			if(tracking){
				trackingButton.setText("Stop Tracking");
			}else
				trackingButton.setText("Start Tracking");
			updateLocation();
		}

		public void updateLocation(){
			StatusRequest request;
			if(tracking){
				request = new StatusRequest(Operation.SET, new Watch());
				String name = leaderName.getText().toString();
				Leader thisLeader = new Leader(name, );// insert current position
				thisLeader.GenerateID();
			}
		}
		public void checkServerStatus(){
			Operation op = Operation.CHECKSERVER;
			StatusRequest request = new StatusRequest(op, new Watch());
			NetWorker worker = new NetWorker();
			StatusReply reply = worker.exchange(request);

			if(reply.isOnline){
				serverStatusTextView.setText("Server Online");
			}else
				serverStatusTextView.setText("Server Offline");
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.bi_ri_leader, menu);
			return true;
		}

		private void addMapFragment(){
			mMapFragment = MapFragment.newInstance(loadMapOptions());
			FragmentTransaction fragmentTransaction =
					getFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.mapFrame, mMapFragment);
			fragmentTransaction.commit();

		}
		private GoogleMapOptions loadMapOptions(){
			GoogleMapOptions options = new GoogleMapOptions();
			options.camera(new CameraPosition(new LatLng(47.660577, -117.421095), 17, 0, 0));//coords, zoom, tilt, bearing
			//options.zoomGesturesEnabled(false);
			//options.zoomControlsEnabled(false);
			return options;
		}
		private void setUpMapIfNeeded() {
			//while(!mMapFragment.isAdded()){}
			// Do a null check to confirm that we have not already instantiated the map.
			if (mMap == null) {
				mMap = mMapFragment.getMap();
				// Check if we were successful in obtaining the map.
				if (mMap != null) {
					// The Map is verified. It is now safe to manipulate the map.


				}
			}
		}
	}



