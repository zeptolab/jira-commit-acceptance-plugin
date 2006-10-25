package com.atlassian.jira.ext.commitacceptance.server.action;

/**
 * Read-only interface for acceptance settings.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public interface ReadOnlyAcceptanceSettings {
    boolean isMustHaveIssue();
    boolean isMustBeUnresolved();
    boolean isMustBeAssignedToCommiter();
}
