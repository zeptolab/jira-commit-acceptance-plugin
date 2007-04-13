package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.math.RandomUtils;

import com.atlassian.jira.ext.commitacceptance.server.exception.AcceptanceException;
import com.atlassian.jira.ext.commitacceptance.server.test.Mockery;
import com.atlassian.jira.issue.MutableIssue;

/**
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

		// test with unresolved
		for(int i = 0; i < 3; i++) {
			MutableIssue issue = Mockery.createIssue();
			issues.add(issue);
		}
		predicate.evaluate(issues);

		// test with resolved
		for(int i = 0; i < 2; i++) {
			MutableIssue issue = Mockery.createIssue();
			issue.setResolution(Mockery.createResolution().getGenericValue());
			issues.add(issue);
		}
		try {
			predicate.evaluate(issues);
			fail("AcceptanceException is expected for issue set including resolved issues");
		} catch(AcceptanceException ex) {
			// do nothing
		}
	}

	public void testAreIssuesAssignedToPredicate() {
		String assigneeName = "assignee-" + RandomUtils.nextInt();
		AreIssuesAssignedToPredicate predicate = new AreIssuesAssignedToPredicate(assigneeName);

		// test empty
		Set issues = new HashSet();
		predicate.evaluate(issues);

		// test with assigned
		for(int i = 0; i < 3; i++) {
			MutableIssue issue = Mockery.createIssue();
			issue.setAssigneeId(assigneeName);
			issues.add(issue);
		}
		predicate.evaluate(issues);

		// test with unassigned
// FIXME it depends on UserUtils that cannot be mocked
//		for(int i = 0; i < 2; i++) {
//			MutableIssue issue = Mockery.createIssue();
//			issue.setAssigneeId("--");
//			issues.add(issue);
//		}
//		predicate.evaluate(issues);
	}
}
