package com.atlassian.jira.ext.commitacceptance.server.evaluator;

import com.atlassian.core.util.collection.EasyList;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.security.login.LoginResult;
import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettings;
import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettingsManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.login.LoginManager;
import com.atlassian.jira.user.MockUser;
import com.atlassian.jira.user.util.UserManager;
import org.apache.commons.lang.StringUtils;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.constraint.IsEqual;
import org.jmock.core.constraint.IsInstanceOf;
import org.jmock.core.constraint.IsNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class EvaluateServiceTest extends MockObjectTestCase {

    private Mock mockProjectManager;

    private Mock mockIssueManager;

    private Mock mockUserManager;

    private Mock mockLoginManager;

    private Mock mockAcceptanceSettingsManager;

    private EvaluateService evaluateService;

    private boolean userCanAuthenticate;

	protected void setUp() throws Exception {
		super.setUp();

        mockProjectManager = new Mock(ProjectManager.class);
        mockIssueManager = new Mock(IssueManager.class);
        mockUserManager = new Mock(UserManager.class);
        mockLoginManager = new Mock(LoginManager.class);
        mockAcceptanceSettingsManager = new Mock(AcceptanceSettingsManager.class);

        evaluateService = new EvaluateService((ProjectManager) mockProjectManager.proxy(), (IssueManager) mockIssueManager.proxy(), (UserManager) mockUserManager.proxy(), (LoginManager) mockLoginManager.proxy(), (AcceptanceSettingsManager) mockAcceptanceSettingsManager.proxy()) {
            protected User getUser(String userName) {
                return EvaluateServiceTest.this.getUser();
            }
        };
	}

    protected User getUser() {
        return new MockUser("dchui", "David Chui", "no-reply@atlassian.com");
    }

    public void testAccessCommitWithFailedUserAuthentication() {
        Mock mockLoginResult = new Mock(LoginResult.class);
        mockLoginResult.expects(once()).method("isOK").will(returnValue(false));

        mockLoginManager.expects(once()).method("authenticate").with(
                new IsInstanceOf(User.class),
                new IsEqual("password")
        ).will(returnValue(mockLoginResult.proxy()));
        
        assertFalse(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", "TST, TEST", "TST-1")));
    }

    public void testAccessCommitWithNoProjectKeys() {
        Mock mockLoginResult = new Mock(LoginResult.class);
        mockLoginResult.expects(once()).method("isOK").will(returnValue(true));

        mockLoginManager.expects(once()).method("authenticate").with(
                new IsInstanceOf(User.class),
                new IsEqual("password")
        ).will(returnValue(mockLoginResult.proxy()));
        
        assertFalse(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", StringUtils.EMPTY, "TST-1")));
    }

    public void testAccessCommitWithInvalidProjectKeys() {
        Mock mockLoginResult = new Mock(LoginResult.class);
        mockLoginResult.expects(once()).method("isOK").will(returnValue(false));

        mockLoginManager.expects(once()).method("authenticate").with(
                new IsInstanceOf(User.class),
                new IsEqual("password")
        ).will(returnValue(mockLoginResult.proxy()));

        assertFalse(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", "TST", "TST-1")));
    }

    public void testAccessCommitWithWithNoIssueKeysAndGlobalSettings() {
        userCanAuthenticate = true;
        mockAcceptanceSettingsManager.expects(once()).method("getSettings").with(new IsNull()).will(returnValue(new AcceptanceSettings()));

        evaluateService = new EvaluateService((ProjectManager) mockProjectManager.proxy(), (IssueManager) mockIssueManager.proxy(), (UserManager) mockUserManager.proxy(), (LoginManager) mockLoginManager.proxy(), (AcceptanceSettingsManager) mockAcceptanceSettingsManager.proxy()) {
            protected User getUser(String userName) {
                return EvaluateServiceTest.this.getUser();
            }

            protected List getIssueKeysFromString(String commitMessage) {
                return Collections.EMPTY_LIST;
            }
        };

        Mock mockLoginResult = new Mock(LoginResult.class);
        mockLoginResult.expects(once()).method("isOK").will(returnValue(true));

        mockLoginManager.expects(once()).method("authenticate").with(
                new IsInstanceOf(User.class),
                new IsEqual("password")
        ).will(returnValue(mockLoginResult.proxy()));

        /* Since no issues are referred by the commit message, there are no issues to check against. We should allow the
         * user to commit the code in this sense.
         */
        assertTrue(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", "*", StringUtils.EMPTY)));
        mockAcceptanceSettingsManager.verify();
    }

    public void testAccessCommitWithWithInvalidIssueKeysAndGlobalSettings() {
        userCanAuthenticate = true;
        mockAcceptanceSettingsManager.expects(once()).method("getSettings").with(new IsNull()).will(returnValue(new AcceptanceSettings()));

        mockIssueManager.expects(once()).method("getIssueObject").with(eq("TST-1")).will(returnValue(null));

        evaluateService = new EvaluateService((ProjectManager) mockProjectManager.proxy(), (IssueManager) mockIssueManager.proxy(), (UserManager) mockUserManager.proxy(), (LoginManager) mockLoginManager.proxy(), (AcceptanceSettingsManager) mockAcceptanceSettingsManager.proxy()) {
            protected User getUser(String userName) {
                return EvaluateServiceTest.this.getUser();
            }

            protected List getIssueKeysFromString(String commitMessage) {
                return EasyList.build("TST-1");
            }
        };

        Mock mockLoginResult = new Mock(LoginResult.class);
        mockLoginResult.expects(once()).method("isOK").will(returnValue(true));

        mockLoginManager.expects(once()).method("authenticate").with(
                new IsInstanceOf(User.class),
                new IsEqual("password")
        ).will(returnValue(mockLoginResult.proxy()));

        /* Since the commit message contains issue keys which do not point to existing issues, we don't allow to commit
         * to happen.
         */
        assertFalse(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", "*", "TST-1")));
        mockAcceptanceSettingsManager.verify();
    }

    public void testAccessCommitWithWithAValidIssueKeysAndGlobalSettings() {
        Mock mockIssue = new Mock(MutableIssue.class);
        Issue issue = (Issue) mockIssue.proxy();

        userCanAuthenticate = true;
        mockAcceptanceSettingsManager.expects(once()).method("getSettings").with(new IsNull()).will(returnValue(new AcceptanceSettings()));

        mockIssueManager.expects(once()).method("getIssueObject").with(eq("TST-1")).will(returnValue(issue));

        evaluateService = new EvaluateService((ProjectManager) mockProjectManager.proxy(), (IssueManager) mockIssueManager.proxy(), (UserManager) mockUserManager.proxy(), (LoginManager) mockLoginManager.proxy(), (AcceptanceSettingsManager) mockAcceptanceSettingsManager.proxy()) {
            protected User getUser(String userName) {
                return EvaluateServiceTest.this.getUser();
            }

            protected List getIssueKeysFromString(String commitMessage) {
                return EasyList.build("TST-1");
            }

            protected String checkIssuesAcceptance(String committerName, Project project, Set issues) {
                /* If there are issues passed into this method, the issue keys named in the commit message do refer
                 * to existing issues in the system. So we're good. And when we're good, we'll just pretend that the set of issues
                 * met the requirements of the predicates.
                 */
                return null == issues || issues.size() == 0 ? "Fake commit denial" : null;
            }
        };

        Mock mockLoginResult = new Mock(LoginResult.class);
        mockLoginResult.expects(once()).method("isOK").will(returnValue(true));

        mockLoginManager.expects(once()).method("authenticate").with(
                new IsInstanceOf(User.class),
                new IsEqual("password")
        ).will(returnValue(mockLoginResult.proxy()));
        
        assertTrue(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", "*", "TST-1")));
        mockAcceptanceSettingsManager.verify();
    }

    /**
	 * Returns <code>true</code> if the result string returned by the evaluator
	 * means the commit was accepted.
	 */
	protected boolean isCommitAccepted(String result) {
		return result.startsWith(Boolean.TRUE.toString() + "|");
	}
}
