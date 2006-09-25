package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Predicate;

/**
 * Abstract predicate to extend by concrete predicates.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public abstract class AbstractJiraPredicate implements Predicate {
	/**
	 * Returns the set of {@link xxx} objects. 
	 */
	protected Set getIssues(Object parameters) {
		return (Set)((Map)parameters).get("issues");
	}
}
