package upf.dad.project.server;

import javax.ws.rs.Path;

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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import upf.dad.project.client.Message;
import upf.dad.project.data.User;

@Path("/users")
public class UserService {
	
	// Publicar un usuario nuevo
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(User newUser) {
		RestServer.addUser(newUser);
		return Response.status(200).entity(newUser).build();
	}
	
	// Obtener todos los usuarios
	@GET
	@Path("/getUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getUsers() {
		return RestServer.getUsers();
	}
	
	// Notificar al usuario las free slots de las estaciones suscritas
	@GET
	@Path("/notify/{chat_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifySlots(@PathParam("chat_id") String chat_id){
		RestServer.notifyUserchatID(Long.parseLong(chat_id));
		return Response.status(200).build();
	}
}