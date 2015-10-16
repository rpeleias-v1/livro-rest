package br.com.casadocodigo.modelo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Estoque {

	private Map<String, Cerveja> cervejas = new HashMap<String, Cerveja>();
	
	public Estoque() {
		Cerveja stella = new Cerveja("Stella Artois", "A cerveja belga mais francesa do mundo", "Femsa", Cerveja.Tipo.LAGER);
		Cerveja erdinger = new Cerveja("Erdinger Alcohol Free", "Cerveja de trigo sem álcool", "Erdinger", Cerveja.Tipo.WEIZEN);
		this.cervejas.put(stella.getNome(), stella);
		this.cervejas.put(erdinger.getNome(), erdinger);
	}
	
	public Collection<Cerveja> listarCervejas() {
		return new ArrayList<Cerveja>(this.cervejas.values());
	}
	
	public void adicionarCervejas(Cerveja cerveja) {
		this.cervejas.put(cerveja.getNome(), cerveja);
	}
	
	public Cerveja recuperarCervejaPeloNome(String nome) {
		return this.cervejas.get(nome);
	}
}
