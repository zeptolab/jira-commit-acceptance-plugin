package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

public class AreIssuesInProjectPredicateTest extends MockObjectTestCase {

    private AreIssuesInProjectPredicate areIssuesInProjectPredicate;

    private Mock mockProject;

    private Project project;

    protected void setUp() throws Exception {
        super.setUp();
        mockProject = new Mock(Project.class);
        mockProject.expects(once()).method("getKey").withNoArguments().will(returnValue("TST-1"));

        project = (Project) mockProject.proxy();
        areIssuesInProjectPredicate = new AreIssuesInProjectPredicate(project);
    }

    public void testEvaluateWithEmptyIssues() {
        mockProject.reset(); /* Reset expectations. Project.getKey() is never called */
        areIssuesInProjectPredicate.evaluate(Collections.EMPTY_SET);
        /* There should be no exceptions raised */
    }

    public void testEvaluateWithSomeIssuesNotBelongingInTargetProject() {
        Mock anotherMockProject = new Mock(Project.class);
        Project anotherProject = (Project) anotherMockProject.proxy();
        Set issues = new HashSet();


        for (int i = 0; i < 2; ++i) {
            Mock mockIssue = new Mock(Issue.class);
            
            mockIssue.expects(once()).method("getProjectObject").withNoArguments().will(returnValue(i == 0 ? anotherProject : project));
            mockIssue.expects(once()).method("getKey").withNoArguments().will(returnValue("TST-" + String.valueOf(i + 1)));
            issues.add(mockIssue.proxy());
        }

        try {
            areIssuesInProjectPredicate.evaluate(issues);
            fail("Expected PredicateViolatedException to be thrown when there are no issue from the target project.");
        } catch (final PredicateViolatedException pve) {
            /* Success */
        }
    }

    public void testEvaluateWithAtLeastOneIssueBelongingToTargetProject() {
        Mock mockIssue = new Mock(Issue.class);
        Issue issue = (Issue) mockIssue.proxy();
        Set issues = new HashSet();

        mockProject.reset(); /* Reset expectations */

        mockIssue.expects(once()).method("getProjectObject").withNoArguments().will(returnValue(project));
        issues.add(issue);


        areIssuesInProjectPredicate.evaluate(issues);
        /* Success */
    }
}
