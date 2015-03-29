package uk.ac.nott.cs.g54dia.multidemo.worker_agent;

import java.util.ArrayList;

import uk.ac.nott.cs.g54dia.multidemo.data_structure.myPoint;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myTask;
import uk.ac.nott.cs.g54dia.multilibrary.*;

public class Beliefs {

	// internal state
	private boolean isExplorationFinished;
	
	private int fuel;
	private int water;
	private myPoint currentPosition;
	private Cell currentCell;
	
	private int environmentSize;
	
	// information from view
	ArrayList<myPoint> detectedWells;
	ArrayList<myPoint> detectedStations;
	ArrayList<myTask> detectedTasks;
	
	public Beliefs(int environmentSize) {
		
		// initialize exploration state
		this.isExplorationFinished = false;
		
		// initialize fuel
		this.fuel = Tanker.MAX_FUEL;
		
		// initialize water
		this.water = 0;
		
		// initialize last position
		this.currentPosition = new myPoint(0,0);
		this.currentCell = null;
		
		// initialize environment size
		this.environmentSize = environmentSize;
		
		// information from view
		this.detectedWells = new ArrayList<myPoint>();
		this.detectedStations = new ArrayList<myPoint>();
		this.detectedTasks = new ArrayList<myTask>();
	}
	
	// functions for field 'EXPLORATIONSTATE'
	public void updateExplorationState() {
		this.isExplorationFinished = true;
	}
	public boolean isExplorationFinished() {
		return this.isExplorationFinished;
	}

	
	// functions for field 'FUEL'
	public int getFuel() {
		return this.fuel;
	}
	
	
	// functions for field 'WATER'
	public int getWater() {
		return this.water;
	}
	
	
	// functions for field 'CURRENTPOSITION'
	public myPoint getCurrentPosition() {
		return this.currentPosition;
	}
	
	
	// functions for field 'CURRENTCELL'
	public Cell getCurrentCell() {
		return this.currentCell;
	}
	
	// functions for field 'VIEW INFORMATION'
	public ArrayList<myPoint> getDetectedWells() {
		return this.detectedWells;
	}
	
	public ArrayList<myPoint> getDetectedStations() {
		return this.detectedStations;
	}
	
	public ArrayList<myTask> getDetectedTask() {
		return this.detectedTasks;
	}
	
	// update beliefs
	public void updateBeliefs(Cell[][] view, int fuel,int water, Cell currentCell) {

		// update fuel
		this.fuel = fuel;
				
		// update water
		this.water = water;
				
		// update current cell
		this.currentCell = currentCell;
		
		// view process
		this.detectedWells.clear();
		this.detectedStations.clear();
		this.detectedTasks.clear();
		for (int i=0;i<view.length;i++) {
			for (int j=view[i].length-1;j>=0;j--) {
				
				int x = this.currentPosition.getX() - Tanker.VIEW_RANGE + i;
				int y = this.currentPosition.getY() + Tanker.VIEW_RANGE - j;
				myPoint current = new myPoint(x,y);		
				
				if (Math.abs(x) <= environmentSize && Math.abs(y) <= environmentSize) {
					// is this cell a well ?
					if ((view[i][j]) instanceof Well && !this.isExplorationFinished) {
						detectedWells.add(current);
					}						
					// is this cell a station ? if so, is there any task ?
					else if ((view[i][j]) instanceof Station) {
						if (!this.isExplorationFinished) {
							detectedStations.add(current);
						}
						if (((Station)view[i][j]).getTask() != null) {
							myTask task = new myTask(current,((Station)view[i][j]).getTask());
							detectedTasks.add(task);
						}
					}
					else {
						// empty cell
					}
				}
			}
		}
	
	}
	
}
