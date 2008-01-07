package com.atlassian.jira.ext.commitacceptance.server.action;

import org.apache.commons.lang.math.RandomUtils;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


import com.atlassian.jira.config.properties.ApplicationProperties;


/**
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class AcceptanceSettingsManagerTests extends MockObjectTestCase {

    private Mock mockApplicationProperties;

    private ApplicationProperties applicationProperties;

    private AcceptanceSettingsManager acceptanceSettingsManager;

	protected void setUp() throws Exception {
		super.setUp();

        mockApplicationProperties = new Mock(ApplicationProperties.class);
        applicationProperties = (ApplicationProperties) mockApplicationProperties.proxy();

        acceptanceSettingsManager = new AcceptanceSettingsManagerImpl(applicationProperties);
	}

	/**
	 * Tests whether global rules are properly persisted.
	 */
	public void testPersistGlobalRules() {
		AcceptanceSettings acceptanceSettings = new AcceptanceSettings();
		acceptanceSettings.setUseGlobalRules(true);

        /* Setup expectations */
        mockApplicationProperties
                .expects(once())
                .method("setOption")
                .with(
                        eq(AcceptanceSettingsManagerImpl.USE_GLOBAL_RULES_KEY_PREFIX),
                        eq(acceptanceSettings.getUseGlobalRules()));
        mockApplicationProperties
                .expects(once())
                .method("setOption")
                .with(
                        eq(AcceptanceSettingsManagerImpl.MUST_HAVE_ISSUE_KEY_PREFIX),
                        eq(acceptanceSettings.isMustHaveIssue()));
        mockApplicationProperties
                .expects(once())
                .method("setOption")
                .with(
                        eq(AcceptanceSettingsManagerImpl.MUST_BE_ASSIGNED_TO_COMMITER_KEY_PREFIX),
                        eq(acceptanceSettings.isMustBeAssignedToCommiter()));
        mockApplicationProperties
                .expects(once())
                .method("setOption")
                .with(
                        eq(AcceptanceSettingsManagerImpl.MUST_BE_UNRESOLVED_KEY_PREFIX),
                        eq(acceptanceSettings.isMustBeUnresolved()));
        mockApplicationProperties
                .expects(once())
                .method("setString")
                .with(
                        eq(AcceptanceSettingsManagerImpl.ACCEPT_ISSUES_FOR),
                        eq(String.valueOf(acceptanceSettings.getAcceptIssuesFor())));


        acceptanceSettingsManager.setSettings(null, acceptanceSettings);
        /* If all the methods are invocked to their expectations, we pass this test */
        mockApplicationProperties.verify();
    }

	/**
	 * Tests whether project-dependent rules are properly persisted.
	 */
	public void testPersistProjectRules() {
		String projectKey = "PR-" + RandomUtils.nextInt();
		AcceptanceSettings acceptanceSettings = new AcceptanceSettings();

        /* Setup expectations */
        mockApplicationProperties
                .expects(once())
                .method("setOption")
                .with(
                        eq(AcceptanceSettingsManagerImpl.USE_GLOBAL_RULES_KEY_PREFIX + "." + projectKey),
                        eq(acceptanceSettings.getUseGlobalRules()));
        mockApplicationProperties
                .expects(once())
                .method("setOption")
                .with(
                        eq(AcceptanceSettingsManagerImpl.MUST_HAVE_ISSUE_KEY_PREFIX + "." + projectKey),
                        eq(acceptanceSettings.isMustHaveIssue()));
        mockApplicationProperties
                .expects(once())
                .method("setOption")
                .with(
                        eq(AcceptanceSettingsManagerImpl.MUST_BE_ASSIGNED_TO_COMMITER_KEY_PREFIX + "." + projectKey),
                        eq(acceptanceSettings.isMustBeAssignedToCommiter()));
        mockApplicationProperties
                .expects(once())
                .method("setOption")
                .with(
                        eq(AcceptanceSettingsManagerImpl.MUST_BE_UNRESOLVED_KEY_PREFIX + "." + projectKey),
                        eq(acceptanceSettings.isMustBeUnresolved()));
        mockApplicationProperties
                .expects(once())
                .method("setString")
                .with(
                        eq(AcceptanceSettingsManagerImpl.ACCEPT_ISSUES_FOR + "." + projectKey),
                        eq(String.valueOf(acceptanceSettings.getAcceptIssuesFor())));

		acceptanceSettingsManager.setSettings(projectKey, acceptanceSettings);
        /* If all the methods are invocked to their expectations, we pass this test */
        mockApplicationProperties.verify();
    }

	/**
	 * Tests whether the default settings are returned for unknown projects.
	 */
	public void testGetDefaultProjectRules() {
		String unknownProjectKey = "PR-" + RandomUtils.nextInt();

        mockApplicationProperties.expects(atLeastOnce()).method("getOption").with(isA(String.class)).will(returnValue(false));
        mockApplicationProperties.expects(atLeastOnce()).method("getString").with(isA(String.class)).will(returnValue(String.valueOf(0)));

        assertEquals(new AcceptanceSettings(), acceptanceSettingsManager.getSettings(unknownProjectKey));
	}
}
