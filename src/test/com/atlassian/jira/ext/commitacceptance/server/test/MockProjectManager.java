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
public class MockProjectManager implements ProjectManager {

	public List convertToProjects(Collection arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getComponent(Long arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getComponent(GenericValue arg0, String arg1) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getComponents(GenericValue arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getComponents(List arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public User getDefaultAssignee(GenericValue arg0, GenericValue arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getNextId(GenericValue arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	public GenericValue getProject(GenericValue arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getProject(Long arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getProjectByKey(String arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getProjectByName(String arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getProjectCategories() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getProjectCategory(Long arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getProjectCategoryByName(String arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public GenericValue getProjectCategoryFromProject(GenericValue arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public Project getProjectObj(Long arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public Project getProjectObjByKey(String arg0) {
		return arg0.startsWith("EXIST") ? Mockery.createProject() : null;
	}

	public Collection getProjects() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getProjectsByLead(User arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getProjectsFromProjectCategory(GenericValue arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getProjectsWithNoCategory() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDefaultAssignee(GenericValue arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDefaultAssignee(GenericValue arg0, GenericValue arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void refreshProjectDependencies(GenericValue arg0) throws DataAccessException {
		// TODO Auto-generated method stub

	}

	public void setProjectCategory(GenericValue arg0, GenericValue arg1) throws DataAccessException {
		// TODO Auto-generated method stub

	}

	public void updateProject(GenericValue arg0) throws DataAccessException {
		// TODO Auto-generated method stub

	}

	public void updateProjectCategory(GenericValue arg0) throws DataAccessException {
		// TODO Auto-generated method stub

	}// TODO arg names
}
