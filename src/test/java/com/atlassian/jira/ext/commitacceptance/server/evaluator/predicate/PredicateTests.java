package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.math.RandomUtils;

import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.ext.commitacceptance.server.test.Mockery;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;

/**
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class PredicateTests extends TestCase {
	public void testHasIssuePredicate() {
		Project project = Mockery.createProject();
		Set issues = new HashSet();

		HasIssuePredicate predicate = new HasIssuePredicate();

		// test empty
		try {
			predicate.evaluate(issues);
			fail("CommitRejectedException is expected for empty issue set");
		} catch(PredicateViolatedException ex) {
			// do nothing
		}

		// test non-empty
		for(int i = 0; i < 3; i++) {
			issues.add(Mockery.createIssue(project));
		}
		predicate.evaluate(issues);
	}

	public void testHasIssueInProjectPredicate() {
		Project project = Mockery.createProject();
		Project project2 = Mockery.createProject();
		Set issues = new HashSet();

		HasIssueInProjectPredicate predicate = new HasIssueInProjectPredicate(project);

		// test empty
		try {
			predicate.evaluate(issues);
			fail("CommitRejectedException is expected for empty issue set");
		} catch(PredicateViolatedException ex) {
			// do nothing
		}

		// test with external issues only
		for(int i = 0; i < 3; i++) {
			issues.add(Mockery.createIssue(project2));
		}
		try {
			predicate.evaluate(issues);
			fail("CommitRejectedException is expected for external issues only");
		} catch(PredicateViolatedException ex) {
			// do nothing
		}

		// test with issues in project as well
		for(int i = 0; i < 2; i++) {
			issues.add(Mockery.createIssue(project));
		}
		predicate.evaluate(issues);
	}

	public void testAreIssuesInProjectPredicate() {
		Project project = Mockery.createProject();
		Project project2 = Mockery.createProject();
		Set issues = new HashSet();

		AreIssuesInProjectPredicate predicate = new AreIssuesInProjectPredicate(project);

		// test empty
		predicate.evaluate(issues);

		// test with issues in project only
		for(int i = 0; i < 2; i++) {
			issues.add(Mockery.createIssue(project));
		}
		predicate.evaluate(issues);

		// test with external issues as well
		for(int i = 0; i < 2; i++) {
			issues.add(Mockery.createIssue(project2));
		}
		try {
			predicate.evaluate(issues);
			fail("CommitRejectedException is expected for external issues");
		} catch(PredicateViolatedException ex) {
			// do nothing
		}
	}

	public void testAreIssuesUnresolvedPredicate() {
		Project project = Mockery.createProject();
		Set issues = new HashSet();

		AreIssuesUnresolvedPredicate predicate = new AreIssuesUnresolvedPredicate();

		// test empty
		predicate.evaluate(issues);

		// test with unresolved
		for(int i = 0; i < 3; i++) {
			MutableIssue issue = Mockery.createIssue(project);
			issues.add(issue);
		}
		predicate.evaluate(issues);

		// test with resolved
		for(int i = 0; i < 2; i++) {
			MutableIssue issue = Mockery.createIssue(project);
			issue.setResolution(Mockery.createResolution().getGenericValue());
			issues.add(issue);
		}
		try {
			predicate.evaluate(issues);
			fail("CommitRejectedException is expected for issue set including resolved issues");
		} catch(PredicateViolatedException ex) {
			// do nothing
		}
	}

	public void testAreIssuesAssignedToPredicate() {
		Project project = Mockery.createProject();
		Set issues = new HashSet();

		String assigneeName = "assignee-" + RandomUtils.nextInt();
		AreIssuesAssignedToPredicate predicate = new AreIssuesAssignedToPredicate(assigneeName);

		// test empty
		predicate.evaluate(issues);

		// test with assigned
		for(int i = 0; i < 3; i++) {
			MutableIssue issue = Mockery.createIssue(project);
			issue.setAssigneeId(assigneeName);
			issues.add(issue);
		}
		predicate.evaluate(issues);

		// test with unassigned
// FIXME it depends on UserUtils and IssueUtils and those cannot be mocked
//		for(int i = 0; i < 2; i++) {
//			MutableIssue issue = Mockery.createIssue();
//			issue.setAssigneeId("--");
//			issues.add(issue);
//		}
//		predicate.evaluate(issues);
	}
}
