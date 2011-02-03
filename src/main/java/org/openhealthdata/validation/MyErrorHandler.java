/*
 * Copyright 2010 OpenHealthData, Inc.
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
package org.openhealthdata.validation;

import java.util.ArrayList;
import java.util.List;

import org.openhealthdata.validation.result.ErrorType;
import org.openhealthdata.validation.result.InFileLocation;
import org.openhealthdata.validation.result.TestResultType;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MyErrorHandler implements ErrorHandler {
	
	private List<ErrorType> tResult = new ArrayList<ErrorType>();
	
	/**
	 * Resets the internal error cache. This should be called between
	 * validations.
	 */
	public void resetErrorCache(){
		tResult.clear();
	}
	
	/**
	 * Return the list of errors from the last validation.
	 * @return
	 */
	public List<ErrorType> getErrors(){
		return tResult;
	}
	
	/**
	 * Add an Error to the error cache
	 * @param er
	 */
	public void addError(ErrorType er){
		tResult.add(er);
	}

	/**
	 * Handles SAX error and add to Error cache
	 */
	public void error(SAXParseException ex) throws SAXException {
		handleError(ex);
	}
	
	/**
	 * Handles SAX fatal error and add to Error cache
	 */
	public void fatalError(SAXParseException ex) throws SAXException {
		handleError(ex);
	}

	/**
	 * Handles SAX warning error and add to Error cache
	 */
	public void warning(SAXParseException ex) throws SAXException {
		handleError(ex);
	}
	
	/*
	 * Creates a new ErrorType from the SAX Exception
	 */
	private boolean handleError(SAXParseException ex){
			ErrorType er = new ErrorType();
			er.setServerity(ErrorType.FATAL);
			InFileLocation loc = new InFileLocation();
			loc.setColumnNumber(ex.getColumnNumber());
			loc.setLineNumber(ex.getLineNumber());
			er.setInFileLocation(loc);
			er.setMessage(ex.getMessage());
			tResult.add(er);
		return true;
	}

}
