package uk.ac.nott.cs.g54dia.multidemo.agent_manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import uk.ac.nott.cs.g54dia.multidemo.communication.Response;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myPoint;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myTask;
import uk.ac.nott.cs.g54dia.multilibrary.Tanker;

public class AgentManager_Beliefs {
	
	private ArrayList<String> agentsWithoutTask;
	
	// agent information
	private String agentID;
	private myPoint agentCurrentPosition;
	private int agentCurrentFuel;
	private int agentCurrentWater;
	
	// environment size
	private int environmentSize;
	
	// before optimization
	private ArrayList<myPoint> stations;
	private ArrayList<myPoint> wells;

	private ArrayList<myPoint> explorationPoints;
	private ArrayList<myPoint> allocatedExplorationPoints;
	
	// after optimization
	private HashMap<myPoint,myPoint> bestWellForStation;
	
	private boolean isExplorationFinished;
	private ArrayList<myPoint> optimalExplorationPoints;
	
	private ArrayList<myTask> tasks;
	private ArrayList<myTask> allocatedTasks;
	
	public AgentManager_Beliefs(int environmentSize) {
		
		this.agentsWithoutTask = new ArrayList<String>();
		
		// initialize agent information
		this.agentID = null;
		this.agentCurrentPosition = new myPoint(0,0);
		this.agentCurrentFuel = Tanker.MAX_FUEL;
		this.agentCurrentWater = 0;
		
		// initialize environment size
		this.environmentSize = environmentSize;
		
		/* before optimization */
		// initialize stations 
		this.stations = new ArrayList<myPoint>();
		// initialize wells
		this.wells = new ArrayList<myPoint>();
	
		// initialize exploration points
		this.explorationPoints = new ArrayList<myPoint>();
		this.allocatedExplorationPoints = new ArrayList<myPoint>();
		
		/* after optimization */
		// initialize hash table
		this.bestWellForStation = new HashMap<myPoint,myPoint>();
		
		// initialize optimal exploration points
		this.isExplorationFinished = false;
		this.optimalExplorationPoints = new ArrayList<myPoint>();
		
		// initialize task
		this.tasks = new ArrayList<myTask>();
		this.allocatedTasks = new ArrayList<myTask>();
		
		// generate initial exploration points
		this.generateExplorationPoints(environmentSize);
	}

	/* 
	 * 
	 * methods used before optimization
	 *   
	 */
	// generate initial spiral exploration method
	private void generateExplorationPoints(int environmentSize) {
		
		this.explorationPoints.add(new myPoint(0,0));
		
		int valueSize = (environmentSize-Tanker.VIEW_RANGE)/(Tanker.VIEW_RANGE*2+1);
		int remaining = (environmentSize-Tanker.VIEW_RANGE)%(Tanker.VIEW_RANGE*2+1);
		int[] value;
		if (remaining != 0) {
			value = new int[(valueSize+1)*2+1];
		}
		else {
			value = new int[valueSize*2+1];
		}
		
		value[(value.length-1)/2] = 0;
		for (int i=1;i<value.length;i++) {
			if (i >= value.length-2 && remaining != 0) {
				if (i%2 != 0) {
					value[((value.length-1)/2)+((i+1)/2)] = value[((value.length-1)/2)+((i+1)/2)-1]+remaining;
				}
				else {
					value[((value.length-1)/2)-(i/2)] = value[((value.length-1)/2)-(i/2)+1]-remaining;
				}
			}
			else {
				if (i%2 != 0) {
					value[((value.length-1)/2)+((i+1)/2)] = ((i+1)/2)*(Tanker.VIEW_RANGE*2+1);
				}
				else {
					value[((value.length-1)/2)-(i/2)] = -(i/2)*(Tanker.VIEW_RANGE*2+1);
				}
			}
		}
		
		int numOfPoints = value.length*value.length;
		int lastXIndex = (value.length-1)/2;
		int lastYIndex = (value.length-1)/2;
		int lastDir = 0;
		int[] lastLength = {0,0};
		int count = 0;
		while (count < numOfPoints-1) {
			if (lastLength[0] == lastLength[1]) {
				lastLength[0] = lastLength[1];
				lastLength[1]++;
			}
			else {
				lastLength[0] = lastLength[1];
			}
			for (int i=0; i < lastLength[1] && count < numOfPoints;i++) {
				switch (lastDir) {
				case 0:
					lastYIndex++;
					break;
				case 1:
					lastXIndex--;
					break;
				case 2:
					lastYIndex--;
					break;
				case 3:
					lastXIndex++;
					break;
				}
				this.explorationPoints.add(new myPoint(value[lastXIndex],value[lastYIndex]));
				count++;
				if (count >= numOfPoints-1) {
					break;
				}
			}
			lastDir = (lastDir+1)%4 ;
		}
	}
		
	/*
	 * 
	 * optimization methods
	 *  
	 */
	// station - well table
	private void generateStationWellPair() {
		
		for (int i=0;i<this.stations.size();i++) {		
			int minDistance = -1;
			myPoint closestWell = null;
			for (int j = 0;j<this.wells.size();j++) {
				int distance = Math.max(Math.abs(this.stations.get(i).getX()-this.wells.get(j).getX()), Math.abs(this.stations.get(i).getY()-this.wells.get(j).getY()));
				if (minDistance == -1) {
					minDistance = distance;
					closestWell = this.wells.get(j);
				}
				else {
					if (distance < minDistance) {
						minDistance = distance;
						closestWell = this.wells.get(j);
					}
				}
			}
			this.bestWellForStation.put(this.stations.get(i), closestWell);
		}
		
	}
	
	// optimize exploration points
	private void geneateOptimalExplorationPoints(boolean generateNew) {
		if (generateNew) {	
			
			// get max_X and min_X
			int max_X = -1;
			int min_X = -1;
			for (int i=0;i<this.stations.size();i++) {
				int x = this.stations.get(i).getX();
				if (i == 0) {
					max_X = x;
					min_X = x;
				}
				else {
					if (x > max_X) {
						max_X = x;
					}
					else if (x < min_X) {
						min_X = x;
					}
				}
			}
			// get max_Y and min_Y
			int max_Y = -1;
			int min_Y = -1;
			for (int i=0;i<this.stations.size();i++) {
				int y = this.stations.get(i).getY();
				if (i == 0) {
					max_Y = y;
					min_Y = y;
				}
				else {
					if (y > max_Y) {
						max_Y = y;
					}
					else if (y < min_Y) {
						min_Y = y;
					}
				}
			}	
			int width;
			if ((max_X-min_X+1)%(Tanker.VIEW_RANGE*2+1) != 0) {
				width = (max_X-min_X+1)/(Tanker.VIEW_RANGE*2+1)+1;
			}
			else {
				width = (max_X-min_X+1)/(Tanker.VIEW_RANGE*2+1);
			}
			
			int height;
			if ((max_Y-min_Y+1)%(Tanker.VIEW_RANGE*2+1) != 0) {
				height = (max_Y-min_Y+1)/(Tanker.VIEW_RANGE*2+1)+1;
			}
			else {
				height = (max_Y-min_Y+1)/(Tanker.VIEW_RANGE*2+1);
			}
			
			int number = 0;
			ArrayList<Integer> density = new ArrayList<Integer>();
			for (int i=0;i<width;i++) {
				for (int j=0;j<height;j++) {
					number = 0;
					for (int index = 0;index < this.stations.size();index++) {
						if (this.stations.get(index).getX() >= Math.max(-this.environmentSize, min_X+i*Tanker.VIEW_RANGE*2) 
								&& this.stations.get(index).getX() < Math.min(this.environmentSize, min_X+(i+1)*Tanker.VIEW_RANGE*2) 
								&& this.stations.get(index).getY() < Math.min(this.environmentSize, max_Y-j*Tanker.VIEW_RANGE*2)
								&& this.stations.get(index).getY() >= Math.max(-this.environmentSize, max_Y-(j+1)*Tanker.VIEW_RANGE*2)) {
							number++;
						}
						else if (i == 0 && j == height-1) {
							if (this.stations.get(index).getX() == Math.min(this.environmentSize, min_X+(i+1)*Tanker.VIEW_RANGE*2)
									|| this.stations.get(index).getY() == Math.min(this.environmentSize, max_Y-j*Tanker.VIEW_RANGE*2)) {
								number++;
							}
						}
						else if (i == 0 && this.stations.get(index).getY() == Math.min(this.environmentSize, max_Y-j*Tanker.VIEW_RANGE*2)) {
							number++;
						}
						else if (j == height-1 && this.stations.get(index).getX() == Math.min(this.environmentSize, min_X+(i+1)*Tanker.VIEW_RANGE*2)) {
							number++;
						}				
					}
					if (number != 0) {
						this.optimalExplorationPoints.add(new myPoint((Math.max(-this.environmentSize, min_X+i*Tanker.VIEW_RANGE*2)+Math.min(this.environmentSize, min_X+(i+1)*Tanker.VIEW_RANGE*2))/2,(Math.min(this.environmentSize, max_Y-j*Tanker.VIEW_RANGE*2)+Math.max(-this.environmentSize, max_Y-(j+1)*Tanker.VIEW_RANGE*2))/2));
						density.add(number);
					}
				}
			}

			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.addAll(density);
			temp.sort(new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1 - o2;
				}
			});
			
			for (int i=0;i<density.size();i++) {
				for (int j=0;j<temp.size();j++) {
					if (density.get(i) == temp.get(j) && i != j) {
						myPoint toBeInserted = this.optimalExplorationPoints.remove(i);
						this.optimalExplorationPoints.add(j, toBeInserted);
						break;
					}
				}
			}
		}
	
		for (int i=0;i<this.optimalExplorationPoints.size();i++) {
			this.explorationPoints.add(this.optimalExplorationPoints.get(i));
		}
	}

	/*
	 * 
	 *  update view
	 * 
	 */
	public Response updateView(myPoint currentPosition, ArrayList<myPoint> detectedWells, ArrayList<myPoint> detectedStations, ArrayList<myTask> detectedTasks) {
		Response response = null;
		
		if (!this.isExplorationFinished) {
			// update wells
			for (int i=0;i<detectedWells.size();i++) {
				if (!this.isPositionRepeated(detectedWells.get(i), this.wells)) {
					this.wells.add(detectedWells.get(i));
				}
			}
			
			for (int i=0;i<detectedStations.size();i++) {
				if (!this.isPositionRepeated(detectedStations.get(i), this.stations)) {
					this.stations.add(detectedStations.get(i));
				}
			}
		}
		
		// update tasks
		for (int i=0;i<detectedTasks.size();i++) {
			if (!this.isTaskRepeated(detectedTasks.get(i), this.tasks, this.allocatedTasks)) {
				this.tasks.add(detectedTasks.get(i));
			}
		}
		
		// update detected cell
		this.explorationPoints.remove(currentPosition);
		this.allocatedExplorationPoints.remove(currentPosition);
		if (this.explorationPoints.size() == 0) {
			if (!this.isExplorationFinished && this.allocatedExplorationPoints.size() == 0) {
				this.isExplorationFinished = true;
				response = new Response(Response.UPDATE_EXPLORATIONSTATE);
				
				// for task list optimization
				this.generateStationWellPair();

				// optimize exploration point
				this.geneateOptimalExplorationPoints(true);
			}
			else {
				// reset optimal exploration points
				this.geneateOptimalExplorationPoints(false);
			}
			this.explorationPoints.remove(currentPosition);
		}
		
		return response;
	}
	
	// insertion helpers 
	private boolean isPositionRepeated(myPoint position, ArrayList<myPoint> domain) {
		boolean isRepeated = false;
		for (int i =0;i<domain.size();i++) {
			if (position.equals(domain.get(i))) {
				isRepeated = true;
				break;
			}
		}
		return isRepeated;
	}
	private boolean isTaskRepeated(myTask task, ArrayList<myTask> tasks, ArrayList<myTask> allocatedTasks) {
		boolean isRepeated = false;
		for (int i =0;i<tasks.size();i++) {
			if (task.equals(tasks.get(i))) {
				isRepeated = true;
				break;
			}
		}
		if (!isRepeated) {
			for (int i =0;i<allocatedTasks.size();i++) {
				if (task.equals(allocatedTasks.get(i))) {
					isRepeated = true;
					break;
				}
			}
		}
		return isRepeated;
	}
	
	/*
	 * 
	 *  update AgentInformation
	 * 
	 */
	public void updateAgentInformation(myPoint agentCurrentPosition, int agentCurrentFuel, int agentCurrentWater, myTask finishedTask) {
		this.agentCurrentPosition = agentCurrentPosition;
		this.agentCurrentFuel = agentCurrentFuel;
		this.agentCurrentWater = agentCurrentWater;
		
		if (finishedTask != null) {
			this.allocatedTasks.remove(finishedTask);
		}
	}
	
	/*
	 * 
	 * Getters
	 * 
	 */
	public ArrayList<String> getAgentsWithoutTask() {
		return this.agentsWithoutTask;
	}
	
	public String getAgentID() {
		return this.agentID;
	}
	
	public myPoint getAgentCurrentPosition() {
		return this.agentCurrentPosition;
	}
	
	public int getAgentCurrentFuel() {
		return this.agentCurrentFuel;
	}
	
	public int getAgentCurrentWater() {
		return this.agentCurrentWater;
	}
	
	public ArrayList<myPoint> getStations() {
		return this.stations;
	}
	
	public ArrayList<myPoint> getWells() {
		return this.wells;
	}
	
	public ArrayList<myPoint> getExplorationPoints() {
		return this.explorationPoints;
	}
	
	public ArrayList<myPoint> getAllocatedExplorationPoints() {
		return this.allocatedExplorationPoints;
	}
	
	public HashMap<myPoint,myPoint> getStationWellTable() {
		return this.bestWellForStation;
	}
	
	public boolean isExplorationFinished() {
		return this.isExplorationFinished;
	}

	public ArrayList<myTask> getTasks() {
		return this.tasks;
	}

	public ArrayList<myTask> getAllocatedTasks() {
		return this.allocatedTasks;
	}
}
