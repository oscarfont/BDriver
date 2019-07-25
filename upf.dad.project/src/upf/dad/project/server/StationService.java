package upf.dad.project.server;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import upf.dad.project.data.Station;
import upf.dad.project.data.Stations;

@Path("/stations")

public class StationService {

	// Obtener una estacion a partir de su ID
	@GET
	@Path("/get/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Station getId(@PathParam("id") String id) {
		if (RestServer.findStationById(id) == null) {
			throw new WebApplicationException(404);
		} else {
			return RestServer.findStationById(id);
		}
	}

	// Obtener las estaciones proximas a una en particular a partir de su ID
	@GET
	@Path("/get/nearbyStations/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Stations getNearbyStations(@PathParam("id") String id) {

		// Inicializamos las variables
		Stations nearbyStations = new Stations();
		Station selectedStation = new Station();

		// Busca la estacion de la cual sacaremos sus nearby stations
		selectedStation = RestServer.findStationById(id);

		// Obtenemos las estaciones más cercanas
		String stringIds = selectedStation.getNearbyStations();
		String[] idsArray = stringIds.split(", ");
		for (String currentId : idsArray) {
			nearbyStations.add(RestServer.findStationById(currentId));
		}

		return nearbyStations;
	}

	// Obtener las estadisticas calculadas
	@GET
	@Path("/get/statistics")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatistics() {

		// Inicializamos las varibles necesarias
		String stats = null, slotsinfo = null;
		int alta = 0, baja = 0;
		float altitude = 0, mediaAlta = 0, mediaBaja = 0;
		ArrayList<Float> samplesBaja = new ArrayList<Float>();
		ArrayList<Float> samplesAlta = new ArrayList<Float>();

		// Encontramos la mayor altitud
		for (Station currentStation : RestServer.getListStations().getStations()) {
			if (Float.parseFloat(currentStation.getAltitude()) > altitude) {
				altitude = Float.parseFloat(currentStation.getAltitude());
			}
		}
		
		slotsinfo = "#BicingNews (2/2) Las IDs de las estaciones que están actualmente llenas son: ";
		
		// Recorremos la lista de estaciones
		for (Station currentStation : RestServer.getListStations().getStations()) {

			// Comparamos las altitudes para parte alta y baja de la ciudad
			if (Float.parseFloat(currentStation.getAltitude()) >= Math.round(altitude / 2)) {
				alta++;
				samplesAlta.add(Float.parseFloat(currentStation.getSlots()));
			} else {
				baja++;
				samplesBaja.add(Float.parseFloat(currentStation.getSlots()));
			}
			
			// Obtenemos las estaciones que estan llenas
			if (Integer.parseInt(currentStation.getSlots()) == 0) {
				/*slotsinfo = slotsinfo + "Est. " + currentStation.getId() + ", en " + currentStation.getStreetName();
				// Miramos si tiene numero de calle o no para formatear bien el
				if (currentStation.getStreetNumber().equalsIgnoreCase("")) {
					slotsinfo = slotsinfo + ".\n";
				} else {
					slotsinfo = slotsinfo + ", " + currentStation.getStreetNumber() + ".\n";
				}*/
				slotsinfo = slotsinfo + currentStation.getId() + ", "; 
			}
		}

		slotsinfo = slotsinfo.substring(0, slotsinfo.length()-2) + ".";
		stats = "#BicingInfo (1/2) ";

		// Comparamos los resultados de las altitudes
		if (alta < baja) {
			stats = stats + "Hay más estaciones de bicing en la parte baja " + "que en la parte alta de la ciudad.\n";
		} else if (alta > baja) {
			stats = stats + "Hay más estaciones de bicing en la parte alta " + "que en la parte baja de la ciudad.\n";
		} else if (alta == baja) {
			stats = stats + "Hay el mismo número de estaciones de bicing en la parte alta"
					+ "que en la parte baja de la ciudad.\n";
		}

		// Media de sitios vacios en la parte alta
		for (float sample : samplesAlta) {
			mediaAlta += sample;
		}
		mediaAlta = mediaAlta / samplesAlta.size();

		// Media de sitios vacios en la parte baja
		for (float sample : samplesBaja) {
			mediaBaja += sample;
		}
		mediaBaja = mediaBaja / samplesBaja.size();

		// Generamos el mensaje de las medias alta y baja de la ciudad
		stats = stats + "---\n"
				+ "La media actual de los sitios libres en la parte alta de la ciudad es de "
				+ Math.round(mediaAlta) + ", mientras que en la parte baja es de " + Math.round(mediaBaja) + ".\n";

		return stats + slotsinfo;
	}

	// Obtenemos una lista con todas las estaciones de la base de datos
	@GET
	@Path("/get/stations")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Station> getStations() {
		return RestServer.getListStations().getStations();
	}
}