package uk.ac.nott.cs.g54dia.multidemo.communication;

import java.util.ArrayList;

import uk.ac.nott.cs.g54dia.multidemo.data_structure.myPoint;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myTask;

public class Report {

	private myPoint agentCurrentPosition;
	private ArrayList<myPoint> wells;
	private ArrayList<myPoint> stations;
	private ArrayList<myTask> tasks;
	
	public Report(myPoint agentCurrentPosition, ArrayList<myPoint> wells, ArrayList<myPoint> stations, ArrayList<myTask> tasks) {
		this.agentCurrentPosition = agentCurrentPosition;
		this.wells = wells;
		this.stations = stations;
		this.tasks = tasks;
	}
	
	public myPoint getAgentCurrentPosition() {
		return this.agentCurrentPosition;
	}

	public ArrayList<myPoint> getWells() {
		return wells;
	}

	public ArrayList<myPoint> getStations() {
		return stations;
	}

	public ArrayList<myTask> getTasks() {
		return tasks;
	}
}
