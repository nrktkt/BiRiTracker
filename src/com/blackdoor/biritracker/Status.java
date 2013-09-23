/**
 * 
 */
package com.blackdoor.biritracker;

import java.io.Serializable;
import java.util.List;
//import blackdoor.util.Watch;
/**
 * @author kAG0
 *
 */
abstract class Status implements Serializable {
	public enum Operation{
		GET, SET, CHECKSERVER
	}
	public Operation operation;
	public Watch time;
	/**
	 * @param operation
	 * @param time
	 */
	public Status(Operation operation, Watch time) {
		super();
		this.operation = operation;
		this.time = time;
	}
	public Status(){
		
	}

}
