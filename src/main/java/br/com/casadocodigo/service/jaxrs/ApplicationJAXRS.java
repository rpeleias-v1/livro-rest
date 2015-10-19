package br.com.casadocodigo.service.jaxrs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.jettison.JettisonFeature;

@ApplicationPath("services")
public class ApplicationJAXRS extends Application{
	
	/*@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(CervejaService.class);
		return classes;
	}*/
		
	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put("jersey.config.server.provider.packages", "br.com.casadocodigo.service.jaxrs");
		return properties;
	}
	
	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();
		singletons.add(new JettisonFeature());
		return singletons;
	}
}
