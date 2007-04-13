package com.atlassian.jira.ext.commitacceptance.server.action;

import org.apache.commons.lang.math.RandomUtils;

import com.atlassian.jira.ext.commitacceptance.server.test.Mockery;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class AcceptanceSettingsManagerTests extends TestCase {
	private AcceptanceSettingsManager acceptanceSettingsManager;

	protected void setUp() throws Exception {
		super.setUp();

		acceptanceSettingsManager = new AcceptanceSettingsManagerImpl(Mockery.createApplicationProperties());
	}

	/**
	 * Tests whether global rules are properly persisted.
	 */
	public void testGlobal() {
		AcceptanceSettings acceptanceSettings = Mockery.createAcceptanceSettings();
		acceptanceSettings.setUseGlobalRules(true);

		acceptanceSettingsManager.setSettings(null, acceptanceSettings);
		assertEquals(acceptanceSettings, acceptanceSettingsManager.getSettings(null));
	}

	/**
	 * Tests whether project-dependent rules are properly persisted.
	 */
	public void testProjectDependent() {
		String projectKey = "PR-" + RandomUtils.nextInt();
		AcceptanceSettings acceptanceSettings = Mockery.createAcceptanceSettings();

		acceptanceSettingsManager.setSettings(projectKey, acceptanceSettings);
		assertEquals(acceptanceSettings, acceptanceSettingsManager.getSettings(projectKey));
	}

	/**
	 * Tests whether the default settings are returned for unknown projects.
	 */
	public void testFallback() {
		String unknownProjectKey = "PR-" + RandomUtils.nextInt();

		assertEquals(new AcceptanceSettings(), acceptanceSettingsManager.getSettings(unknownProjectKey));
	}
}
