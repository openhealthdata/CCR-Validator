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

import org.w3c.dom.Document;
import java.util.List;
import org.openhealthdata.validation.result.ErrorType;

/**
 * Abstract class for creating an XSD validator
 * @author swaldren
 *
 */
public abstract class SchemaValidator {
	protected String name;

	/**
	 * Return <code>true</code> if the XML instance is valid, otherwise 
	 * return <code>false</code>
	 * 
	 * @param xml XML instance to be validated
	 * @return
	 */
	public abstract boolean isValid(Document xml);
	
	/**
	 * Return a list of errors during the validation
	 * @return
	 */
	public abstract List<ErrorType> getErrors();
	
	/**
	 * Give the schema validator a name
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Get the name of the schema validator
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get the JAXB object that is the root node of the XML instance validated
	 * 
	 * @return return <code>null</code> is invalid XML instance
	 */
	public abstract Object getValidRootObject();
}
