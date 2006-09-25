package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

/**
 * FIXME
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class HasIssuePredicate extends AbstractJiraPredicate {
	public boolean evaluate(Object object) {
		return !getIssues(object).isEmpty();
	}
}
