package com.atlassian.jira.ext.commitacceptance.server.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.issue.*;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.util.JiraKeyUtils;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;

import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesAssignedToPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.HasIssuePredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesUnresolvedPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.JiraPredicate;

import com.atlassian.jira.ext.commitacceptance.server.exception.AcceptanceException;

/**
 * Joins parameters boolean and string into the string using '|' as delimiter.
 * It is used for passing boolean/string pair to XML-RPC client.
 * 
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 * @version $Id$
 */
class AcceptanceResult {
	static String toString(boolean acceptance, String comment) {
		String result = Boolean.toString(acceptance);
		result += '|';
		result += comment;
		return result;
	}
}

/**
 * Invoked when receiving a commit request.
 * Writes "true" (accepted) or "false" (rejected) to the Response
 * based on the log message and the settings.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class EvaluateAction {
	/*
	 * JIRA services.
	 */
	private IssueManager issueManager;
	private PermissionManager permissionManager;
    
	private AcceptanceSettingsManager settingsManager;
	
	public EvaluateAction(IssueManager issueManager, PermissionManager permissionManager, AcceptanceSettingsManager settingsManager) {
		this.issueManager = issueManager;
		this.permissionManager = permissionManager;
		this.settingsManager = settingsManager;
	}

	/**
	 * Evaluates acceptance rules for the given commit information and accepts or rejects the commit.
     * This is only method that exposed and can be executed by a user through XML-RPC.
     * 
     * @param userName, a login name of the SCM account.
     * @param password, a password of the SCM account.
     * @param commiterName, a name of the person that commiting a code.
     * @param commitMessage, a message that a commiter entered before commiting.
     * @return a string like <code>"status|comment"</code>, where <code>status true</code> if the commit is
     *         accepted and <code>false</code> if rejected.    
	 */
	public String acceptCommit(String userName, String password, String commiterName, String commitMessage) {
		try {
			// Test SCM login and password.
			testUser(userName, password);

			// Test a commiter name.
			User commiter = getCommiter(commiterName);

			// Parse the commit message and collect issues.
			Set issues = getIssues(commitMessage, commiter);

			// Check issues with acceptance settings.
			checkIssuesAcceptance(issues, commiterName);
            
		} catch(AcceptanceException e) {
			return AcceptanceResult.toString(false, e.getMessage());
		}

		return AcceptanceResult.toString(true, "Accepted.");
	}

	/**
	 * Tries to login to JIRA with given account information and
     * throws <code>AcceptanceException</code> if something goes wrong.
     * 
     * @param userName, a login name to be used.
     * @param password, a password to be used.
	 */
	private void testUser(String userName, String password) {
		try {
			User user = UserUtils.getUser(userName);
			if ((user == null) || (!user.authenticate(password))) {
				throw new EntityNotFoundException();
			}
		} catch (EntityNotFoundException e) {
			throw new AcceptanceException("Invalid user name or password.");
		}
	}

	/**
	 * Tests a given commiter name with JIRA. It throws <code>AcceptanceException</code>
     * if the name is wrong and returns a corresponding <code>User</code> object otherwise.
     * 
     * @param commiterName, a name of the commiter to be tested.
     * @return a <code>User</code> object for the given commiter name. 
	 */
	private User getCommiter(String commiterName) {
		User commiter = null;
		try {
			commiter = UserUtils.getUser(commiterName);
		} catch (EntityNotFoundException e) {
			throw new AcceptanceException("Wrong commiter name.");
		}

		return commiter;
	}

	/**
	 * Returns an issue for the given issue key only if it exists in JIRA and a commiter
     * has permission to browse it. Throws <code>AcceptanceException</code> otherwise.
     *  
     * @param issueKey, an issue key.
     * @param commiter, a commiter.
     * @return an <code>Issue</code> object for the given issue key.
	 */
	private Issue getIssue(String issueKey, User commiter) {
		Issue issue = issueManager.getIssueObject(issueKey);

		try {
			// Ensure that an issue key is valid.
			if (issue == null ||
				!permissionManager.hasPermission(Permissions.BROWSE, issue.getGenericValue(), commiter)) {
				throw new Exception();
			}
		} catch(Exception e) {
			throw new AcceptanceException(issueKey + " issue does not exist or you don't have permission.");
		}

		return issue;
	}

	/**
	 * Extracts issue keys from the commit message and collects issues.
     * 
     * @param commitMessage, a commit message.
     * @param commiter, a commiter.
     * @return a set of issues extracted from the commit message.
	 */
	private Set getIssues(String commitMessage, User commiter) {
		// Parse a commit message and get issue keys it contains.
		List issueKeys = JiraKeyUtils.getIssueKeysFromString(commitMessage);

		// Collect issues.
		Set issues = new HashSet();
		for (Iterator it=issueKeys.iterator(); it.hasNext();) {
			Issue issue = getIssue((String)it.next(), commiter);
			// Put it into the set of issues.
			issues.add(issue);
		}

		return issues;
	}

	/**
	 * Checks issues with the given acceptance settings.
     * Throws <code>AcceptanceException</code> if at least one issue doesn't
     * meet the acceptance settings.
     * 
     * @param issues, a set of issues to be checked.
     * @param commiterName, a commiter name.
	 */
	private void checkIssuesAcceptance(Set issues, String commiterName) {
		List predicates = new ArrayList();
        ReadOnlyAcceptanceSettings settings = settingsManager.getSettings();
        
		// construct
		if(settings.isMustHaveIssue()) {
			predicates.add(new HasIssuePredicate());
		}
		if(settings.isMustBeAssignedToCommiter()) {
			predicates.add(new AreIssuesAssignedToPredicate(commiterName));
		}
		if(settings.isMustBeUnresolved()) {
			predicates.add(new AreIssuesUnresolvedPredicate());
		}

		// evaluate
		for(Iterator it = predicates.iterator(); it.hasNext();) {
			((JiraPredicate)it.next()).evaluate(issues);
		}
	}
}
