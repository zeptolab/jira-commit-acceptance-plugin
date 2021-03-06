package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.memory.MemoryPropertySet;
import org.apache.commons.lang.StringUtils;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AreIssuesAssignedToPredicateTest extends MockObjectTestCase {

    private ApplicationUser targetAssignee;

    private AreIssuesAssignedToPredicate areIssuesAssignedToPredicate;

    private String targetAssigneeName;

    private PropertySet propertySet;

    protected void setUp() throws Exception {
        super.setUp();

        targetAssigneeName = "dchui";
        
        areIssuesAssignedToPredicate = new AreIssuesAssignedToPredicate(targetAssigneeName) {
            protected ApplicationUser getUser() {
                return targetAssignee;
            }

			protected String getErrorMessage(final Issue issue, final ApplicationUser assignee) {
				return StringUtils.EMPTY;
			}
        };

        propertySet = new MemoryPropertySet();
        propertySet.init(new HashMap(), new HashMap());
        targetAssignee = new MockApplicationUser(targetAssigneeName, "David Chui", "no-reply@atlasian.com");
    }

    public void testEvaluateWithEmptyIssues() {
        areIssuesAssignedToPredicate.evaluate(Collections.EMPTY_SET);
        /* No exceptions should be raised */
    }

    public void testEvaluateWithIssuesWithoutAssigneeAndTargetAssigneeIsValid() {
        Mock mockIssue = new Mock(Issue.class);
        Issue issue = (Issue) mockIssue.proxy();
        Set issues = new HashSet();

        issues.add(issue);

        mockIssue.expects(once()).method("getAssigneeId").withNoArguments().will(returnValue(null));
        mockIssue.expects(once()).method("getKey").withNoArguments().will(returnValue("TST-1"));

        try {
            areIssuesAssignedToPredicate.evaluate(issues);
            fail("Expected PredicateViolatedException to be thrown when any issues do not have an assignee.");
        } catch (final PredicateViolatedException pve) {
            /* Success */
        }
    }

    public void testEvaluateWithIssuesWithoutAssigneeAndTargetAssigneeIsInvalid() {
        Mock mockIssue = new Mock(Issue.class);
        Issue issue = (Issue) mockIssue.proxy();
        Set issues = new HashSet();

        issues.add(issue);

        mockIssue.expects(once()).method("getAssigneeId").withNoArguments().will(returnValue(null));
        mockIssue.expects(once()).method("getKey").withNoArguments().will(returnValue("TST-1"));

        try {
            areIssuesAssignedToPredicate = new AreIssuesAssignedToPredicate(targetAssigneeName) {
                protected ApplicationUser getUser() {
                    throw new PredicateViolatedException("Fake EntityNotFoundException.");
                }

    			protected String getErrorMessage(final Issue issue, final ApplicationUser assignee) {
    				return StringUtils.EMPTY;
    			}
            };
            areIssuesAssignedToPredicate.evaluate(issues);
            
            fail("Expected PredicateViolatedException to be thrown when any issues do not have an assignee.");
        } catch (final PredicateViolatedException pve) {
            /* Success */
        }
    }
}
