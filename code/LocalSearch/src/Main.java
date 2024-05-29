import IA.Bicing.Stop;
import IA.Bicing.Route;
import IA.Bicing.Estaciones;
import IA.Bicing.Estacion;
import IA.Bicing.BicingBoard;
import IA.Bicing.GetSuccessorsHillClimbing;
import IA.Bicing.GetSuccessorsSimulatedAnnealing;
import IA.Bicing.BicingHeuristic;
import IA.Bicing.BicingHeuristicComplex;
import IA.Bicing.BicingGoalTest;

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.sql.Time;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;  // Import the Scanner class

public class Main {
	
    public static void main(String[] args) throws Exception{

        /*
        TODO un print con las opciones del programa: -help, -end, -begin
        */
    	int searchMode = 1; //0 is HillClimbing 1 is SimulatedAnnealing
    	int init = 0; //0 is null solution 1 is optimal solution 2 is random solution ##Shouldn't be changed in theory
        int nbikes = 1250; //+1250 con cada cambio de tamano
        int nstations = 25; //+25 con cada cambio de tamano
        int ntrucks = 5; //+5 con cada cambio de tamano
        int demand = 0; // 0 is equilibrada 1 is hora punta ## PARA EL EXPERIMENTO 6
        int seed =  9324;
        int heuristic = 1; // 0 is gain heuristic 1 is gain-cost heuristic ## PARA EL EXPERIMENTO 5
        
        for (int i = 0; i < args.length; i++) {
          switch (args[i]) {
          case "-b":
          try {
        	  nbikes = Integer.parseInt(args[++i]);
              }
          catch (NumberFormatException e)
              {
               System.out.println(args[i] + " is not an integer.");
               System.exit(0);
              }

          break;

          case "-e":
          try {
        	  nstations = Integer.parseInt(args[++i]);
              }
          catch (NumberFormatException e)
              {
                System.out.println(args[i] + " is not a integer.");
                System.exit(0);
              }

          break;

          case "-s":
          try {
               seed = Integer.parseInt(args[++i]);
              }
          catch (NumberFormatException e)
              {
                System.out.println(args[i] + " is not an integer.");
                System.exit(0);
              }
          break;
          
          case "-i":
              try {
                   init = Integer.parseInt(args[++i]);
                  }
              catch (NumberFormatException e)
                  {
                    System.out.println(args[i] + " is not an integer.");
                    System.exit(0);
                  }
              break;

          case "-t":
          try {
        	  ntrucks =  Integer.parseInt(args[++i]);
              }
          catch (NumberFormatException e)
              {
                System.out.println(args[i] + " is not an integer.");
                System.exit(0);
              }
          break;
          
          case "-d":
              try {
            	  demand = Integer.parseInt(args[++i]);
                  }
              catch (NumberFormatException e)
                  {
                    System.out.println(args[i] + " is not a integer.");
                    System.exit(0);
                  }

          break;

          case "-h":
              try {
                  heuristic = Integer.parseInt(args[++i]);
              } catch (NumberFormatException e) {
                  System.out.println(args[i] + "is not an integer.");
                  System.exit(0);
              }
          break;

          default:
        	  // arg
              System.out.println("The option " + args[i] + " not valid.");
              System.out.println("-b [Number of packages] ");
              System.out.println("-e [Number of stations]");
              System.out.println("-t [Number of trucks]");
              System.out.println("-s [Seed]");
              System.out.println("-i [Init option is null/optim/rand (0/1/2)]");
              System.out.println("-d [Option of demand normal/rush (0/1)]");
              System.out.println("-h [Heuristic Function just gain/complex(0/1)]");
              System.exit(0);
              break;
          }
        }
        Estaciones est = new Estaciones(nstations, nbikes, demand, seed);
        BicingBoard board = new BicingBoard(est,nbikes,ntrucks,init);
        board.printStations();
        board.printRoutes();
        System.out.println("The initial gain is " + board.get_heur1());
        System.out.println("The initial cost is " + board.get_heur2());
        System.out.println("The initial total distance traversed is " + board.getLongitudTotal() + "m");
        int ks[] = {1, 5, 25, 125};
        double lambdas[] = {1, 0.1, 0.01, 0.001};
        if (searchMode == 0) {
        	if(heuristic == 0) {
                BicingHillClimbingSearch(board);
            } else if(heuristic == 1) {
            	BicingHillClimbingSearchComplex(board);
            }	
        }
        else {
        	if(heuristic == 0) {
                BicingSimulatedAnnealingSearch(board, 100000, 100, 1, 1);
            } else if(heuristic == 1) {
            	BicingSimulatedAnnealingSearchComplex(board, 100, 100000, 1, 1);
            }
        }

     }
    
    private static void BicingHillClimbingSearch(BicingBoard board) {
        System.out.println("\nBicing HillClimbing Simple Heuristic  -->");
        try {
            long time = System.currentTimeMillis();
            Problem problem =  new Problem(board,
            				new GetSuccessorsHillClimbing(),
            				new BicingGoalTest(),
            				new BicingHeuristic());
            Search search =  new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem,search);

            BicingBoard newBoard = (BicingBoard)search.getGoalState();
            time = System.currentTimeMillis() - time;
            newBoard.printStations();
            newBoard.printRoutes();
            System.out.println("ACTIONS TAKEN: ");
            printActions(agent.getActions());
            System.out.println("The gain is " + newBoard.get_heur1());
            System.out.println("The gain minus cost si " + newBoard.get_heur2());
            System.out.println("The cost is " + newBoard.calculate_heur2_slow());
            System.out.println("The total distance traversed is " + newBoard.getLongitudTotal());
            printInstrumentation(agent.getInstrumentation());
            System.out.println(time + " ms");
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void BicingHillClimbingSearchComplex(BicingBoard board) {
        System.out.println("\nBicing HillClimbing Complex Heuristic  -->");
        try {
            long time = System.currentTimeMillis();
            Problem problem =  new Problem(board,
            				new GetSuccessorsHillClimbing(),
            				new BicingGoalTest(),
            				new BicingHeuristicComplex());
            Search search =  new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem,search);

            BicingBoard newBoard = (BicingBoard)search.getGoalState();
            time = System.currentTimeMillis() - time;
            System.out.println("The gain is " + newBoard.get_heur1());
            System.out.println("The gain minus cost si " + newBoard.get_heur2());
            System.out.println("The cost is " + newBoard.calculate_heur2_slow());
            System.out.println("The total distance traversed is " + newBoard.getLongitudTotal());
            printInstrumentation(agent.getInstrumentation());
            System.out.println(time + " ms");
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private static void BicingSimulatedAnnealingSearch(BicingBoard board, int i_steps, int i_stiter, int i_k, double i_lambda) {
        System.out.println("\nBicing SimulatedAnnealing Simple Heuristic  -->");
        try {
            long time = System.currentTimeMillis();
            Problem problem =  new Problem(board,
            				new GetSuccessorsSimulatedAnnealing(),
            				new BicingGoalTest(),
            				new BicingHeuristic());
            Search search =  new SimulatedAnnealingSearch(i_steps, i_stiter, i_k, i_lambda);
            SearchAgent agent = new SearchAgent(problem,search);

            BicingBoard newBoard = (BicingBoard)search.getGoalState();
            time = System.currentTimeMillis() - time;
            newBoard.printStations();
            newBoard.printRoutes();
            //System.out.println("ACTIONS TAKEN: ");
            //printActions(agent.getActions());
            System.out.println("K value is " + i_k);
            System.out.println("Lambda value is " + i_lambda);
            System.out.println("The gain is " + newBoard.get_heur1());
            System.out.println("The gain minus cost si " + newBoard.get_heur2());
            System.out.println("The cost is " + newBoard.calculate_heur2_slow());
            System.out.println("The total distance traversed is " + newBoard.getLongitudTotal());
            printInstrumentation(agent.getInstrumentation());
            System.out.println(time + " ms");
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void BicingSimulatedAnnealingSearchComplex(BicingBoard board, int i_steps, int i_stiter, int i_k, double i_lambda) {
        System.out.println("\nBicing SimulatedAnnealing Simple Heuristic  -->");
        try {
            long time = System.currentTimeMillis();
            Problem problem =  new Problem(board,
            				new GetSuccessorsSimulatedAnnealing(),
            				new BicingGoalTest(),
            				new BicingHeuristicComplex());
            Search search =  new SimulatedAnnealingSearch(i_steps, i_stiter, i_k, i_lambda);
            SearchAgent agent = new SearchAgent(problem,search);

            BicingBoard newBoard = (BicingBoard)search.getGoalState();
            time = System.currentTimeMillis() - time;
            newBoard.printStations();
            newBoard.printRoutes();
            //System.out.println("ACTIONS TAKEN: ");
            //printActions(agent.getActions());
            System.out.println("The gain is " + newBoard.get_heur1());
            System.out.println("The gain minus cost si " + newBoard.get_heur2());
            System.out.println("The cost is " + newBoard.calculate_heur2_slow());
            System.out.println("The total distance traversed is " + newBoard.getLongitudTotal());
            printInstrumentation(agent.getInstrumentation());
            System.out.println(time + " ms");
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }
    
    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
}
