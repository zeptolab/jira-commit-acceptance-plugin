package com.atlassian.jira.ext.commitacceptance.server.action;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.config.properties.ApplicationProperties;

/**
 * Manages the site-wide acceptance settings.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class AcceptanceSettingsManagerImpl implements AcceptanceSettingsManager {
	/**
	 * Prefixes for rule options. For global rules these are used as they are,
	 * for project-dependent rules the key of the project will be appended.
	 * @see #getOptionName(GenericValue, String)
	 */
    private static final String MUST_HAVE_ISSUE_KEY_PREFIX = "jira.plugins.commitacceptance.settings.MustHaveIssue";
    private static final String MUST_BE_ASSIGNED_TO_COMMITER_KEY_PREFIX = "jira.plugins.commitacceptance.settings.MustBeAssignedToCommiter";
    private static final String MUST_BE_UNRESOLVED_KEY_PREFIX = "jira.plugins.commitacceptance.settings.MustBeUnresolved";

    /**
     * JIRA service.
     */
    private ApplicationProperties applicationProperties;

    public AcceptanceSettingsManagerImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public AcceptanceSettings getSettings(String projectKey) {
    	AcceptanceSettings acceptanceSettings = new AcceptanceSettings();

        acceptanceSettings.setMustHaveIssue(applicationProperties.getOption(getOptionName(projectKey, MUST_HAVE_ISSUE_KEY_PREFIX)));
        acceptanceSettings.setMustBeAssignedToCommiter(applicationProperties.getOption(getOptionName(projectKey, MUST_BE_ASSIGNED_TO_COMMITER_KEY_PREFIX)));
        acceptanceSettings.setMustBeUnresolved(applicationProperties.getOption(getOptionName(projectKey, MUST_BE_UNRESOLVED_KEY_PREFIX)));

        return acceptanceSettings;
    }

    public void setSettings(String projectKey, AcceptanceSettings acceptanceSettings) {
        applicationProperties.setOption(getOptionName(projectKey, MUST_HAVE_ISSUE_KEY_PREFIX), acceptanceSettings.isMustHaveIssue());
        applicationProperties.setOption(getOptionName(projectKey, MUST_BE_ASSIGNED_TO_COMMITER_KEY_PREFIX), acceptanceSettings.isMustBeAssignedToCommiter());
        applicationProperties.setOption(getOptionName(projectKey, MUST_BE_UNRESOLVED_KEY_PREFIX), acceptanceSettings.isMustBeUnresolved());
    }

    /**
     * Returns the global or project-dependent property name for the setting.
     */
    public static String getOptionName(String projectKey, String prefix) {
    	return (projectKey == null) ? prefix : prefix + "." + projectKey;
    }
}
