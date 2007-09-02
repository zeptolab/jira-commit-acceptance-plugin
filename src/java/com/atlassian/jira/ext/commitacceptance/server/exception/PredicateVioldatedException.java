package com.atlassian.jira.ext.commitacceptance.server.exception;

/**
 * Unchecked exception which indicates that a predicate was violated.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class PredicateVioldatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PredicateVioldatedException() {
	}

	public PredicateVioldatedException(String message) {
		super(message);
	}

	public PredicateVioldatedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PredicateVioldatedException(Throwable cause) {
		super(cause);
	}
}
