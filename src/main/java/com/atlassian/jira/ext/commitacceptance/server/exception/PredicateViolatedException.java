package com.atlassian.jira.ext.commitacceptance.server.exception;

/**
 * Unchecked exception which indicates that a predicate was violated.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class PredicateViolatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PredicateViolatedException() {
	}

	public PredicateViolatedException(String message) {
		super(message);
	}

	public PredicateViolatedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PredicateViolatedException(Throwable cause) {
		super(cause);
	}
}
