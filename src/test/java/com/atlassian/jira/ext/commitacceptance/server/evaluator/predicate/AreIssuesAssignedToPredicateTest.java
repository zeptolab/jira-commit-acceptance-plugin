package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import com.opensymphony.user.User;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.ProviderAccessor;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.memory.MemoryPropertySet;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import org.jmock.MockObjectTestCase;
import org.jmock.Mock;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

public class AreIssuesAssignedToPredicateTest extends MockObjectTestCase {

    private User targetAssignee;

    private AreIssuesAssignedToPredicate areIssuesAssignedToPredicate;

    private String targetAssigneeName;

    private Mock mockProviderAccessor;

    private Mock mockCredentialsProvider;

    private Mock mockProfileProvider;

    private ProviderAccessor providerAccessor;

    private CredentialsProvider credentialsProvider;

    private ProfileProvider profileProvider;

    private PropertySet propertySet;

    protected void setUp() throws Exception {
        super.setUp();

        targetAssigneeName = "dchui";
        
        areIssuesAssignedToPredicate = new AreIssuesAssignedToPredicate(targetAssigneeName) {
            protected User getUser() throws EntityNotFoundException {
                return targetAssignee;
            }
        };

        mockCredentialsProvider = new Mock(CredentialsProvider.class);
        mockCredentialsProvider.expects(once()).method("load").withAnyArguments().will(returnValue(true));
        credentialsProvider = (CredentialsProvider) mockCredentialsProvider.proxy();

        propertySet = new MemoryPropertySet();
        propertySet.init(new HashMap(), new HashMap());

        mockProfileProvider = new Mock(ProfileProvider.class);
        mockProfileProvider.expects(once()).method("handles").withAnyArguments().will(returnValue(true));
        mockProfileProvider.expects(once()).method("getPropertySet").withAnyArguments().will(returnValue(propertySet));
        profileProvider = (ProfileProvider) mockProfileProvider.proxy();

        mockProviderAccessor = new Mock(ProviderAccessor.class);
        mockProviderAccessor.expects(once()).method("getCredentialsProvider").withAnyArguments().will(returnValue(credentialsProvider));
        providerAccessor = (ProviderAccessor) mockProviderAccessor.proxy();
        targetAssignee = new User(targetAssigneeName, providerAccessor);
    }

    public void testEvaluateWithEmptyIssues() {
        mockProfileProvider.reset(); /* Reset expectations */
        areIssuesAssignedToPredicate.evaluate(Collections.EMPTY_SET);
        /* No exceptions should be raised */
    }

    public void testEvaluateWithIssuesWithoutAssigneeAndTargetAssigneeIsValid() {
        Mock mockIssue = new Mock(Issue.class);
        Issue issue = (Issue) mockIssue.proxy();
        Set issues = new HashSet();

        issues.add(issue);

        mockProviderAccessor.expects(once()).method("getProfileProvider").withAnyArguments().will(returnValue(profileProvider));

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

        /* Reset expectations */
        mockCredentialsProvider.reset();
        mockProfileProvider.reset();
        mockProviderAccessor.reset();

        mockIssue.expects(once()).method("getAssigneeId").withNoArguments().will(returnValue(null));
        mockIssue.expects(once()).method("getKey").withNoArguments().will(returnValue("TST-1"));

        try {
            areIssuesAssignedToPredicate = new AreIssuesAssignedToPredicate(targetAssigneeName) {
                protected User getUser() throws EntityNotFoundException {
                    throw new EntityNotFoundException("Fake EntityNotFoundException.");
                }
            };
            areIssuesAssignedToPredicate.evaluate(issues);
            
            fail("Expected PredicateViolatedException to be thrown when any issues do not have an assignee.");
        } catch (final PredicateViolatedException pve) {
            /* Success */
        }
    }
}
