package uk.ac.nott.cs.g54dia.multidemo.agent_manager;

import uk.ac.nott.cs.g54dia.multidemo.communication.Response;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myPoint;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myTask;
import uk.ac.nott.cs.g54dia.multilibrary.Tanker;

public class AgentManager_Plans {
	
	// main function
	public Response execute(AgentManager_Beliefs beliefs, AgentManager_Desires desires) {
		
		Response response = null;
		
		String ID;
		myPoint currentPosition;
		myPoint target;
		int fuel;
		switch (desires.getCurrentDesire()) {
		
		// desire selection
		case TRAVERSE_MAP:
			
			currentPosition = beliefs.getAgentCurrentPosition();
			fuel = beliefs.getAgentCurrentFuel();

			int targetIndex = -1;
			if (beliefs.getExplorationPoints().size() !=0) {
				targetIndex = 0;
			}

			if (targetIndex != -1) {
				// if there is enough work
				target = beliefs.getExplorationPoints().remove(targetIndex);
				beliefs.getAllocatedExplorationPoints().add(target);
			}
			else {
				// not enough work
				if (currentPosition.equals(new myPoint(0,0))) {
					int random = (int)(Math.random()*4);
					switch (random) {
					case 0:
						target = new myPoint(0,1);
						break;
					case 1:
						target = new myPoint(0,-1);
						break;
					case 2:
						target = new myPoint(1,0);
						break;
					default:
						target = new myPoint(-1,0);
						break;
					}
				}
				else {
					target = new myPoint(0,0);
				}
			}
			
			// generate response for agent
			if (this.isRouteSafe(fuel, currentPosition, new myPoint[]{target}) >=0) {
				response = new Response(Response.DO_EXPLORATION,new myPoint[]{target});
			}
			else {
				if (currentPosition.equals(new myPoint(0,0))) {
					response = new Response(Response.DO_EXPLORATION,new myPoint[]{target});
				}
				else {
					response = new Response(Response.DO_EXPLORATION,new myPoint[]{new myPoint(0,0),target});
				}
			}
			
			ID = beliefs.getAgentID();
			if (beliefs.getAgentsWithoutTask().indexOf(ID) == -1) {
				beliefs.getAgentsWithoutTask().add(ID);
			}
			
			break;
			
		case FINISH_TASK:
			
			currentPosition = beliefs.getAgentCurrentPosition();
			fuel = beliefs.getAgentCurrentFuel();
			int water = beliefs.getAgentCurrentWater();
			
			/*
			 *  select a task with the lowest cost 
			 */
			int minCost = -1;
			myPoint[] route = null;
			int taskIndex = -1;
			for (int i=0;i<beliefs.getTasks().size();i++) {
				myTask currentTask = beliefs.getTasks().get(i);
				target = currentTask.getStationPosition();
				
				if (water >= currentTask.getTask().getRequired() && this.isRouteSafe(fuel, currentPosition, new myPoint[]{target}) >= 0) {
					if (target.equals(currentPosition)) {
						route = new myPoint[]{};
					}
					else {
						route = new myPoint[]{target};
					}
					taskIndex = i;
					break;
				}
				else {
					myPoint well = beliefs.getStationWellTable().get(target);
						
					if (this.isRouteSafe(fuel, currentPosition, new myPoint[]{well,target}) >= 0) {
						if (well.equals(currentPosition)) {
							route = new myPoint[]{target};
						}
						else {
							route = new myPoint[]{well,target};
						}
						taskIndex = i;
						break;
					}
					else {
						int cost;
						myPoint[] route_temp;
						if (currentPosition.equals(new myPoint(0,0))) {
							route_temp = new myPoint[]{well,target};
									
							if (this.isRouteSafe(Tanker.MAX_FUEL, currentPosition, route_temp) < 0) {
								route_temp = new myPoint[]{well,new myPoint(0,0),target};
							}
						}
						else {
							route_temp = new myPoint[]{new myPoint(0,0),well,target};
							
							if (this.isRouteSafe(Tanker.MAX_FUEL, new myPoint(0,0), route_temp) < 0) {
								route_temp = new myPoint[]{new myPoint(0,0),well,new myPoint(0,0),target};
							}
						}
						
						cost = Math.abs(this.isRouteSafe(0, currentPosition, route_temp));
						
						if (minCost == -1) {
							minCost = cost;
							route = route_temp;
							taskIndex = i;
						}
						else {
							if (cost < minCost) {
								minCost = cost;
								route = route_temp;
								taskIndex = i;
							}
						}
					}
				}
			}
			
			// remove allocated task from task list and add to allocated task list
			myTask selectedTask = beliefs.getTasks().remove(taskIndex);
			beliefs.getAllocatedTasks().add(selectedTask);

			// generate response
			response = new Response(Response.FINISH_TASK,selectedTask,route);
			
			ID = beliefs.getAgentID();
			if (beliefs.getAgentsWithoutTask().indexOf(ID) != -1) {
				beliefs.getAgentsWithoutTask().remove(ID);
			}
			
			break;
		default:
			System.out.println("No current desire : Plan don't know how to execute !!");
			break;
		}
		
		
		return response;
	}
	
	/*
	 * Whether tanker can go there
	 * @return remaining fuel
	 * */
	private int isRouteSafe(int fuel,myPoint startingPoint, myPoint[] route) {
		
		int sum = 0;
		// fuel used by go to the first point from starting point
		int dx0 = startingPoint.getX() - route[0].getX();
		int dy0 = startingPoint.getY() - route[0].getY();
		int distance0 = Math.max(Math.abs(dx0), Math.abs(dy0));
		sum += distance0;
		
		// fuel used by go to each node
		for(int i=1;i<route.length;i++) {
			int dx1 = route[i-1].getX() - route[i].getX();
			int dy1 = route[i-1].getY() - route[i].getY();
			int distance1 = Math.max(Math.abs(dx1), Math.abs(dy1));
			sum += distance1;
		}
		
		// fuel used by return from target
		int dx2 = route[route.length-1].getX();
		int dy2 = route[route.length-1].getY();
		int distance2 = Math.max(Math.abs(dx2), Math.abs(dy2));
		
		return fuel - sum - distance2;
	}
}
