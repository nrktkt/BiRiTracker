/**
 * 
 */
package com.blackdoor.biritracker;

/**
 * @author kAG0
 *
 */
public class NetWorker {
	NetWorker(){
		
	}
	public StatusReply exchange(StatusRequest request){
		StatusReply reply;
		reply = new StatusReply();
		reply.isOnline = true;
		return reply;
	}
}
