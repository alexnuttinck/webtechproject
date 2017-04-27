package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import ontology.EnterpriseModel;

// TODO check if field has value before attempting to write
// TODO GEOM data
/**
 * Parser to instantiate a RDF/XML based on an OWL ontology and an Oracle database content.
 */
public class Parser {
	// Name space of our ontology
	private static final String NS = "http://www.semanticweb.org/Namur/entreprise-ontology#";
	// Name space of the VCARD ontology (used to describe people)
	private static final String VCARD = "http://www.w3.org/2006/vcard/ns#";

	/**
	 * Retrieve the content of a OWL ontology stored in a file.
	 * 
	 * @param owlDataFile Name of the OWL ontology file
	 * @param model Instance of the OWL ontology
	 */
	public void putEntreprise(String owlDataFile, OntModel model){
		model = EnterpriseModel.getOntologyModel(owlDataFile);
	}
	
	public void putCafe(String owlDataFile, OntModel model){
	
	}
	
	public void putRestaurant(String owlDataFile, OntModel model){
		
	}
	
	
	/**
	 * Parse the different entities (Entreprise, Cafe and Restaurant) from the database to a RDF file.
	 * 
	 * @param owlDataFile OWL Ontology file
	 * @param outputFile Name of the file where to write the RDF content
	 */
	public void parseDatabaseToRDF(String owlDataFile, String outputFile){
		OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM_RULE_INF , null);
		this.putEntreprise("C:\\Users\\Axel\\git\\webtechproject\\EntrepriseProtege.owl",model);
		model.setNsPrefix("entreprise", NS);
		model.setNsPrefix("vcard", VCARD);
		parseEntreprises(model);
		parseCafes();
		parseRestaurants();
		try{
			exportModel(model, outputFile);
		} catch (IOException e){
			System.err.println("An error occured while exporting the instantiated model...");
			e.printStackTrace();
		}
	}
	
	/**
	 * Select all enterprises (not Cafe nor Restaurant) from the database and parse them in a RDF file.
	 */
	private static void parseEntreprises(OntModel model){
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver");    
			Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:technoweb","test","test");  
			Statement stmt=con.createStatement();  
			// TODO remove Cafes and Restaurants
			ResultSet rs=stmt.executeQuery("select * from \"ELYX_DATA\".\"ECON_ENTREPRISES_GEO\"");
		
			int i =0;
			
			while(rs.next()) {
				
				//transformToRDF(rs, model);
				parseEntreprise(rs,model);
				i++;
				if(i>2) break;
			}
			con.close();  
		} catch(Exception e){
			System.err.println("An error occured while extracting from the database...\n"+e.getMessage());
			e.printStackTrace();
		}
			
	}
	
	/**
	 * Parse the enterprise contained at the current location in the ResultSet to the OntModel.
	 * 
	 * @param result Database row
	 * @param model OntModel being populated
	 * @throws SQLException
	 */
	private static void parseEntreprise(ResultSet result, OntModel model) throws SQLException{
		// Create common individual
		Individual entreprise = model.createIndividual(NS+result.getString("no_tva"), model.getOntClass(NS+"Entreprise"));
		// Add different properties
		parseHasLegalAdresseProperty(entreprise, model, result, "Entreprise");
		parseHasContactProperty(entreprise,model,result,"Entreprise");
		parseHasFormeJuri(entreprise,model,result);
		parseHasTVA(entreprise,model,result);
		parseIsEstablished(entreprise, model, result);
		parseIsSubjectTVA(entreprise,model,result);
		parseHasLanguageProperty(entreprise, model, result);
		parseHasLegalName(entreprise,model,result);
		parseHasDirector(entreprise, model, result);
	}
	
	private static void parseCafes(){}
	
	private static void parseRestaurants(){}
	
	
	private static void parseHasLegalAdresseProperty(Individual entrepriseInstance, OntModel model, ResultSet result, String owlClass) throws SQLException{
		String numeroField = owlClass.equals("Entreprise") ? "numero" : "numero_eta";
		String postalCodeField = owlClass.equals("Entreprise") ? "cp" : "cp_eta";
		String localiteField = owlClass.equals("Entreprise") ? "localite" : "localite_eta";
		String codeRueField = owlClass.equals("Entreprise") ? "coderue" : "coderue_eta";
		String streetAddressField = owlClass.equals("Entreprise") ? "rue" : "rue_eta";

		Individual address = model.createIndividual(NS+result.getString(numeroField)+", "+result.getString(streetAddressField), model.getOntClass(NS+"AdresseLegale"));
		address.addProperty(model.getProperty(VCARD+"street-address"),result.getString(numeroField)+", "+result.getString(streetAddressField))
				.addProperty(model.getProperty(VCARD+"country-name"), "Belgique")
				.addProperty(model.getProperty(VCARD+"postal-code"), result.getString(postalCodeField))
				.addProperty(model.getProperty(VCARD+"region"), "Namur")
				.addProperty(model.getProperty(VCARD+"locality"),result.getString(localiteField))
				.addProperty(model.getProperty(NS+"hasStreetCode"),result.getString(codeRueField));
		
		entrepriseInstance.addProperty(model.getProperty(NS+"hasAdresseLegale"), address);
	}
	
	private static void parseHasContactProperty(Individual entrepriseInstance, OntModel model, ResultSet result, String owlClass) throws SQLException{
		if (owlClass.equals("Entreprise")){
			entrepriseInstance.addProperty(model.getProperty(NS+"hasContact"),
											model.createResource(model.getResource(NS+"Contact"))
												.addProperty(model.getProperty(VCARD+"email"),result.getString("email")!=null ? result.getString("email") : "")
												.addProperty(model.getProperty(VCARD+"tel"), result.getString("tel")!=null ? result.getString("tel") : ""));
		} else{
			entrepriseInstance.addProperty(model.getProperty(NS+"hasContact"),
					model.createResource(model.getResource(NS+"Contact"))
						.addProperty(model.getProperty(VCARD+"email"), result.getString("email_eta")!=null ? result.getString("email_eta") : "")
						.addProperty(model.getProperty(VCARD+"tel"), result.getString("tel_eta")!=null ? result.getString("tel_eta") : ""));
		}
	}
	
	
	private static void parseHasFormeJuri(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		entrepriseInstance.addProperty(model.getProperty(NS+"hasFormeJuri"), 
										model.createResource(model.getResource(NS+"SA"))
												.addProperty(model.getProperty(NS+"label"), result.getString("fjur_lib2") )
												.addProperty(model.getProperty(NS+"nace"), result.getString("nacerev1"))
												.addProperty(model.getProperty(NS+"naceLabel"), result.getString("naclib"))
												.addProperty(model.getProperty(NS+"subscribed"),result.getString("insc_dat")));
	}
	
	/**
	 * Parse hasTVA property to an Enterprise Individual.
	 * hasTVA is a NoTVA property mapped to no_tva. It starts with "BE" followed by the TVA number
	 * 
	 * @param entrepriseInstance Enterprise Individual being instantiated
	 * @param model OntModel being populated
	 * @param result Database row
	 * @throws SQLException
	 */
	private static void parseHasTVA(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		entrepriseInstance.addProperty(model.getProperty(NS+"hasTVA"),
										model.createResource(model.getResource(NS+"NoTVA"))
												.addLiteral(model.getProperty(NS+"NoTVA"), "BE"+result.getString("no_tva")));
	}
	
	/**
	 * Parse isSubjectTVA to the Enterprise Individual.
	 * isSubjectTVA is a AssujTVA property mapped to assuj_tva. It is either "Yes" or "No"
	 * 
	 * @param entrepriseInstance Enterprise Individual being instantiated
	 * @param model OntModel being populated
	 * @param result Database row
	 * @throws SQLException
	 */
	private static void parseIsSubjectTVA(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		entrepriseInstance.addProperty(model.getProperty(NS+"isSubjectTVA"),
										model.createResource(model.getResource(NS+"AssujTVA"))
												.addLiteral(model.getProperty(NS+"AssujTVA"), result.getString("assuj_tva").equals("Y") ? "Yes" : "No"));
	}
	
	/**
	 * Parse hasLanguage property to an Individual instance
	 * hasLanguage is mapped to langue
	 * 
	 * @param entrepriseInstance Individual being instantiated
	 * @param model OntModel being populated
	 * @param result Database row
	 * @throws SQLException
	 */
	private static void parseHasLanguageProperty(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		entrepriseInstance.addProperty(model.getProperty(VCARD+"hasLanguage"), result.getString("langue").equals("1") ? "FR" : "Unknown...");
	}
	
	/**
	 * Parse a nomLegal property to a given Entreprise Individual.
	 * nomLegal is mapped to nom
	 * 
	 * @param entrepriseInstance Enterprise Individual being populated
	 * @param model OntModel being populated
	 * @param result Database row
	 * @throws SQLException
	 */
	private static void parseHasLegalName(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		entrepriseInstance.addProperty(model.getProperty(NS+"nomLegal"), result.getString("nom"));	
	}
	
	/**
	 * Parse an Etablissement Individual to the RDF and link it to an Entreprise Individual.
	 * An Etablissement has:
	 * 	- Contact infos (@see parseHasContactProperty)
	 *  - Legal Address (@see parseHasLegalAdresseProperty)
	 *  - nomLegal mapped to eta_nom
	 *  - subscribed mapped to insc_dat_eta 
	 *  
	 * @param entrepriseInstance Enterprise Individual to which the Etablissement is to be link.
	 * @param model OntModel being populated
	 * @param result Database row 
	 * @throws SQLException
	 */
	private static void parseIsEstablished(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		Individual etablissement = model.createIndividual(NS+result.getString("eta_nom"), model.getOntClass(NS+"Etablissement"));
		parseHasContactProperty(etablissement, model, result, "Etablissement");
		parseHasLegalAdresseProperty(etablissement, model, result, "Etablissement");
		etablissement.addProperty(model.getProperty(NS+"nomLegal"), result.getString("eta_nom"));
		etablissement.addProperty(model.getProperty(NS+"subscribed"),result.getString("insc_dat_eta"));
		entrepriseInstance.addProperty(model.getProperty(NS+"isEstablished"), etablissement);
	}
	
	/**
	 * Parse a Directeur Individual to the RDF.
	 * A Directeur extends a Personne and has:
	 * 	- hasLanguage mapped to dir_langue
	 *  - function mapped to dir_fonct_label
	 *  - genre mapped to dir_sexe
	 *  - nomDeFamille mapped to dir_nom
	 *  - prenom mapped to dir_prn
	 * 
	 * @param entrepriseInstance Enterprise Individual being populated 
	 * @param model RDF model being populated
	 * @param result Database row
	 * @throws SQLException if the specified field (column name) isn't found.
	 */
	private static void parseHasDirector(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		Individual directeur = model.createIndividual(NS+result.getString("dir_prn")+result.getString("dir_nom"), model.getOntClass(NS+"Directeur"));
		directeur.addProperty(model.getProperty(VCARD+"hasLanguage"), result.getString("dir_langue").equals("1") ? "FR" : "")
					.addProperty(model.getProperty(NS+"function"), result.getString("dir_fonct_lib"))
					.addProperty(model.getProperty(NS+"genre"), result.getString("dir_sexe").equals("1")? "Male" : "Female")
					.addProperty(model.getProperty(NS+"nomDeFamille"), result.getString("dir_nom"))
					.addProperty(model.getProperty(NS+"prenom"), result.getString("dir_prn"));
				
		entrepriseInstance.addProperty(model.getProperty(NS+"hasDirecteur"), directeur);
	}

	/**
	 * Export an OntModel to the specified file as RDF/XML. If no file name is specified, the output is redirected to System.out
	 * 
	 * @param model OntModel to export
	 * @param outputFileName Name of the file where to write.
	 * @throws IOException
	 */
	private static void exportModel(OntModel model, String outputFileName) throws IOException{
		FileWriter out = null;
		try {
			if(outputFileName!=null){
				out = new FileWriter( outputFileName );
				model.write( out, "RDF/XML" );
			} else{
				model.write(System.out, "RDF/XML" );	
			}
		}
		finally {
		   try {
		       out.close();
		   }
		   catch (IOException closeException) {}
		}
	}
}
