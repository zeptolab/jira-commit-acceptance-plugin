package com.atlassian.jira.ext.commitacceptance.client;

/**
 * Client-side component to be installed to the SCM system. 
 * <p>
 * Fires an HTTP POST request passing the commit information to the serverside and
 * receives a single boolean written as text to the response. Based on
 * the boolean, it accepts (<code>true</code>) or rejects (<code>false</code>)
 * the commit.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class Client {
	/**
	 * Arguments:
	 * <ol>
	 * 	<li>log message from the SCM system</li>
	 * </ol>
	 */
	public static int main(String[] args) {
		// parse commandline args
		// String logMessage = args[0];
		// TODO

		// fire request
		// TODO

		// parse response
		boolean accepted = true; // TODO

		// return error code: 0 if accepted or 1 if rejected
		return accepted ? 0 : 1;
	}
}
