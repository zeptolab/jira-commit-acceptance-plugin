package com.atlassian.jira.ext.commitacceptance.server.evaluator;

import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettingsManager;
import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettings;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.Project;
import com.atlassian.core.util.collection.EasyList;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.ProviderAccessor;
import com.opensymphony.user.User;
import com.opensymphony.user.provider.CredentialsProvider;
import org.apache.commons.lang.StringUtils;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.constraint.IsNull;

import java.util.List;
import java.util.Collections;
import java.util.Set;

/**
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class EvaluateServiceTest extends MockObjectTestCase {

    private Mock mockProjectManager;

    private Mock mockIssueManager;

    private Mock mockAcceptanceSettingsManager;

    private Mock mockProviderAccessor;

    private Mock mockCredentialsProvider;

    private ProjectManager projectManager;

    private IssueManager issueManager;

    private AcceptanceSettingsManager settingsManager;

    private ProviderAccessor providerAccessor;

    private CredentialsProvider credentialsProvider;

    private EvaluateService evaluateService;

	protected void setUp() throws Exception {
		super.setUp();

        mockProjectManager = new Mock(ProjectManager.class);
        projectManager = (ProjectManager) mockProjectManager.proxy();

        mockIssueManager = new Mock(IssueManager.class);
        issueManager = (IssueManager) mockIssueManager.proxy();

        mockAcceptanceSettingsManager = new Mock(AcceptanceSettingsManager.class);
        settingsManager = (AcceptanceSettingsManager) mockAcceptanceSettingsManager.proxy();

        mockCredentialsProvider = new Mock(CredentialsProvider.class);
        mockCredentialsProvider.expects(once()).method("load").withAnyArguments().will(returnValue(true));
        credentialsProvider = (CredentialsProvider) mockCredentialsProvider.proxy();

        mockProviderAccessor = new Mock(ProviderAccessor.class);
        providerAccessor = (ProviderAccessor) mockProviderAccessor.proxy();

        evaluateService = new EvaluateService(projectManager, issueManager, settingsManager) {
            protected User getUser(String userName) throws EntityNotFoundException {
                return EvaluateServiceTest.this.getUser();
            }
        };
	}

    protected User getUser() {
        return new User("dchui", providerAccessor);
    }

    public void testAccessCommitWithFailedUserAuthentication() {
        mockCredentialsProvider.expects(once()).method("authenticate").withAnyArguments().will(returnValue(false));
        mockProviderAccessor.expects(atLeastOnce()).method("getCredentialsProvider").withAnyArguments().will(returnValue(credentialsProvider));

        assertFalse(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", "TST, TEST", "TST-1")));
    }

    public void testAccessCommitWithNoProjectKeys() {
        mockCredentialsProvider.expects(once()).method("authenticate").withAnyArguments().will(returnValue(true));
        mockProviderAccessor.expects(atLeastOnce()).method("getCredentialsProvider").withAnyArguments().will(returnValue(credentialsProvider));

        assertFalse(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", StringUtils.EMPTY, "TST-1")));
    }

    public void testAccessCommitWithInvalidProjectKeys() {
        mockCredentialsProvider.expects(once()).method("authenticate").withAnyArguments().will(returnValue(true));
        mockProviderAccessor.expects(atLeastOnce()).method("getCredentialsProvider").withAnyArguments().will(returnValue(credentialsProvider));
        mockProjectManager.expects(once()).method("getProjectObjByKey").with(eq("TST")).will(returnValue(null));

        assertFalse(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", "TST", "TST-1")));
    }

    public void testAccessCommitWithWithNoIssueKeysAndGlobalSettings() {
        mockCredentialsProvider.expects(once()).method("authenticate").withAnyArguments().will(returnValue(true));
        mockProviderAccessor.expects(atLeastOnce()).method("getCredentialsProvider").withAnyArguments().will(returnValue(credentialsProvider));
        mockAcceptanceSettingsManager.expects(once()).method("getSettings").with(new IsNull()).will(returnValue(new AcceptanceSettings()));

        evaluateService = new EvaluateService(projectManager, issueManager, settingsManager) {
            protected User getUser(String userName) throws EntityNotFoundException {
                return EvaluateServiceTest.this.getUser();
            }

            protected List getIssueKeysFromString(String commitMessage) {
                return Collections.EMPTY_LIST;
            }
        };

        /* Since no issues are referred by the commit message, there are no issues to check against. We should allow the
         * user to commit the code in this sense.
         */
        assertTrue(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", "*", StringUtils.EMPTY)));
        mockAcceptanceSettingsManager.verify();
    }

    public void testAccessCommitWithWithInvalidIssueKeysAndGlobalSettings() {
        mockCredentialsProvider.expects(once()).method("authenticate").withAnyArguments().will(returnValue(true));
        mockProviderAccessor.expects(atLeastOnce()).method("getCredentialsProvider").withAnyArguments().will(returnValue(credentialsProvider));
        mockAcceptanceSettingsManager.expects(once()).method("getSettings").with(new IsNull()).will(returnValue(new AcceptanceSettings()));

        mockIssueManager.expects(once()).method("getIssueObject").with(eq("TST-1")).will(returnValue(null));

        evaluateService = new EvaluateService(projectManager, issueManager, settingsManager) {
            protected User getUser(String userName) throws EntityNotFoundException {
                return EvaluateServiceTest.this.getUser();
            }

            protected List getIssueKeysFromString(String commitMessage) {
                return EasyList.build("TST-1");
            }
        };

        /* Since the commit message contains issue keys which do not point to existing issues, we don't allow to commit
         * to happen.
         */
        assertFalse(isCommitAccepted(evaluateService.acceptCommit("dchui", "password", "dchui", "*", "TST-1")));
        mockAcceptanceSettingsManager.verify();
    }

    public void testAccessCommitWithWithAValidIssueKeysAndGlobalSettings() {
        Mock mockIssue = new Mock(MutableIssue.class);
        Issue issue = (Issue) mockIssue.proxy();

        mockCredentialsProvider.expects(once()).method("authenticate").withAnyArguments().will(returnValue(true));
        mockProviderAccessor.expects(atLeastOnce()).method("getCredentialsProvider").withAnyArguments().will(returnValue(credentialsProvider));
        mockAcceptanceSettingsManager.expects(once()).method("getSettings").with(new IsNull()).will(returnValue(new AcceptanceSettings()));

        mockIssueManager.expects(once()).method("getIssueObject").with(eq("TST-1")).will(returnValue(issue));

        evaluateService = new EvaluateService(projectManager, issueManager, settingsManager) {
            protected User getUser(String userName) throws EntityNotFoundException {
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
