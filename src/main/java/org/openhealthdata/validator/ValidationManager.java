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
package org.openhealthdata.validator;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.openhealthdata.validation.CCRV1SchemaValidator;
import org.openhealthdata.validation.result.BaseValidationManager;
import org.openhealthdata.validation.result.RuleType;
import org.openhealthdata.validation.result.ValidationResult;
import org.openhealthdata.validation.result.ValidationResultManager;
import org.openhealthdata.validator.drools.KnowledgeBaseManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ValidationManager {

	private CCRV1SchemaValidator ccrSchVal;
        private Logger logger = Logger.getLogger(this.getClass().getName());
    private KnowledgeBaseManager kbaseManager;

	/**
	 * Create a new default ValidationManager.  Make sure you have 
	 * added your CCR XSD file (see READMe.txt)
	 */
	public ValidationManager(KnowledgeBaseManager kbaseManager) {
        this.kbaseManager = kbaseManager;
		String schemalocation = "org/astm/ccr/CCRV1.xsd";
		setupSchemaValidator(schemalocation);
	}

	/*
	 * Create the CCR XSD Validator which will be inserted into working memory
	 */
	private void setupSchemaValidator(String schemalocation) {
		try {
			ccrSchVal = new CCRV1SchemaValidator();
		} catch (SAXException e) {
			logger.log(Level.SEVERE, "Failure to setup Schema Validator", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Marshal the ValidationResult to XML
	 */
	public String convertToXML(ValidationResult vr){
		String context = "org.openhealthdata.validation.result";
		StringWriter sw = new StringWriter();
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(context);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(vr, sw);
			return sw.toString();
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, "Error converting ValidationResult to XML returning empty String", e);
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Validate the XML File
	 * @param xml
	 * @return
	 */
	public ValidationResult validate(File xml) {
		return validate(xml, xml.getName());
	}
	
	/**
	 * Validate the XML File but return the result as marshalled XML
	 * @param xml
	 * @return
	 */
	public String validateToString(File xml) {
		ValidationResult vr = validate(xml, xml.getName());
		return convertToXML(vr);
		
	}
	
	/*
	 * Performs the validation by creating a Knowledge session and 
	 * inserting the <code>Object</code> XML into working memory 
	 * along with the CCRV1SchemaValidator
	 */
	private ValidationResult validate(Object xml, String fileName) {
        KnowledgeBase kbase = kbaseManager.getKnowledgeBase();
		// Get Default ValidationResult and add to ValidationManager
		ValidationResultManager valResMan = new BaseValidationManager();
		ValidationResult result = new ValidationResult();
		result.setDisclaimer("This validation test provides no warranty that all constraints were checked for given " +
				"profiles.  Additional testing may be required. Any issues should be reported to testing entity.");
		result.setFileTested(fileName);
		valResMan.setResult(result);
		// Set the runtime of the validation
		try {
			result.setTimestamp(DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar()));
		} catch (DatatypeConfigurationException e) {
                    logger.log(Level.SEVERE, "Error creating XML datetime type", e);
			e.printStackTrace();
		}
		// Create a working memory session
		Collection<KnowledgePackage> pkgs = kbase.getKnowledgePackages();
		logger.log(Level.INFO, "Pkg list: "+pkgs.size());
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<Rule> rules = new ArrayList<Rule>();
        for (KnowledgePackage kpackage : kbase.getKnowledgePackages()){
            rules.addAll(kpackage.getRules());
        }
		result.getRules().getRule().addAll(getRuleTypes(rules));
		//Set the global variable which will be used by the rules to aggregate results of validation
		ksession.setGlobal("val_result", valResMan);
		ksession.insert(xml);
		ksession.insert(ccrSchVal);
		// Run the rules
		ksession.fireAllRules();
		// Recycle the knowledge session
		ksession.dispose();
		
		// Dispose of the knowledge session logger
		//klogger.close();

		// Get the Validation Result to return
		ValidationResult vr = valResMan.getResult();
		// return the Validation Result from the session
		return vr;
	}

	/**
	 * Validate the XML String - Not Implemented Yet
	 * @param xml
	 * @return
	 */
	public ValidationResult validate(String xml) {
		throw new UnsupportedOperationException("Not Implemented Yet");
                // TODO Implement validate XML string
                // Need to parse XML string into a DOM and then call
                // the validate(Document) method
	}

	/**
	 * Validate the XML DOM - Not Implemented Yet
	 * @param xml
	 * @return
	 */
	public ValidationResult validate(Document xml) {
		throw new UnsupportedOperationException("Not Implemented Yet");
                // TODO Implement validate XML Document
                // Need to add a rule to org.ccr.rules.core to look for a
                // DOM in memory and check for root node of <ContinuityOfCare>
                
	}

    private List<RuleType> getRuleTypes(List<Rule> rules){
        List<RuleType> types = new ArrayList<RuleType>();
        for (Rule r : rules) {
            RuleType rt = new RuleType();
            rt.setName(r.getName());
            rt.setPackage(r.getPackageName());
            // Check for the appropriate meta-attributes
            Map<String,Object> metaData = r.getMetaData();
            String profile = (String) metaData.get("profile");
            if (profile != null){
                rt.setProfile(profile);
            }
            String testid = (String) metaData.get("testid");
            if(testid != null){
                rt.setId(testid);
            }
            String title = (String) metaData.get("title");
            if(title != null){
                rt.setTitle(title);
            }
            String description = (String) metaData.get("description");
            if (description != null){
                rt.setDescription(description);
            }
            String source = (String) metaData.get("source");
            if (source != null){
                rt.setSource(source);
            }
            String author = (String) metaData.get("author");
            if (author !=null){
                rt.setAuthor(author);
            }
            types.add(rt);
        }
        return types;
    }

}
