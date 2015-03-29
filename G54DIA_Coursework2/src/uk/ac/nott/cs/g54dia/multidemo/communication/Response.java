package uk.ac.nott.cs.g54dia.multidemo.communication;

import uk.ac.nott.cs.g54dia.multidemo.data_structure.myPoint;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myTask;

public class Response {
	
	public static final int UPDATE_EXPLORATIONSTATE = 0;
	public static final int TERMINATE_EXPLORATION = 1;
	public static final int DO_EXPLORATION = 2;
	public static final int FINISH_TASK = 3;
	
	private int action;
	
	private myTask task;
	private myPoint[] route;
	
	public Response(int action) {
		this.action = action;
		
		this.task = null;
		this.route = null;
	}
	
	public Response(int action, myPoint[] route) {
		this.action = action;
		
		this.task = null;
		this.route = route;
	}
	
	public Response(int action, myTask task, myPoint[] route) {
		this.action = action;
		
		this.task = task;
		this.route = route;
	}

	public int getAction() {
		return action;
	}
	
	public myTask getTask() {
		return task;
	}

	public myPoint[] getRoute() {
		return route;
	}
}
