package com.atlassian.jira.ext.commitacceptance.server.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.atlassian.jira.config.properties.ApplicationProperties;

/**
 * FIXME
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class MockApplicationProperties implements ApplicationProperties {// TODO arg names
	private Map properties = new HashMap();

	public void refresh() {
	}

	public String getDefaultBackedString(String arg0) {
		return null;
	}

	public String getDefaultBackedText(String arg0) {
		return null;
	}

	public Locale getDefaultLocale() {
		return null;
	}

	public String getDefaultString(String arg0) {
		return null;
	}

	public String getContentType() {
		return null;
	}

	public String getEncoding() {
		return null;
	}

	public Collection getKeys() {
		return null;
	}

	public boolean exists(String arg0) {
		return false;
	}

	public boolean getOption(String arg0) {
		Boolean option = (Boolean)properties.get(arg0);
		return (option != null) ? option.booleanValue() : false;
	}

	public void setOption(String arg0, boolean arg1) {
		properties.put(arg0, Boolean.valueOf(arg1));
	}

	public String getString(String arg0) {
		return null;
	}

	public void setString(String arg0, String arg1) {
	}

	public Collection getStringsWithPrefix(String arg0) {
		return null;
	}

	public String getText(String arg0) {
		return null;
	}

	public void setText(String arg0, String arg1) {
	}
}
