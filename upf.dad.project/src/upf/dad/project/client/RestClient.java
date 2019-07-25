package upf.dad.project.client;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.transform.Result;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.quartz.SchedulerException;

import com.sun.net.httpserver.HttpServer;

import upf.dad.project.data.Station;
import upf.dad.project.data.Stations;
import upf.dad.project.data.User;
import upf.dad.project.server.ScheduledTestJob;
import upf.dad.project.server.StationService;

public class RestClient {

	public RestClient() {
	}

	// Main
	public static void main(String[] args) throws IOException, SchedulerException {

		/*
		 * Client client = ClientBuilder.newClient(); String token =
		 * "486703057:AAFRZw1F-WS_Hobo2vsQ9Wp3qgRiD_zM-KI"; Message message =
		 * new Message(chatID, "Message"); WebTarget targetSendMessage =
		 * client.target("https://api.telegram.org").path("/bot" + token +
		 * "/sendMessage"); String response = targetSendMessage.request()
		 * .post(Entity.entity(message, MediaType.APPLICATION_JSON_TYPE),
		 * String.class);
		 */

		// Añadir usuario DEMO
		int[] stationsSubs = { 100, 99, 2 };
		User David = new User("999999", 12345, stationsSubs);

		Client client = ClientBuilder.newClient();
		WebTarget targetAddUser = client.target("http://localhost:15000").path("/users/add");

		User response = targetAddUser.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(David, MediaType.APPLICATION_JSON), User.class);
	}
}