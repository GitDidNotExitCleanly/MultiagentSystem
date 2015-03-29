package uk.ac.nott.cs.g54dia.multidemo;

import java.util.ArrayList;

import uk.ac.nott.cs.g54dia.multidemo.agent_manager.AgentManager_Beliefs;
import uk.ac.nott.cs.g54dia.multidemo.agent_manager.AgentManager_Desires;
import uk.ac.nott.cs.g54dia.multidemo.agent_manager.AgentManager_Plans;
import uk.ac.nott.cs.g54dia.multidemo.communication.Broadcast;
import uk.ac.nott.cs.g54dia.multidemo.communication.Report;
import uk.ac.nott.cs.g54dia.multidemo.communication.Request;
import uk.ac.nott.cs.g54dia.multidemo.communication.Response;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.Goal;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myPoint;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myTask;

public class AgentManager {
	
	private AgentManager_Beliefs beliefs;
	private AgentManager_Desires desires;
	private AgentManager_Plans plans;
	
	public AgentManager(int environmentSize) {
		this.beliefs = new AgentManager_Beliefs(environmentSize);
		this.desires = new AgentManager_Desires();
		this.plans = new AgentManager_Plans();
	}
	
	// how to deal with report
	public void onReceiveReport(Report report) {
		// resolve report
		myPoint agentCurrentPosition = report.getAgentCurrentPosition();
		ArrayList<myTask> agentDetectedTasks = report.getTasks();
		ArrayList<myPoint> agentDetectedWells = report.getWells();
		ArrayList<myPoint> agentDetectedStations = report.getStations();
		
		// update view
		Response response = this.beliefs.updateView(agentCurrentPosition, agentDetectedWells, agentDetectedStations, agentDetectedTasks);
		
		if (response != null) {
			Broadcast.sendResponseToAllAgents(this, response);
		}
		
		// update desire
		if (this.desires.getCurrentDesire() == Goal.NONE) {
			this.desires.addDesires(Goal.TRAVERSE_MAP);
		}
		else if (this.desires.getCurrentDesire() == Goal.FINISH_TASK) {
			if (this.beliefs.getTasks().size() == 0) {
				this.desires.popDesires();	
			}
		}
		else {
			if (this.beliefs.getTasks().size() != 0) {
				if (this.beliefs.isExplorationFinished()) {
					this.desires.addDesires(Goal.FINISH_TASK);
					
					// immediately command agents to stop current exploration
					int taskSize = this.beliefs.getTasks().size();
					ArrayList<String> agentsWithoutTask = this.beliefs.getAgentsWithoutTask();
					int agentSize = agentsWithoutTask.size();
					for (int i=0;i<Math.min(agentSize, taskSize);i++) {
						Broadcast.sendResponseToAgent(this, agentsWithoutTask.get(i), new Response(Response.TERMINATE_EXPLORATION));
					}
					
				}
			}
		}
	}
	
	// how to deal with request
	public Response onReceiveRequest(Request request) {
		// resolve request
		myPoint agentCurrentPosition = request.getCurrentPosition();
		int fuel = request.getCurrentFuel();
		int water = request.getCurrentWater();
		myTask finishedTask = request.getFinishedTask();
		
		// update agent information
		this.beliefs.updateAgentInformation(agentCurrentPosition, fuel, water, finishedTask);
		
		// select plan
		Response response = this.plans.execute(beliefs, desires);
		return response;
	}
}
