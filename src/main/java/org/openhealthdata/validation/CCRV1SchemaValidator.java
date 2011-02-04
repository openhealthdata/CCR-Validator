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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.astm.ccr.ContinuityOfCareRecord;
import org.openhealthdata.validation.result.ErrorType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class handles the XSD validation
 * @author swaldren
 *
 */
public class CCRV1SchemaValidator extends SchemaValidator {

	//TODO Check for performance and streamline code
	// Had to update to support Java 1.6 which has a bug in the way it was done.
	
	private MyErrorHandler eHandler = new MyErrorHandler();
	private Validator validator;
	private ContinuityOfCareRecord ccr = null;

        private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Constructor which excepts the File location of the XSD.  If no 
	 * File was used special URIResolver would be needed.
	 * 
	 * @param schemaLocation
	 * @throws SAXException
	 */
	public CCRV1SchemaValidator(String schemaLocation) throws SAXException{
		// create Validator
		// create a SchemaFactory capable of understanding WXS schemas
	    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

	    // load a WXS schema, represented by a Schema instance
	    Source schemaFile = new StreamSource(new File(schemaLocation));
	    Schema schema = factory.newSchema(schemaFile);
	    
		// create a Validator instance, which can be used to validate an instance document
		validator = schema.newValidator();
		validator.setErrorHandler(eHandler);

                logger.log(Level.INFO, "Created "+this.getClass().getName()+" instance");
	}
	
	/**
	 * Constructor which uses the default location of the CCR XSD.  Per the 
	 * READMe.txt, you need to add your own CCR XSD file due to licenses.
	 * @throws SAXException
	 */
	public CCRV1SchemaValidator()throws SAXException{
		// create Validator
		// create a SchemaFactory capable of understanding WXS schemas
	    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    URL xsdURL = this.getClass().getClassLoader().getResource("/CCRV1.xsd");
	    File xsdFile = null;
		try {
			xsdFile = new File(xsdURL.toURI());
		} catch (URISyntaxException e) {
			logger.log(Level.SEVERE, "Resource for XSD not Found", e);
			e.printStackTrace();
		}
	    // load a WXS schema, represented by a Schema instance
	    Source schemaFile = new StreamSource(xsdFile);
	    Schema schema = factory.newSchema(schemaFile);
	    
		// create a Validator instance, which can be used to validate an instance document
		validator = schema.newValidator();
		validator.setErrorHandler(eHandler);

                logger.log(Level.INFO, "Created "+this.getClass().getName()+" instance");
	}
	/**
	 * Runs the list of errors from the error handler
	 */
	@Override
	public List<ErrorType> getErrors() {
		return eHandler.getErrors();
	}

	/**
	 * Returns the CCR as an Object (<code>org.astm.ccr.ContinuityOfCareRecord</code>)
	 */
	@Override
	public Object getValidRootObject() {	
		return ccr;
	}

	/**
	 * This method does the validation.  Errors are stored in the 
	 * <code>ErrorHandler</code>
	 */
	@Override
	public boolean isValid(Document xml) {
		// First reset the cache of errors from last run.
		eHandler.resetErrorCache();
		// Perform the validation
		validate(xml);
		// It is invalid if there are any errors in the ErrorHandler
		if (eHandler.getErrors().size() > 0){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * This method does the validation.  Errors are stored in the 
	 * <code>ErrorHandler</code>
	 */	
	public boolean isValid(StreamSource xml) {
		// First reset the cache of errors from last run.
		eHandler.resetErrorCache();
		// Perform the validation
		validate(xml);
		// It is invalid if there are any errors in the ErrorHandler
		if (eHandler.getErrors().size() > 0){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * This method does the validation.  Errors are stored in the 
	 * <code>ErrorHandler</code>
	 */	
	public boolean isValid(InputStream xml) {
		// First reset the cache of errors from last run.
		eHandler.resetErrorCache();
		// Perform the validation
		validate(xml);
		// It is invalid if there are any errors in the ErrorHandler
		if (eHandler.getErrors().size() > 0){
			return false;
		} else {
			return true;
		}
	}

	/**
	 * This method does the validation.  Errors are stored in the 
	 * <code>ErrorHandler</code>
	 */		
	public boolean isValid(File xml) {
		// First reset the cache of errors from last run.
		eHandler.resetErrorCache();
		// Perform the validation
		validate(xml);
		// It is invalid if there are any errors in the ErrorHandler
		if (eHandler.getErrors().size() > 0){
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * Do the actual validation
	 */
	private void validate(StreamSource src){
		// Reset Valid Root Object
		ccr = null;
		try {
			validator.validate(src);
			Document xml = parseStreamSource(src, false);
			if (xml != null){
				JAXBContext jc = JAXBContext.newInstance( "org.astm.ccr" );
				Unmarshaller um = jc.createUnmarshaller();
				ccr = (ContinuityOfCareRecord)um.unmarshal(xml);
			}
		} catch (SAXException e) {
			// This is the exception thrown for an invalid CCR XML
			// so no need to capture the exception or print the trace.
			// Just log the validation error.
			logger.log(Level.SEVERE, "Not Valid CCR XML: "+e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception during validating", e);
			e.printStackTrace();
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, "Exception during unmarshalling XML DOM", e);
			e.printStackTrace();
		}
		
	}

	/*
	 * Do the actual validation
	 */
	private void validate(InputStream src){
		ccr = null;
		try {
			SAXSource source = new SAXSource(new InputSource(src));
			validator.validate(source);
			Document xml = parseStreamSource(src, false);
			if (xml != null){
				JAXBContext jc = JAXBContext.newInstance( "org.astm.ccr" );
				Unmarshaller um = jc.createUnmarshaller();
				ccr = (ContinuityOfCareRecord)um.unmarshal(xml);
			}
		} catch (SAXException e) {
			// This is the exception thrown for an invalid CCR XML
			// so no need to capture the exception or print the trace.
			// Just log the validation error.
			logger.log(Level.SEVERE, "Not Valid CCR XML: "+e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception during validating", e);
			e.printStackTrace();
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, "Exception during unmarshalling XML DOM", e);
			e.printStackTrace();
		}
		
	}

	/*
	 * Do the actual validation
	 */	
	private void validate(File src){
		ccr = null;
		try {
			SAXSource source = new SAXSource(new InputSource(new FileReader(src)));
			validator.validate(source);
			Document xml = parseFile(src, false);
			if (xml != null){
				JAXBContext jc = JAXBContext.newInstance( "org.astm.ccr" );
				Unmarshaller um = jc.createUnmarshaller();
				ccr = (ContinuityOfCareRecord)um.unmarshal(xml);
			}
		} catch (SAXException e) {
			// This is the exception thrown for an invalid CCR XML
			// so no need to capture the exception or print the trace.
			// Just log the validation error.
			logger.log(Level.SEVERE, "Not Valid CCR XML: "+e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception during validating", e);
			e.printStackTrace();
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, "Exception during unmarshalling XML DOM", e);
			e.printStackTrace();
		}
		
	}
	/*
	 * Create a DOM from the XML File
	 */
	private Document parseFile(File source, boolean validating) {
		try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validating);
            // need to be namespace aware for JAXB to be able to unmarshal DOM
            factory.setNamespaceAware(true);

            // Create the builder and parse the file
            //Document doc = factory.newDocumentBuilder().parse(source);
            Document doc = factory.newDocumentBuilder().parse(source);
            return doc;
        } catch (SAXException e) {
            logger.log(Level.SEVERE, "A parsing error occurred; the xml input is not valid", e);
        	e.printStackTrace();
        	return null;
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "A parsing error occurred; the xml input is not valid", e);
        	e.printStackTrace();
        	return null;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "A parsing error occurred; the xml input is not valid", e);
        	e.printStackTrace();
        	return null;
        }
	}
	
	private Document parseStreamSource(InputStream source, boolean validating) {
		try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validating);
            // need to be namespace aware for JAXB to be able to unmarshal DOM
            factory.setNamespaceAware(true);

            // Create the builder and parse the file
            //Document doc = factory.newDocumentBuilder().parse(source);
            Document doc = factory.newDocumentBuilder().parse(source);
            return doc;
        } catch (SAXException e) {
            logger.log(Level.SEVERE, "A parsing error occurred; the xml input is not valid", e);
        	e.printStackTrace();
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "A parsing error occurred; the xml input is not valid", e);
        	e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "A parsing error occurred; the xml input is not valid", e);
        	e.printStackTrace();
        }
        return null;
	}
	
	private Document parseStreamSource(StreamSource source, boolean validating) {
		try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validating);
            // need to be namespace aware for JAXB to be able to unmarshal DOM
            factory.setNamespaceAware(true);

            // Create the builder and parse the file
            //Document doc = factory.newDocumentBuilder().parse(source);
            Document doc = factory.newDocumentBuilder().parse(source.getInputStream());
            return doc;
        } catch (SAXException e) {
            logger.log(Level.SEVERE, "A parsing error occurred; the xml input is not valid", e);
        	e.printStackTrace();
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "A parsing error occurred; the xml input is not valid", e);
        	e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "A parsing error occurred; the xml input is not valid", e);
        	e.printStackTrace();
        }
        return null;
	}

	private void validate(Document xml) {
		try {
			ccr = null;
			validator.validate(new DOMSource(xml));
				JAXBContext jc = JAXBContext.newInstance( "org.astm.ccr" );
				Unmarshaller um = jc.createUnmarshaller();
				ccr = (ContinuityOfCareRecord)um.unmarshal(new DOMSource(xml));
			
		} catch (SAXException e) {
			// This is the exception thrown for an invalid CCR XML
			// so no need to capture the exception or print the trace.
			// Just log the validation error.
			logger.log(Level.SEVERE, "Not Valid CCR XML: "+e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception during validating", e);
			e.printStackTrace();
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, "Exception during unmarshalling XML DOM", e);
			e.printStackTrace();
		}
	}
}
