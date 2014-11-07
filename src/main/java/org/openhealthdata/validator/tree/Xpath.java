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
 * Represents a section of an XPath to an XML element.
 * 
 * @author wlaun
 *
 */
public class Xpath {

    public static final Xpath ROOT = new Xpath();
    
    private Xpath   parent;
    private String  name;
    private int     index = -1;
    
    private Xpath(){
        this( null, "" );
    }
    
    /**
     * Constructor for a non-repeating element.
     * @param parent the Xpath to the parent.
     * @param name the element name
     */
    public Xpath( Xpath parent, String name ){
        this.parent = parent;
        this.name   = name;
    }
    
    /**
     * Constructor for a repeating element.
     * @param parent the Xpath to the parent.
     * @param name the element name
     * @param index the index
     */
    public Xpath( Xpath parent, String name, int index ){
        this( parent, name );
        this.index  = index;
    }

    /**
     * Return the Xpath for a document root,  
     * @return an Xpath object
     */
    public static Xpath getROOT() {
        return ROOT;
    }

    /**
     * Returns the Xpath object to this Xpath's parent.
     * @return an Xpath object
     */
    public Xpath getParent() {
        return parent;
    }

    /**
     * Returns the element name.
     * @return a String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the index.
     * @return an int value
     */
    public int getIndex() {
        return index;
    }

    /**
     * Return true if the element may occur more than once.
     * @return true if the element may occur more than once.
     */
    public boolean isRepeatable() {
        return index >= 0;
    }
    
    /**
     * Returns the full XPath from the root.
     * @return a String
     */
    public String getPath(){
        return (parent == null ? "" : parent.getPath() + "/" ) +
               this.name +
               (isRepeatable() ? ("[" + index + "]"): "" );
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        return getPath();
    }
}
