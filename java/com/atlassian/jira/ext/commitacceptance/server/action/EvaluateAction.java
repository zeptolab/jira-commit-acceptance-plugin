package com.atlassian.jira.ext.commitacceptance.server.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Predicate;

import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesAssignedToPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesInStatePredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.HasIssuePredicate;

/**
 * Invoked when receiving a commit request.
 * Writes "true" (accepted) or "false" (rejected) to the Response
 * based on the log message and the settings.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class EvaluateAction {
	public void execute() {// TODO move login to Evaluator
		// TODO prepare parameters
		String logMessage = null;// TODO get
		Set issues = null;// TODO parse log message and load issues

		Map parameters = new HashMap();
		parameters.put("logMessage", logMessage);
		parameters.put("issues", issues);

		// load settings and construct criteria
		AcceptanceSettings settings = new AcceptanceSettings(); // TODO load
		Set predicates = new HashSet();

		if(settings.isMustHaveIssue()) {
			predicates.add(new HasIssuePredicate());
		}
		if(settings.isMustBeAssignedToSpecificUser()) {
			predicates.add(new AreIssuesAssignedToPredicate());
		}
		if(settings.isMustBeInSpecificState()) {
			predicates.add(new AreIssuesInStatePredicate());
		}

		// evaluate
		boolean accepted = true;
		for(Iterator it = predicates.iterator(); it.hasNext();) {
			if(((Predicate)it.next()).evaluate(issues) == false) {
				// reject if any predicate is false
				accepted = false;
			}
		}

		// write to response
		// TODO response.write(Boolean.toString(accepted));
	}
}
