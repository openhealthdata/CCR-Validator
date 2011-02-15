package org.openhealthdata.validator.drools;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.openhealthdata.validator.DroolsUtil;
import org.openhealthdata.validator.listeners.ValidationKnowledgeBaseEventListener;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 2/15/11
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class InternalKnowledgeBaseManager implements KnowledgeBaseManager{

    private KnowledgeBase kbase;
    private static final String RULES_FOLDER = "/rules";
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public KnowledgeBase getKnowledgeBase() {
        if( kbase == null ){
            try {
                kbase = createKnowledgeBase();
            } catch (Exception e) {
                String errorMessage = "unable to create knowledgeBase";
                logger.log(Level.SEVERE, errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }
        }
        return kbase;
    }

	/*
	 * Create the KnowledgeBase from base rules in "core" and "v1" packages
	 */
    private KnowledgeBase createKnowledgeBase() throws URISyntaxException {
		KnowledgeBuilder builder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		// Get the location of the root rules folder from the classpath
		URL rules = this.getClass().getResource(RULES_FOLDER);
		// Get file handler from the URL
		File rDir = new File(rules.toURI());
		// Make sure it is a directory and load all subfolders
		if (rDir.isDirectory()){
			for (File f : rDir.listFiles()){
				//only process directories
				if (f.isDirectory()){
					addDirectory(f, builder);
				}
			}
		}
		// Create a new knowledgebase
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		// Add the rule packages to the new knowledge base
		kbase.addKnowledgePackages(builder.getKnowledgePackages());

        return kbase;
	}

	/*
	 * Add the knowledge resources in the directory into the KnowledgeBuilder
	 */
	private void addDirectory(File dir, KnowledgeBuilder kbuilder) {
		// TODO Add non-DRL files - for future
		logger.log(Level.INFO,"addDirectory: " + dir.getAbsolutePath());
		if (dir.isDirectory()) {
			File[] resources = dir.listFiles(DroolsUtil
					.getFilter(ResourceType.DRL));

			if (resources != null) {
				for (int i = 0; i < resources.length; i++) {
					logger.log(Level.INFO,"adding: " + resources[i].getName());
					kbuilder.add(ResourceFactory.newFileResource(resources[i]),
							ResourceType.DRL);
					// check for any errors and stop processing if found
					if (kbuilder.hasErrors()){
                        KnowledgeBuilderErrors kbuilderErrors = kbuilder.getErrors();
                        for(KnowledgeBuilderError error : kbuilderErrors){
                            logger.log(Level.SEVERE, "kbuilder error: "+ error.getMessage());
                        }
						throw new RuntimeException(kbuilderErrors.toString());					}
				}
			}
		} else {
			throw new RuntimeException("File is not a directory:"+dir.getName());
		}
		logger.log(Level.FINEST,"Builder Pkg: "+kbuilder.getKnowledgePackages().size());
	}

}
