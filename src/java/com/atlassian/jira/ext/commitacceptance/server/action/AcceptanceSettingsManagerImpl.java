package com.atlassian.jira.ext.commitacceptance.server.action;

import com.atlassian.jira.config.properties.ApplicationProperties;


/**
 * Manages the site-wide acceptance settings.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class AcceptanceSettingsManagerImpl implements AcceptanceSettingsManager {
    private static final String mustHaveIssueKey = "jira.plugins.commitacceptance.settings.MustHaveIssue";
    private static final String mustBeAssignedToCommiterKey = "jira.plugins.commitacceptance.settings.MustBeAssignedToCommiter";
    private static final String mustBeUnresolvedKey = "jira.plugins.commitacceptance.settings.MustBeUnresolved";
    
    /*
     * Stores the side-wide acceptance settings.
     */
    private AcceptanceSettings settings = new AcceptanceSettings();

    /*
     * JIRA service.
     */
    private ApplicationProperties applicationProperties;

    public AcceptanceSettingsManagerImpl(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
        
        // Load settings.
        settings.setMustHaveIssue(applicationProperties.getOption(mustHaveIssueKey));
        settings.setMustBeAssignedToCommiter(applicationProperties.getOption(mustBeAssignedToCommiterKey));
        settings.setMustBeUnresolved(applicationProperties.getOption(mustBeUnresolvedKey));
    }
    
    public ReadOnlyAcceptanceSettings getSettings() {
        return settings;
    }

    public void setSettings(AcceptanceSettings acceptanceSettings) {
        this.settings = acceptanceSettings;
        
        // Save settings.
        applicationProperties.setOption(mustHaveIssueKey, settings.isMustHaveIssue());
        applicationProperties.setOption(mustBeAssignedToCommiterKey, settings.isMustBeAssignedToCommiter());
        applicationProperties.setOption(mustBeUnresolvedKey, settings.isMustBeUnresolved());
    }
}
