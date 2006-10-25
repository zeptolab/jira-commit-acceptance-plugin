package com.atlassian.jira.ext.commitacceptance.server.action;

import com.atlassian.jira.web.action.JiraWebActionSupport;


/**
 * Loads and saves the site-wide acceptance settings and
 * handles the UI related.
 * @see AcceptanceSettings
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class ConfigureAction extends JiraWebActionSupport {
	private static final long serialVersionUID = 1L;

	private AcceptanceSettingsManager settingsManager;
    private AcceptanceSettings settings = new AcceptanceSettings();
    private String submitted;
    
	public ConfigureAction(AcceptanceSettingsManager settingsManager)
	{
		this.settingsManager = settingsManager;
	}
	
    public String execute() throws Exception {
        // apply new settings
        if (submitted != null) {
            settingsManager.setSettings(settings);
        }
        return SUCCESS;
    }

	public boolean isMustBeAssignedToCommiter() {
		return settingsManager.getSettings().isMustBeAssignedToCommiter();
	}

	public void setMustBeAssignedToCommiter(boolean mustBeAssignedToCommiter) {
        settings.setMustBeAssignedToCommiter(mustBeAssignedToCommiter);
	}

	public boolean isMustBeUnresolved() {
		return settingsManager.getSettings().isMustBeUnresolved();
	}

	public void setMustBeUnresolved(boolean mustBeUnresolved) {
        settings.setMustBeUnresolved(mustBeUnresolved);
	}

	public boolean isMustHaveIssue() {
		return settingsManager.getSettings().isMustHaveIssue();
	}

	public void setMustHaveIssue(boolean mustHaveIssue) {
        settings.setMustHaveIssue(mustHaveIssue);
	}

    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
}
