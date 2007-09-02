package com.atlassian.jira.ext.commitacceptance.server.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;

/**
 * Loads and saves the commit acceptance settings and handles the UI related.
 *
 * @see AcceptanceSettings
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class ConfigureAction extends JiraWebActionSupport {
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ConfigureAction.class);

	/*
	 * Services.
	 */
	private ProjectManager projectManager;
	private AcceptanceSettingsManager acceptanceSettingsManager;

	/**
	 * Key of the selected project or empty string for global settings.
	 */
	private String projectKey = "";
	/**
	 * Commit acceptance settings edited.
	 */
    private AcceptanceSettings settings = new AcceptanceSettings();
    /**
     * Submission identifier value.
     */
    private String submitted;

	public ConfigureAction(ProjectManager projectManager, AcceptanceSettingsManager acceptanceSettingsManager) {
		this.projectManager = projectManager;
		this.acceptanceSettingsManager = acceptanceSettingsManager;
	}

    public String execute() throws Exception {
    	// reject if user has no admin rights
    	if(!isHasPermission(Permissions.ADMINISTER)) {
    		return ERROR;
    	}

        if (submitted == null) {
            // load old settings
        	logger.info("Loading commit acceptance settings for [" + projectKey + "]");
    		settings = acceptanceSettingsManager.getSettings(StringUtils.trimToNull(projectKey));
        } else {
            // save new settings
        	logger.info("Saving commit acceptance settings for [" + projectKey + "]");
           	acceptanceSettingsManager.setSettings(StringUtils.trimToNull(projectKey), settings);
        }
        return SUCCESS;
    }

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

    public boolean getUseGlobalRules() {
    	return settings.getUseGlobalRules();
    }

    public void setUseGlobalRules(boolean useGlobalRules) {
    	settings.setUseGlobalRules(useGlobalRules);
    }

	public boolean isMustHaveIssue() {
		return settings.isMustHaveIssue();
	}

	public void setMustHaveIssue(boolean mustHaveIssue) {
        settings.setMustHaveIssue(mustHaveIssue);
	}

	public boolean isMustHaveIssueInProject() {
		return settings.isMustHaveIssueInProject();
	}

	public void setMustHaveIssueInProject(boolean mustHaveIssue) {
		settings.setMustHaveIssueInProject(mustHaveIssue);
	}

	public boolean isMustIssuesBeInProject() {
		return settings.isMustIssuesBeInProject();
	}

	public void setMustIssuesBeInProject(boolean mustIssuesBeInProject) {
		settings.setMustIssuesBeInProject(mustIssuesBeInProject);
	}

	public boolean isMustBeUnresolved() {
		return settings.isMustBeUnresolved();
	}

	public void setMustBeUnresolved(boolean mustBeUnresolved) {
        settings.setMustBeUnresolved(mustBeUnresolved);
	}

    public boolean isMustBeAssignedToCommiter() {
		return settings.isMustBeAssignedToCommiter();
	}

	public void setMustBeAssignedToCommiter(boolean mustBeAssignedToCommiter) {
        settings.setMustBeAssignedToCommiter(mustBeAssignedToCommiter);
	}

	public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }

	/**
	 * Returns the list of all available projects.
	 */
    public List getProjects() {
    	List projects = new ArrayList(projectManager.getProjects());
    	Collections.sort(projects, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				return ((GenericValue)obj1).getString("key").compareTo(((GenericValue)obj2).getString("key"));
			}
    	});
    	return projects;
    }
}
