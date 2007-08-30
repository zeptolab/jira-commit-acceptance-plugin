package com.atlassian.jira.ext.commitacceptance.server.test;

import java.util.Collection;
import java.util.List;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.opensymphony.user.User;

/**
 * FIXME
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class MockProjectManager implements ProjectManager {// TODO reorder + arg names
	public List convertToProjects(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	public GenericValue getComponent(Long arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public GenericValue getComponent(GenericValue arg0, String arg1) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public Collection getComponents(GenericValue arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public Collection getComponents(List arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public User getDefaultAssignee(GenericValue arg0, GenericValue arg1) {
		throw new UnsupportedOperationException();
	}

	public long getNextId(GenericValue arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public GenericValue getProject(GenericValue arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public GenericValue getProject(Long arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public GenericValue getProjectByKey(String arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public GenericValue getProjectByName(String arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public Collection getProjectCategories() throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public GenericValue getProjectCategory(Long arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public GenericValue getProjectCategoryByName(String arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public GenericValue getProjectCategoryFromProject(GenericValue arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public Project getProjectObj(Long arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public Project getProjectObjByKey(String arg0) {
		return arg0.startsWith("EXIST") ? Mockery.createProject() : null;// TODO
	}

	public Collection getProjects() throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public Collection getProjectsByLead(User arg0) {
		throw new UnsupportedOperationException();
	}

	public Collection getProjectsFromProjectCategory(GenericValue arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public Collection getProjectsWithNoCategory() throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public boolean isDefaultAssignee(GenericValue arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean isDefaultAssignee(GenericValue arg0, GenericValue arg1) {
		throw new UnsupportedOperationException();
	}

	public void refresh() {
		throw new UnsupportedOperationException();
	}

	public void refreshProjectDependencies(GenericValue arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public void setProjectCategory(GenericValue arg0, GenericValue arg1) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public void updateProject(GenericValue arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public void updateProjectCategory(GenericValue arg0) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	public Project getProjectObjByName(String arg0) {
		throw new UnsupportedOperationException();
	}

}
