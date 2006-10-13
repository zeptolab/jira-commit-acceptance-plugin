package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Set;

import com.atlassian.jira.ext.commitacceptance.server.exception.AcceptanceException;

/**
 * FIXME
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 */
public class HasIssuePredicate implements JiraPredicate {
	public void evaluate(Set issues)
    {
		if (issues.isEmpty())
        {
		    throw new AcceptanceException("A commit message must contain at least one issue key.");
        }
	}
}
