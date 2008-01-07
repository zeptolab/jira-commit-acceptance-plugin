package com.atlassian.jira.ext.commitacceptance.server.evaluator;

import com.atlassian.jira.ext.commitacceptance.server.test.Mockery;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class EvaluateServiceTests extends TestCase {// TODO add more tests
	private EvaluateService evaluateService;

	protected void setUp() throws Exception {
		super.setUp();

		evaluateService = new EvaluateService(Mockery.createProjectManager(), null, null);
	}

	public void testAccepted() {
// FIXME it depends on UserUtils that cannot be mocked
//		String result = evaluateService.acceptCommit("userName", "password", "committerName", "projectKey", "commitMessage");
//		assertTrue(isCommitAccepted(result));
	}

	/**
	 * Returns <code>true</code> if the result string returned by the evaluator
	 * means the commit was accepted.
	 */
	protected boolean isCommitAccepted(String result) {
		return result.startsWith(Boolean.TRUE.toString() + "|");
	}
}
