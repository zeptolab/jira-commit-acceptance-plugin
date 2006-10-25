package com.atlassian.jira.ext.commitacceptance.server.action;


/**
 * Manages the site-wide acceptance settings.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public interface AcceptanceSettingsManager {
    ReadOnlyAcceptanceSettings getSettings();
    void setSettings(AcceptanceSettings acceptanceSettings);
}
