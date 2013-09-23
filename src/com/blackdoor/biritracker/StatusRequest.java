package com.blackdoor.biritracker;

//import blackdoor.util.Watch;

public class StatusRequest extends Status{
	public Leader leader;
	
	StatusRequest(Operation operation, Watch time){
		super(operation, time);
	}
}
