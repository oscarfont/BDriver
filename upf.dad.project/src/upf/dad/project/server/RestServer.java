package upf.dad.project.server;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.sun.net.httpserver.HttpServer;

import upf.dad.project.client.Message;
import upf.dad.project.data.Station;
import upf.dad.project.data.Stations;
import upf.dad.project.data.User;

public class RestServer {

	static Stations listStations = new Stations();
	static List<User> users = new ArrayList<User>();

	public static void main(String[] args) throws IOException, SchedulerException {

		try {

			URI baseUri = UriBuilder.fromUri("http://localhost/").port(15000).build();
			ResourceConfig config = new ResourceConfig(StationService.class, UserService.class);
			HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
			System.out.println("Server started...");

			// Specify the job's details..
			JobDetail testJob = JobBuilder.newJob(ScheduledTestJob.class).withIdentity("testJob").build();
			JobDetail jobUpdates = JobBuilder.newJob(UpdatesJob.class).withIdentity("updatesJob").build();

			// Specify the running period of the job
			Trigger testTrigger = TriggerBuilder.newTrigger()
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
					.build();

			Trigger updatesTrigger = TriggerBuilder.newTrigger()
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
					.build();

			// Schedule the job
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			sched.start();
			sched.scheduleJob(testJob, testTrigger);
			sched.scheduleJob(jobUpdates, updatesTrigger);

		} catch (SchedulerException s) {
			System.out.println(s.getMessage());
		}

	}

	// Getters
	public static Stations getListStations() { return listStations; }
	public static List<User> getUsers() { return users; }

	// Setters
	public static void setListStations(Stations newStations) { listStations = newStations; }

	// Funcion para pasar estaciones de String a Int
	public static int[] stationsToint(String[] subscribedStations) {

		int[] outputStations = new int[subscribedStations.length];

		for (int i = 0; i < subscribedStations.length; i++) {
			outputStations[i] = Integer.parseInt(subscribedStations[i]);
		}

		return outputStations;

	}

	// Enviar mensaje
	public static void enviarMensaje(long chatID, String texto) {
		Message message = new Message(chatID, texto);
		Client client = ClientBuilder.newClient();
		WebTarget targetSendMessage = client.target("https://api.telegram.org")
				.path("/bot486703057:AAFRZw1F-WS_Hobo2vsQ9Wp3qgRiD_zM-KI/sendMessage");
		String response = targetSendMessage.request().post(Entity.entity(message, MediaType.APPLICATION_JSON_TYPE),
				String.class);
	}

	// Añadir usuarios a la lista de usuarios
	public static void createandAddUser(String Userinfo) {

		// Obtenemos la info del update
		String Id = Userinfo.substring(0, Userinfo.indexOf(","));
		String Msg = Userinfo.substring(Userinfo.indexOf("Suscribir") + 10, Userinfo.length());
		

		String[] Stations = Msg.split(", ");
		int[] StationsnewUser = stationsToint(Stations);

		// Creamos usuario
		User newUser = new User("phoneNumber", Long.parseLong(Id), StationsnewUser);

		// Añadimos usuario
		if (addUser(newUser)) {
			// Enviamos mensaje de suscripción al Usuario
			String texto = "¡Se ha suscrito a nuestros servicios con éxito!\n"
					+ "Si quiere ser notificado cada 1 minuto sobre el estado de sus estaciones suscritas envie: Notificame.\n"
					+ "Esperemos que le sean útiles nuestros servicios :)\n" + "Atentamente los creadores de BDriver.";
			enviarMensaje(Long.parseLong(Id), texto);
		}
	}

	// Añadir Usuario y devuelve true o false en funcion si lo añadió o no
	public static boolean addUser(User newUser) {
		boolean added = false;
		// Si el usuario no existe, lo añadimos a la lista de usuarios
		if (!verifyUser(newUser)) {
			users.add(newUser);
			added = true;
		}

		return added;
	}

	// Verificar si el usuario ya esta registrado o no
	public static boolean verifyUser(User newUser) {
		boolean exists = false;
		for (User currentUser : getUsers()) {
			if (currentUser.getChatId() == newUser.getChatId()) {
				exists = true;
			}
		}
		return exists;
	}

	// Buscar una estacion a partir de su ID
	public static Station findStationById(String id) {
		for (Station currentStation : getListStations().getStations()) {
			if (currentStation.getId().equalsIgnoreCase(id)) {
				return currentStation;
			}
		}
		throw new NotFoundException("La estación no se encuentra en la base de datos.\n");
	}

	// Buscar usuario por chatId
	public static User findUserByChatID(long Id) {
		for (User currentUser : users) {
			if (currentUser.getChatId() == Id) {
				return currentUser;
			}
		}
		throw new NotFoundException("El usuario no se encuentra en la base de datos.\n");
	}
	
	// Comprobar si existe usuario dado chatId
	public static boolean existsUser(long Id) {
		boolean exists = false;
		for (User currentUser : users) {
			if (currentUser.getChatId() == Id) {
				exists = true;
			}
		}
		return exists;
	}

	// Notificar a los usuarios por ChatId
	public static void notifyUserchatID(long chatID) {

		// Generamos la notificacion
		int[] substations;
		String info = "";
		Station currentStation = new Station();
		User auxUser = findUserByChatID(chatID);
		substations = auxUser.getSubscribedStations();

		for (int currentID : substations) {
			currentStation = findStationById(Integer.toString(currentID));
			info = info + "Hay " + currentStation.getSlots() + " slots libres en la estación nº" + currentID + ", de "
					+ currentStation.getStreetName();
			if (currentStation.getStreetNumber().equalsIgnoreCase("")) {
				info = info + ".\n\n";
			} else {
				info = info + ", " + currentStation.getStreetNumber() + ".\n\n";
			}
		}

		info = info.substring(0, info.length() - 2);

		// Enviamos la notificacion
		enviarMensaje(chatID, info);
	}

	// Deletes duplicated Messages
	public static String[] delteDuplicates(String[] Updates) {
		
		String[] shorterUpdates = new String[Updates.length];
		
		for(int i=0 ; i<Updates.length ; i++) {
			
			if(!Updates[i].contains("ok")) {
				String currentUpdate;
				String text;
				String chat_id = Updates[i].substring(Updates[i].indexOf("\"id") + 5, Updates[i].indexOf("is_bot") - 2);
				if(Updates[i].contains("offset")) {
					text = Updates[i].substring(Updates[i].indexOf("text") + 7, Updates[i].indexOf("entities") - 3);
				}else {
					if(Updates[i].contains("Notificame")){
						text = Updates[i].substring(Updates[i].indexOf("text") + 7, Updates[i].lastIndexOf('"'));
					} else {
						text = Updates[i].substring(Updates[i].indexOf("text") + 7, Updates[i].lastIndexOf('"') - 5);
					}
				}
				
				currentUpdate = chat_id + ", " + text;
				shorterUpdates[i] = currentUpdate;
			}
			
		}
		
		// This deletes duplicates and null instances
		Set<String> setofUpdates = new HashSet<String>(Arrays.asList(shorterUpdates));
		if(setofUpdates.contains(null)) {
			setofUpdates.remove(null);
		}
		
		return setofUpdates.toArray(new String[setofUpdates.size()]);
		
	}

	// Analizes messages in Updates and calls respective function
	public static void analyzeUpdates(String Updates) {

		// hacemos un array de updates y borramos duplicados
		String[] arrayUpdate = delteDuplicates(Updates.split("update_id"));

		for (String update : arrayUpdate) {
			// Obtenemos chat_id
			long chat_id = Long.parseLong(update.substring(0, update.indexOf(",")));
			if (update.contains("Suscribir")&&!existsUser(chat_id)) {
				// Creamos usuario a partir de su mensaje
				createandAddUser(update);
			} else if (update.contains("Notificame")&&existsUser(chat_id)) {
				// Notificamos Usuario con su chat_id
				Client client = ClientBuilder.newClient();
				WebTarget targetNotify = client.target("http://localhost:15000").path("/users/notify/" + chat_id);
				Response respuesta = targetNotify.request(MediaType.APPLICATION_JSON_TYPE)
						.get(new GenericType<Response>() {});
			}
		}
	}

}