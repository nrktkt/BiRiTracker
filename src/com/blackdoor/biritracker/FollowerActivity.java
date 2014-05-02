package com.blackdoor.biritracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import pref.prefrences;
import server.BiRiServer;

import com.blackdoor.biritracker.BiRiMapManipulator.Role;
import com.blackdoor.biritracker.util.SystemUiHider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FollowerActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 10000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	private LatLng leaderloc;
	private Timer updateTimer;
	private LatLng myLoc;
	private String rideName;
	//private double latitude;
	//private double longitude;

	private Socket connection;
	private GoogleMap map;
	private BiRiMapManipulator mapman;
	private LocationManager locationManager;
	private Criteria locCriteria;

	Handler mMapHandler = new Handler();
	Runnable mMapRunnable = new Runnable() {

		@Override
		public void run() {
			//TODO get location and update that bad boy
			mapman.addAndmanageMarkers(leaderloc);
		}

	};


	private void uiHiderStuff() {
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.map);
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.buttonLeaveRide).setOnTouchListener(
				mDelayHideTouchListener);
	}

	@Override
	protected void onDestroy() {
		updateTimer.cancel();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follower);
		
		locCriteria = new Criteria();
		locCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		Intent i = getIntent();
		rideName = i.getStringExtra(SelectActivity.RIDE_NAME_EXTRA);

		setupActionBar();
		uiHiderStuff();
		
		
		setupLocation();
		setupMap();
		
		setupTimer(4);
	}
	
	private void setupLocation(){
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		
		//let's get location only on updates, maybe more battery efficient.
		/*// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		     
		    	lastGoodLoc = new LatLng(location.getLatitude(),location.getLongitude());
		    }
			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);*/
		
	}
	
	private void updateLocation(){
		//TODO put some stuff here to get location!!
		Location currentLoc = locationManager.getLastKnownLocation(locationManager.getBestProvider(locCriteria, true));
		myLoc = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
		//latitude = mylocation.getLatitude();
		//longitude = mylocation.getLongitude();
		mapman.setFollowerLocation(myLoc);
		
		
		//setupLocListener();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		setupMap();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}
	
	public void setupMap() {
		setUpMapIfNeeded();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setMyLocationEnabled(true);
		mapman = new BiRiMapManipulator(Role.FOLLOW,map);
		updateLocation();
		//map.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));
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

	private void setupTimer(int x) {
		System.out.println("ride created");
		final int tryout = x;

		final Toast toast = Toast.makeText(
				FollowerActivity.this.getApplicationContext(),
				"Failed to update location from server", Toast.LENGTH_LONG);
		updateTimer = new Timer(true);
		updateTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				for (int i = 0; i < tryout; i++) {
					try {
						updateLocationFromServer();
						i = tryout * 2;
					} catch (IOException e) {
						e.printStackTrace();
						// Looper.prepare();
						// Toast toast = Toast.makeText(
						// FollowerActivity.this.getApplicationContext(),
						// e.getMessage(), Toast.LENGTH_LONG);
						toast.show();
					}
				}
			}

		}, 0, prefrences.UPDATE_PERIOD);
	}

	private void updateLocationFromServer() throws IOException {
		connection = LeaderActivity.connectToServer();
		PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		// System.out.println(rideName);
		out.println(BiRiServer.Codes.REQUEST.name() + prefrences.NULL
				+ rideName + prefrences.NULL);

		String replyLine = in.readLine();
		connection.close();

		StringTokenizer tk = new StringTokenizer(replyLine, ""
				+ prefrences.NULL);
		if (!tk.nextToken().equalsIgnoreCase(BiRiServer.Codes.LOC.name())) {
			throw new IOException("Failed to update location from server");
		}

		double latitude = Double.parseDouble(tk.nextToken());
		double longitude = Double.parseDouble(tk.nextToken());
		leaderloc = new LatLng(latitude, longitude);
		System.out.println(latitude + " " + longitude);
		//update both leader and follower so that map pos will be correct
		mapman.setLeaderLocation(latitude, longitude);
		updateLocation();
		mMapHandler.post(mMapRunnable);
	}



	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
