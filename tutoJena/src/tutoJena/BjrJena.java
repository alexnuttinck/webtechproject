package tutoJena;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.*;

public class BjrJena {
	
	public static void main(String[] args) {
        Model monModele = ModelFactory.createDefaultModel();
        // Créer une ressource MESSAGE
        Resource message = monModele.createResource("http://webtutoriel/message");
        // Créer une propriété VALEUR
        Property valeurProp = monModele.createProperty("http://webtutoriel/valeur");
        // Assigner une valeur à la ressource
        message.addProperty(valeurProp, "Bonjour à tous");
        // Imprimer le contenu du modèle dans la syntaxe TTL 
        monModele.write( System.out , "TTL");
    }
	
}
