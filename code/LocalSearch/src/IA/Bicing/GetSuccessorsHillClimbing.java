package IA.Bicing;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class GetSuccessorsHillClimbing implements SuccessorFunction {
	public List<Successor> getSuccessors(Object aState) {
		ArrayList<Successor> retVal = new ArrayList<Successor>();
		BicingBoard currentState = (BicingBoard) aState;
		int ntrucks = currentState.getNumberTrucks();
		int nstations = currentState.getNumberStations();
		
		
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
		/*
		//DISCARDED Apply operator setFullRoute
		for(int i = 0; i < ntrucks; i++) {
			for(int j = 0; j < nstations; j++) {
				for(int j2 = 0; j2 < nstations; j2++) {
					for(int j3 = 0; j3 < nstations; j3++) {
						if(currentState.canSetFullRoute(i, j, j2, j3)) {
							BicingBoard successorState = new BicingBoard(currentState);
							successorState.setFullRoute(i, j, j2, j3);
							
							String action = "Truck " + i + " set their route with stop " + j + " as their" + 
											"origin stop, stop " + j2 + " as their first destination and stop" +
											j3 + " as their second destination";
							retVal.add(new Successor(action, successorState));
						}
					}
				}
			}
		}
		*/
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
		/*
		//DISCARDED Apply operator removeRoute
		for(int i = 0; i < ntrucks; i++) {
			if(currentState.canRemoveRoute(i)) {
				BicingBoard successorState = new BicingBoard(currentState);
				successorState.removeRoute(i);
				
				String action = "Truck " + i + " removed all stops from their route";
				retVal.add(new Successor(action, successorState));
			}
		}
		//DISCARDED Apply operator changeImpact
		for(int i = 0; i < ntrucks; i++) {
			for(int j = 0; j < nstations; j++) {
				for(int k = -30; k < 30; k++) {
					if(currentState.canChangeImpact(i, j, k)) {
						BicingBoard successorState = new BicingBoard(currentState);
						successorState.changeImpact(i, j, k);
						
						String action = "Truck " + i + " changed the bikes in stop " + j + " by a factor of " + k;
						retVal.add(new Successor(action, successorState));
					}
				}
			}
		}
		
		//DISCARDED Apply operator changeFlow
		for(int i = 0; i < ntrucks; i++) {
			for(int j = 0; j < 2; j++) {
				for(int k = -30; k < 30; k++) {
					if(currentState.canChangeFlow(i, j, k)) {
						BicingBoard successorState = new BicingBoard(currentState);
						successorState.changeFlow(i, j, k);
						
						String action = "Truck " + i + " flowed the bikes with mode " + j + " by a factor of " + k;
						retVal.add(new Successor(action, successorState));
					}
				}
			}
		}
		*/
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
		/*
		//DISCARDED Apply operator addStopOld
		for(int i = 0; i < ntrucks; i++) {
			for(int j = 0; j < nstations; j++) {
				for(int k = -30; k < 30; k++) {
					if(currentState.canAddStopOld(i, j, k)) {
						BicingBoard successorState = new BicingBoard(currentState);
						successorState.addStopOld(i, j, k);
						
						String action = "OLD: Truck " + i + " added the stop " + j + " with number of bikes " + k;
						retVal.add(new Successor(action, successorState));
					}
				}
			}
		}
		
		//DISCARDED Apply operator addTwoStopOld
		for(int i = 0; i < ntrucks; i++) {
			for(int j = 0; j < nstations; j++) {
				for(int k = -30; k < 30; k++) {
					for(int j2 = 0; j2 < nstations; j2++) {
						for(int k2 = -30; k2 < 30; k2++) {
							if(currentState.canAddTwoStopOld(i, j, k, j2, k2)) {
								BicingBoard successorState = new BicingBoard(currentState);
								successorState.addTwoStopOld(i, j, k, j2 ,k2);
								
								String action = "OLD: Truck " + i + " added the stop " + j + " with number of bikes " + k +
												" and the stop " + j2 + " with number of bikes " + k2;
								retVal.add(new Successor(action, successorState));
							}
						}
					}
				}
			}
		}
		
		//DISCARDED Apply operator switchStopOld
		for(int i = 0; i < ntrucks; i++) {
			for(int j = 0; j < nstations; j++) {
				for(int j2 = 0; j2 < nstations; j2++) {
					for(int k = -30; k < 30; k++) {
						if(currentState.canSwitchStopOld(i, j, j2, k)) {
							BicingBoard successorState = new BicingBoard(currentState);
							successorState.switchStopOld(i, j, j2, k);
							
							String action = "OLD: Truck " + i + " changed the stop " + j + " for stop with ID " + j2 +
											" with number of bikes " + k;
							retVal.add(new Successor(action, successorState));
						}
					}
				}
			}
		}
		*/
		return retVal;
	}
}
