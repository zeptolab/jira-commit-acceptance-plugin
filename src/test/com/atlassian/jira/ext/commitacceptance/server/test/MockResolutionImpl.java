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
public class MockResolutionImpl implements Resolution {
	private GenericValue genericValue = new GenericValue(new ModelEntity());

	public GenericValue getGenericValue() {
		return genericValue;
	}

	public String getId() {
		return null;
	}

	public String getName() {
		return null;
	}

	public void setName(String name) {
	}

	public String getDescription() {
		return null;
	}

	public void setDescription(String description) {
	}

	public Long getSequence() {
		return null;
	}

	public void setSequence(Long sequence) {
	}

	public String getIconUrl() {
		return null;
	}

	public void setIconUrl(String iconUrl) {
	}

	public String getNameTranslation() {
		return null;
	}

	public String getDescTranslation() {
		return null;
	}

	public String getNameTranslation(I18nHelper helper) {
		return null;
	}

	public String getDescTranslation(I18nHelper helper) {
		return null;
	}

	public String getNameTranslation(String helper) {
		return null;
	}

	public String getDescTranslation(String helper) {
		return null;
	}

	public void setTranslation(String arg0, String arg1, String arg2, Locale arg3) {

	}

	public void deleteTranslation(String arg0, Locale arg1) {
	}

	public PropertySet getPropertySet() {
		return null;
	}

	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
