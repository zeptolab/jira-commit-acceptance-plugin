package com.atlassian.jira.ext.commitacceptance.server.action;

import org.ofbiz.core.entity.GenericValue;

/**
 * Manages the global or project-dependent commit acceptance settings.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public interface AcceptanceSettingsManager {
	/**
	 * Returns the settings saved for the given project or globally.
	 * @param project if <code>null</code>, the global rules will be returned.
	 */
	AcceptanceSettings getSettings(GenericValue project);
	/**
	 * Saves the settings for the given project or globally.
	 * @param project if <code>null</code>, these will be saved as global rules.
	 */
    void setSettings(GenericValue project, AcceptanceSettings acceptanceSettings);
}
