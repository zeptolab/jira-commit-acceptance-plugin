package com.atlassian.jira.ext.commitacceptance.server.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.atlassian.jira.config.properties.ApplicationProperties;

/**
 * Mock implementation that stores the properties in memory.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class MockApplicationProperties implements ApplicationProperties {// TODO reorder + arg names
	private Map properties = new HashMap();

	public void refresh() {
		throw new UnsupportedOperationException();
	}

	public String getDefaultBackedString(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getDefaultBackedText(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Locale getDefaultLocale() {
		throw new UnsupportedOperationException();
	}

	public String getDefaultString(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getContentType() {
		throw new UnsupportedOperationException();
	}

	public String getEncoding() {
		throw new UnsupportedOperationException();
	}

	public Collection getKeys() {
		throw new UnsupportedOperationException();
	}

	public boolean exists(String arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean getOption(String arg0) {
		Boolean option = (Boolean)properties.get(arg0);
		return (option != null) ? option.booleanValue() : false;
	}

	public void setOption(String arg0, boolean arg1) {
		properties.put(arg0, Boolean.valueOf(arg1));
	}

	public String getString(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setString(String arg0, String arg1) {
		throw new UnsupportedOperationException();
	}

	public Collection getStringsWithPrefix(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getText(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setText(String arg0, String arg1) {
		throw new UnsupportedOperationException();
	}
}
