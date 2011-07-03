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
package org.openhealthdata.validator.tree;

/**
 * Represents a wrapper for an XML element inserted as a fact.
 * @author wlaun
 */
public class Element {
	private static int counter = 0;
	
    private Xpath  xpath;
    private Object object;
    private int    ordinal;

    /**
     * Constructor.
     * @param xpath the Xpath object denoting the XML element
     * @param object the fact object
     */
    public Element( Xpath xpath, Object object ){
        this.xpath   = xpath;
        this.object  = object;
        this.ordinal = ++counter;
    }
    
    /**
     * Returns the Xpath denoting the XML element
     * @return an Xpath object
     */
    public Xpath getXpath() {
        return xpath;
    }

    /**
     * Returns the fact object resulting from the XML element.
     * @return an Object
     */
    public Object getObject() {
        return object;
    }
    
    /**
     * Returns the element's ordinal, a numbering in document order;
     * @return an int value
     */
    public int getOrdinal(){
    	return ordinal;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        return xpath.toString() + "(" + ordinal + ") => " + object.getClass().getSimpleName(); 
    }
}
