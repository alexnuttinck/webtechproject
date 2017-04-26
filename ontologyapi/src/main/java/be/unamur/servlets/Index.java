package be.unamur.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import be.unamur.forms.RequestForm;
import be.unamur.forms.Form;

public class Index extends HttpServlet {
	
	public static final String ATT_RESULT = "result"; 
	public static final String ATT_TYPE = "type"; 

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.getServletContext().getRequestDispatcher(Constants.VIEW_INDEX).forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RequestForm form = new RequestForm();
		String result = form.requestOWL(request);
		String type = Form.getValueField(request, Form.TYPE);
		HttpSession session = request.getSession();
		
		if (form.isSuccessful()) {
			
			request.setAttribute(ATT_RESULT, result);
			request.setAttribute(ATT_TYPE, type);
			this.getServletContext().getRequestDispatcher(Constants.VIEW_INDEX).forward(request, response);
		}
		
		else
		{
		response.sendRedirect(request.getContextPath() + Constants.CONTROLLER_INDEX);
		Constants.alert(session, Constants.AlertType.ERROR, "Requête SPARQL Erronée"); 
		}			
	}
}
