package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class HasIssuePredicateTest extends MockObjectTestCase {

    private HasIssuePredicate hasIssuePredicate;

    protected void setUp() throws Exception {
        super.setUp();
        hasIssuePredicate = new HasIssuePredicate();
    }

    public void testEvaluateWithEmptyIssues() {
		HasIssuePredicate predicate = new HasIssuePredicate();
        
        try {
			predicate.evaluate(Collections.EMPTY_SET);
			fail("CommitRejectedException is expected for empty issue set");
		} catch(PredicateViolatedException ex) {
            /* Success */
        }
	}

    public void testEvaluateWithNonEmptyIssues() {
        final Set issues = new HashSet();

        issues.add(new Mock(Issue.class).proxy());
        hasIssuePredicate.evaluate(issues);
        /* Should pass with no errors */
    }
}
