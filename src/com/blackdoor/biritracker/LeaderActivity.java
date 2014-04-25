package com.blackdoor.biritracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import pref.prefrences;
import server.BiRiServer;

import com.blackdoor.biritracker.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
public class LeaderActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

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
	
	
	private String rideName;
	private String devID;
	//private LatLng lastGoodLoc;
	private double latitude;
	private double longitude;
	private Socket connection;
	private Timer updateTimer;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_leader);
		setupActionBar();
		fullScreenShiznitFromOnCreate();
		
		Intent i = getIntent();
		rideName = i.getStringExtra("RIDE_NAME");
		
	}

	protected void onStart(){
		devID = Settings.Secure.ANDROID_ID;
		final int tryout = 6;
		for(int i = 0; i<tryout; i++){
			try {
				createRide();
				i=tryout*2;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		updateTimer = new Timer(true);
		updateTimer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				for(int i = 0; i<tryout; i++){
					try {
						updateLocationOnServer();
						i = tryout*2;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}, 10000, 30000);
	}
	
	private void createRide() throws Exception{
		updateLocation();
		final double lat = latitude;
		final double lng = longitude;
		AsyncTask<String, Void, String> socketTask = new AsyncTask<String, Void, String>(){

			@Override
			protected String doInBackground(String... params) {
				String ret = "";
				try{
					Socket connection = connectToServer();
					PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
					BufferedReader in = new BufferedReader(
					        new InputStreamReader(connection.getInputStream()));
					out.println(BiRiServer.Codes.CREATE.name() + prefrences.NULL + 
							rideName + prefrences.NULL + 
							devID + prefrences.NULL +
							latitude + prefrences.NULL +//TODO
							longitude + prefrences.NULL//TODO
							);
					ret = in.readLine();
					connection.close();
				}catch(IOException e){
					e.printStackTrace();
				}
				return ret;
			}
			
			protected void onPostExecute(String param){
				if(!param.equalsIgnoreCase(BiRiServer.Codes.OK.name())){
					Toast toast = Toast.makeText(LeaderActivity.this.getApplicationContext(), "Failed to create ride", Toast.LENGTH_LONG);
					toast.show();
				}
			}
			
			
		};
//		connectToServer();
//		PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
//		BufferedReader in = new BufferedReader(
//		        new InputStreamReader(connection.getInputStream()));
//		out.println(BiRiServer.Codes.CREATE.name() + prefrences.NULL + 
//				rideName + prefrences.NULL + 
//				devID + prefrences.NULL +
//				latitude + prefrences.NULL +//TODO
//				longitude + prefrences.NULL//TODO
//				);
//		
//		if(!in.readLine().equalsIgnoreCase(BiRiServer.Codes.OK.name())){
//			throw new Exception("Ride creation failed");
//		}
//		connection.close();
	
	
	}
	private void updateLocation(){
		
	}
	
	private void updateLocationOnServer() throws IOException{
		updateLocation();
		connectToServer();
		PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(connection.getInputStream()));
		out.println(BiRiServer.Codes.SUBMIT.name() + prefrences.NULL +
				devID + prefrences.NULL +
				latitude + prefrences.NULL +
				longitude + prefrences.NULL);
		
		if(!in.readLine().equalsIgnoreCase(BiRiServer.Codes.OK.name())){
			throw new IOException("Failed to update location on server");
		}
		connection.close();
	}
	
	private Socket connectToServer() throws IOException, IOException{
		connection = new Socket(prefrences.serverAddress, prefrences.defaultPort);
		return connection;
	}
	
	
	
	public void onButtonEndRide(View v){
		int tryOut = 5;
		for(int i = 0; i < tryOut; i++){
			try{
				connectToServer();
				PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(connection.getInputStream()));
				out.println(BiRiServer.Codes.DESTROY.name() + prefrences.NULL +
						devID + prefrences.NULL);
				
				if(in.readLine().equalsIgnoreCase(BiRiServer.Codes.OK.name())){
					i = tryOut*2;
				}
				connection.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onDestroy(){
		updateTimer.cancel();
		super.onDestroy();
	}
	
	//just get that all out of the way...
	private void fullScreenShiznitFromOnCreate(){
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

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
		findViewById(R.id.buttonEndRide).setOnTouchListener(
				mDelayHideTouchListener);
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
