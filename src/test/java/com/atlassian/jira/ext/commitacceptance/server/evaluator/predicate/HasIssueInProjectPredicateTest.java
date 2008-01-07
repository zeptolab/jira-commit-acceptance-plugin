package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HasIssueInProjectPredicateTest extends MockObjectTestCase {

    private HasIssueInProjectPredicate hasIssueInProjectPredicate;

    private Mock mockProject;

    private Project project;

    protected void setUp() throws Exception {
        super.setUp();
        mockProject = new Mock(Project.class);
        mockProject.expects(once()).method("getKey").withNoArguments().will(returnValue("TST-1"));

        project = (Project) mockProject.proxy();
        hasIssueInProjectPredicate = new HasIssueInProjectPredicate(project);
    }

    public void testEvaluateWithEmptyIssues() {

        try {
            hasIssueInProjectPredicate.evaluate(Collections.EMPTY_SET);
            fail("Expected PredicateViolatedException to be thrown.");
        } catch (final PredicateViolatedException pve) {
            /* Success */
        }
    }

    public void testEvaluateWithNoIssuesBelongingToTargetProject() {
        Mock anotherMockProject = new Mock(Project.class);
        Mock mockIssue = new Mock(Issue.class);
        Issue issue = (Issue) mockIssue.proxy();
        Project anotherProject = (Project) anotherMockProject.proxy();
        Set issues = new HashSet();

        mockIssue.expects(once()).method("getProjectObject").withNoArguments().will(returnValue(anotherProject));
        issues.add(issue);

        try {
            hasIssueInProjectPredicate.evaluate(issues);
            fail("Expected PredicateViolatedException to be thrown when there are no issue from the target project.");
        } catch (final PredicateViolatedException pve) {
            /* Success */
        }
    }

    public void testEvaluateWhenEverythingIsOk() {
        Mock mockIssue = new Mock(Issue.class);
        Issue issue = (Issue) mockIssue.proxy();
        Set issues = new HashSet();

        mockProject.reset(); /* Reset expectations */

        mockIssue.expects(once()).method("getProjectObject").withNoArguments().will(returnValue(project));
        issues.add(issue);


        hasIssueInProjectPredicate.evaluate(issues);
        /* Success */
    }
}
