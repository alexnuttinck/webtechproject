package be.unamur.requestSPARQL;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.json.JSONML;
import org.json.JSONObject;

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

    public static ResultSet querySPARQL(String request){
    	
    	String path = "C:\\Users\\nutti\\workspace\\webtechproject\\ontologyapi\\src\\main\\ressources\\test.rdf";
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