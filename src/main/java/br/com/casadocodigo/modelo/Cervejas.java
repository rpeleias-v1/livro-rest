package br.com.casadocodigo.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Cervejas {

	private List<Cerveja> cervejas = new ArrayList<Cerveja>();
	
	@XmlElement(name = "cerveja")
	public List<Cerveja> getCervejas() {
		return cervejas;
	}
	
	public void setCervejas(List<Cerveja> cervejas) {
		this.cervejas = cervejas;
	}
}
