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
import java.util.StringTokenizer;

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
	private Object dataBase;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int port;
		//handle args[]
		BiRiServer server = new BiRiServer(port);
		server.doServerLoop();

	}
	
	public BiRiServer(int port) throws IOException{
		this.port = port;
		serverSocket = new ServerSocket(port);
	}
	
	private void doServerLoop(){
		while(true){
			try {
				new ServerThread(serverSocket.accept(), dataBase).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	public class ServerThread extends Thread {
	    private Socket socket = null;
	    private Object dataBase;
		private PrintWriter out = null;
		private BufferedReader in = null;

	    public ServerThread(Socket socket, Object dataBase) {
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
				
				out.println(reply);
						
				socket.close();
	    	}
	    		catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
	    
	    private String handleCreate(String rideName, String deviceID, double lat, double lng){
	    	
	    }
	    private String handleSubmit(String deviceID, double lat, double lng){
	    	
	    }
	    private String handleRequest(String rideName){
	    	
	    }
	    private String handleDestroy(String deviceID){
	    	
	    }
	    private String handleRideList(){
	    	
	    }
	}

}
