package be.unamur.forms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class Form {

	// Field names
	public static final String REQUESTSPARQL = "requestSparql";
	public static final String TYPE = "type";
	// End field names

	private boolean isSuccessful;
	private Map<String, String> errors;

	public Form() {
		isSuccessful = false;
		errors = new HashMap<String, String>();
	}

	/**
	 * 
	 * @return A map of the errors encountered during the signUp method with the
	 *         corresponding invalid fields
	 */
	public Map<String, String> getErrors() {
		return errors;
	}

	/**
	 * 
	 * @return Returns whether or not the signUp method was successful
	 */
	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setSuccessful(boolean success) {
		this.isSuccessful = success;
	}

	protected void setError(String field, String message) {
		errors.put(field, message);
	}

	protected String getValueField(HttpServletRequest request, String field) {
		String value = request.getParameter(field);
		if (value == null || value.trim().length() == 0) {
			return null;
		} else {
			return value.trim();
		}
	}

	protected boolean hasErrors() {
		return !errors.isEmpty();
	}
}
