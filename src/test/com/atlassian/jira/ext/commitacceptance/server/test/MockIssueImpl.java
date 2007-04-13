package com.atlassian.jira.ext.commitacceptance.server.test;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
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
public class MockIssueImpl implements MutableIssue {// TODO reorder + arg names
	private String key;
	private GenericValue resolution;
	private String assigneeId;

	public Map getModifiedFields() {
		throw new UnsupportedOperationException();
	}

	public void resetModifiedFields() {
		throw new UnsupportedOperationException();
	}

	public void setAffectedVersions(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public void setAssignee(User arg0) {
		throw new UnsupportedOperationException();
	}

	public void setAssigneeId(String assigneeId) {
		this.assigneeId = assigneeId;
	}

	public void setComponents(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public void setCreated(Timestamp arg0) {
		throw new UnsupportedOperationException();
	}

	public void setCustomFieldValue(CustomField arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	public void setDescription(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setDueDate(Timestamp arg0) {
		throw new UnsupportedOperationException();
	}

	public void setEnvironment(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setEstimate(Long arg0) {
		throw new UnsupportedOperationException();
	}

	public void setExternalFieldValue(String arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	public void setFixVersions(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public void setIssueType(GenericValue arg0) {
		throw new UnsupportedOperationException();
	}

	public void setIssueTypeId(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setKey(String arg0) {
		this.key = key;
	}

	public void setOriginalEstimate(Long arg0) {
		throw new UnsupportedOperationException();
	}

	public void setParentId(Long arg0) {
		throw new UnsupportedOperationException();
	}

	public void setPriority(GenericValue arg0) {
		throw new UnsupportedOperationException();
	}

	public void setPriorityId(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setProject(GenericValue arg0) {
		throw new UnsupportedOperationException();
	}

	public void setReporter(User arg0) {
		throw new UnsupportedOperationException();
	}

	public void setReporterId(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setResolution(GenericValue resolution) {
		this.resolution = resolution;
	}

	public void setResolutionId(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setSecurityLevel(GenericValue arg0) {
		throw new UnsupportedOperationException();
	}

	public void setStatus(GenericValue arg0) {
		throw new UnsupportedOperationException();
	}

	public void setStatusId(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setSummary(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setTimeSpent(Long arg0) {
		throw new UnsupportedOperationException();
	}

	public void setUpdated(Timestamp arg0) {
		throw new UnsupportedOperationException();
	}

	public void setVotes(Long arg0) {
		throw new UnsupportedOperationException();
	}

	public void setWorkflowId(Long arg0) {
		throw new UnsupportedOperationException();
	}

	public Collection getAffectedVersions() {
		throw new UnsupportedOperationException();
	}

	public User getAssignee() {
		throw new UnsupportedOperationException();
	}

	public String getAssigneeId() {
		return assigneeId;
	}

	public Collection getAttachments() {
		throw new UnsupportedOperationException();
	}

	public Collection getComponents() {
		throw new UnsupportedOperationException();
	}

	public Timestamp getCreated() {
		throw new UnsupportedOperationException();
	}

	public Object getCustomFieldValue(CustomField arg0) {
		throw new UnsupportedOperationException();
	}

	public String getDescription() {
		throw new UnsupportedOperationException();
	}

	public Timestamp getDueDate() {
		throw new UnsupportedOperationException();
	}

	public String getEnvironment() {
		throw new UnsupportedOperationException();
	}

	public Long getEstimate() {
		throw new UnsupportedOperationException();
	}

	public Object getExternalFieldValue(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Collection getFixVersions() {
		throw new UnsupportedOperationException();
	}

	public Long getId() {
		throw new UnsupportedOperationException();
	}

	public IssueRenderContext getIssueRenderContext() {
		throw new UnsupportedOperationException();
	}

	public GenericValue getIssueType() {
		throw new UnsupportedOperationException();
	}

	public IssueType getIssueTypeObject() {
		throw new UnsupportedOperationException();
	}

	public String getKey() {
		return key;
	}

	public Long getOriginalEstimate() {
		throw new UnsupportedOperationException();
	}

	public GenericValue getParent() {
		throw new UnsupportedOperationException();
	}

	public Long getParentId() {
		throw new UnsupportedOperationException();
	}

	public Issue getParentObject() {
		throw new UnsupportedOperationException();
	}

	public GenericValue getPriority() {
		throw new UnsupportedOperationException();
	}

	public Priority getPriorityObject() {
		throw new UnsupportedOperationException();
	}

	public GenericValue getProject() {
		throw new UnsupportedOperationException();
	}

	public Project getProjectObject() {
		throw new UnsupportedOperationException();
	}

	public User getReporter() {
		throw new UnsupportedOperationException();
	}

	public String getReporterId() {
		throw new UnsupportedOperationException();
	}

	public GenericValue getResolution() {
		return resolution;
	}

	public Resolution getResolutionObject() {
		return (resolution != null) ? new MockResolutionImpl() : null;// TODO pointless?
	}

	public GenericValue getSecurityLevel() {
		throw new UnsupportedOperationException();
	}

	public Long getSecurityLevelId() {
		throw new UnsupportedOperationException();
	}

	public GenericValue getStatus() {
		throw new UnsupportedOperationException();
	}

	public Status getStatusObject() {
		throw new UnsupportedOperationException();
	}

	public Collection getSubTaskObjects() {
		throw new UnsupportedOperationException();
	}

	public Collection getSubTasks() {
		throw new UnsupportedOperationException();
	}

	public String getSummary() {
		throw new UnsupportedOperationException();
	}

	public Long getTimeSpent() {
		throw new UnsupportedOperationException();
	}

	public Timestamp getUpdated() {
		throw new UnsupportedOperationException();
	}

	public Long getVotes() {
		throw new UnsupportedOperationException();
	}

	public Long getWorkflowId() {
		throw new UnsupportedOperationException();
	}

	public boolean isCreated() {
		throw new UnsupportedOperationException();
	}

	public boolean isEditable() {
		throw new UnsupportedOperationException();
	}

	public boolean isSubTask() {
		throw new UnsupportedOperationException();
	}

	public GenericValue getGenericValue() {
		throw new UnsupportedOperationException();
	}

	public Long getLong(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getString(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Timestamp getTimestamp(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void store() {
		throw new UnsupportedOperationException();
	}
}
