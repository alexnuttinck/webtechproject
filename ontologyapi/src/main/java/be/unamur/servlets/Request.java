package be.unamur.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.unamur.forms.RequestForm;

public class Request extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String ATT_RESULT = "result"; 

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.getServletContext().getRequestDispatcher(Constants.VIEW_REQUEST).forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RequestForm form = new RequestForm();
		String result = form.requestOWL(request);
		HttpSession session = request.getSession();
		
		if (form.isSuccessful()) {
			request.setAttribute(ATT_RESULT, result);
			this.getServletContext().getRequestDispatcher(Constants.VIEW_REQUEST).forward(request, response);
		}
		
		else
		{
		response.sendRedirect(request.getContextPath() + Constants.CONTROLLER_REQUEST);
		Constants.alert(session, Constants.AlertType.ERROR, "Requête SPARQL Erronée"); 
		}			
	}

}
