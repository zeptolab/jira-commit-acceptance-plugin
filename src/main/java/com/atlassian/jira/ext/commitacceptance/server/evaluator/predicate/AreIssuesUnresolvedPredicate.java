package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Iterator;
import java.util.Set;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.project.Project;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;

/**
 * All issues passed to this predicate should be in UNRESOLVED.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class AreIssuesUnresolvedPredicate extends AbstractPredicate {
	public void evaluate(Set issues) {
		for (Iterator it = issues.iterator(); it.hasNext();) {
			Issue issue = (Issue)it.next();
			Resolution resolution = issue.getResolutionObject();

			// reject if any issue is resolved
			if (resolution != null)	{
				throw new PredicateViolatedException(getErrorMessageWhenIssueIsResolved(issue));
			}
		}
	}

	public void evaluate(Set issues, Project project) {
		evaluate(issues);
	}
	
	public String getErrorMessageWhenIssueIsResolved(final Issue issue) {
		return getI18nBean().getText("commitAcceptance.predicate.issueUnresolved.errorMessageWhenIssueIsResolved", issue.getKey());
	}
}
