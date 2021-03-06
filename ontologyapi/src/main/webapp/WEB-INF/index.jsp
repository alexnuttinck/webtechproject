<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
<head>
<title>VilleDeNamurAPI</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>

	<div class="container">
		<div class="row">
			<div class="col-md-2">
			<br>
				<img class="img-responsive" src="images/logo_namur_ville.jpg"
					class="img-rounded" width="209" height="403"
					alt="Logo Ville de Namur">
			</div>
			<div class="col-md-4">
				<h2>Ville De Namur API</h2>
				<form class="form-horizontal" role="form" method="post"
					action="<c:url value="/index"/>">
					<fieldset>
						<legend>SPARQL Request</legend>
						<p>Type your request below</p>
						<div class="form-group">
							<textarea style="resize: vertical;" class="form-control" rows="5"
								name="requestSparql"><c:out
									value="${ requestSparql }" /></textarea>
						</div>
						<c:if test="${ !empty sessionAlertMessage }">
							<div
								class="alert alert-${sessionAlertType} }warning alert-dismissible"
								role="alert">
								<button type="button" class="close" data-dismiss="alert"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								${sessionAlertMessage}
							</div>
							<c:remove var="sessionAlertMessage" />
							<c:remove var="sessionAlertType" />
						</c:if>

						<div class="form-group">
							<label for="type">TYPE Result :</label> <select
								class="form-control" name="type">
								<option>json</option>
								<option>xml</option>
								<option>csv</option>
								<option>csv-table</option>
								<option>rdf/xml</option>
							</select>
						</div>

						<button type="submit" id="SubmitRequestButton"
							class="btn btn-primary">
							Send <span class="glyphicon glyphicon-send"></span>
						</button>
					</fieldset>
				</form>

				<br> <br>
				<div class="row">
					<form class="col-md-4" action="index" method="post"
						action="<c:url value="/index"/>">
						<input type="hidden" name="requestSparql"
							value="PREFIX entreprise:  &lt;http://www.semanticweb.org/Namur/entreprise-ontology#&gt;
SELECT DISTINCT ?nomLegal ?NoTVA
WHERE {
    ?Entreprise entreprise:nomLegal ?nomLegal.
      ?Entreprise entreprise:hasTVA ?tva.
      ?tva entreprise:NoTVA ?NoTVA
}" />
						<input type="submit" class="btn btn-info" value="Example1"
							name="requestSparql" id="SubmitRequestButton" />
					</form>
					<form class="col-md-4" action="index" method="post"
						action="<c:url value="/index"/>">
						<input type="hidden" name="requestSparql"
							value="PREFIX entreprise:  &lt;http://www.semanticweb.org/Namur/entreprise-ontology#&gt;
PREFIX vcard:  &lt;http://www.w3.org/2006/vcard/ns#&gt;
SELECT DISTINCT ?nomLegal ?streetAddress ?postalcode ?locality 
WHERE {
	?Entreprise entreprise:nomLegal ?nomLegal.
     ?Entreprise entreprise:hasAdresseLegale ?adresse.
     ?adresse vcard:street-address ?streetAddress.
   	?adresse vcard:postal-code ?postalcode.
    ?adresse vcard:locality ?locality. 
}" />
						<input type="submit" class="btn btn-info" value="Example2"
							name="requestSparql" id="SubmitRequestButton" />
					</form>
					<form class="col-md-4" action="index" method="post"
						action="<c:url value="/index"/>">
						<input type="hidden" name="requestSparql"
							value="PREFIX entreprise:  &lt;http://www.semanticweb.org/Namur/entreprise-ontology#&gt;
SELECT DISTINCT ?nomLegal ?nom ?prenom
WHERE {
	?Entreprise entreprise:nomLegal ?nomLegal.
    ?Entreprise entreprise:hasDirecteur ?directeur .
?directeur entreprise:nomDeFamille ?nom .
?directeur entreprise:prenom ?prenom.
}" />
						<input type="submit" class="btn btn-info" value="Example3"
							name="requestSparql" id="SubmitRequestButton" />
					</form>
				</div>

				<br> <br>
				<form class="form-horizontal" role="form" method="post"
					action="<c:url value="/index"/>">
					<fieldset>
						<legend>French Request</legend>
						<div class="form-group">
							<select class="form-control" name="requestSparql">
								<option
									value="PREFIX entreprise:  &lt;http://www.semanticweb.org/Namur/entreprise-ontology#&gt;
SELECT DISTINCT ?nomLegal ?NoTVA
WHERE {
    ?Entreprise entreprise:nomLegal ?nomLegal.
      ?Entreprise entreprise:hasTVA ?tva.
      ?tva entreprise:NoTVA ?NoTVA
}">Ensemble
									des num&eacute;ros de TVA des entreprises</option>
								<option
									value="PREFIX entreprise:  &lt;http://www.semanticweb.org/Namur/entreprise-ontology#&gt;
PREFIX vcard:  &lt;http://www.w3.org/2006/vcard/ns#&gt;
SELECT DISTINCT ?nomLegal ?streetAddress ?postalcode ?locality 
WHERE {
	?Entreprise entreprise:nomLegal ?nomLegal.
     ?Entreprise entreprise:hasAdresseLegale ?adresse.
     ?adresse vcard:street-address ?streetAddress.
   	?adresse vcard:postal-code ?postalcode.
    ?adresse vcard:locality ?locality. 
}">Ensemble
									des noms d'entreprises avec leurs adresses respectives</option>
								<option
									value="PREFIX entreprise:  &lt;http://www.semanticweb.org/Namur/entreprise-ontology#&gt;
SELECT DISTINCT ?nomLegal ?nom ?prenom
WHERE {
	?Entreprise entreprise:nomLegal ?nomLegal.
    ?Entreprise entreprise:hasDirecteur ?directeur .
?directeur entreprise:nomDeFamille ?nom .
?directeur entreprise:prenom ?prenom.
}">Ensemble
									des noms et pr&eacute;noms des directeurs d'entreprise</option>
							</select>
						</div>

						<div class="form-group">
							<label for="type">TYPE Result :</label> <select
								class="form-control" name="type">
								<option>json</option>
								<option>xml</option>
								<option>csv</option>
								<option>csv-table</option>
								<option>rdf/xml</option>
							</select>
						</div>

						<button type="submit" id="SubmitRequestButton"
							class="btn btn-primary">
							Send <span class="glyphicon glyphicon-send"></span>
						</button>
					</fieldset>
				</form>

			</div>
	
			<div class="col-md-6" style="padding-right: 0;margin-right: 0;" >
			<br>
				<c:if test="${ !empty result }">
				<div>
					<legend class="col-xm-1">SPARQL Result</legend>
					<br>
					<div class="form-group">
						<c:if test="${type eq 'xml'}">
							<!-- format xml -->
							<pre
								style="height: auto; max-height: 500px; overflow: auto; background-color: #eeeeee; word-break: normal !important; word-wrap: normal !important; white-space: pre !important; white-space: pre-wrap;"><c:out value="${ result }" />
							</pre>
						</c:if>
						<c:if test="${type eq 'json'}">
							<pre
								style="height: auto; max-height: 500px; overflow: auto; background-color: #eeeeee; word-break: normal !important; word-wrap: normal !important; white-space: pre !important; white-space: pre-wrap;"><c:out value="${ result }" />
						</pre>
						</c:if>
						<c:if test="${type eq 'csv'}">
							<pre
								style="height: auto; max-height: 500px; overflow: auto; background-color: #eeeeee; word-break: normal !important; word-wrap: normal !important; white-space: pre !important; white-space: pre-wrap;"><c:out value="${ result }" />
						</pre>
						</c:if>
						<c:if test="${type eq 'csv-table'}">
							<pre
								style="height: auto; max-height: 500px; overflow: auto; background-color: #eeeeee; word-break: normal !important; word-wrap: normal !important; white-space: pre !important; white-space: pre-wrap;"><c:out value="${ result }" />
						</pre>
						</c:if>
						<c:if test="${type eq 'rdf/xml'}">
							<pre
								style="height: auto; max-height: 500px; overflow: auto; background-color: #eeeeee; word-break: normal !important; word-wrap: normal !important; white-space: pre !important; white-space: pre-wrap;"><c:out value="${ result }" />
						</pre>
						</c:if>
					</div>
					</div>
				</c:if>
			</div>
		</div>
	</div>
</body>

<!-- Bootstrap Core CSS -->
<link
	href="${pageContext.request.contextPath}/dist/css/bootstrap.min.css"
	rel="stylesheet">
</html>

