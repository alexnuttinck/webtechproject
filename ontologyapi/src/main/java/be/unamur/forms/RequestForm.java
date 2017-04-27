package be.unamur.forms;

import javax.servlet.http.HttpServletRequest;

import be.unamur.requestSPARQL.RequestHandle;;

public class RequestForm extends Form {

	/**
	 * @param request
	 *            The HTTP request object from the servlet which contains the
	 *            necessary fields to parse sparqle request
	 *            
	 * @return The result from spraqle request
	 */
	public String requestOWL(HttpServletRequest request) {
		String requestSparql = getValueField(request, REQUESTSPARQL);
		String type = getValueField(request, TYPE);
		String result="";
		
		if(requestSparql == null)
		{
			setError(REQUESTSPARQL, "is null");
		}else
		{

			if(!requestSparql.matches(".*SELECT.*"))
			{
				setError(REQUESTSPARQL, "not select word");
			}

			if (type.equals("json"))
			{
				try {result = RequestHandle.toJson(requestSparql);}
				catch (Exception e)
				{	
					System.out.println(e.getMessage());
					setError(REQUESTSPARQL, "spraql request error");}
			}
			else 
			{
				try{result = RequestHandle.toXML(requestSparql);}
				catch (Exception e)
				{	System.out.println(e.getMessage());
					setError(REQUESTSPARQL, "spraql request error");}
			}
		}


		if (!hasErrors()) {
			setSuccessful(true);
		}

		return result;
	}
}
