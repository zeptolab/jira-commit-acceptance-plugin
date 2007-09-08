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
public class HasIssueInProjectPredicate implements JiraPredicate {
	private Project project;

	public HasIssueInProjectPredicate(Project project) {
		this.project = project;
	}

	public void evaluate(Set issues) {
		if (project == null) {
			throw new PredicateViolatedException("HasIssueInProjectPredicate cannot be used in the global settings. Contact your JIRA administrator.");
		}

		for (Iterator it = issues.iterator(); it.hasNext();) {
			Issue issue = (Issue)it.next();

			// If we found one, we're done
			if (project.equals(issue.getProjectObject())) {
				return;
			}
		}

		// none matched, so reject
		throw new PredicateViolatedException("Commit message must include at least one issue from project [" + project.getKey() + "].");
	}
}
