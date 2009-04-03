package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Iterator;
import java.util.Set;

import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.util.I18nHelper;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;

/**
 * All issues passed to this predicate should be assigned to the given person
 * (<code>assigneeName</code>).
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class AreIssuesAssignedToPredicate extends AbstractPredicate {
	
	private String assigneeName;

	public AreIssuesAssignedToPredicate(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	public void evaluate(Set issues) {
		for (Iterator it = issues.iterator(); it.hasNext();) {
			Issue issue = (Issue)it.next();

			// if at least one issue is not assigned to the correct person.
			if ((issue.getAssigneeId() == null) || (!issue.getAssigneeId().equals(assigneeName))) {
				User assignee = null;
				
				try {
					assignee = getUser();
//					cause = "Issue [" + issue.getKey() + "] must be assigned to " + assigneeName + " (" + assignee.getFullName() + ").";
				} catch (EntityNotFoundException e) {
//					cause = "Issue [" + issue.getKey() + "] is not assigned to the correct person.";
				}

				throw new PredicateViolatedException(getErrorMessage(issue, assignee));
			}
		}
	}

    protected User getUser() throws EntityNotFoundException {
        return UserUtils.getUser(assigneeName);
    }
    
    protected String getErrorMessage(final Issue issue, final User assignee) {
    	final I18nHelper i18nHelper = getI18nHelper();
    	
    	if (null != assignee) {
    		return i18nHelper.getText(
    				"commitAcceptance.predicate.issuesAssigned.errorMessageWhenUserWithAssigneeNameExists",
    				issue.getKey(), assigneeName, assignee.getFullName());
    	} else {
    		return i18nHelper.getText(
    				"commitAcceptance.predicate.issuesAssigned.errorMessageWhenUserWithAssigneeNameDoesNotExist", issue.getKey());
    	}
    }
}
