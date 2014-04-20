package com.blackdoor.biritracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pref.prefrences;
import server.BiRiServer;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.os.Build;

public class SelectActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		private Spinner spinnerRideOptions;
		public static final String COULDNOTDLSTRING = "Could not get list of available rides ;(";
		public static final String RIDE_NAME_EXTRA = "RIDE_NAME_EXTRA";
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_select,
					container, false);
			
			spinnerRideOptions = (Spinner) getView().findViewById(R.id.spinnerRideOptions);
			List<String> rides = null;
			final int tryout = 6;
			for(int i = 0; i<tryout; i++){
			try {
				rides = getAvailableRides();
				i = tryout + 1;
			} catch (Exception e) {
				if (i == tryout)
					rides = new ArrayList<String>();
					rides.add(COULDNOTDLSTRING);
				e.printStackTrace();
			}}
			
			ArrayAdapter<String> adapterRideOptions = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, rides);
			spinnerRideOptions.setAdapter(adapterRideOptions);
			
			return rootView;
		}
		
		private List<String> getAvailableRides() throws Exception{
			List<String> rides;
			String inline;
			StringTokenizer tk = null;
			try{
					Socket connection = new Socket(prefrences.serverAddress, prefrences.defaultPort);
					PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
					BufferedReader in = new BufferedReader(
					        new InputStreamReader(connection.getInputStream()));
					
					out.println(BiRiServer.Codes.RIDELISTREQ.name() + prefrences.NULL);
					inline = in.readLine();
					
					connection.close();
					
					tk = new StringTokenizer(inline, ""+prefrences.NULL);
					
					if(!tk.nextToken().equalsIgnoreCase(BiRiServer.Codes.RIDELISTREP.name())){
						throw new Exception("Failed to get list of rides.");
					}
					
				}catch (IOException e){
					throw new Exception("Failed to connect to server.");
				}
			rides = new ArrayList<String>();
			while(tk.hasMoreTokens()){
				rides.add(tk.nextToken());
			}			
			return rides;
		}
		
		public void onButtonJoinRide(View v){
			String rideName = spinnerRideOptions.getItemAtPosition(spinnerRideOptions.getSelectedItemPosition()).toString();
			Intent i = new Intent(getActivity(), FollowerActivity.class);
			i.putExtra(RIDE_NAME_EXTRA, rideName);
			startActivity(i);
		}
	}

}
