package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Set;

/**
 * A predicate to implement by concrete predicates.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 */
public interface JiraPredicate {
    public void evaluate(Set issues);
}
