package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Iterator;
import java.util.Set;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateVioldatedException;
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
		for (Iterator it = issues.iterator(); it.hasNext();) {
			Issue issue = (Issue)it.next();

			// If we found one, we're done
			if (project.equals(issue.getProjectObject())) {
				return;
			}
		}

		// none matched, so reject
		throw new PredicateVioldatedException("Commit message must include at least one issue from project [" + project.getKey() + "].");
	}
}
