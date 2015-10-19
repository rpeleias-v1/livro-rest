package br.com.casadocodigo.modelo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Estoque {

	private Map<String, Cerveja> cervejas = new HashMap<String, Cerveja>();
	
	public Estoque() {
		Cerveja stella = new Cerveja("Stella Artois", "A cerveja belga mais francesa do mundo", "Femsa", Cerveja.Tipo.LAGER);
		Cerveja erdinger = new Cerveja("Erdinger Alcohol Free", "Cerveja de trigo sem álcool", "Erdinger", Cerveja.Tipo.WEIZEN);
		this.cervejas.put(stella.getNome(), stella);
		this.cervejas.put(erdinger.getNome(), erdinger);
	}
	
	public List<Cerveja> listarCervejas() {
		return new ArrayList<Cerveja>(this.cervejas.values());
	}
	
	public List<Cerveja> listarCervejas(int numeroPagina, int tamanhoPagina) {
		int indiceInicial = numeroPagina * tamanhoPagina;
		int indiceFinal = indiceInicial + tamanhoPagina;
		
		List<Cerveja> cervejas = listarCervejas();
		
		if (cervejas.size() > indiceInicial) {
			if (cervejas.size() > indiceFinal) {
				cervejas = cervejas.subList(indiceInicial, indiceFinal);
			} else {
				cervejas = cervejas.subList(indiceInicial, cervejas.size());
			}
		} else {
			cervejas = new ArrayList<>();
		}
		return cervejas;
	}
	
	public void adicionarCerveja(Cerveja cerveja) throws CervejaJaExisteException {
		if (this.cervejas.containsKey(cerveja.getNome())) {
			throw new CervejaJaExisteException();
		}
		this.cervejas.put(cerveja.getNome(), cerveja);
	}
	
	public Cerveja recuperarCervejaPeloNome(String nome) {
		return this.cervejas.get(nome);
	}
	
	public void atualizarCerveja(Cerveja cerveja) {
		if (this.cervejas.containsKey(cerveja.getNome())) {
			throw new CervejaNaoEncontradaException();
		}
		cervejas.put(cerveja.getNome(), cerveja);
	}

	public void apagarCerveja(String cerveja) {
		if (!this.cervejas.containsKey(cerveja)) {
			throw new CervejaNaoEncontradaException();
		}
		cervejas.remove(cerveja);
	}
}
