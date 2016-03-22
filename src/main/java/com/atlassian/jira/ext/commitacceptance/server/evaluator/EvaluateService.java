package com.atlassian.jira.ext.commitacceptance.server.evaluator;

import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettings;
import com.atlassian.jira.ext.commitacceptance.server.action.AcceptanceSettingsManager;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesAssignedToPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesInProjectPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesUnresolvedPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.HasIssueInProjectPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.HasIssuePredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.JiraPredicate;
import com.atlassian.jira.ext.commitacceptance.server.exception.InvalidAcceptanceArgumentException;
import com.atlassian.jira.ext.commitacceptance.server.exception.PredicateViolatedException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.JiraKeyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Invoked when receiving a commit request.
 * Evaluates whether the commit is accepted.
 *
 * @author <a href="mailto:ferenc.kiss@midori.hu">Ferenc Kiss</a>
 * @version $Id$
 */
@Path("/")
public class EvaluateService {
    private static Logger logger = Logger.getLogger(EvaluateService.class);

    /*
     * Services.
     */
    private ProjectManager projectManager;
    private IssueManager issueManager;
    private UserManager userManager;
    private AcceptanceSettingsManager settingsManager;
    private AcceptanceSettings settings;

    /**
     * Project key that represents the global settings.
     */
    private final String GLOBAL_PROJECT_KEY = "*";

    public EvaluateService(
            final ProjectManager projectManager,
            final IssueManager issueManager,
            final UserManager userManager,
            final AcceptanceSettingsManager settingsManager) {
        this.projectManager = projectManager;
        this.issueManager = issueManager;
        this.userManager = userManager;
        this.settingsManager = settingsManager;
    }

    /**
     * Evaluates acceptance rules for the given commit information and accepts or rejects the commit.
     * This is only method that exposed and can be executed by a user through XML-RPC.
     *
     * @param committerName a name of the person that commiting a code.
     * @param projectKeys   is the key(s) of JIRA project(s) where the commit belongs. This can be multiple keys separated by comma like "TST,ARP,PLG".  If a key is '*', the global settings are used.
     * @param commitMessage a message that a committer entered before commiting.
     * @return a string like <code>"status|comment"</code>, where <code>status true</code> if the commit is accepted and <code>false</code> if rejected.
     */
    @GET
    @Path("/")
    @Produces("application/json")
    public Result acceptCommit(@QueryParam("committerName") String committerName,
                               @QueryParam("projectKeys") String projectKeys,
                               @QueryParam("commitMessage") String commitMessage) {// FIXME change in scripts
        logger.info("Evaluating commit from \"" + committerName + "\" in [" + projectKeys + "]");

        Result result = null;
        try {
            Project project;

            // prepare arguments
            committerName = StringUtils.trim(committerName);
            projectKeys = StringUtils.trim(projectKeys);
            commitMessage = StringUtils.trim(commitMessage);

            // convert committer name to lowercase (JIRA user names are lowercase)
            committerName = StringUtils.lowerCase(committerName);

            // accept commit if at least one project accepts it
            String projectKeyAcceptedBy = null;
            String projectKeyArray[] = StringUtils.split(projectKeys, ',');
            if (projectKeyArray.length == 0) {
                throw new InvalidAcceptanceArgumentException("'projectKey' value \"" + projectKeys + "\" must contain at least one valid project key, check the VCS hook script configuration.");
            }

            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < projectKeyArray.length; i++) {
                // get project
                String projectKey = StringUtils.trimToEmpty(projectKeyArray[i]);

                if (projectKey.equals(GLOBAL_PROJECT_KEY)) {
                    project = null;
                } else {
                    project = projectManager.getProjectObjByKey(projectKey);
                    if (project == null) {
                        throw new InvalidAcceptanceArgumentException("No project with key [" + projectKey + "] found, check the VCS hook script configuration.");
                    }
                }

                // get project settings
                settings = settingsManager.getSettings((project == null) ? null : project.getKey());

                // parse the commit message and collect issues.
                Set issues = loadIssuesByMessage(commitMessage);

                // check issues with acceptance settings.
                String projectResult = checkIssuesAcceptance(committerName, project, issues);
                if (projectResult == null) {
                    projectKeyAcceptedBy = projectKey;
                    break;
                } else {
                    // save result when rejected and move to the next project
                    buffer.append("Project [" + projectKey + "]: " + projectResult + " ");
                }
            }

            if (projectKeyAcceptedBy != null)
                result = Result.ok();
            else
                result = Result.error("No project accepts this commit. " + StringUtils.trim(buffer.toString()));
        } catch (InvalidAcceptanceArgumentException ex) {
            result = Result.error(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unable to evaluate", ex);
            result = Result.error("A fatal error occurred in JIRA and has been logged. Contact your system administrator.");
        }

        logger.info("Commit acceptance result: \"" + result + "\"");
        return result;
    }

    /**
     * Joins parameters boolean and string into the string using '|' as delimiter.
     * It is used for passing boolean/string pair to XML-RPC client.
     *
     * @param acceptance <code>true</code> if the commit should be accepted.
     * @param comment    a comment to be passed to a commiter.
     */
    private static String acceptanceResultToString(boolean acceptance, String comment) {
        StringBuffer result = new StringBuffer();
        result.append(Boolean.toString(acceptance));
        result.append('|');
        result.append(comment);
        return result.toString();
    }

    /**
     * Returns an issue for the given issue key only if it exists in JIRA and a committer
     * has permission to browse it. Throws <code>AcceptanceException</code> otherwise.
     *
     * @param issueKey an issue key.
     * @return an <code>Issue</code> object for the given issue key.
     */
    private Issue loadIssue(String issueKey) {
        Issue issue = issueManager.getIssueObject(issueKey);

        try {
            // Ensure that an issue key is valid.
            if (issue == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new InvalidAcceptanceArgumentException("Issue [" + issueKey + "] does not exist or you don't have permission to access it.");
        }

        return issue;
    }

    /**
     * Extracts issue keys from the commit message and collects issues.
     *
     * @param commitMessage a commit message.
     * @return a set of issues extracted from the commit message.
     */
    private Set loadIssuesByMessage(String commitMessage) {
        // Parse a commit message and get issue keys it contains.
        List issueKeys = getIssueKeysFromString(commitMessage);

        // Collect issues.
        Set issues = new HashSet();
        for (Iterator it = issueKeys.iterator(); it.hasNext(); ) {
            Issue issue = loadIssue((String) it.next());
            // Put it into the set of issues.
            issues.add(issue);
        }

        return issues;
    }

    protected List getIssueKeysFromString(String commitMessage) {
        return JiraKeyUtils.getIssueKeysFromString(commitMessage);
    }

    /**
     * Returns <code>null</code> if the commit can be accepted in that project,
     * or the error message if not.
     *
     * @param project       to check against or <code>null</code> if checking against global settings.
     * @param committerName a committer name.
     * @param issues        a set of issues to be checked.
     */
    protected String checkIssuesAcceptance(String committerName, Project project, Set issues) {
        if (settings.getUseGlobalRules()) {
            // load global rules if those override the project specific ones
            logger.debug("Using global rules");
            settings = settingsManager.getSettings(null);
        }

        // construct
        List predicates = new ArrayList();

        try {
            if (settings.isMustHaveIssue()) {
                predicates.add(new HasIssuePredicate());
            }
            if (settings.isMustBeUnresolved()) {
                predicates.add(new AreIssuesUnresolvedPredicate());
            }
            if (settings.isMustBeAssignedToCommiter()) {
                predicates.add(new AreIssuesAssignedToPredicate(committerName));
            }
            if (project != null) {
                if (settings.getAcceptIssuesFor() == AcceptanceSettings.ONE_FOR_THIS) {
                    predicates.add(new HasIssueInProjectPredicate(project));
                } else if (settings.getAcceptIssuesFor() == AcceptanceSettings.ONLY_FOR_THIS) {
                    predicates.add(new AreIssuesInProjectPredicate(project));
                }
            }

            // evaluate
            for (Iterator it = predicates.iterator(); it.hasNext(); ) {
                ((JiraPredicate) it.next()).evaluate(issues);
            }
        } catch (PredicateViolatedException ex) {
            return ex.getMessage();
        }

        return null;
    }

    public static class Result {
        @JsonProperty
        private String status;
        @JsonProperty
        private String message;

        public Result(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public static Result ok() {
            return new Result("ok", "Success");
        }

        public static Result error(String message) {
            return new Result("error", message);
        }

        public String getStatus() {
            return status;
        }
    }
}
