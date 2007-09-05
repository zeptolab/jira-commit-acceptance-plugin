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
    /* TODO */
    public static int ONLY_FOR_THIS = 0;
    public static int ONE_FOR_THIS = 1;
    public static int FOR_ANY = 2;

    /**
	 * If <code>true</code> the global settings override
	 * the project-specific settings.
	 */
	private boolean useGlobalRules;

	/**
     * If <code>true</code> the commit message must contain
     * at least one valid issue key.
     */
    private boolean mustHaveIssue;

    /**
     * If <code>true</code>, all the issues must be unresolved.
     */
    private boolean mustBeUnresolved;

    /**
     * If <code>true</code>, all the issues must be assigned to
     * the commiter.
     */
    private boolean mustBeAssignedToCommiter;

    /**
     * TODO
     */
    private int acceptIssuesFor;

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

	public int getAcceptIssuesFor() {
		return acceptIssuesFor;
	}

	public void setAcceptIssuesFor(int acceptIssuesFor) {
		this.acceptIssuesFor = acceptIssuesFor;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		AcceptanceSettings other = (AcceptanceSettings)obj;
		return (useGlobalRules == other.getUseGlobalRules()) &&
				(mustHaveIssue == other.isMustHaveIssue()) &&
				(mustBeUnresolved == other.isMustBeUnresolved()) &&
				(mustBeAssignedToCommiter == other.isMustBeAssignedToCommiter() &&
				(acceptIssuesFor == other.getAcceptIssuesFor()));
	}

	public int hashCode() {
		return new HashCodeBuilder(79, 11).append(useGlobalRules).
					append(mustHaveIssue).
					append(mustBeUnresolved).
					append(mustBeAssignedToCommiter).
					append(acceptIssuesFor).hashCode();
	}
}
