package ontology;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

public class EnterpriseModel {

	//Source of the ontology and Namespace
	String SOURCE = "http://live.dbpedia.org/ontology/Company";
	String NS = SOURCE + "#";
	
	//We use OWL DL (not full nor lite). With rule based reasoner (OWL rules)
	OntModel entreprise = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM_RULE_INF );
	
	
}
