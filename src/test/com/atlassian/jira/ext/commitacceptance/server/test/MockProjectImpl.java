package com.atlassian.jira.ext.commitacceptance.server.test;

import java.util.Collection;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.project.Project;
import com.opensymphony.user.User;

/**
 * FIXME
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class MockProjectImpl implements Project {// TODO reorder + arg names
	public Long getAssigneeType() {
		throw new UnsupportedOperationException();
	}

	public Collection getComponents() {
		throw new UnsupportedOperationException();
	}

	public Long getCounter() {
		throw new UnsupportedOperationException();
	}

	public String getDescription() {
		throw new UnsupportedOperationException();
	}

	public GenericValue getGenericValue() {
		throw new UnsupportedOperationException();
	}

	public Long getId() {
		throw new UnsupportedOperationException();
	}

	public String getKey() {
		throw new UnsupportedOperationException();
	}

	public User getLead() {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		throw new UnsupportedOperationException();
	}

	public GenericValue getProjectCategory() {
		throw new UnsupportedOperationException();
	}

	public String getUrl() {
		throw new UnsupportedOperationException();
	}

	public Collection getVersions() {
		throw new UnsupportedOperationException();
	}
}