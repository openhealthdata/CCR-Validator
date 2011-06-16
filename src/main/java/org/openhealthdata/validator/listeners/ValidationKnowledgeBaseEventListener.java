package org.openhealthdata.validator.listeners;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.definition.rule.Rule;
import org.drools.event.knowledgebase.AfterFunctionRemovedEvent;
import org.drools.event.knowledgebase.AfterKnowledgeBaseLockedEvent;
import org.drools.event.knowledgebase.AfterKnowledgeBaseUnlockedEvent;
import org.drools.event.knowledgebase.AfterKnowledgePackageAddedEvent;
import org.drools.event.knowledgebase.AfterKnowledgePackageRemovedEvent;
/*
import org.drools.event.knowledgebase.AfterProcessAddedEvent;
import org.drools.event.knowledgebase.AfterProcessRemovedEvent;
*/
import org.drools.event.knowledgebase.AfterRuleAddedEvent;
import org.drools.event.knowledgebase.AfterRuleRemovedEvent;
import org.drools.event.knowledgebase.BeforeFunctionRemovedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseLockedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseUnlockedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgePackageAddedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgePackageRemovedEvent;
/*
import org.drools.event.knowledgebase.BeforeProcessAddedEvent;
import org.drools.event.knowledgebase.BeforeProcessRemovedEvent;
*/
import org.drools.event.knowledgebase.BeforeRuleAddedEvent;
import org.drools.event.knowledgebase.BeforeRuleRemovedEvent;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.openhealthdata.validation.result.RuleType;

/**
 * This Listener creates and manages a <code>RuleType</code> list 
 * for the ValdiationManager
 * @author ohdohd
 *
 */
public class ValidationKnowledgeBaseEventListener implements
		KnowledgeBaseEventListener {

	List<RuleType> rules = new LinkedList<RuleType>();
	
	public List<RuleType> getRules() {
		return rules;
	}
	
	public void afterFunctionRemoved(AfterFunctionRemovedEvent arg0) {
		// Nothing needed to be done
	}

	public void afterKnowledgeBaseLocked(AfterKnowledgeBaseLockedEvent arg0) {
		// Nothing needed to be done
	}

	public void afterKnowledgeBaseUnlocked(AfterKnowledgeBaseUnlockedEvent arg0) {
		// Nothing needed to be done
	}

	public void afterKnowledgePackageAdded(AfterKnowledgePackageAddedEvent arg0) {
		// Nothing needed to be done
	}

	public void afterKnowledgePackageRemoved(
			AfterKnowledgePackageRemovedEvent arg0) {
		// Nothing needed to be done
	}

	public void afterRuleAdded(AfterRuleAddedEvent rae) {
		RuleType rt = new RuleType();
		Rule r = rae.getRule();
		rt.setName(r.getName());
		rt.setPackage(r.getPackageName());
		// Check for the appropriate meta-attributes
		String profile = r.getMetaAttribute("profile");
		if (profile != null){
			rt.setProfile(profile);
		}
		String testid = r.getMetaAttribute("testid");
		if(testid != null){
			rt.setId(testid);
		}
		String title = r.getMetaAttribute("title");
		if(title != null){
			rt.setTitle(title);
		}
		String description = r.getMetaAttribute("description");
		if (description != null){
			rt.setDescription(description);
		}
		String source = r.getMetaAttribute("source");
		if (source != null){
			rt.setSource(source);
		}
		String author = r.getMetaAttribute("author");
		if (author !=null){
			rt.setAuthor(author);
		}
		rules.add(rt);
	}

	public void afterRuleRemoved(AfterRuleRemovedEvent rre) {
		// TODO Increase performance
		// Assume small number of rules
		for (int i=0;i<rules.size();i++){
			if (rules.get(i).getName() == rre.getRule().getName()){
				rules.remove(i);
				break;
			}
		}
	}

	public void beforeFunctionRemoved(BeforeFunctionRemovedEvent arg0) {
		// Nothing needed to be done
	}

	public void beforeKnowledgeBaseLocked(BeforeKnowledgeBaseLockedEvent arg0) {
		// Nothing needed to be done
	}

	public void beforeKnowledgeBaseUnlocked(
			BeforeKnowledgeBaseUnlockedEvent arg0) {
		// Nothing needed to be done
	}

	public void beforeKnowledgePackageAdded(
			BeforeKnowledgePackageAddedEvent arg0) {
		// Nothing needed to be done
	}

	public void beforeKnowledgePackageRemoved(
			BeforeKnowledgePackageRemovedEvent arg0) {
		// Nothing needed to be done
	}

	public void beforeRuleAdded(BeforeRuleAddedEvent arg0) {
		// Nothing needed to be done
	}

	public void beforeRuleRemoved(BeforeRuleRemovedEvent arg0) {
		// Nothing needed to be done
	}
/*  for drools 5.2
	public void afterProcessAdded(AfterProcessAddedEvent arg0) {
		// Nothing needed to be done
		
	}

	public void afterProcessRemoved(AfterProcessRemovedEvent arg0) {
		// Nothing needed to be done
		
	}

	public void beforeProcessAdded(BeforeProcessAddedEvent arg0) {
		// Nothing needed to be done
		
	}

	public void beforeProcessRemoved(BeforeProcessRemovedEvent arg0) {
		// Nothing needed to be done
		
	}
*/

}
