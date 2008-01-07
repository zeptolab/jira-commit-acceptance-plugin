package com.atlassian.jira.ext.commitacceptance.server.test;

import org.apache.commons.lang.math.RandomUtils;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettings;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.project.Project;

/**
 * Factory to instantiate test- or mock objects.
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

	// - JIRA services --------------------------------------------------------

	public static MockProjectManager createProjectManager() {
		return new MockProjectManager();
	}

	public static ApplicationProperties createApplicationProperties() {
		return new MockApplicationProperties();
	}

	// - JIRA entities --------------------------------------------------------

	public static Project createProject() {
		return new MockProjectImpl();
	}

	public static MutableIssue createIssue(Project project) {
		MockIssueImpl issue = new MockIssueImpl();
		issue.setProjectObject(project);
		issue.setKey(project.getKey() + "-" + RandomUtils.nextInt());
		return issue;
	}

	public static Resolution createResolution() {
		return new MockResolutionImpl();
	}
}
