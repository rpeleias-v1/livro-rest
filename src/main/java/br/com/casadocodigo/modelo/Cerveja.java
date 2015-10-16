package br.com.casadocodigo.modelo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Cerveja {

	private String nome;
	private String descricao;
	private String cervejaria;
	private Tipo tipo;
	
	public Cerveja() {
	}

	public Cerveja(String nome, String descricao, String cervejaria, Tipo tipo) {
		this.nome = nome;
		this.descricao = descricao;
		this.cervejaria = cervejaria;
		this.tipo = tipo;
	}

	public enum Tipo {
		LAGER, PILSEN, PALE_ALE, INDIAN_PALE_ALE, WEIZEN, CHOCOLATE;
	}
	
	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return this.nome + " - " + this.descricao;
	}
}
