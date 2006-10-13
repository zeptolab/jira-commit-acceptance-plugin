package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Iterator;
import java.util.Set;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.resolution.Resolution;

import com.atlassian.jira.ext.commitacceptance.server.exception.AcceptanceException;

/**
 * FIXME
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 */
public class AreIssuesUnresolvedPredicate implements JiraPredicate {
	public void evaluate(Set issues)
    {
        for (Iterator it=issues.iterator(); it.hasNext();)
        {
            Issue issue = (Issue)it.next();
            Resolution resolution = issue.getResolutionObject();

            // If at least one issue is resolved.
            if (resolution != null)
            {
                throw new AcceptanceException(issue.getKey() + " issue must be in UNRESOLVED.");
            }
        }
	}
}
