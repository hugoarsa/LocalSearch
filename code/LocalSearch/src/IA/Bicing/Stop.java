package IA.Bicing;

public class Stop {
    private int stationId;  // Identificador de la estación (entero)
    private int impact;     // Impacto en el número de bicicletas (positivo o negativo)

    // Constructor
    public Stop(int stationId, int impact) {
        this.stationId = stationId;
        this.impact = impact;
    }
    
    public Stop shallowCopy() {
    	Stop newStop = new Stop(this.stationId, this.impact);
    	return newStop;
    }

    // Método para obtener el identificador de la estación
    public int getStationId() {
        return stationId;
    }

    // Método para obtener el impacto en el número de bicicletas
    public int getImpact() {
        return impact;
    }

    // Método para establecer el identificador de la estación
    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    // Método para establecer el impacto en el número de bicicletas
    public void setImpact(int impact) {
        this.impact = impact;
    }
}