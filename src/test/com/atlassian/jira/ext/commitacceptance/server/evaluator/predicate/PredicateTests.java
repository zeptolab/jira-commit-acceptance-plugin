package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.math.RandomUtils;

import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesAssignedToPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesUnresolvedPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.HasIssuePredicate;
import com.atlassian.jira.ext.commitacceptance.server.exception.AcceptanceException;
import com.atlassian.jira.ext.commitacceptance.server.test.Mockery;

/**
 * FIXME
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class PredicateTests extends TestCase {
	public void testHasIssuePredicate() {
		HasIssuePredicate predicate = new HasIssuePredicate();

		// test empty
		Set issues = new HashSet();
		try {
			predicate.evaluate(issues);
			fail("AcceptanceException is expected for empty issue set");
		} catch(AcceptanceException ex) {
			// do nothing
		}

		// test non-empty
		issues.add(Mockery.createIssue());
		predicate.evaluate(issues);
	}

	public void testAreIssuesUnresolvedPredicate() {
		AreIssuesUnresolvedPredicate predicate = new AreIssuesUnresolvedPredicate();

		// test empty
		Set issues = new HashSet();
		predicate.evaluate(issues);

		// TODO
//		Issue issue = Mockery.createIssue();
//		issue.getGenericValue().set("status", );// TODO status mock?
	}

	public void testAreIssuesAssignedToPredicate() {
		String assigneeName = "assignee-" + RandomUtils.nextInt();
		AreIssuesAssignedToPredicate predicate = new AreIssuesAssignedToPredicate(assigneeName);

		// test empty
		Set issues = new HashSet();
		predicate.evaluate(issues);

		// TODO
	}
}
