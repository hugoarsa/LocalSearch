package IA.Bicing;
import java.util.Random;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Arrays;

public class BicingBoard {

    /// Numero de bicis
    private int nbikes;
    
    /// Numero de estaciones
    private int nstations;
    
    /// Numero de camiones
    private int ntrucks;
    
    /// Lista con las estaciones que nos da el enunciado
    static private Estaciones stations;
    
    static private int[][] distances;
    
    /// Vector con las rutas posibles
    private Route[] routes;
    
    /// Vector que nos dice si las estaciones son de inicio
    private boolean[] start_stations;
    
    /// Vector que contiene de la estacion i el impacto que ejercemos sobre ella
    private int[] impact_stations;
    
    /// Valor heurístico 1
    private int gain;

    private int inf = 2147483647;
    /////////////////////////////////////////
    /////////////BASIC METHODS///////////////
    /////////////////////////////////////////
    
    public int getNumberBikes() {
    	return nbikes;
    }
    
    public int getNumberStations() {
    	return nstations;
    }
    
    public int getNumberTrucks() {
    	return ntrucks;
    }
    
    public Estaciones getStations() {
    	return stations;
    }
    
    public int[][] getDistances(){
    	return distances;
    }
    
    public Route[] getRouteAssignations() {
    	return routes;
    }
    
    public boolean[] getOriginStations() {
    	return start_stations;
    }
    
    public int[] getImpactStations() {
    	return impact_stations;
    }
    
    public int getGainHeuristic() {
    	return gain;
    }
    
    public int getLongitudTotal() {
    	int longitud = 0;
    	for(int i = 0; i < ntrucks; i++) {
    		Route r = routes[i];
        	Optional<Stop> ns1 = r.getFirstStop();
        	Optional<Stop> ns2 = r.getSecondStop();
        	Optional<Stop> ns3 = r.getThirdStop();
        	if(ns1.isPresent() && ns2.isPresent()) {
        		Stop s1 = ns1.get();
        		Stop s2 = ns2.get();
        		longitud = longitud + distances[s1.getStationId()][s2.getStationId()];
        		if(ns3.isPresent()) {
        			Stop s3 = ns3.get();
        			longitud = longitud + distances[s2.getStationId()][s3.getStationId()];
        		}
        	} 
    	}
    	return longitud;
    }
    
    public void printRoutes() {
    	for(int i = 0; i < routes.length; i++) {
    		Route route = routes[i];
    		int a, b, c;
    		int x, y ,z;
    		a = b = c = -1;
    		x = y = z = 0;
    		Optional<Stop> optFirstStop = route.getFirstStop();
    		if(optFirstStop.isPresent()) {
    			a = optFirstStop.get().getStationId();
    			x = optFirstStop.get().getImpact();
    		}
    		Optional<Stop> optSecondStop = route.getSecondStop();
    		if(optSecondStop.isPresent()) {
    			b = optSecondStop.get().getStationId();
    			y = optSecondStop.get().getImpact();
    		}
    		Optional<Stop> optThirdStop = route.getThirdStop();
    		if(optThirdStop.isPresent()) {
    			c = optThirdStop.get().getStationId();
    			z = optThirdStop.get().getImpact();
    		}
    		System.out.println("[" + a + "//" + x + "," + b + "//" + y + "," + c + "//" + z + "]");
    	}
    }
    
    public void printStations() {
    	int i = 0;
    	for (Estacion e : stations) {
    		System.out.println("STATION " + i);
    		System.out.println("Demanda " + e.getDemanda());
    		System.out.println("Next " + e.getNumBicicletasNext());
    		System.out.println("NoUsadas " + e.getNumBicicletasNoUsadas());
    		i++;
    	}
    }
    /////////////////////////////////////////
    /////////////COPY CONSTRUCTOR////////////
    /////////////////////////////////////////
    
    public BicingBoard(BicingBoard other) {
    	this.nbikes = other.getNumberBikes();
    	this.nstations = other.getNumberStations();
    	this.ntrucks = other.getNumberTrucks();
    	Route[] routesOther = other.getRouteAssignations();
    	this.routes = new Route[routesOther.length];
    	for(int i = 0; i < routesOther.length; i++) {
    		this.routes[i] = routesOther[i].shallowCopy();
    	}
    	int[] impactsOther = other.getImpactStations();
    	this.impact_stations = new int[impactsOther.length];
    	for(int i = 0; i < impactsOther.length; i++) {
    		this.impact_stations[i] = impactsOther[i];
    	}
    	boolean[] originsOther = other.getOriginStations();
    	this.start_stations = new boolean[originsOther.length];
    	for(int i = 0; i < originsOther.length; i++) {
    		this.start_stations[i] = originsOther[i];
    	}
    	this.gain = other.getGainHeuristic();
    }
    
    
    /////////////////////////////////////////
    /////////////INITIAL STATE///////////////
    /////////////////////////////////////////


    public BicingBoard(Estaciones e, int nb, int nt, int strat) {
        nbikes = nb;
        ntrucks = nt;
        nstations = e.size();
        stations = e;
        
        // Inicializar los vectores de utilización y recorridos con el tamaño adecuado
        routes = new Route[nt];
        
        for (int i = 0; i < ntrucks; ++i)
        	routes[i] = new Route(null,null,null);
        
        start_stations = new boolean[nstations];
        
        for (int i = 0; i < nstations; ++i)
        	start_stations[i] = false;

        impact_stations = new int[nstations];
        
        for (int i = 0; i < nstations; ++i)
        	impact_stations[i] = 0;
        
        distances = calculateDistanceMatrix(e);

        if (strat == 0){
            //solucion null
            //System.out.println(routes);
    		//System.out.println(start_stations);
    		//System.out.println(impact_stations);
            gain = 0;
        }

        else if (strat == 1){
            //Solución optima
            int[] max_bikes = findTopK(ntrucks);
            for (int i = 0; i < ntrucks; ++i){
                int firstStop_id = max_bikes[i];
                //System.out.println(firstStop_id + " tiene " + available_bikes(firstStop_id));
                
                int secondStop_id = closest(firstStop_id);
                //System.out.println(secondStop_id + " necesita " + needed_bikes(secondStop_id));
                
                if(canAddStop(i, firstStop_id)) {
                	addStop(i, firstStop_id);
                	if(canAddStop(i, secondStop_id)) {
                		addStop(i, secondStop_id);
                	}
                }
                
            }

            gain = calculate_heur1_slow();
            
        }
        
        else if (strat == 2){
            //Solución random
        	
        	Random rand = new Random();
            for (int i = 0; i < ntrucks; ++i){
                int firstStop_id = rand.nextInt(nstations);
                //System.out.println(firstStop_id + " tiene " + available_bikes(firstStop_id));
                
                int secondStop_id = rand.nextInt(nstations);
                //System.out.println(secondStop_id + " necesita " + needed_bikes(secondStop_id));
                
                int thirdStop_id = rand.nextInt(nstations);
                //System.out.println(secondStop_id + " necesita " + needed_bikes(secondStop_id));
                
                if(canAddStop(i, firstStop_id)) {
                	addStop(i, firstStop_id);
                	if(canAddStop(i, secondStop_id)) {
                		addStop(i, secondStop_id);
                		if(canAddStop(i, thirdStop_id)) {
                    		addStop(i, thirdStop_id);
                    	}
                	}
                }
                
            }

            gain = calculate_heur1_slow();
        }
    }    

    private int[] findTopK(int k) {
    	if (k <= 0 || k > stations.size()) {
            //System.out.println("Invalid value of k.");
            return new int[0];
        }

        int[] topStations = new int[k];

        int[] stationIndices = new int[stations.size()];
        
        for (int i = 0; i < stations.size(); i++) {
            stationIndices[i] = i;
        }

        for (int i = 0; i < k; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < stations.size(); j++) {
                if (available_bikes(stationIndices[j]) > available_bikes(stationIndices[maxIndex])) {
                    maxIndex = j;
                }
            }
            int temp = stationIndices[i];
            stationIndices[i] = stationIndices[maxIndex];
            stationIndices[maxIndex] = temp;
        }

        for (int i = 0; i < k; i++) {
            topStations[i] = stationIndices[i];
        }

        return topStations;
    }
    
    private int available_bikes(int a) {
    	Estacion est = stations.get(a);
    	int availableBikes = Math.min(est.getNumBicicletasNext() - est.getDemanda(), est.getNumBicicletasNoUsadas());
    	return Math.min(availableBikes, 30);
    }
    
    private int needed_bikes(int a) {
    	Estacion est = stations.get(a);
    	return est.getDemanda() - est.getNumBicicletasNext() - impact_stations[a];
    }
    
    private int closest(int o){
        int dmin1 = inf;
        int id = 0;
        for (int i = 0; i < distances[o].length; ++i){
            if (distances[o][i] < dmin1 && needed_bikes(i) > 0 && distances[o][i] != 0) {
                dmin1 = distances[o][i];
                id = i;
            }
        }
        return id;
    }
    
    /////////////////////////////////////////
    ///////////////DISTANCES/////////////////
    /////////////////////////////////////////

    /*!\brief Calcula la distancia entre dos estaciones
    *
    * @param [i] Estacion i
    * @param [j] Estacion j
    */
    public static int calculateDistance(Estacion i, Estacion j) {
        int ix = i.getCoordX();
        int iy = i.getCoordY();
        int jx = j.getCoordX();
        int jy = j.getCoordY();

        // Calculate the distance using the formula d(i, j) = |ix − jx| + |iy − jy|
        int distance = Math.abs(ix - jx) + Math.abs(iy - jy);
        return distance;
    }
    
    public static int[][] calculateDistanceMatrix(Estaciones e) {
        int numStations = e.size();
        int[][] distanceMatrix = new int[numStations][numStations];

        for (int i = 0; i < numStations; i++) {
            for (int j = 0; j < numStations; j++) {
                distanceMatrix[i][j] = calculateDistance(e.get(i), e.get(j));
            }
        }

        return distanceMatrix;
    }
    
    /////////////////////////////////////////
    ///////////////HEURISTIC/////////////////
    /////////////////////////////////////////
    
    /*!\brief Calcula el heurístico simple de forma lenta para la fase inicial O(S)
    *
    */
    public double get_heur1() {
    	return (double) gain;
    }
    
    /*!\brief Calcula el heurístico simple de forma lenta para la fase inicial O(S)
    *
    */
    public double get_heur2() {
    	return ((double) gain) - calculate_heur2_slow();
    }
    
    /*!\brief Calcula el heurístico simple de forma lenta para la fase inicial O(S)
    *
    */
    public int calculate_heur1_slow() {
    	int acc = 0;
	    for (int i = 0; i < stations.size(); i++) {
	        acc = acc + station_gain(i);
	    }
    	return acc;
    }
    
    /*!\brief Calcula el heuristíco complejo (teniendo en cuenta carburante, de forma lenta para la solucion inicial O(S + R)
    *
    */
    public double calculate_heur2_slow() {
    	double acc = 0;    	
    	for (int i = 0; i < ntrucks; i++) {
	    	acc = acc + getCostGas(i);
	    }
    	return acc;
    }
    
    /*!\brief Calcula la ganancia o pérdida asociada a una estacion
    *
    * @param [r] La ruta de la que queremos calcular su coste en gasolina
    */
    private int station_gain(int s_index) {
    	Estacion s = stations.get(s_index);
    	int gain_i = 0;
        if (impact_stations[s_index]>=0) { //recompensamos bicis añadidas en sitios de necesidad
            gain_i = Math.max(0, Math.min(impact_stations[s_index],s.getDemanda() - s.getNumBicicletasNext()));
        } else if ((s.getNumBicicletasNext() - s.getDemanda())<=0){ 
        	//si legamos aqui asumimos que el impacto es negativo
            //si además entra a este if (es decir esta en deficit)
            //hemos de descontar el impacto que tuvimos
            gain_i = impact_stations[s_index];
        } else if ((s.getNumBicicletasNext() - s.getDemanda() + impact_stations[s_index])<0){
        	//Ahora es un subcaso mas sofisticado pues el deficit lo hemos introducido
        	//nosotros. Antes no estaba)
        	gain_i = impact_stations[s_index] + Math.abs(s.getNumBicicletasNext() - s.getDemanda());
        }
        return gain_i;
    }
    
    
    
    /*!\brief Calcula el coste de gasolina de completar la ruta r
    *
    * @param [r] La ruta de la que queremos calcular su coste en gasolina
    */
    private double getCostGas(int r_index) {
    	Route r = routes[r_index];
    	Optional<Stop> ns1 = r.getFirstStop();
    	Optional<Stop> ns2 = r.getSecondStop();
    	Optional<Stop> ns3 = r.getThirdStop();
    	
    	double i_cost = 0;
    	
    	if(ns1.isPresent() && ns2.isPresent()) {
    		Stop s1 = ns1.get();
    		Stop s2 = ns2.get();
    		int taken = - s1.getImpact();
    		//coste = coste + km * euro/km
    		i_cost = i_cost + ((double) distances[s1.getStationId()][s2.getStationId()] / 1000.0) * ((taken + 9) / 10);
    		int remain = taken - s2.getImpact();
    		if(ns3.isPresent()) {
    			Stop s3 = ns3.get();
    			i_cost = i_cost + ((double) distances[s2.getStationId()][s3.getStationId()] / 1000.0) * ((remain + 9) / 10);
    		}
    	} 
    	
    	return i_cost;
    }
    
    /////////////////////////////////////////
    ///////////////OPERATORS/////////////////
    /////////////////////////////////////////
    
    private boolean checkSum(Optional<Stop> i_optFirstStop, Optional<Stop> i_optSecondStop, Optional<Stop> i_optThirdStop) {
    	int firstStopImpact = 0;
    	int secondStopImpact = 0;
		int thirdStopImpact = 0;
		if(i_optFirstStop.isPresent()) {
			//System.out.println("DEBUG1");
			firstStopImpact = i_optFirstStop.get().getImpact();
		}
		if(i_optSecondStop.isPresent()) {
			//System.out.println("DEBUG2");
			secondStopImpact = i_optSecondStop.get().getImpact();
		}
		if(i_optThirdStop.isPresent()) {
			//System.out.println("DEBUG3");
			thirdStopImpact = i_optThirdStop.get().getImpact();
		}
		
		return (firstStopImpact + secondStopImpact + thirdStopImpact) <= 0;
    }
    
    public boolean canJumpStartRoute(int i_truckID, int i_origStopID, int i_destStopID) {
    	if(routes[i_truckID].getFirstStop().isPresent()) {
    		return false;
    	}
    	if(start_stations[i_origStopID]) {
    		return false;
    	}
    
    	int bikesAvailableOrig = available_bikes(i_origStopID);
    	int demandDest =  needed_bikes(i_destStopID);
    	return bikesAvailableOrig >= 0 && demandDest >= 0;
    }
    
    public void jumpStartRoute(int i_truckID, int i_origStopID, int i_destStopID) {
    	gain = gain - station_gain(i_origStopID);
    	gain = gain - station_gain(i_destStopID);
    	
    	Route route = routes[i_truckID];
    	int bikesAvailableOrig = available_bikes(i_origStopID);

    	int bikesDest = Math.min(bikesAvailableOrig, needed_bikes(i_destStopID));
    	
    	Stop origStop = new Stop(i_origStopID, -bikesAvailableOrig);
    	Stop destStop = new Stop(i_destStopID, bikesDest);
    	
    	route.setFirstStop(origStop);
    	route.setSecondStop(destStop);
    	
    	start_stations[i_origStopID] = true;
    	impact_stations[i_origStopID] -= bikesAvailableOrig;
    	impact_stations[i_destStopID] += bikesDest;
    	
    	gain = gain + station_gain(i_origStopID);
    	gain = gain + station_gain(i_destStopID);
    }
    
    public boolean canSetFullRoute(int i_truckID, int i_origStopID, int i_destStopID, int i_destStopID2) {
    	if(routes[i_truckID].getFirstStop().isPresent()) {
    		return false;
    	}
    	if(start_stations[i_origStopID]) {
    		return false;
    	}
    	if(i_destStopID == i_destStopID2) {
    		return false;
    	}
    	int bikesAvailableOrig = available_bikes(i_origStopID);
    	int demandDest =  needed_bikes(i_destStopID);
    	int demandDest2 = needed_bikes(i_destStopID2);
    	return bikesAvailableOrig >= 0 && demandDest >= 0 && demandDest2 >= 0;
    }
    
    public void setFullRoute(int i_truckID, int i_origStopID, int i_destStopID, int i_destStopID2) {
    	gain = gain - station_gain(i_origStopID);
    	gain = gain - station_gain(i_destStopID);
    	gain = gain - station_gain(i_destStopID2);
    	
    	Route route = routes[i_truckID];
    	int bikesAvailableOrig = available_bikes(i_origStopID);
    	int bikesDest = Math.min(bikesAvailableOrig, needed_bikes(i_destStopID));
    	int bikesDest2 = Math.min(bikesAvailableOrig-bikesDest, needed_bikes(i_destStopID2));
    	
    	Stop origStop = new Stop(i_origStopID, -bikesAvailableOrig);
    	Stop destStop = new Stop(i_destStopID, bikesDest);
    	Stop destStop2 = new Stop(i_destStopID2, bikesDest2);
    	
    	route.setFirstStop(origStop);
    	route.setSecondStop(destStop);
    	route.setThirdStop(destStop2);
    	
    	start_stations[i_origStopID] = true;
    	impact_stations[i_origStopID] -= bikesAvailableOrig;
    	impact_stations[i_destStopID] += bikesDest;
    	impact_stations[i_destStopID2] += bikesDest2;
    	
    	gain = gain + station_gain(i_origStopID);
    	gain = gain + station_gain(i_destStopID);
    	gain = gain + station_gain(i_destStopID2);
    }
    
    public boolean canAddStop(int i_truckID, int i_stopID) {
    	Route route = routes[i_truckID];
		if(route.getFirstStop().isEmpty()) {
			if(start_stations[i_stopID]) {
	    		return false;
	    	}
	    	int bikesAvailableOrig = available_bikes(i_stopID);
	    	return bikesAvailableOrig >= 0;
		}
		else if(route.getSecondStop().isEmpty() || route.getThirdStop().isEmpty()) {
	    	int demandDest = needed_bikes(i_stopID);
	    	return demandDest >= 0;
		}
		return false;
    }
    
    public void addStop(int i_truckID, int i_stopID) {
    	gain = gain - station_gain(i_stopID);
    	
    	Route route = routes[i_truckID];
    	Stop stopToAdd;
    	if(route.getFirstStop().isEmpty()) {
	    	int bikesAvailableOrig = available_bikes(i_stopID);

	    	stopToAdd = new Stop(i_stopID, -bikesAvailableOrig);
	    	route.setFirstStop(stopToAdd);
	    	start_stations[i_stopID] = true;
	    	impact_stations[i_stopID] -= bikesAvailableOrig;
    	}
    	else if(route.getSecondStop().isEmpty()) {
    		int bikesAvailableOrig = -(route.getFirstStop().get().getImpact());
        	int bikesDest = Math.min(bikesAvailableOrig, needed_bikes(i_stopID));
        	stopToAdd = new Stop(i_stopID, bikesDest);
        	route.setSecondStop(stopToAdd);
        	impact_stations[i_stopID] += bikesDest;
    	}
    	else {
    		int bikesAvailableOrig = -(route.getFirstStop().get().getImpact());
    		int bikesGivenFirstStop = route.getSecondStop().get().getImpact();
    		int bikesRemaining = bikesAvailableOrig - bikesGivenFirstStop;
        	int bikesDest = Math.min(bikesRemaining, needed_bikes(i_stopID));
        	stopToAdd = new Stop(i_stopID, bikesDest);
        	route.setThirdStop(stopToAdd);
        	impact_stations[i_stopID] += bikesDest;
    	}
    	
    	gain = gain + station_gain(i_stopID);
    }
    
    public boolean canRemoveStop(int i_truckID) {
    	Route route = routes[i_truckID];
    	if(route.getFirstStop().isPresent() || route.getSecondStop().isPresent() || route.getThirdStop().isPresent()) {
    		return true;
    	}
    	return false;
    }
    
    public void removeStop(int i_truckID) {
    	
    	Stop stopToRemove;
    	Route route = routes[i_truckID];
    	if(route.getThirdStop().isPresent()) {
    		stopToRemove = route.getThirdStop().get();
    		route.setThirdStop(null);
    		
    	}
    	else if (route.getSecondStop().isPresent()) {
    		stopToRemove = route.getSecondStop().get();
    		route.setSecondStop(null);
    	}
    	else {
    		stopToRemove = route.getFirstStop().get();
    		route.setFirstStop(null);
    		start_stations[stopToRemove.getStationId()] = false;
    	}
    	gain = gain - station_gain(stopToRemove.getStationId());
		int removedImpact = stopToRemove.getImpact();
		impact_stations[stopToRemove.getStationId()] -= removedImpact;
		
		gain = gain + station_gain(stopToRemove.getStationId());
    }
    
    public boolean canRemoveRoute(int i_truckID) {
    	Route route = routes[i_truckID];
    	if(route.getFirstStop().isPresent() || route.getSecondStop().isPresent() || route.getThirdStop().isPresent()) {
    		return true;
    	}
    	return false;
    }
    
    public void removeRoute(int i_truckID) {
    	while(canRemoveStop(i_truckID)) {
    		removeStop(i_truckID);
    	}
    }
    
    public boolean canChangeImpact (int i_truckID, int i_stopID, int i_impactChanged) {
    	Route route = routes[i_truckID];
    	Stop modifiedStop;
    	if(route.getFirstStop().isPresent()) {
    		Stop firstStop = route.getFirstStop().get();
    		if(firstStop.getStationId() == i_stopID) {
    			modifiedStop = new Stop(i_stopID, firstStop.getImpact() + i_impactChanged);
    			int newImpact = modifiedStop.getImpact();
    			boolean sumBool = checkSum(Optional.of(modifiedStop), route.getSecondStop(), route.getThirdStop());
    			return  newImpact <= 0 && sumBool && newImpact >= -30 && 
        				Math.abs(newImpact) <= stations.get(i_stopID).getNumBicicletasNoUsadas();
    		}
    		if(route.getSecondStop().isPresent()) {
    			Stop secondStop = route.getSecondStop().get();
        		if(secondStop.getStationId() == i_stopID) {
        			modifiedStop = new Stop(i_stopID, secondStop.getImpact() + i_impactChanged);
        			int newImpact = modifiedStop.getImpact();
        			boolean sumBool = checkSum(route.getFirstStop(), Optional.of(modifiedStop), route.getThirdStop());
        			return  newImpact >= 0 && sumBool;
        		}
        		if(route.getThirdStop().isPresent()) {
        			Stop thirdStop = route.getThirdStop().get();
            		if(thirdStop.getStationId() == i_stopID) {
            			modifiedStop = new Stop(i_stopID, thirdStop.getImpact() + i_impactChanged);
            			int newImpact = modifiedStop.getImpact();
            			boolean sumBool = checkSum(route.getFirstStop(), route.getSecondStop(), Optional.of(modifiedStop));
            			return  newImpact >= 0 && sumBool;
            		}
        		}
    		}
    	}
    	return false;
    }
    
    public void changeImpact (int i_truckID, int i_stopID, int i_impactChanged) {
    	gain = gain - station_gain(i_stopID);
    	
    	Route route = routes[i_truckID];
    	Stop stopModified = route.getFirstStop().get();
    	if(stopModified.getStationId() == i_stopID) {
    		stopModified = new Stop(i_stopID, stopModified.getImpact() + i_impactChanged);
    		route.setFirstStop(stopModified);
    		impact_stations[i_stopID] += i_impactChanged;
    	}
    	else {
    		stopModified = route.getSecondStop().get();
    		if(stopModified.getStationId() == i_stopID) {
        		stopModified = new Stop(i_stopID, stopModified.getImpact() + i_impactChanged);
        		route.setSecondStop(stopModified);
        		impact_stations[i_stopID] += i_impactChanged;
        	}
    		else {
        		stopModified = route.getThirdStop().get();
        		if(stopModified.getStationId() == i_stopID) {
            		stopModified = new Stop(i_stopID, stopModified.getImpact() + i_impactChanged);
            		route.setThirdStop(stopModified);
            		impact_stations[i_stopID] += i_impactChanged;
            	}
        	}
    	}
    	
    	gain = gain + station_gain(i_stopID);
    }
    public boolean canSwitchStop(int i_truckID, int i_pos, int i_newStopID) {
    	Route route = routes[i_truckID];
    	if(i_pos == 0) {
    		if(route.getFirstStop().isEmpty()) {
    			return false;
    		}
    		if(start_stations[i_newStopID]) {
    			return false;
    		}
    		else {
    	    	int bikesAvailableNew = available_bikes(i_newStopID);
    	    	return bikesAvailableNew >= 0;
    		}
    	}
    	else if(i_pos == 1 || i_pos == 2) {
    		if (i_pos == 1 && route.getSecondStop().isEmpty()) {
    			return false;
    		}
    		if (i_pos == 2 && route.getThirdStop().isEmpty()) {
    			return false;
    		}
	    	int demandNew = needed_bikes(i_newStopID);
	    	return demandNew >= 0;
    	}
    		
    	return false;
    }
    
    public void switchStop(int i_truckID, int i_pos, int i_newStopID) {
    	Route route = routes[i_truckID];
    	int firstStopID, secondStopID, thirdStopID;
    	firstStopID = secondStopID = thirdStopID = -1;
    	if(route.getFirstStop().isPresent()) {
    		firstStopID = route.getFirstStop().get().getStationId();
    	}
    	if(route.getSecondStop().isPresent()) {
    		secondStopID = route.getSecondStop().get().getStationId();
    	}
    	if(route.getThirdStop().isPresent()) {
    		thirdStopID = route.getThirdStop().get().getStationId();
    	}
    	
    	this.removeRoute(i_truckID);
    	if(i_pos == 0) {
    		this.addStop(i_truckID, i_newStopID);
    		if(secondStopID >= 0) {
    			this.addStop(i_truckID, secondStopID);
    			if(thirdStopID >= 0) {
    				this.addStop(i_truckID, thirdStopID);
    			}
    		}
    	}
    	else if (i_pos == 1) {
    		this.addStop(i_truckID, firstStopID);
    		this.addStop(i_truckID, i_newStopID);
    		if(thirdStopID >= 0) {
    			this.addStop(i_truckID, thirdStopID);
    		}
    	}
    	else {
    		this.addStop(i_truckID, firstStopID);
    		this.addStop(i_truckID, secondStopID);
    		this.addStop(i_truckID, i_newStopID);
    	}
    }
    
    public boolean canChangeFlow(int i_truckID, int i_mode, int i_bikes) {
    	Route route = routes[i_truckID];
    	if(i_mode == 0) {
    		if(route.getFirstStop().isEmpty() || route.getSecondStop().isEmpty()) {
    			return false;
    		}
    		int newImpact1, newImpact2;
    		newImpact1 = route.getFirstStop().get().getImpact() - i_bikes;
    		newImpact2 = route.getSecondStop().get().getImpact() + i_bikes;
    		return newImpact1 <= 0 && newImpact1 >= -30 && newImpact2 >= 0 &&
    				Math.abs(newImpact1) <= stations.get(route.getFirstStop().get().getStationId()).getNumBicicletasNoUsadas();
    	}
    	else if (i_mode == 1) {
    		if(route.getSecondStop().isEmpty() || route.getThirdStop().isEmpty()) {
    			return false;
    		}
    		int newImpact1, newImpact2;
    		newImpact1 = route.getSecondStop().get().getImpact() - i_bikes;
    		newImpact2 = route.getThirdStop().get().getImpact() + i_bikes;
    		return newImpact1 >= 0 && newImpact2 >= 0;
    	}
    	return false;
    }
    
    public void changeFlow(int i_truckID, int i_mode, int i_bikes) {
    	Route route = routes[i_truckID];
    	if(i_mode == 0) {
    		this.changeImpact(i_truckID, route.getFirstStop().get().getStationId(), -i_bikes);
    		this.changeImpact(i_truckID, route.getSecondStop().get().getStationId(), i_bikes);
    	}
    	else {
    		this.changeImpact(i_truckID, route.getSecondStop().get().getStationId(), -i_bikes);
    		this.changeImpact(i_truckID, route.getThirdStop().get().getStationId(), i_bikes);
    	}
    }
    
    public boolean canAddStopOld(int i_truckID, int i_stopID, int i_bikesImpact) {
    	Route route = routes[i_truckID];
		Stop stopToAdd = new Stop(i_stopID, i_bikesImpact);
    	if(!route.getFirstStop().isPresent()) {
    		return !start_stations[i_stopID] && i_bikesImpact <= 0 && 
    				i_bikesImpact >= -30 && Math.abs(i_bikesImpact) <= stations.get(i_stopID).getNumBicicletasNoUsadas();
    				
    	}
    	else if (!route.getSecondStop().isPresent()) {
    		boolean sumBool = checkSum(route.getFirstStop(), Optional.of(stopToAdd), Optional.empty());
    		return i_bikesImpact >= 0 && sumBool;
    	}
    	else if (!route.getThirdStop().isPresent()) {
    		boolean sumBool = checkSum(route.getFirstStop(), route.getSecondStop(), Optional.of(stopToAdd));
    		return i_bikesImpact >= 0 && sumBool;
    	}
    	return false;
    }
    
    public void addStopOld(int i_truckID, int i_stopID, int i_bikesImpact) {
    	gain = gain - station_gain(i_stopID);
    	
    	Stop stopToAdd = new Stop(i_stopID, i_bikesImpact);
    	Route route = routes[i_truckID];
    	
    	if(!route.getFirstStop().isPresent()) {
    		route.setFirstStop(stopToAdd);
    		start_stations[i_stopID] = true;
    		impact_stations[i_stopID] += i_bikesImpact;
    	}
    	else if (!route.getSecondStop().isPresent()) {
    		route.setSecondStop(stopToAdd);
    		impact_stations[i_stopID] += i_bikesImpact;
    	}
    	else {
    		route.setThirdStop(stopToAdd);
    		impact_stations[i_stopID] += i_bikesImpact;
    	}
    	
    	gain = gain + station_gain(i_stopID);
    }
    
    public boolean canAddTwoStopOld(int i_truckID, int i_stopID, int i_bikesImpact, int i_stop2ID, int i_bikesImpact2) {
    	Route route = routes[i_truckID];
		Stop stopToAdd = new Stop(i_stopID, i_bikesImpact);
		Stop secondStopToAdd = new Stop(i_stop2ID, i_bikesImpact2);
		if(i_stopID == i_stop2ID) return false;
    	if(!route.getFirstStop().isPresent()) {
    		boolean firstStopCheck = !start_stations[i_stopID] && i_bikesImpact <= 0 && 
    				i_bikesImpact >= -30 && Math.abs(i_bikesImpact) <= stations.get(i_stopID).getNumBicicletasNoUsadas();
    		boolean sumBool = checkSum(Optional.of(stopToAdd), Optional.of(secondStopToAdd), Optional.empty());
    		boolean secondStopCheck = i_bikesImpact2 >= 0;
    		return firstStopCheck && secondStopCheck && sumBool;
    	}
    	return false;
    }
    
    public void addTwoStopOld(int i_truckID, int i_stopID, int i_bikesImpact, int i_stop2ID, int i_bikesImpact2) {
    	gain = gain - station_gain(i_stopID);
    	gain = gain - station_gain(i_stop2ID);
    	
    	Stop stopToAdd = new Stop(i_stopID, i_bikesImpact);
    	Stop secondStopToAdd = new Stop(i_stop2ID, i_bikesImpact2);
    	Route route = routes[i_truckID];
    	
		route.setFirstStop(stopToAdd);
		route.setSecondStop(secondStopToAdd);
		start_stations[i_stopID] = true;
		impact_stations[i_stopID] += i_bikesImpact;
		impact_stations[i_stop2ID] += i_bikesImpact2;
	
    	gain = gain + station_gain(i_stopID);
    	gain = gain + station_gain(i_stop2ID);
    }
    
    public boolean canSwitchStopOld(int i_truckID, int i_oldStopID, int i_newStopID, int i_newBikesImpact) {
    	Route route = routes[i_truckID];
    	Stop oldStop;
    	Stop newStop = new Stop(i_newStopID, i_newBikesImpact);
    	if(route.getFirstStop().isPresent()) {
    		oldStop = route.getFirstStop().get();
    		if(oldStop.getStationId() == i_oldStopID) {
    			boolean sumBool = checkSum(Optional.of(newStop), route.getSecondStop(), route.getThirdStop());
    			return !start_stations[i_newStopID] && i_newBikesImpact <= 0 && sumBool &&
        				i_newBikesImpact >= -30 && Math.abs(i_newBikesImpact) <= stations.get(i_newStopID).getNumBicicletasNoUsadas();
    		}
    		else if (route.getSecondStop().isPresent()) {
    			oldStop = route.getSecondStop().get();
    			if(oldStop.getStationId() == i_oldStopID) {
    				boolean sumBool = checkSum(route.getFirstStop(), Optional.of(newStop), route.getThirdStop());
    	    		return i_newBikesImpact >= 0 && sumBool;
    			}
    			else if (route.getThirdStop().isPresent()) {
    				oldStop = route.getThirdStop().get();
    				if(oldStop.getStationId() == i_oldStopID) {
        				boolean sumBool = checkSum(route.getFirstStop(), route.getSecondStop(), Optional.of(newStop));
        	    		return i_newBikesImpact >= 0 && sumBool;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    public void switchStopOld (int i_truckID, int i_oldStopID, int i_newStopID, int i_newBikesImpact) {
    	gain = gain - station_gain(i_oldStopID) - station_gain(i_newStopID);
    	
    	Route route = routes[i_truckID];
    	Stop oldStop = route.getFirstStop().get();
    	Stop newStop = new Stop(i_newStopID, -i_newBikesImpact);
    	if (oldStop.getStationId() == i_oldStopID) {
    		start_stations[i_oldStopID] = false;
    		route.setFirstStop(newStop);
    		start_stations[i_newStopID] = true;
    	}
    	else {
    		oldStop = route.getSecondStop().get();
    		if(oldStop.getStationId() == i_oldStopID) {
    			route.setSecondStop(newStop);
    		}
    		else {
    			oldStop = route.getThirdStop().get();
    			route.setThirdStop(newStop);
    		}
    	}
		impact_stations[i_oldStopID] -= oldStop.getImpact();
		impact_stations[i_newStopID] += i_newBikesImpact;
		
		gain = gain + station_gain(i_oldStopID) + station_gain(i_newStopID);
    }
    
}