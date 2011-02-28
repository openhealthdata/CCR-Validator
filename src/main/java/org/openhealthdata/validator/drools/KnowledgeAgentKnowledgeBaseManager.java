package org.openhealthdata.validator.drools;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.io.ResourceFactory;

/**
 * creation date: 2/21/11
 */
public class KnowledgeAgentKnowledgeBaseManager implements KnowledgeBaseManager{

    private KnowledgeAgent kagent;

    public KnowledgeAgentKnowledgeBaseManager() {
        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty( "drools.agent.scanDirectories", "true" );
        this.kagent = KnowledgeAgentFactory.newKnowledgeAgent("OpenHealthDataAgent", aconf);
        this.kagent.applyChangeSet(ResourceFactory.newInputStreamResource(getClass().getResourceAsStream("/change-set.xml")));
    }

    public KnowledgeBase getKnowledgeBase() {
        return kagent.getKnowledgeBase();
    }
}
