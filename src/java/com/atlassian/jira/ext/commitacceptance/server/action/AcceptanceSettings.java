package com.atlassian.jira.ext.commitacceptance.server.action;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Wraps the commit acceptance settings for one project
 * or globally.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class AcceptanceSettings {
	/**
	 * If <code>true</code> the global settings override
	 * the project-specific settings.
	 */
	private boolean useGlobalRules;
    /**
     * If <code>true</code> the commit message must contain
     * at least valid issue key.
     */
    private boolean mustHaveIssue;

    /**
     * If <code>true</code>, all the issues must be assigned to
     * the commiter.
     */
    private boolean mustBeAssignedToCommiter;

    /**
     * If <code>true</code>, all the issues must be unresolved.
     */
    private boolean mustBeUnresolved;


    public boolean getUseGlobalRules() {
		return useGlobalRules;
	}

	public void setUseGlobalRules(boolean useGlobalRules) {
		this.useGlobalRules = useGlobalRules;
	}

	public boolean isMustHaveIssue() {
        return mustHaveIssue;
    }

    public void setMustHaveIssue(boolean mustHaveIssue) {
        this.mustHaveIssue = mustHaveIssue;
    }

    public boolean isMustBeUnresolved() {
        return mustBeUnresolved;
    }

    public void setMustBeUnresolved(boolean mustBeUnresolved) {
        this.mustBeUnresolved = mustBeUnresolved;
    }

    public boolean isMustBeAssignedToCommiter() {
        return mustBeAssignedToCommiter;
    }

    public void setMustBeAssignedToCommiter(boolean mustBeAssignedToCommiter) {
        this.mustBeAssignedToCommiter = mustBeAssignedToCommiter;
    }

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		AcceptanceSettings other = (AcceptanceSettings)obj;
		return (useGlobalRules == other.getUseGlobalRules()) &&
				(mustHaveIssue == other.isMustHaveIssue()) &&
				(mustBeUnresolved == other.isMustBeUnresolved()) &&
				(mustBeAssignedToCommiter == other.isMustBeAssignedToCommiter());
	}

	public int hashCode() {
		return new HashCodeBuilder(79, 11).append(useGlobalRules).append(mustHaveIssue).append(mustBeUnresolved).append(mustBeAssignedToCommiter).hashCode();
	}
}
