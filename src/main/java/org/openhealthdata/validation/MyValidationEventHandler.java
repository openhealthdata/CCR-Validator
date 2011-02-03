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

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;

import org.openhealthdata.validation.result.ErrorType;
import org.openhealthdata.validation.result.InFileLocation;
import org.openhealthdata.validation.result.TestResultType;

/**
 * This class is a JAXB ValidationEventHanler that create ErrorType errors
 * @author swaldren
 *
 */
public class MyValidationEventHandler implements javax.xml.bind.ValidationEventHandler {

	public MyValidationEventHandler() {}

	public StringBuffer errorCache = new StringBuffer();
	private List<ErrorType> tResult = new ArrayList<ErrorType>();
	
	public void resetErrorCache(){
		errorCache.delete(0,errorCache.length());
		tResult.clear();
	}
	
	public List<ErrorType> getErrors(){
		return tResult;
	}
	
	public void addError(ErrorType er){
		tResult.add(er);
	}
	public boolean handleEvent(ValidationEvent ve) {
		//will continue to pass validate even with errors
		//but will append errors to errorCache
		if (ve.getSeverity() != ValidationEvent.WARNING) {
			ValidationEventLocator vel = ve.getLocator();
			ErrorType er = new ErrorType();
			er.setServerity(TestResultType.FAILED);
			InFileLocation loc = new InFileLocation();
			loc.setColumnNumber(vel.getColumnNumber());
			loc.setLineNumber(vel.getLineNumber());
			er.setInFileLocation(loc);
			er.setMessage(ve.getMessage());
			tResult.add(er);
			
			errorCache.append("Line:Col[");
			errorCache.append(vel.getLineNumber());
			errorCache.append(":");
			errorCache.append(vel.getColumnNumber());
			errorCache.append("]:");
			errorCache.append(ve.getMessage());
			errorCache.append("\r\n");
			
			return true;
		}
		return true;
	}
}


