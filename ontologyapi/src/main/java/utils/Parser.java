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
import org.apache.jena.rdf.model.Resource;

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
	// SQL select queries
	private static final String SELECT_ENTREPRISES = "select * from \"ELYX_DATA\".\"ECON_ENTREPRISES_GEO\" "
													+ "where no_tva not in ( (select matricule from \"ELYX_DATA\".\"GLH_CAFE_GEO\" where matricule is not null) "
													+ "union (select matricule from \"ELYX_DATA\".\"GLH_RESTAURANT_GEO\" where matricule is not null))";

	private static final String SELECT_CAFES = "select * from \"ELYX_DATA\".\"GLH_CAFE_GEO\" cafe, \"ELYX_DATA\".\"ECON_ENTREPRISES_GEO\" entre "
												+ "where cafe.matricule = entre.no_tva";
	
	private static final String SELECT_RESTAURANTS = "select * from \"ELYX_DATA\".\"GLH_RESTAURANT_GEO\" resto, \"ELYX_DATA\".\"ECON_ENTREPRISES_GEO\" entre "
													+ "where resto.matricule = entre.no_tva";
	
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
		parseCafes(model);
		parseRestaurants(model);
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
			ResultSet rs=stmt.executeQuery(SELECT_ENTREPRISES);
	
			while(rs.next()) {
				Individual entreprise = model.createIndividual(NS+rs.getString("no_tva"), model.getOntClass(NS+"Entreprise"));
				parseEntreprise(rs,model, entreprise);
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
	private static void parseEntreprise(ResultSet result, OntModel model, Individual entreprise) throws SQLException{
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
	
	private static void parseCafes(OntModel model){
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver");    
			Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:technoweb","test","test");  
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery(SELECT_CAFES);
	
			while(rs.next()) {
				Individual cafe = model.createIndividual(NS+rs.getString("no_tva"), model.getOntClass(NS+"Cafe"));
				parseCafe(rs,model,cafe);
			}
			con.close();  
		} catch(Exception e){
			System.err.println("An error occured while extracting from the database...\n"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void parseCafe(ResultSet result, OntModel model, Individual cafe) throws SQLException{
		// Parse Enterprise related properties
		parseEntreprise(result, model, cafe);
		// Parse Cafe specific properties
		parseHasZoneLocal(result, model, cafe);
		parseHasTerr(result,model,cafe);
		parseHasLicense(result,model,cafe);
	}
	
	private static void parseRestaurants(OntModel model){
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver");    
			Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:technoweb","test","test");  
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery(SELECT_RESTAURANTS);

			while(rs.next()) {
				Individual restaurant = model.createIndividual(NS+rs.getString("no_tva"), model.getOntClass(NS+"Restaurant"));
				parseRestaurant(rs,model, restaurant);
			}
			con.close();  
		} catch(Exception e){
			System.err.println("An error occured while extracting from the database...\n"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void parseRestaurant(ResultSet result, OntModel model, Individual restaurant) throws SQLException{
		// Parse Enterprise related properties
		parseEntreprise(result, model, restaurant);
		// Parse restaurant specific properties
		parseHasZoneLocal(result, model, restaurant);
		parseHasTerr(result, model, restaurant);
		parseHasLicense(result,model,restaurant);
	}
	
	private static void parseHasZoneLocal(ResultSet result, OntModel model, Individual cafe) throws SQLException{
		// If all null then skip
		if( (result.getString("zone")==null || result.getString("zone").equals("null"))
			&& (result.getString("slocalite")==null || result.getString("slocalite").equals("null")) ) return;
		
		Resource resource = model.createResource();
		
		if((result.getString("zone")!=null && !result.getString("zone").equals("null"))) 
				resource.addLiteral(model.getProperty(NS+"Zone"), result.getString("zone"));
		
		if((result.getString("slocalite")!=null && !result.getString("slocalite").equals("null")))
				resource.addLiteral(model.getProperty(NS+"sLocality"), result.getString("slocalite"));
		
		cafe.addProperty(model.getProperty(NS+"hasZoneLocal"), resource);
	}
	
	private static void parseHasTerr(ResultSet result, OntModel model, Individual cafe) throws SQLException{
		if (result.getString("type_terrasse")==null || result.getString("type_terrasse").equals("null")) return;
		
		cafe.addProperty(model.getProperty(NS+"hasTerr"), 
							model.createResource(model.getResource(NS+"Terrace")
									.addProperty(model.getProperty(NS+"terraceType"), result.getString("type_terrasse"))));
	}
	
	private static void parseHasLicense(ResultSet result, OntModel model, Individual cafe) throws SQLException{
		if ( (result.getString("date_ferme")==null || result.getString("date_ferme").equals("null"))
			 && (result.getString("date_octroi")==null || result.getString("date_octroi").equals("null"))
			 && (result.getString("date_ouvert")==null || result.getString("date_ouvert").equals("null"))
			) return;
		
		Resource resource = model.createResource(model.getResource(NS+"License"));
		
		if (result.getString("date_ferme")!=null && !result.getString("date_ferme").equals("null"))
				resource.addProperty(model.getProperty(NS+"close"), result.getString("date_ferme"));
		
		if ((result.getString("date_octroi")!=null && !result.getString("date_octroi").equals("null")))
				resource.addProperty(model.getProperty(NS+"octroiLicense"), result.getString("date_octroi"));
		
		if (result.getString("date_ouvert")!=null && !result.getString("date_ouvert").equals("null"))
				resource.addProperty(model.getProperty(NS+"open"), result.getString("date_ouvert"));
		
		cafe.addProperty(model.getProperty(NS+"hasLicense"), resource);
	}
	
	
	private static void parseHasLegalAdresseProperty(Individual entrepriseInstance, OntModel model, ResultSet result, String owlClass) throws SQLException{
		String numeroField = owlClass.equals("Entreprise") ? "numero" : "numero_eta";
		String postalCodeField = owlClass.equals("Entreprise") ? "cp" : "cp_eta";
		String localiteField = owlClass.equals("Entreprise") ? "localite" : "localite_eta";
		String codeRueField = owlClass.equals("Entreprise") ? "coderue" : "coderue_eta";
		String streetAddressField = owlClass.equals("Entreprise") ? "rue" : "rue_eta";

		if ( (result.getString(numeroField)==null || result.getString(numeroField).equals("null"))
				|| (result.getString(streetAddressField)==null || result.getString(streetAddressField).equals("null"))
				)
			return;
		
		if ( (result.getString(numeroField)==null || result.getString(numeroField).equals("null"))
			&& (result.getString(streetAddressField)==null || result.getString(streetAddressField).equals("null"))
			&& (result.getString(postalCodeField)==null || result.getString(postalCodeField).equals("null"))
			&& (result.getString(localiteField)==null || result.getString(localiteField).equals("null"))
			&& (result.getString(codeRueField)==null || result.getString(codeRueField).equals("null"))
			)
			return;
		
		
		Individual address = model.createIndividual(NS+result.getString(numeroField)+", "+result.getString(streetAddressField), model.getOntClass(NS+"AdresseLegale"));
		address.addProperty(model.getProperty(VCARD+"street-address"),result.getString(numeroField)+", "+result.getString(streetAddressField));
		address.addProperty(model.getProperty(VCARD+"country-name"), "Belgique");
		address.addProperty(model.getProperty(VCARD+"region"), "Namur");
		
		if(result.getString(postalCodeField)!=null && !result.getString(postalCodeField).equals("null"))
			address.addProperty(model.getProperty(VCARD+"postal-code"), result.getString(postalCodeField));

		if(result.getString(localiteField)!=null && !result.getString(localiteField).equals("null"))
			address.addProperty(model.getProperty(VCARD+"locality"),result.getString(localiteField));
		
		if(result.getString(codeRueField)!=null && !result.getString(codeRueField).equals("null"))
				address.addProperty(model.getProperty(NS+"hasStreetCode"),result.getString(codeRueField));
	
		
		entrepriseInstance.addProperty(model.getProperty(NS+"hasAdresseLegale"), address);
	}
	
	private static void parseHasContactProperty(Individual entrepriseInstance, OntModel model, ResultSet result, String owlClass) throws SQLException{
		
		if( 	( (result.getString("email")==null || result.getString("email").equals("null"))
					&& (result.getString("tel")==null || result.getString("tel").equals("null")) )
				|| ( (result.getString("email_eta")==null || result.getString("email_eta").equals("null"))
						&& (result.getString("tel_eta")==null || result.getString("tel_eta").equals("null")))
			)
			return;
		
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
		if (result.getString("fjur_lib1")==null || result.getString("fjur_lib1").equals("null")) return;
		
		//Resource resource = model.createResource(model.getResource(NS+result.getString("fjur_lib1")));
		Resource resource = model.createResource();
		
		if (result.getString("fjur_lib2")!=null && !result.getString("fjur_lib2").equals("null"))
			resource.addProperty(model.getProperty(NS+"label"), result.getString("fjur_lib2"));
		
		if(result.getString("nacerev1")!=null && !result.getString("nacerev1").equals("null"))
			resource.addProperty(model.getProperty(NS+"nace"), result.getString("nacerev1"));
		
		if( result.getString("naclib")!=null && !result.getString("naclib").equals("null"))
			resource.addProperty(model.getProperty(NS+"naceLabel"), result.getString("naclib"));
		
		if(result.getString("insc_dat")!=null && !result.getString("insc_dat").equals("null"))
			resource.addProperty(model.getProperty(NS+"subscribed"),result.getString("insc_dat"));
			
		entrepriseInstance.addProperty(model.getProperty(NS+"hasFormeJuri"), resource);
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
		if (result.getString("eta_nom")==null || result.getString("eta_nom").equals("null"))
			return;
		
		Individual etablissement = model.createIndividual(NS+result.getString("eta_nom"), model.getOntClass(NS+"Etablissement"));
		parseHasContactProperty(etablissement, model, result, "Etablissement");
		parseHasLegalAdresseProperty(etablissement, model, result, "Etablissement");
		if (result.getString("eta_nom")!=null && !result.getString("eta_nom").equals("null"))
			etablissement.addProperty(model.getProperty(NS+"nomLegal"), result.getString("eta_nom"));
		if (result.getString("insc_dat_eta")!=null && !result.getString("insc_dat_eta").equals("null"))
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
		
		if (result.getString("dir_langue")!=null && !result.getString("dir_langue").equals("null"))
			directeur.addProperty(model.getProperty(VCARD+"hasLanguage"), result.getString("dir_langue").equals("1") ? "FR" : "");
		
		if (result.getString("dir_fonct_lib")!=null && !result.getString("dir_fonct_lib").equals("null"))
			directeur.addProperty(model.getProperty(NS+"function"), result.getString("dir_fonct_lib"));
		
		if ( result.getString("dir_sexe")!=null && !result.getString("dir_sexe").equals("null"))
			directeur.addProperty(model.getProperty(NS+"genre"), result.getString("dir_sexe").equals("1")? "Male" : "Female");
					
		if (result.getString("dir_nom")!=null && !result.getString("dir_nom").equals("null"))
			directeur.addProperty(model.getProperty(NS+"nomDeFamille"), result.getString("dir_nom"));
					
		if (result.getString("dir_prn")!=null && !result.getString("dir_prn").equals("null"))
			directeur.addProperty(model.getProperty(NS+"prenom"), result.getString("dir_prn"));
				
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
