package com.blackdoor.biritracker;

import java.util.List;

//import blackdoor.util.Watch;

public class StatusReply extends Status{
	public boolean isOnline;
	public List<Leader> leaders;
	StatusReply(){
		super();
	}
	/**
	 * @param isOnline
	 * @param leaders
	 */
	public StatusReply(Operation operation, Watch time, boolean isOnline, List<Leader> leaders) {
		super(operation, time);
		this.isOnline = isOnline;
		this.leaders = leaders;
	}
}
