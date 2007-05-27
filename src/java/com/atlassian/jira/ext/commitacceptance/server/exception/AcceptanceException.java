package com.atlassian.jira.ext.commitacceptance.server.exception;

/**
 * Unchecked exception which indicates that a commit cannot be accepted.
 * 
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
public class AcceptanceException extends RuntimeException {// TODO should be CommitRejectedException
	private static final long serialVersionUID = 1L;

	public AcceptanceException() {
	}

	public AcceptanceException(String message) {
		super(message);
	}

	public AcceptanceException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcceptanceException(Throwable cause) {
		super(cause);
	}
}
