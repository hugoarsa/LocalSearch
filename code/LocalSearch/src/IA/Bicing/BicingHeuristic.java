package IA.Bicing;

import aima.search.framework.HeuristicFunction;

public class BicingHeuristic implements HeuristicFunction {
	public double getHeuristicValue(Object state){
        double h = - ((BicingBoard) state).get_heur1();
        return h;
    }
}
