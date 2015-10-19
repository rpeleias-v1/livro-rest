package br.com.casadocodigo.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

import br.com.casadocodigo.modelo.Cerveja;
import br.com.casadocodigo.modelo.Cervejas;
import br.com.casadocodigo.modelo.Estoque;

@WebServlet(value = "/cervejas/*")
public class CervejaServlet extends HttpServlet {

	private Estoque estoque = new Estoque();
	private static JAXBContext context;

	static {
		try {
			context = JAXBContext.newInstance(Cervejas.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String acceptHeader = req.getHeader("Accept");
		if (acceptHeader == null || acceptHeader.contains("application/xml")) {
			escreveXML(req, resp);
		} else if (acceptHeader.contains("application/json")) {
			escreveJSON(req, resp);
		} else {
			resp.sendError(415); // formato não suportado
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String identificador = null;

		try {
			try {
				identificador = obtemIdentificador(req);
				
			} catch (RecursoSemIdentificadorException e) {
				resp.sendError(400, e.getMessage());
			}
			
			if (identificador != null && estoque.recuperarCervejaPeloNome(identificador) != null) {
				resp.sendError(409, "Já existe uma cerveja com esse nome!");
			}
			
			String tipoDeConteudo = req.getContentType();
			
			if ((tipoDeConteudo.startsWith("text/xml") || tipoDeConteudo.startsWith("application/xml"))) {
				escreveObjetoXML(req, resp, identificador);
			} else if (tipoDeConteudo.startsWith("application/json")){
				escreveObjetoJSON(req, resp, identificador);
			} else {
				resp.sendError(415);
			}

			
		} catch (JAXBException | JSONException | XMLStreamException e) {
			resp.sendError(500, e.getMessage());
		}
	}

	private void escreveObjetoJSON(HttpServletRequest req, HttpServletResponse resp, String identificador)
			throws IOException, JSONException, XMLStreamException, JAXBException {
		List<String> lines = IOUtils.readLines(req.getInputStream());
		StringBuilder builder = new StringBuilder();
		for (String line : lines) {
			builder.append(line);
		}
		
		MappedNamespaceConvention convention = new MappedNamespaceConvention();
		JSONObject jsonObject = new JSONObject(builder.toString());
		XMLStreamReader xmlStreamReader = new MappedXMLStreamReader(jsonObject, convention);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Cerveja cerveja = (Cerveja) unmarshaller.unmarshal(xmlStreamReader);
		cerveja.setNome(identificador);
		estoque.adicionarCervejas(cerveja);
		System.out.println(cerveja);
		String requestURI = req.getRequestURI();
		resp.setHeader("Location", requestURI);
		resp.setStatus(201);
		escreveJSON(req, resp);
	}

	private void escreveObjetoXML(HttpServletRequest req, HttpServletResponse resp, String identificador)
			throws JAXBException, IOException {
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Cerveja cerveja = (Cerveja) unmarshaller.unmarshal(req.getInputStream());
		cerveja.setNome(identificador);
		estoque.adicionarCervejas(cerveja);
		System.out.println(cerveja);
		String requestURI = req.getRequestURI();
		resp.setHeader("Location", requestURI);
		resp.setStatus(201);
		escreveXML(req, resp);
	}

	private void escreveXML(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Object objetoAEscrever = localizaObjetoASerEnviado(req);
		
		if (objetoAEscrever== null) {
			resp.sendError(404);
			return;
		}

		try {			
			resp.setContentType("application/xml;charset=UTF-8");
			Marshaller marshaller = context.createMarshaller();			
			marshaller.marshal(objetoAEscrever, resp.getWriter());
		} catch (JAXBException e) {
			resp.sendError(500);
		}
	}

	private void escreveJSON(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Object objetoAEscrever = localizaObjetoASerEnviado(req);
		
		if (objetoAEscrever== null) {
			resp.sendError(404);
			return;
		}

		try {
			resp.setContentType("application/json;charset=UTF-8");
			MappedNamespaceConvention convention = new MappedNamespaceConvention();
			XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(convention, resp.getWriter());
			Marshaller marshaller = context.createMarshaller();			
			marshaller.marshal(objetoAEscrever, xmlStreamWriter);
		} catch (JAXBException e) {
			resp.sendError(500);
		}
	}

	private Object localizaObjetoASerEnviado(HttpServletRequest req) {
		Object objeto = null;
		try {
			String identificador = obtemIdentificador(req);
			objeto = estoque.recuperarCervejaPeloNome(identificador);
		} catch (RecursoSemIdentificadorException e) {
			Cervejas cervejas = new Cervejas();
			cervejas.setCervejas(new ArrayList<Cerveja>(estoque.listarCervejas()));
			objeto = cervejas;
		}
		return objeto;
	}

	private String obtemIdentificador(HttpServletRequest req) throws RecursoSemIdentificadorException {
		String requestURI = req.getRequestURI();
		String[] pedacosDaURI = requestURI.split("/");

		boolean contextoCervejasEncontrado = false;
		for (String contexto : pedacosDaURI) {
			if (contexto.equals("cervejas")) {
				contextoCervejasEncontrado = true;
				continue;
			}
			if (contextoCervejasEncontrado) {
				try {
					return URLDecoder.decode(contexto, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					return URLDecoder.decode(contexto);
				}
			}
		}
		throw new RecursoSemIdentificadorException("Recurso sem Identificador");
	}

}
