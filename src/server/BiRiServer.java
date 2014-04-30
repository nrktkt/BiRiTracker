package server;

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

public class BiRiServer {

	public static final char NULL = '\u0000';

	public enum Codes {
		CREATE, // create ride
		SUBMIT, // submit current location
		REQUEST, // request current location
		DESTROY, // destroy ride
		RIDELISTREQ, // request list of rides
		OK, FAIL, LOC, // return last location for a ride
		RIDELISTREP; // return list of rides
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
		 * int port; //handle args[]
		 */
		BiRiServer server = new BiRiServer(prefrences.defaultPort);
		server.doServerLoop();

	}

	public BiRiServer(int port) throws IOException {
		this.port = port;
		serverSocket = new ServerSocket(port);
		dataBase = new BiRiDatabase();

	}

	public void start() throws IOException {
		doServerLoop();
	}

	public void stop() {
		running = false;
	}

	private void doServerLoop() throws IOException {
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(
				System.in));
		while (running) {
			System.out.println("loop");

			try {
				new ServerThread(serverSocket.accept(), dataBase).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (keyboard.ready()) {
				System.out.println("boop");
			}
		}
	}
}