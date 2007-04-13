package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Iterator;
import java.util.Set;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.resolution.Resolution;

import com.atlassian.jira.ext.commitacceptance.server.exception.AcceptanceException;

/**
 * All issues passed to this predicate should be in UNRESOLVED. If it's not true,
 * <code>AcceptanceException</code> will be thrown.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class AreIssuesUnresolvedPredicate implements JiraPredicate {
	public void evaluate(Set issues) {
		for (Iterator it = issues.iterator(); it.hasNext();) {
			Issue issue = (Issue)it.next();
			Resolution resolution = issue.getResolutionObject();

			// reject if any issue is resolved
			if (resolution != null)	{
				throw new AcceptanceException("Issue [" + issue.getKey() + "] must be in UNRESOLVED.");
			}
		}
	}
}
