package ontology;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

public class CafeModel {

	//Source of the ontology and Namespace
	String SOURCE = "";
	String NS = SOURCE + "#";
		
	//We use OWL DL (not full nor lite). With rule based reasoner (OWL rules)
	OntModel cafe = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM_RULE_INF );
	
}
