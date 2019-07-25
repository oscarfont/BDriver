package upf.dad.project.data;

import java.util.ArrayList;
import java.util.List;

public class Stations {
	
	// Lista para guardar las estaciones
	private List<Station> stations;
	
	// Constructor
	public Stations() {	
		stations = new ArrayList<Station>();
	}
	
	// Añadir una estacion a la lista mediante su ID
	public void add(Station id) {
		stations.add(id);
	}
	
	// Getter
	public List<Station> getStations() { return stations; }
	
	// Setter
	public void setStations(List<Station> stations) { this.stations = stations; }
	
	// toString
	public String toString() {
		String aux = null;
		for (Station station : stations) {
			aux = aux + station.toString();
		}
		return aux;
	}	
}