package uk.ac.nott.cs.g54dia.multidemo.communication;

import uk.ac.nott.cs.g54dia.multidemo.AgentManager;
import uk.ac.nott.cs.g54dia.multidemo.IntelligentTanker;
import uk.ac.nott.cs.g54dia.multilibrary.Fleet;

public class Broadcast {
	
	private static AgentManager manager;
	
	private static Fleet fleet;
	
	public Broadcast(AgentManager manager, Fleet fleet) {
		Broadcast.manager = manager;
		Broadcast.fleet = fleet;
	}
	
	public static void sendReportToManager(IntelligentTanker agent, Report report) {
		manager.onReceiveReport(report);
	}
	
	public static Response sendTaskRequestToManager(IntelligentTanker agent, Request request) {
		return manager.onReceiveRequest(request);
	}
	
	public static void sendResponseToAgent(AgentManager manager, IntelligentTanker agent, Response response) {
		agent.onReceiveResponse(response);
	}
	
	public static void sendResponseToAgent(AgentManager manager, String agentID, Response response) {
		for (int i=0;i<fleet.size();i++) {
			if (fleet.get(i).toString().equals(agentID)) {
				((IntelligentTanker)fleet.get(i)).onReceiveResponse(response);
				break;
			}
		}	
	}
	
	public static void sendResponseToAllAgents(AgentManager manager, Response response) {
		for (int i=0;i<fleet.size();i++) {
			((IntelligentTanker)fleet.get(i)).onReceiveResponse(response);
		}		
	}
}
