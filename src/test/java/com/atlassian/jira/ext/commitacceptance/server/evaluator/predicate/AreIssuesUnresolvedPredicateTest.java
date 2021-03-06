package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.resolution.Resolution;

public class AreIssuesUnresolvedPredicateTest extends MockObjectTestCase {

    private AreIssuesUnresolvedPredicate areIssuesUnresolvedPredicate;

    protected void setUp() throws Exception {
        super.setUp();
        areIssuesUnresolvedPredicate = new AreIssuesUnresolvedPredicate() {

			public String getErrorMessageWhenIssueIsResolved(Issue issue) {
				return StringUtils.EMPTY;
			}
        	
        };
    }

    public void testEvaluateWithEmptyIssues() {
        areIssuesUnresolvedPredicate.evaluate(Collections.EMPTY_SET);
        /* There should be no exceptions raised */
    }

    public void testEvaluateWithSomeIssuesHavingResolutions() {
        Mock mockResolution = new Mock(Resolution.class);
        Resolution resolution = (Resolution) mockResolution.proxy();
        Set issues = new HashSet();


        for (int i = 0; i < 2; ++i) {
            Mock mockIssue = new Mock(Issue.class);

            mockIssue.expects(once()).method("getResolutionObject").withNoArguments().will(returnValue(i == 0 ? null : resolution));
            mockIssue.expects(once()).method("getKey").withNoArguments().will(returnValue("TST-" + String.valueOf(i + 1)));
            issues.add(mockIssue.proxy());
        }

        try {
            areIssuesUnresolvedPredicate.evaluate(issues);
            fail("Expected PredicateViolatedException to be thrown when there are resolved issues");
        } catch (final PredicateViolatedException pve) {
            /* Success */
        }
    }

    public void testEvaluateWithAtLeastAnIssueBelongingToTargetProject() {
        Mock mockIssue = new Mock(Issue.class);
        Issue issue = (Issue) mockIssue.proxy();
        Set issues = new HashSet();
        

        mockIssue.expects(once()).method("getResolutionObject").withNoArguments().will(returnValue(null));
        issues.add(issue);

        areIssuesUnresolvedPredicate.evaluate(issues);
        /* Success */
    }
}
