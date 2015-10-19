package br.com.casadocodigo.service.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/olaMundo")
public class TesteService {

	@GET
	@Produces("text/plain")
	public String dizOla() {
		return "Olá, mundo REST!";
	}
}
