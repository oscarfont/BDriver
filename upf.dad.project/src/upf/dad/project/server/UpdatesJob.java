package upf.dad.project.server;

import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UpdatesJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		System.out.println("Executing UpdatesJob at " + new Date());
		
		// Creamos el cliente y el web target de la API del bicing
		Client client = ClientBuilder.newClient();
		
		// Miramos las Updates del Bot de Telegram
		WebTarget targetUpdates = client.target("https://api.telegram.org").path("/bot486703057:AAFRZw1F-WS_Hobo2vsQ9Wp3qgRiD_zM-KI/getUpdates");
		
		String Updates = targetUpdates.request(
				MediaType.APPLICATION_JSON_TYPE).get(new GenericType<String>() {});
		
		// Analizamos las updates
		RestServer.analyzeUpdates(Updates);
		
	}

}
