package IA.Bicing;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GetSuccessorsSimulatedAnnealing implements SuccessorFunction {
	public List getSuccessors(Object aState) {
		ArrayList<Successor> retVal = new ArrayList<Successor>();
		ArrayList<Successor> retValRandomized = new ArrayList<Successor>();
		BicingBoard currentState = (BicingBoard) aState;
		int ntrucks = currentState.getNumberTrucks();
		int nstations = currentState.getNumberStations();
		
		Random rand = new Random();
		
		//Apply operator jumpStartRoute
		for(int i = 0; i < ntrucks; i++) {
			for(int j = 0; j < nstations; j++) {
				for(int j2 = 0; j2 < nstations; j2++) {
					if(currentState.canJumpStartRoute(i, j, j2)) {
						BicingBoard successorState = new BicingBoard(currentState);
						successorState.jumpStartRoute(i, j, j2);
						
						String action = "Truck " + i + " jumpstarted their route with stop " + j + " as their" + 
										"origin stop and stop " + j2 + " as their first destination";
						retVal.add(new Successor(action, successorState));
					}
				}
			}
		}
		
		//Apply operator addStop
		for(int i = 0; i < ntrucks; i++) {
			for(int j = 0; j < nstations; j++) {
				if(currentState.canAddStop(i, j)) {
					BicingBoard successorState = new BicingBoard(currentState);
					successorState.addStop(i, j);
					
					String action = "Truck " + i + " added stop " + j + " to the end of their route";
					retVal.add(new Successor(action, successorState));
				}
			}
		}
		
		//Apply operator removeStop
		for(int i = 0; i < ntrucks; i++) {
			if(currentState.canRemoveStop(i)) {
				BicingBoard successorState = new BicingBoard(currentState);
				successorState.removeStop(i);
				
				String action = "Truck " + i + " removed the last stop of their route";
				retVal.add(new Successor(action, successorState));
			}
		}
		
		//Apply operator switchStop
		for(int i = 0; i < ntrucks; i++) {
			for(int j = 0; j < 3; j++) {
				for(int k = 0; k < nstations; k++) {
					if(currentState.canSwitchStop(i, j, k)) {
						BicingBoard successorState = new BicingBoard(currentState);
						successorState.switchStop(i, j, k);
						
						String action = "Truck " + i + " changed the stop in position " + j + " for stop with ID " + k;
						retVal.add(new Successor(action, successorState));
					}
				}
			}
		}
		
		if(retVal.size() > 0) {
			int randomIdx = rand.nextInt(retVal.size());
			retValRandomized.add(retVal.get(randomIdx));
		}
		return retValRandomized;
	}
}
