/*
 * Copyright 2011 OpenHealthData, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openhealthdata.validation.result;

import java.util.List;
import static org.openhealthdata.validation.result.BaseValidationManager.getUpdatedStatus;

/**
 * Provides methods for adding an <code>ErrorType</code> object to
 * the list in a <code>TestResultType</code> object.
 * 
 * @author wlaun
 */
public class TestResultWrapper extends TestResultType {

	/**
	 * Constructor.
	 */
	public TestResultWrapper(){
		super();
	}

	/**
	 * Add a fatal <code>ErrorType</code> with the given message.
	 * @param message the message
	 * @return the ErrorType object
	 */
	public ErrorType addFatal(String message){
		return this.addError(message, ErrorType.FATAL );
	}
	
	/**
	 * Add a warning <code>ErrorType</code> with the given message.
	 * @param message the message
	 * @return the ErrorType object
	 */
	public ErrorType addWarning(String message){
		return this.addError(message, ErrorType.WARN );
	}
	
	/**
	 * Add an <code>ErrorType</code> with the given message and severity.
	 * @param message the message
	 * @param severity the severity
	 * @return the ErrorType object
	 */
	public ErrorType addError(String message, String severity) {
		// Check to see if it is a fatal error and set result status
		if (severity.equals(ErrorType.FATAL)) {
			BaseValidationManager.getInstance().setStatus(ValidationResult.FAILED);
		}
		ErrorType e = new ErrorType();
		e.setMessage(message);		
		e.setSeverity(severity);
		getError().add(e);
		setStatus(getUpdatedStatus(getStatus(), severity));
		return e;
	}

	/**
	 * Add an <code>ErrorType</code> with the given message, severity and XPath.
	 * @param message the message
	 * @param severity the severity
	 * @param xpath a String containint an XPath
	 * @return the ErrorType object
	 */
	public ErrorType addError( String message, String severity, String xpath) {
		ErrorType e = addError( message, severity );
        e.setXPathLocation(xpath);
        return e;
	}
	
	/**
	 * Add an elsewhere created <code>ErrorType</code>, while updating
	 * the summary status.
	 * @param error the <code>ErrorType</code> to be added.
	 */
	public void addError(ErrorType error) {
		setStatus(getUpdatedStatus(getStatus(), error.getSeverity()));
		getError().add(error);
	}

	/**
	 * Add elsewhere created <code>ErrorType</code> objects, while updating
	 * the summary status.
	 * @param errorTypes the list of <code>ErrorType</code> objects to be added.
	 */
	public void addError(List<ErrorType> errorTypes) {
		for (ErrorType e : errorTypes) {
			setStatus(getUpdatedStatus(getStatus(), e.getSeverity()));
			getError().add(e);
		}
	}

	/**
	 * Add an <code>ErrorType</code> with the given message, severity, line and
	 * column number.
	 * @param message the message 
	 * @param severity the severity
	 * @param lineNumber the line number 
	 * @param columnNumber the column number
	 */
	public void addError(String message, String severity,
			int lineNumber, int columnNumber) {
		// Check to see if it is a fatal error and set result status
		if (severity.equals(ErrorType.FATAL)) {
			BaseValidationManager.getInstance().setStatus(ValidationResult.FAILED);
		}
		
		ErrorType e = new ErrorType();
		InFileLocation loc = new InFileLocation();
		loc.setColumnNumber(columnNumber);
		loc.setLineNumber(lineNumber);
		e.setInFileLocation(loc);
		e.setSeverity(severity);
		e.setMessage(message);
		if (getStatus() != null) {
			setStatus(severity);
			getError().add(e);
		} else {
			setStatus(getUpdatedStatus(getStatus(), severity));
			getError().add(e);
		}
	}
}
