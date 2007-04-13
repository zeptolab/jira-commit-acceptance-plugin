package com.atlassian.jira.ext.commitacceptance.server.test;

import java.sql.Timestamp;
import java.util.Collection;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.opensymphony.user.User;

/**
 * FIXME
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class MockIssueImpl implements Issue {// TODO do it with a mock lib?
	public Collection getAffectedVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getAssignee() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAssigneeId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getAttachments() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getCreated() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getCustomFieldValue(CustomField arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getDueDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getEstimate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getExternalFieldValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getFixVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public IssueRenderContext getIssueRenderContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getIssueType() {
		// TODO Auto-generated method stub
		return null;
	}

	public IssueType getIssueTypeObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getOriginalEstimate() {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getParentId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Issue getParentObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getPriority() {
		// TODO Auto-generated method stub
		return null;
	}

	public Priority getPriorityObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	public Project getProjectObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getReporter() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReporterId() {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getResolution() {
		// TODO Auto-generated method stub
		return null;
	}

	public Resolution getResolutionObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getSecurityLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getSecurityLevelId() {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	public Status getStatusObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getSubTaskObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getSubTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getTimeSpent() {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getUpdated() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getVotes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getWorkflowId() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCreated() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubTask() {
		// TODO Auto-generated method stub
		return false;
	}

	public GenericValue getGenericValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getLong(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void store() {
		// TODO Auto-generated method stub

	}// TODO arg names
}
