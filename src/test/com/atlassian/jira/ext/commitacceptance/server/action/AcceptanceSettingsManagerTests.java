package com.atlassian.jira.ext.commitacceptance.server.action;

import org.apache.commons.lang.math.RandomUtils;

import com.atlassian.jira.ext.commitacceptance.server.test.Mockery;

import junit.framework.TestCase;

/**
 * FIXME
 *
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
	 * TODO Tests whether
	 */
	public void testFallback() {// TODO no fall back at the moment!
		String invalidProjectKey = "PR-" + RandomUtils.nextInt();

		assertEquals(acceptanceSettingsManager.getSettings(null), acceptanceSettingsManager.getSettings(invalidProjectKey));
	}
}
