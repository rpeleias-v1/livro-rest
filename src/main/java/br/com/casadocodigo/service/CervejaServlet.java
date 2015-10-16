package br.com.casadocodigo.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.codehaus.jettison.mapped.MappedNamespaceConvention;
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

	private void escreveJSON(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			Marshaller marshaller = context.createMarshaller();
			resp.setContentType("application/xml;charset=UTF-8");
			PrintWriter out = resp.getWriter();

			Cervejas cervejas = new Cervejas();
			cervejas.setCervejas(new ArrayList<Cerveja>(estoque.listarCervejas()));
			marshaller.marshal(cervejas, out);
		} catch (JAXBException e) {
			resp.sendError(500, e.getMessage());
		}
	}

	private void escreveXML(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			Cervejas cervejas = new Cervejas();
			cervejas.setCervejas(new ArrayList<Cerveja>(estoque.listarCervejas()));
			
			MappedNamespaceConvention convention = new MappedNamespaceConvention();
			MappedXMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(convention, resp.getWriter());
			Marshaller marshaller = context.createMarshaller();
			resp.setContentType("application/xml;charset=UTF-8");
			marshaller.marshal(cervejas, xmlStreamWriter);
		} catch (JAXBException e) {
			resp.sendError(500, e.getMessage());
		}

	}

}
