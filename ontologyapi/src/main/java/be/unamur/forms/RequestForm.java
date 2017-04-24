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
		
		if (type=="json")
		{
			result = RequestHandle.toJson(requestSparql);
		}
		else 
		{
			result = RequestHandle.toXML(requestSparql);
		}
		
		return result;
	}

}
