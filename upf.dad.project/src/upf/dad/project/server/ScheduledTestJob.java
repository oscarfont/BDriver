package upf.dad.project.server;

import java.util.Date;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import upf.dad.project.data.Station;
import upf.dad.project.data.Stations;

public class ScheduledTestJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {

			System.out.println("Executing ScheduledTestJob at " + new Date());

			// Creamos el cliente y el web target de la API del bicing
			Client client = ClientBuilder.newClient();
			WebTarget targetGet = client.target("http://wservice.viabicing.cat").path("/v2/stations");

			// Obtenemos todas las estaciones y las guardamos en la variable station
			Stations station = targetGet.request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<Stations>() {
			});

			// Añadimos las estaciones guardadas en la variable station a la lista de
			// estaciones
			RestServer.setListStations(station);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}