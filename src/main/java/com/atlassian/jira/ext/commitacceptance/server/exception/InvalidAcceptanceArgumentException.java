package com.atlassian.jira.ext.commitacceptance.server.exception;

/**
 * Unchecked exception which indicates that an invalid argument was
 * passed to the acceptance evaluator.
 *
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class InvalidAcceptanceArgumentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidAcceptanceArgumentException() {
	}

	public InvalidAcceptanceArgumentException(String message) {
		super(message);
	}

	public InvalidAcceptanceArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidAcceptanceArgumentException(Throwable cause) {
		super(cause);
	}
}
