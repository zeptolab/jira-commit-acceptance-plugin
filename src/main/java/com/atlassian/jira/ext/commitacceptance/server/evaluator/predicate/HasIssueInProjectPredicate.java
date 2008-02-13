package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Iterator;
import java.util.Set;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;

/**
 * At least one issue passed to this predicate must be for the specified project.
 *
 * @author <a href="mailto:prkolbus@unusualcode.com">Peter Kolbus</a>
 * @version $Id$
 */
public class HasIssueInProjectPredicate extends AbstractPredicate {
    
    private Project project;

	public HasIssueInProjectPredicate(Project project) {
		this.project = project;
	}

	public void evaluate(Set issues) {
		if (project == null) {
			throw new PredicateViolatedException(getErrorMessageWhenUsedInGlobalContext());
		}

		for (Iterator it = issues.iterator(); it.hasNext();) {
			Issue issue = (Issue)it.next();

			// If we found one, we're done
			if (project.equals(issue.getProjectObject())) {
				return;
			}
		}

		// none matched, so reject
		throw new PredicateViolatedException(getErrorMessageWhenThereIsNoIssueInProject());
	}
	
	protected String getErrorMessageWhenUsedInGlobalContext() {
		return getI18nBean().getText("commitAcceptance.predicate.oneIssueInProject.errorMessageWhenUsedInGlobalContext");
	}
	
	protected String getErrorMessageWhenThereIsNoIssueInProject() {
		return getI18nBean().getText("commitAcceptance.predicate.oneIssueInProject.errorMessageWhenIssueIsNotInProject", project.getKey());
	}
}
