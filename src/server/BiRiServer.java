/**
 * 
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import pref.prefrences;

/**
 * @author nfischer3
 *
 */
public class BiRiServer {
	
	public static final char NULL = '\u0000';
	public enum Codes{
		CREATE, // create ride
		SUBMIT, // submit current location
		REQUEST, //request current location
		DESTROY, // destroy ride
		RIDELISTREQ,// request list of rides
		OK,
		FAIL,
		LOC, //return last location for a ride
		RIDELISTREP; //return list of rides
	}
	
	
}
