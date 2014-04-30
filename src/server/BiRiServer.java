package server;
/**
 * 
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
	
	private ServerSocket serverSocket;
	private int port = prefrences.defaultPort;
	private BiRiDatabase dataBase;
	private boolean running = true;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		BiRiDatabase db = new BiRiDatabase();
		db.createRide("TuNi", "123", "6", "6");
		System.out.println(db.requestRideList());
		System.out.println(db.requestLoc("TuNi"));
		db.submitLoc("123", "7", "7");
		System.out.println(db.requestLoc("TuNi"));
		db.createRide("Swamp", "456", "8", "8");
		db.destroyRide("123");
		System.out.println(db.requestRideList() + "/");
		
		
		/*
		int port;
		//handle args[]
		 * 
		 */
		BiRiServer server = new BiRiServer(prefrences.defaultPort);
		server.doServerLoop();
		
	}
	
	public BiRiServer(int port) throws IOException{
		this.port = port;
		serverSocket = new ServerSocket(port);
		dataBase = new BiRiDatabase();

	}
	
	public void start() throws IOException{
		doServerLoop();
	}
	
	public void stop(){
		running = false;
	}
	
	private void doServerLoop() throws IOException{
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
		while(running){
			System.out.println("loop");
			
			try {
				new ServerThread(serverSocket.accept(), dataBase).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(keyboard.ready()){
				System.out.println("boop");
				if(keyboard.readLine().equalsIgnoreCase("exit")){
					stop();
				}
			}
		}
	}
	
	
	
	public class ServerThread extends Thread {
	    private Socket socket = null;
	    private BiRiDatabase dataBase;
		private PrintWriter out = null;
		private BufferedReader in = null;

	    public ServerThread(Socket socket, BiRiDatabase dataBase) {
	        super("ServerThread");
	        this.dataBase = dataBase;
	        this.socket = socket;
	    }
	    
	    public void run() {
	    	String query = null;
	   
	    	try{
	    		out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				query = in.readLine();
				System.out.println('\n');
				System.out.println(query);
				StringTokenizer tk = new StringTokenizer(query, ""+NULL);
				
				String code = tk.nextToken();
				String reply = "";
				if(code.equalsIgnoreCase(Codes.CREATE.toString())){
					
					reply = handleCreate(tk.nextToken(), tk.nextToken(), Double.parseDouble(tk.nextToken()), Double.parseDouble(tk.nextToken()));
				
				}else if(code.equalsIgnoreCase(Codes.DESTROY.name())){
					
					reply = handleDestroy(tk.nextToken());
					
				}else if(code.equalsIgnoreCase(Codes.REQUEST.name())){
					
					reply = handleRequest(tk.nextToken());
					
				}else if(code.equalsIgnoreCase(Codes.RIDELISTREQ.name())){
					
					reply = handleRideList();
					
				}else if(code.equalsIgnoreCase(Codes.SUBMIT.name())){
					
					reply = handleSubmit(tk.nextToken(), Double.parseDouble(tk.nextToken()), Double.parseDouble(tk.nextToken()));
					
				}else{
					reply = "FAIL";
				}
				System.out.println("replying: " + reply);
				out.println(reply);
						
				socket.close();
	    	}
	    		catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
	    
	    private String handleCreate(String rideName, String deviceID, double lat, double lng){
	    	boolean did = dataBase.createRide(rideName, deviceID, String.valueOf(lat), String.valueOf(lng));//"OK" + NULL;
	    	if(did)
	    		return Codes.OK.name() + NULL;
	    	return "NO, just no" + NULL;
	    }
	    private String handleSubmit(String deviceID, double lat, double lng){
	    	boolean did = dataBase.submitLoc(deviceID, String.valueOf(lat), String.valueOf(lng));//"OK" + NULL;
	    	if(did)
	    		return Codes.OK.name() + NULL;
	    	return "NO, just no" + NULL;
	    }
	    private String handleRequest(String rideName){
	    	try {
				return Codes.LOC.name() + NULL + dataBase.requestLoc(rideName);
			} catch (Exception e) {
				e.printStackTrace();
				return e.getMessage() + NULL;
			}//Codes.LOC.name() + NULL + "6" + NULL + "9" + NULL;
	    }
	    private String handleDestroy(String deviceID){
	    	boolean did = dataBase.destroyRide(deviceID);//"OK" + NULL;
	    	if(did)
	    		return Codes.OK.name() + NULL;
	    	return "NO, just no" + NULL;
	    }
	    private String handleRideList(){
	    	return Codes.RIDELISTREP.name() + NULL + dataBase.requestRideList();//return Codes.RIDELISTREP.name() + NULL + "tuNiBiRi" + NULL +"Swamp Ride" + NULL;
	    }
	}
	
	private static class BiRiDatabase{
		private ConcurrentHashMap<String, String> leaders;
		private ConcurrentHashMap<String, String> rides;
		private final String LAT = "LAT_";
		private final String LNG = "LNG_";
		
		
		public BiRiDatabase(){
			leaders = new ConcurrentHashMap<String,String>();
			rides = new ConcurrentHashMap<String,String>();
		}
		
		/**
		 * 
		 * @param rideName
		 * @param devID
		 * @param lat
		 * @param lng
		 * @return true if ride has been created successfully.
		 */
		public boolean createRide(String rideName, String devID, String lat, String lng){
			if(rides.putIfAbsent(rideName, devID) == null){
				leaders.put(devID, rideName);
				rides.put(LAT+rideName, lat);
				rides.put(LNG+rideName, lng);			
				return true;
			}else{
				return false;
			}
		}
		
		public boolean destroyRide(String devID){
			String rideName = leaders.get(devID);
			if(rideName != null){
				leaders.remove(devID);
				rides.remove(LAT+rideName);
				rides.remove(LNG+rideName);
				rides.remove(rideName);
				return true;
			}
			return false;
		}
		
		public boolean submitLoc(String devID, String lat, String lng){
			String rideName = leaders.get(devID);
			if(rideName != null){
				rides.replace(LAT+rideName, lat);
				rides.replace(LNG+rideName, lng);
				return true;
			}
			return false;
		}
		/**
		 * 
		 * @param rideName
		 * @return latitude and longitude as a null separated string
		 * @throws Exception
		 */
		public String requestLoc(String rideName) throws Exception{
			String lat = rides.get(LAT+rideName);
			String lng = rides.get(LNG+rideName);
			if(lat == null || lng == null)
				throw new Exception("Could not find Ride.");
			return lat + NULL + lng + NULL;
		}
		
		public String requestRideList(){
			String rides = "";
			for(String r : this.rides.keySet()){
				if(!(r.substring(0, 4).equalsIgnoreCase("LAT_") || r.substring(0, 4).equalsIgnoreCase("LNG_"))){
					rides += r + NULL;
				}
			}
			if(rides.equals(""))
				return "No rides available." + NULL;
			return rides;
		}
		
		
		
	}
	
}
