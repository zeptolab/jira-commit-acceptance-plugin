package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Set;

/**
 * Predicate interface to implement by concrete predicates.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public interface JiraPredicate {
	void evaluate(Set issues);
}
