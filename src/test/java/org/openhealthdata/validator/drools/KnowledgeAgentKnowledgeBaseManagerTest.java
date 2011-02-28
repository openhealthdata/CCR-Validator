package org.openhealthdata.validator.drools;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * creation date: 2/28/11
 */
public class KnowledgeAgentKnowledgeBaseManagerTest {

    @Test
    @Ignore
    public void testGetKnowledgeBase(){
        final KnowledgeAgentKnowledgeBaseManager kbaseManager = new KnowledgeAgentKnowledgeBaseManager();
        Assert.assertEquals(3, kbaseManager.getKnowledgeBase().getKnowledgePackages().size());
    }
}
