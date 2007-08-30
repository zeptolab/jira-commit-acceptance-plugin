package com.atlassian.jira.ext.commitacceptance.server.action;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.config.properties.ApplicationProperties;

/**
 * Implementation class.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class AcceptanceSettingsManagerImpl implements AcceptanceSettingsManager {
	/**
	 * Prefixes for rule option names. For global rules these are used as they are,
	 * for project-dependent rules the key of the project will be appended.
	 * @see #getRuleOptionName(GenericValue, String)
	 */
    private static final String USE_GLOBAL_RULES_KEY_PREFIX = "jira.plugins.commitacceptance.settings.useGlobalRules";
    private static final String MUST_HAVE_ISSUE_KEY_PREFIX = "jira.plugins.commitacceptance.settings.mustHaveIssue";
    private static final String MUST_HAVE_ISSUE_IN_PROJECT_KEY_PREFIX = "jira.plugins.commitacceptance.settings.mustHaveIssueInProject";
    private static final String MUST_ISSUES_BE_IN_PROJECT_KEY_PREFIX = "jira.plugins.commitacceptance.settings.mustIssuesBeInProject";
    private static final String MUST_BE_ASSIGNED_TO_COMMITER_KEY_PREFIX = "jira.plugins.commitacceptance.settings.mustBeAssignedToCommiter";
    private static final String MUST_BE_UNRESOLVED_KEY_PREFIX = "jira.plugins.commitacceptance.settings.mustBeUnresolved";

    /*
     * Services.
     */
    private ApplicationProperties applicationProperties;

    public AcceptanceSettingsManagerImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public AcceptanceSettings getSettings(String projectKey) {
    	AcceptanceSettings acceptanceSettings = new AcceptanceSettings();

        acceptanceSettings.setUseGlobalRules((projectKey == null) ? true : applicationProperties.getOption(getRuleOptionName(projectKey, USE_GLOBAL_RULES_KEY_PREFIX)));
        acceptanceSettings.setMustHaveIssue(applicationProperties.getOption(getRuleOptionName(projectKey, MUST_HAVE_ISSUE_KEY_PREFIX)));
        acceptanceSettings.setMustHaveIssueInProject(applicationProperties.getOption(getRuleOptionName(projectKey, MUST_HAVE_ISSUE_IN_PROJECT_KEY_PREFIX)));
        acceptanceSettings.setMustIssuesBeInProject(applicationProperties.getOption(getRuleOptionName(projectKey, MUST_ISSUES_BE_IN_PROJECT_KEY_PREFIX)));
        acceptanceSettings.setMustBeAssignedToCommiter(applicationProperties.getOption(getRuleOptionName(projectKey, MUST_BE_ASSIGNED_TO_COMMITER_KEY_PREFIX)));
        acceptanceSettings.setMustBeUnresolved(applicationProperties.getOption(getRuleOptionName(projectKey, MUST_BE_UNRESOLVED_KEY_PREFIX)));

        return acceptanceSettings;
    }

    public void setSettings(String projectKey, AcceptanceSettings acceptanceSettings) {
        applicationProperties.setOption(getRuleOptionName(projectKey, USE_GLOBAL_RULES_KEY_PREFIX), (projectKey == null) ? true : acceptanceSettings.getUseGlobalRules());
        applicationProperties.setOption(getRuleOptionName(projectKey, MUST_HAVE_ISSUE_KEY_PREFIX), acceptanceSettings.isMustHaveIssue());
        applicationProperties.setOption(getRuleOptionName(projectKey, MUST_HAVE_ISSUE_IN_PROJECT_KEY_PREFIX), acceptanceSettings.isMustHaveIssueInProject());
        applicationProperties.setOption(getRuleOptionName(projectKey, MUST_ISSUES_BE_IN_PROJECT_KEY_PREFIX), acceptanceSettings.isMustIssuesBeInProject());
        applicationProperties.setOption(getRuleOptionName(projectKey, MUST_BE_ASSIGNED_TO_COMMITER_KEY_PREFIX), acceptanceSettings.isMustBeAssignedToCommiter());
        applicationProperties.setOption(getRuleOptionName(projectKey, MUST_BE_UNRESOLVED_KEY_PREFIX), acceptanceSettings.isMustBeUnresolved());
    }

    /**
     * Returns the global or project-dependent option name for the rule.
     */
    public static String getRuleOptionName(String projectKey, String prefix) {
    	return (projectKey == null) ? prefix : prefix + "." + projectKey;
    }
}
