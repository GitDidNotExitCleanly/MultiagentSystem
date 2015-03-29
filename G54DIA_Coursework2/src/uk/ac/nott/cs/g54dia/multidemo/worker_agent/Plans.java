package uk.ac.nott.cs.g54dia.multidemo.worker_agent;

import uk.ac.nott.cs.g54dia.multidemo.data_structure.myPoint;
import uk.ac.nott.cs.g54dia.multidemo.data_structure.myTask;
import uk.ac.nott.cs.g54dia.multilibrary.*;

public class Plans {

	// main function
	public Action execute(Desires desires,Beliefs beliefs) {
		
		// if current cell is a fuel pump, refuel
		if (beliefs.getCurrentCell() instanceof FuelPump && beliefs.getFuel() < Tanker.MAX_FUEL) {
			return new RefuelAction();
		}
		// if current cell is a well, load water
		else if (beliefs.getCurrentCell() instanceof Well && beliefs.getWater() < Tanker.MAX_WATER) {
			return  new LoadWaterAction();
		}
		else {
			myPoint currentPosition;
			switch (desires.getCurrentDesire()) {		
			// Desire Selection
			case FINISH_TASK:
				
				currentPosition = beliefs.getCurrentPosition();

				if (desires.getPointer() == desires.getTarget().length) {
					// deliver water
					myTask task = desires.getTask();
					return new DeliverWaterAction(task.getTask());
				}
				else {
					// follow the route
					myPoint[] route = desires.getTarget();
					int pointer = desires.getPointer();
					return this.nextMovement(currentPosition, route[pointer]);
				}

			case TRAVERSE_MAP:
				
				currentPosition = beliefs.getCurrentPosition();
				
				// follow the route
				myPoint[] route = desires.getTarget();
				int pointer = desires.getPointer();
				return this.nextMovement(currentPosition, route[pointer]);
				
			default:
				System.out.println("No such desire !!");
				return null;
				
			}
		}
		
	}
	
	/*
	 * The next action towards the target
	 * @return an action
	 * */
	private Action nextMovement(myPoint currentPosition, myPoint targetPosition) {
		
		int dx = targetPosition.getX() - currentPosition.getX();
		int dy = targetPosition.getY() - currentPosition.getY();
		
		if (dx > 0 && dy > 0) {
			currentPosition.increaseXbyOne();
			currentPosition.increaseYbyOne();
			return new MoveAction(MoveAction.NORTHEAST);
		}
		else if (dx > 0 && dy == 0) {
			currentPosition.increaseXbyOne();
			return new MoveAction(MoveAction.EAST);
		}
		else if (dx > 0 && dy < 0) {
			currentPosition.increaseXbyOne();
			currentPosition.decreaseYbyOne();
			return new MoveAction(MoveAction.SOUTHEAST);
		}
		else if (dx == 0 && dy > 0) {
			currentPosition.increaseYbyOne();
			return new MoveAction(MoveAction.NORTH);
		}
		else if (dx == 0 && dy < 0) {
			currentPosition.decreaseYbyOne();
			return new MoveAction(MoveAction.SOUTH);
		}
		else if (dx < 0 && dy > 0) {
			currentPosition.decreaseXbyOne();
			currentPosition.increaseYbyOne();
			return new MoveAction(MoveAction.NORTHWEST);
		}
		else if (dx < 0 && dy == 0) {
			currentPosition.decreaseXbyOne();
			return new MoveAction(MoveAction.WEST);
		}
		else if (dx < 0 && dy < 0) {
			currentPosition.decreaseXbyOne();
			currentPosition.decreaseYbyOne();
			return new MoveAction(MoveAction.SOUTHWEST);
		}
		
		System.out.println("Agent doesn't move !!");
		return null;
	}
}