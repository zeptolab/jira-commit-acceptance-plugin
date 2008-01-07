package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

public class AreIssuesUnresolvedPredicateTest extends MockObjectTestCase {

    private AreIssuesUnresolvedPredicate areIssuesUnresolvedPredicate;

    protected void setUp() throws Exception {
        super.setUp();
        areIssuesUnresolvedPredicate = new AreIssuesUnresolvedPredicate();
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
