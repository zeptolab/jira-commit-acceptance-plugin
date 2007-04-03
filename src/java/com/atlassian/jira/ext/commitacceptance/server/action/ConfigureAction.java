package com.atlassian.jira.ext.commitacceptance.server.action;

import com.atlassian.jira.web.action.JiraWebActionSupport;

/**
 * Loads and saves the site-wide acceptance settings and
 * handles the UI related.
 *
 * @see AcceptanceSettings
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class ConfigureAction extends JiraWebActionSupport {
	private static final long serialVersionUID = 1L;

	private AcceptanceSettingsManager settingsManager;

	/**
	 * TODO or <code>null</code>
	 */
	private String projectKey = null;
	/**
	 * Commit acceptance settings to persist.
	 */
    private AcceptanceSettings settings = new AcceptanceSettings();
    /**
     * TODO
     */
    private String submitted;

	public ConfigureAction(AcceptanceSettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}

    public String execute() throws Exception {
        // apply new settings
        if (submitted != null) {
            settingsManager.setSettings(null, settings);// TODO pass project
        }
        return SUCCESS;
    }

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public boolean isMustBeAssignedToCommiter() {
		return settingsManager.getSettings(null).isMustBeAssignedToCommiter();// TODO pass project
	}

	public void setMustBeAssignedToCommiter(boolean mustBeAssignedToCommiter) {
        settings.setMustBeAssignedToCommiter(mustBeAssignedToCommiter);
	}

	public boolean isMustBeUnresolved() {
		return settingsManager.getSettings(null).isMustBeUnresolved();// TODO pass project
	}

	public void setMustBeUnresolved(boolean mustBeUnresolved) {
        settings.setMustBeUnresolved(mustBeUnresolved);
	}

	public boolean isMustHaveIssue() {
		return settingsManager.getSettings(null).isMustHaveIssue();// TODO pass project
	}

	public void setMustHaveIssue(boolean mustHaveIssue) {
        settings.setMustHaveIssue(mustHaveIssue);
	}

    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
}
