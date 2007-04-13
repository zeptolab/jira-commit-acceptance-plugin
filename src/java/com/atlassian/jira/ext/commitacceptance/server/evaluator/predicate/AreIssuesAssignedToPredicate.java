package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Iterator;
import java.util.Set;

import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.issue.Issue;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;

import com.atlassian.jira.ext.commitacceptance.server.exception.AcceptanceException;

/**
 * All issues passed to this predicate should be assigned to the given person
 * (<code>assigneeName</code>). If it's not true, <code>AcceptanceException</code>
 * will be thrown.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class AreIssuesAssignedToPredicate implements JiraPredicate {
	private String assigneeName;

	public AreIssuesAssignedToPredicate(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	public void evaluate(Set issues) {
		for (Iterator it = issues.iterator(); it.hasNext();) {
			Issue issue = (Issue)it.next();

			// if at least one issue is not assigned to the correct person.
			if (!issue.getAssigneeId().equals(assigneeName)) {
				String cause = "Issue [" + issue.getKey() + "] ";
				try {
					User assignee = UserUtils.getUser(assigneeName);
					cause += "must be assigned to " + assigneeName + " (" + assignee.getFullName() + ").";
				} catch (EntityNotFoundException e) {
					cause += "is not assigned to the correct person.";
				}

				throw new AcceptanceException(cause);
			}
		}
	}
}
