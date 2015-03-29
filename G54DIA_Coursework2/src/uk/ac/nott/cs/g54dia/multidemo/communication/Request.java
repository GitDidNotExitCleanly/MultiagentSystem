package uk.ac.nott.cs.g54dia.multidemo.communication;

import uk.ac.nott.cs.g54dia.multidemo.data_structure.myPoint;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myTask;

public class Request {

	private String agentID;
	private myPoint currentPosition;
	private int currentFuel;
	private int currentWater;
	
	private myTask finishedTask;
	
	public Request(String agentID, myPoint currentPosition, int currentFuel, int currentWater, myTask finishedTask) {
		this.agentID = agentID;
		this.currentPosition = currentPosition;
		this.currentFuel = currentFuel;
		this.currentWater = currentWater;
		
		this.finishedTask = finishedTask;
	}

	public String getAgentID() {
		return this.agentID;
	}
	
	public myPoint getCurrentPosition() {
		return currentPosition;
	}

	public int getCurrentFuel() {
		return currentFuel;
	}

	public int getCurrentWater() {
		return currentWater;
	}
	
	public myTask getFinishedTask() {
		return finishedTask;
	}
}
