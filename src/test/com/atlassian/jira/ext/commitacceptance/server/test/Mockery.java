package com.atlassian.jira.ext.commitacceptance.server.test;

import org.apache.commons.lang.math.RandomUtils;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettings;
import com.atlassian.jira.issue.Issue;

/**
 * FIXME
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class Mockery {
	public static AcceptanceSettings createAcceptanceSettings() {
		AcceptanceSettings acceptanceSettings = new AcceptanceSettings();

		acceptanceSettings.setUseGlobalRules(RandomUtils.nextBoolean());
		acceptanceSettings.setMustHaveIssue(RandomUtils.nextBoolean());
		acceptanceSettings.setMustBeAssignedToCommiter(RandomUtils.nextBoolean());
		acceptanceSettings.setMustBeUnresolved(RandomUtils.nextBoolean());

		return acceptanceSettings;
	}

	public static ApplicationProperties createApplicationProperties() {
		return new MockApplicationProperties();
	}

	public static Issue createIssue() {
		return new MockIssueImpl();
	}
}
