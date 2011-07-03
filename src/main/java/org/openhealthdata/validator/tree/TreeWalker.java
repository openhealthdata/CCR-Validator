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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.drools.runtime.KnowledgeRuntime;

/**
 * An object of this class provides a simple service for inserting all
 * elements of a DOM tree into a StatefulKnowledgeSession. In addition
 * to each object representing the XML element, an additional fact
 * of class <code>Eleemnt</code> is inserted for associating the original
 * XPath with the XML element.
 * 
 * @author wlaun
 *
 */
public class TreeWalker {
    
    private KnowledgeRuntime kRuntime;
    private Package          pakkage;
    private Reflector        reflector = new Reflector();

    /**
     * Constructor.
     * @param kSession the session into which elements should be inserted,
     */
    public TreeWalker( KnowledgeRuntime kRuntime ){
        this.kRuntime = kRuntime;
    }
    
    /**
     * Inserts all elements of the given XML tree as facts.
     * 
     * @param topName tag name for the top level element
     * @param root the root node
     * @return the number of inserted XML elements
     * @throws Exception
     */
    public int walk( String topName, Object root ) throws Exception {
        this.pakkage = root.getClass().getPackage();
        return doObject( new Xpath( Xpath.ROOT, topName ), root );
    }

    private boolean isList( Class<?> clazz ){
        return java.util.List.class.equals( clazz );
    }

    private int doObject( Xpath xpath, Object object ) throws Exception {
        int res = 1;
        kRuntime.insert( object );
        kRuntime.insert( new Element( xpath, object ) );
        Class<?> clazz = object.getClass();
        Collection<Reflector.Entry> entries = reflector.getMethods( clazz );
        for( Reflector.Entry entry: entries ){
            Method method = entry.getMethod();
            Class<?> returnType = method.getReturnType();
            if( void.class.equals( returnType ) ) continue;               
            if( method.getParameterTypes().length > 0 ) continue;
            if( isList( returnType ) ){
                Object member = method.invoke( object );
                if( member != null ){
//                    System.out.println( "List<?> " + method.getName() );
                    res += doList( xpath, entry.getNodeName(), member );
                }
            } else if( pakkage.equals( returnType.getPackage() ) ){
                Object member = method.invoke( object );
                if( member != null ){
//                    System.out.println( returnType.getSimpleName() + " " + method.getName() );
                    res += doObject( new Xpath( xpath, entry.getNodeName() ), member );
                }
            } else {
//                System.out.println( "Unhandled in class " + clazz.getName() + ": " + 
//                        returnType.getName() + " " + method.getName() + "()" );                
            }
        }
        return res;
    }
    
    private int doList( Xpath path, String name, Object listObject ) throws Exception {
        int res = 0;
        int index = 0;
        for( Object object: (List<?>)listObject ){
            if( pakkage.equals( object.getClass().getPackage() ) ){
                res += doObject( new Xpath( path, name, index++ ), object );
            }
        }
        return res;
    }
}
