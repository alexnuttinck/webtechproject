package utils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.VCARD;

import ontology.EnterpriseModel;


public class Parser {

	private static final String NS = "http://www.semanticweb.org/Namur/entreprise-ontology#";
	private static final String VCARD = "http://www.w3.org/2006/vcard/ns#";

	
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
			ResultSet rs=stmt.executeQuery("select * from \"ELYX_DATA\".\"ECON_ENTREPRISES_GEO\"");
		
			int i =0;
			
			while(rs.next()) {
				
				//transformToRDF(rs, model);
				parseEntreprise(rs,model);
				i++;
				if(i>6) break;
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
	 * @param result
	 * @param model
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
	}
	
	private static void parseCafes(){}
	
	private static void parseRestaurants(){}
	
	
	private static void parseHasLegalAdresseProperty(Individual entrepriseInstance, OntModel model, ResultSet result, String owlClass) throws SQLException{
		if(owlClass.equals("Entreprise")){
			entrepriseInstance.addProperty(model.getProperty(NS+"hasAdresseLegale"), 
											model.createResource(model.getResource(NS+"AdresseLegale"))
																		.addProperty(model.getProperty(VCARD+"street-address"),result.getString("numero")+", "+result.getString("rue"))
																		.addProperty(model.getProperty(VCARD+"country-name"), "Belgique")
																		.addProperty(model.getProperty(VCARD+"postal-code"), result.getString("cp"))
																		.addProperty(model.getProperty(VCARD+"region"), "Namur")
																		.addProperty(model.getProperty(VCARD+"locality"),result.getString("localite"))
																		.addProperty(model.getProperty(NS+"hasStreetCode"),result.getString("coderue")));
		} else{
			entrepriseInstance.addProperty(model.getProperty(NS+"hasAdresseLegale"), 
					model.createResource(model.getResource(NS+"AdresseLegale"))
												.addProperty(model.getProperty(VCARD+"street-address"),result.getString("numero_eta")+", "+result.getString("rue_eta"))
												.addProperty(model.getProperty(VCARD+"country-name"), "Belgique")
												.addProperty(model.getProperty(VCARD+"postal-code"), result.getString("cp_eta"))
												.addProperty(model.getProperty(VCARD+"region"), "Namur")
												.addProperty(model.getProperty(VCARD+"locality"),result.getString("localite_eta"))
												.addProperty(model.getProperty(NS+"hasStreetCode"),result.getString("coderue_eta")));			
		}
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
	
	private static void parseHasTVA(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		entrepriseInstance.addProperty(model.getProperty(NS+"hasTVA"),
										model.createResource(model.getResource(NS+"NoTVA"))
												.addLiteral(model.getProperty(NS+"NoTVA"), "BE"+result.getString("no_tva")));
	}
	
	private static void parseIsSubjectTVA(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		entrepriseInstance.addProperty(model.getProperty(NS+"isSubjectTVA"),
										model.createResource(model.getResource(NS+"AssujTVA"))
												.addLiteral(model.getProperty(NS+"AssujTVA"), result.getString("assuj_tva").equals("Y") ? "Yes" : "No"));
	}
	
	private static void parseHasLanguageProperty(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		entrepriseInstance.addProperty(model.getProperty(VCARD+"hasLanguage"), result.getString("langue").equals("1") ? "FR" : "Unknown...");
	}
	
	private static void parseHasLegalName(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		entrepriseInstance.addProperty(model.getProperty(NS+"nomLegal"), result.getString("nom"));	
	}
	
	private static void parseIsEstablished(Individual entrepriseInstance, OntModel model, ResultSet result) throws SQLException{
		Individual etablissement = model.createIndividual(NS+result.getString("eta_nom"), model.getOntClass(NS+"Etablissement"));
		parseHasContactProperty(etablissement, model, result, "Etablissement");
		parseHasLegalAdresseProperty(etablissement, model, result, "Etablissement");
		etablissement.addProperty(model.getProperty(NS+"nomLegal"), result.getString("eta_nom"));
		etablissement.addProperty(model.getProperty(NS+"subscribed"),result.getString("insc_dat_eta"));
		entrepriseInstance.addProperty(model.getProperty(NS+"isEstablished"), etablissement);
	}
	

	private static void exportModel(OntModel model, String outputFileName) throws IOException{
		FileWriter out = new FileWriter( outputFileName );
		try {
		    //model.write( out, "RDF/XML" );
			model.write(System.out, "RDF/XML" );
		}
		finally {
		   try {
		       out.close();
		   }
		   catch (IOException closeException) {}
		}
	}
}
