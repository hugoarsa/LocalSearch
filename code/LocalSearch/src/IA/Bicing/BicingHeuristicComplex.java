package IA.Bicing;

import aima.search.framework.HeuristicFunction;

public class BicingHeuristicComplex  implements HeuristicFunction {
	public double getHeuristicValue(Object state){
        double h = - ((BicingBoard) state).get_heur2();
        return h;
    }
}
