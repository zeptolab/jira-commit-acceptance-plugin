package com.atlassian.jira.ext.commitacceptance.server.test;

import java.util.Locale;

import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.model.ModelEntity;

import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.util.I18nHelper;
import com.opensymphony.module.propertyset.PropertySet;

/**
 * FIXME
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class MockResolutionImpl implements Resolution {// TODO reorder + arg names
	private GenericValue genericValue = new GenericValue(new ModelEntity());

	public GenericValue getGenericValue() {
		return genericValue;
	}

	public String getId() {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		throw new UnsupportedOperationException();
	}

	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	public String getDescription() {
		throw new UnsupportedOperationException();
	}

	public void setDescription(String description) {
		throw new UnsupportedOperationException();
	}

	public Long getSequence() {
		throw new UnsupportedOperationException();
	}

	public void setSequence(Long sequence) {
		throw new UnsupportedOperationException();
	}

	public String getIconUrl() {
		throw new UnsupportedOperationException();
	}

	public void setIconUrl(String iconUrl) {
		throw new UnsupportedOperationException();
	}

	public String getNameTranslation() {
		throw new UnsupportedOperationException();
	}

	public String getDescTranslation() {
		throw new UnsupportedOperationException();
	}

	public String getNameTranslation(I18nHelper helper) {
		throw new UnsupportedOperationException();
	}

	public String getDescTranslation(I18nHelper helper) {
		throw new UnsupportedOperationException();
	}

	public String getNameTranslation(String helper) {
		throw new UnsupportedOperationException();
	}

	public String getDescTranslation(String helper) {
		throw new UnsupportedOperationException();
	}

	public void setTranslation(String arg0, String arg1, String arg2, Locale arg3) {
		throw new UnsupportedOperationException();
	}

	public void deleteTranslation(String arg0, Locale arg1) {
		throw new UnsupportedOperationException();
	}

	public PropertySet getPropertySet() {
		throw new UnsupportedOperationException();
	}

	public int compareTo(Object arg0) {
		throw new UnsupportedOperationException();
	}
}
