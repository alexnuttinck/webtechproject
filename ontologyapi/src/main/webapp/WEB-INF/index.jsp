<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

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
		<h2>Ville De Namur API</h2>
		<p>Type your request below</p>
		<form role="form" method="post" action="<c:url value="/index"/>">
			<div class="form-group">
				<label for="requestSparql">SPARQL Request :</label> <input type="text"
					class="form-control" name ="requestSparql">
			</div>

			<div class="form-group">
				<label for="type">TYPE Result :</label> <select class="form-control" name ="type">
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

</body>
</html>
