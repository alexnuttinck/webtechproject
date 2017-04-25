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
			<div class="col-6">


				<img src="images/logo_namur_ville.jpg" class="img-rounded"
					width="209" height="403" alt="Logo Ville de Namur">
				<h2>Ville De Namur API</h2>
				<p>Type your request below</p>
				<form role="form" method="post" action="<c:url value="/request"/>">
					<div class="form-group">
						<label for="requestSparql">SPARQL Request :</label> <input
							type="text" class="form-control" name="requestSparql">
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
						</select>
					</div>

					<button type="submit" id="SubmitRequestButton"
						class="btn btn-primary">
						Send <span class="glyphicon glyphicon-send"></span>
					</button>
				</form>

			</div>

			<div class="col-6">
				<label for="result">SPARQL Result :</label>
				<div class="form-group">
					<c:out value="${ result }" />
				</div>
			</div>
		</div>
	</div>
</body>
</html>
