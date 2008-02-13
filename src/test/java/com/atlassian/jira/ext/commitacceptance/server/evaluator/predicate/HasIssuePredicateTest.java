package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;

/**
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class HasIssuePredicateTest extends MockObjectTestCase {

    private HasIssuePredicate hasIssuePredicate;

    protected void setUp() throws Exception {
        super.setUp();
        hasIssuePredicate = new HasIssuePredicate() {

			protected String getErrorMessageWhenCommitMessageDoesNotMentionEvenOneValidIssueKey() {
				return StringUtils.EMPTY;
			}
        	
        };
    }

    public void testEvaluateWithEmptyIssues() {
        try {
        	hasIssuePredicate.evaluate(Collections.EMPTY_SET);
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
