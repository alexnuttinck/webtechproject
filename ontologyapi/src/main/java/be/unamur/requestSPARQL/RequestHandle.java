package be.unamur.requestSPARQL;

import java.io.ByteArrayOutputStream;

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

        // write to a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ResultSetFormatter.outputAsJSON(outputStream, resultsQuery);

        // and turn that into a String
        String json = new String(outputStream.toByteArray());

        return json;
    }
    
    public static String toXML(String request){    

        ResultSet resultsQuery = querySPARQL(request);

        // write to a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ResultSetFormatter.outputAsXML(outputStream, resultsQuery);

        // and turn that into a String
        String xml = new String(outputStream.toByteArray());

        return xml;
    }

    public static ResultSet querySPARQL(String request){
        OntModel enterpriseModel = EnterpriseModel.getModel();
        String queryString = request ;
        Query query = QueryFactory.create(queryString) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, enterpriseModel)) {
            ResultSet results = qexec.execSelect() ;
            for ( ; results.hasNext() ; )
            {
                QuerySolution soln = results.nextSolution() ;
                RDFNode x = soln.get("varName") ;       // Get a result variable by name.
                Resource r = soln.getResource("VarR") ; // Get a result variable - must be a resource
                Literal l = soln.getLiteral("VarL") ;   // Get a result variable - must be a literal
            }
            return results;
        }
    }

}