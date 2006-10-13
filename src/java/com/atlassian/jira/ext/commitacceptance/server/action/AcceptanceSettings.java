package com.atlassian.jira.ext.commitacceptance.server.action;

/**
 * Wraps the site-wide acceptance settings.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class AcceptanceSettings {
	/**
	 * If <code>true</code> the commit message must contain
	 * at least valid issue key.
	 */
	private boolean mustHaveIssue;

	/**
	 * If <code>true</code>, all the issues must be assigned to
	 * the user defined by the other property.
	 * @see #assigneeName
	 */
	private boolean mustBeAssignedToSpecificUser;
	/**
	 * Login name of the assignee required.
	 * Its value is used only the boolean is <code>true</code>,
	 * otherwise it simply stores the last selected user so
	 * that the UI can be re-initialized with that.
	 * @see #mustBeAssignedToSpecificUser
	 */
	private String assigneeName;

	/**
	 * If <code>true</code>, all the issues must be in 
	 * the state defined by the other property.
	 * @see #statusName
	 */
	private boolean mustBeInSpecificState;
	/**
	 * Name of the status required.
	 * Its value is used only the boolean is <code>true</code>,
	 * otherwise it simply stores the last selected state so
	 * that the UI can be re-initialized with that.
	 * @see #mustBeInSpecificState
	 */
	private String statusName; // TODO different type?

    /**
     * If <code>true</code>, all the issues must be unresolved.
     */
    private boolean mustBeUnresolved;
    
	public boolean isMustHaveIssue() {
		return mustHaveIssue;
	}

	public void setMustHaveIssue(boolean mustHaveIssue) {
		this.mustHaveIssue = mustHaveIssue;
	}

	public boolean isMustBeAssignedToSpecificUser() {
		return mustBeAssignedToSpecificUser;
	}

	public void setMustBeAssignedToSpecificUser(boolean mustBeAssignedToSpecificUser) {
		this.mustBeAssignedToSpecificUser = mustBeAssignedToSpecificUser;
	}

	public String getAssigneeName() {
		return assigneeName;
	}

	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	public boolean isMustBeInSpecificState() {
		return mustBeInSpecificState;
	}

	public void setMustBeInSpecificState(boolean mustBeInSpecificState) {
		this.mustBeInSpecificState = mustBeInSpecificState;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
    
    public boolean isMustBeUnresolved() {
        return mustBeUnresolved;
    }

    public void setMustBeUnresolved(boolean mustBeUnresolved) {
        this.mustBeUnresolved = mustBeUnresolved;
    }
}
