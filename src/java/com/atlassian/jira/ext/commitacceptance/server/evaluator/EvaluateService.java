package com.atlassian.jira.ext.commitacceptance.server.evaluator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettings;
import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettingsManager;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesAssignedToPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesUnresolvedPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.HasIssuePredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.JiraPredicate;
import com.atlassian.jira.ext.commitacceptance.server.exception.InvalidAcceptanceArgumentException;
import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.JiraKeyUtils;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;

/**
 * Invoked when receiving a commit request.
 * Evaluates whether the commit is accepted.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
public class EvaluateService {
	private static Logger logger = Logger.getLogger(EvaluateService.class);

	/*
	 * Services.
	 */
	private ProjectManager projectManager;
	private IssueManager issueManager;
	private AcceptanceSettingsManager settingsManager;
	private AcceptanceSettings settings;

	/**
	 * Project key that represents the global settings.
	 */
	private final String GLOBAL_PROJECT_KEY = "*";

	public EvaluateService(ProjectManager projectManager, IssueManager issueManager, AcceptanceSettingsManager settingsManager) {
		this.projectManager = projectManager;
		this.issueManager = issueManager;
		this.settingsManager = settingsManager;
	}

	/**
	 * Evaluates acceptance rules for the given commit information and accepts or rejects the commit.
	 * This is only method that exposed and can be executed by a user through XML-RPC.
	 *
	 * @param userName, a login name of the SCM account.
	 * @param password, a password of the SCM account.
	 * @param committerName a name of the person that commiting a code.
	 * @param projectKeys is the key(s) of JIRA project(s) where the commit belongs. This can be multiple keys separated by comma like "TST,ARP,PLG".  If a key is '*', the global settings are used.
	 * @param commitMessage a message that a committer entered before commiting.
	 * @return a string like <code>"status|comment"</code>, where <code>status true</code> if the commit is accepted and <code>false</code> if rejected.
	 */
	public String acceptCommit(String userName, String password, String committerName, String projectKeys, String commitMessage) {// FIXME change in scripts
		logger.info("Evaluating commit from \"" + committerName + "\" in [" + projectKeys + "]");

		String result = null;
		try {
			Project project;

			// prepare arguments
			userName = StringUtils.trim(userName);
			password = StringUtils.trim(password);
			committerName = StringUtils.trim(committerName);
			projectKeys = StringUtils.trim(projectKeys);
			commitMessage = StringUtils.trim(commitMessage);

			// test SCM login and password
			authenticateUser(userName, password);

			// convert committer name to lowercase (JIRA user names are lowercase)
			committerName = StringUtils.lowerCase(committerName);

			// accept commit if at least one project accepts it
			String projectKeyAcceptedBy = null;
			String projectKeyArray[] = StringUtils.split(projectKeys, ',');
			if(projectKeyArray.length == 0) {
				throw new InvalidAcceptanceArgumentException("'projectKey' value \"" + projectKeys + "\" must contain at least one valid project key, check the VCS hook script configuration.");
			}

			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < projectKeyArray.length; i++) {
				// get project
				String projectKey = StringUtils.trimToEmpty(projectKeyArray[i]);

				if (projectKey.equals(GLOBAL_PROJECT_KEY)) {
					project = null;
				} else {
					project = projectManager.getProjectObjByKey(projectKey);
					if(project == null) {
						throw new InvalidAcceptanceArgumentException("No project with key [" + projectKey + "] found, check the VCS hook script configuration.");
					}
				}

				// get project settings
				settings = settingsManager.getSettings(project == null ? null : project.getKey());

				// parse the commit message and collect issues.
				Set issues = loadIssuesByMessage(commitMessage);

				// check issues with acceptance settings.
				String projectResult = checkIssuesAcceptance(committerName, project, issues);
				if(projectResult == null) {
					projectKeyAcceptedBy = projectKey;
					break;
				} else {
					// save result when rejected and move to the next project
					buffer.append("Project [" + projectKey + "]: " + projectResult + " ");
				}
			}

			// generate result string
			result = (projectKeyAcceptedBy != null) ? acceptanceResultToString(true, "Commit accepted by JIRA in project [" + projectKeyAcceptedBy + "].") : acceptanceResultToString(false, "No project accepts this commit. " + StringUtils.trim(buffer.toString()));
		} catch(InvalidAcceptanceArgumentException ex) {
			result = acceptanceResultToString(false, ex.getMessage());
		} catch(Exception ex) {
			logger.error("Unable to evaluate", ex);
			result = acceptanceResultToString(false, "A fatal error occurred in JIRA and has been logged. Contact your system administrator.");
		}

		logger.info("Commit acceptance result: \"" + result + "\"");
		return result;
	}

	/**
	 * Joins parameters boolean and string into the string using '|' as delimiter.
	 * It is used for passing boolean/string pair to XML-RPC client.
	 *
	 * @param acceptance <code>true</code> if the commit should be accepted.
	 * @param comment a comment to be passed to a commiter.
	 */
	private static String acceptanceResultToString(boolean acceptance, String comment) {
		StringBuffer result = new StringBuffer();
		result.append(Boolean.toString(acceptance));
		result.append('|');
		result.append(comment);
		return result.toString();
	}

	/**
	 * Tries to login to JIRA with given account information and
     * throws {@link InvalidAcceptanceArgumentException} if something goes wrong.
     *
     * @param userName, a login name to be used.
     * @param password, a password to be used.
	 */
	private void authenticateUser(String userName, String password) {
		try {
			User user = UserUtils.getUser(userName);
			if ((user == null) || (!user.authenticate(password))) {
				throw new EntityNotFoundException();
			}
		} catch (EntityNotFoundException e) {
			throw new InvalidAcceptanceArgumentException("Invalid user name or password.");
		}
	}

	/**
	 * Returns an issue for the given issue key only if it exists in JIRA and a committer
     * has permission to browse it. Throws <code>AcceptanceException</code> otherwise.
	 * @param issueKey an issue key.
     *
     * @return an <code>Issue</code> object for the given issue key.
	 */
	private Issue loadIssue(String issueKey) {
		Issue issue = issueManager.getIssueObject(issueKey);

		try {
			// Ensure that an issue key is valid.
			if (issue == null) {
				throw new Exception();
			}
		} catch(Exception e) {
			throw new InvalidAcceptanceArgumentException("Issue [" + issueKey + "] does not exist or you don't have permission to access it.");
		}

		return issue;
	}

	/**
	 * Extracts issue keys from the commit message and collects issues.
	 * @param commitMessage a commit message.
	 *
	 * @return a set of issues extracted from the commit message.
	 */
	private Set loadIssuesByMessage(String commitMessage) {
		// Parse a commit message and get issue keys it contains.
		List issueKeys = JiraKeyUtils.getIssueKeysFromString(commitMessage);

		// Collect issues.
		Set issues = new HashSet();
		for (Iterator it = issueKeys.iterator(); it.hasNext();) {
			Issue issue = loadIssue((String)it.next());
			// Put it into the set of issues.
			issues.add(issue);
		}

		return issues;
	}

	/**
 	 * Returns <code>null</code> if the commit can be accepted in that project,
 	 * or the error message if not.
 	 *
 	 * @param project to check against.
 	 * @param committerName a committer name.
 	 * @param issues a set of issues to be checked.
	 */
	private String checkIssuesAcceptance(String committerName, Project project, Set issues) {
        if(settings.getUseGlobalRules()) {
    		// load global rules if those override the project specific ones
    		logger.debug("Using global rules");
        	settings = settingsManager.getSettings(null);
    	}

		// construct
		List predicates = new ArrayList();

		try {
			if(settings.isMustHaveIssue()) {
				predicates.add(new HasIssuePredicate());
			}
			if(settings.isMustBeUnresolved()) {
				predicates.add(new AreIssuesUnresolvedPredicate());
			}
			if(settings.isMustBeAssignedToCommiter()) {
				predicates.add(new AreIssuesAssignedToPredicate(committerName));
			}

			// evaluate
			for(Iterator it = predicates.iterator(); it.hasNext();) {
				((JiraPredicate)it.next()).evaluate(issues);
			}
		} catch(PredicateViolatedException ex) {
			return ex.getMessage();
		}

		return null;
	}
}
