package ontology;

import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

public class EnterpriseModel {

	//Source of the ontology and Namespace
	String SOURCE = "http://live.dbpedia.org/ontology/Company";
	String NS = SOURCE + "#";
	
	//We use OWL DL (not full nor lite). With rule based reasoner (OWL rules)
	static OntModel entreprise = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM_RULE_INF , null);

	private static Logger LOGGER = Logger.getLogger("ontology.EntrepriseModel");
	
	//Load owl file into jena ontology model
	public static OntModel getOntologyModel(String owlFile)
	{   
	    try 
	    {
	        InputStream in = FileManager.get().open(owlFile);
	        try 
	        {
	            entreprise.read(in, null);
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        LOGGER.info("Ontology " + owlFile + " loaded.");
	    } 
	    catch (JenaException je) 
	    {
	        System.err.println("ERROR" + je.getMessage());
	        je.printStackTrace();
	    }
	    return entreprise;
	}
	
	public static OntModel getModel(){
	    return entreprise;
	}

}