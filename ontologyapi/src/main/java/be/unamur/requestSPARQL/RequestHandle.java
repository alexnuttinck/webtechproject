package be.unamur.requestSPARQL;

import java.io.ByteArrayOutputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.resultset.ResultsFormat;

import ontology.EnterpriseModel;

public class RequestHandle {

	public static String toJson(String request){    

		ResultSet resultsQuery = querySPARQL(request);

		if(resultsQuery == null) return "REQUEST ERROR";
		else{
			// write to a ByteArrayOutputStream
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ResultSetFormatter.outputAsJSON(outputStream, resultsQuery);

			// and turn that into a String
			String json = new String(outputStream.toByteArray());

			return json;
		}
	}

	public static String toXML(String request){    

		ResultSet resultsQuery = querySPARQL(request);

		if(resultsQuery == null) return "REQUEST ERROR";
		else{
			// write to a ByteArrayOutputStream
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ResultSetFormatter.outputAsXML(outputStream, resultsQuery);

			// and turn that into a String
			String xml = new String(outputStream.toByteArray());

			return xml;
		}
	}

	public static String toCSV(String request){    

		ResultSet resultsQuery = querySPARQL(request);

		if(resultsQuery == null) return "REQUEST ERROR";
		else{
			// write to a ByteArrayOutputStream
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ResultSetFormatter.outputAsCSV(outputStream, resultsQuery);

			// and turn that into a String
			String csv = new String(outputStream.toByteArray());

			return csv;
		}
	}

	public static String toCSVSTRUCT(String request){    

		ResultSet resultsQuery = querySPARQL(request);

		if(resultsQuery == null) return "REQUEST ERROR";
		else{
			// write to a ByteArrayOutputStream
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ResultSetFormatter.out(outputStream, resultsQuery);

			// and turn that into a String
			String csv = new String(outputStream.toByteArray());

			return csv;
		}
	}

	public static String toRDF(String request){    

		ResultSet resultsQuery = querySPARQL(request);

		if(resultsQuery == null) return "REQUEST ERROR";
		else{
			// write to a ByteArrayOutputStream
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ResultSetFormatter.output(outputStream, resultsQuery, ResultsFormat.FMT_RDF_XML);

			// and turn that into a String
			String csv = new String(outputStream.toByteArray());

			return csv;
		}
	}

	public static ResultSet querySPARQL(String request){

		String path = "C:\\Users\\nutti\\workspace\\webtechproject\\ontologyapi\\test.rdf";
		System.out.println(path);
		OntModel enterpriseModel = EnterpriseModel.getOntologyModel(path);
		String queryString = request ;
		System.out.println(queryString);
		Query query = QueryFactory.create(queryString) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, enterpriseModel);
		ResultSet results = null;
		try{
			//results = qexec.execSelect() ;
			results = qexec.execSelect() ;
		} finally {
			//	qexec.close();
		}
		return results;
		//return null;
	}

}