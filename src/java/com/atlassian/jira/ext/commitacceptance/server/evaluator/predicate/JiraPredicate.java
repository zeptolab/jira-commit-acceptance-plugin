package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Set;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateVioldatedException;

/**
 * Predicate interface to implement by concrete predicates.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public interface JiraPredicate {
	/**
	 * It should simply return if the predicate evaluates to "true"
	 * or throw {@link PredicateVioldatedException} otherwise.
	 */
	void evaluate(Set issues);
}
