package be.unamur.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import be.unamur.servlets.Constants.AlertType;

public class Constants {
	public static final String VIEW_INDEX = "/WEB-INF/index.jsp";
	public static final String VIEW_REQUEST = "/WEB-INF/request.jsp";
	
	public static final String PATH_WS = "/rest";
	public static final String PATH_ROOT = "/";
	public static final String PATH_BOWER_COMPONENTS = "/bower_components";
	public static final String PATH_DIST = "/dist";
	
    public static final String CONTROLLER_INDEX = "/index";
	public static final String CONTROLLER_REQUEST = "/request";
	
	public static final String DAL_READ_FACTORY = "dalReadFactory";
	public static final String DAL_WRITE_FACTORY = "dalWriteFactory";
	
	public static final String SESSION_ALERT_MESSAGE = "sessionAlertMessage";
	public static final String SESSION_ALERT_TYPE = "sessionAlertType";

	public enum AlertType {
		ERROR("danger"), SUCCESS("success");

		private String name;

		AlertType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

	}

	public static void alert(HttpSession session, AlertType type, String message) {
		session.setAttribute(SESSION_ALERT_MESSAGE, message);
		session.setAttribute(SESSION_ALERT_TYPE, type.toString());
	}
	
	public static void alert(HttpServletRequest request, AlertType type, String message) {
		request.setAttribute(SESSION_ALERT_MESSAGE, message);
		request.setAttribute(SESSION_ALERT_TYPE, type.toString());
	}
	
}
