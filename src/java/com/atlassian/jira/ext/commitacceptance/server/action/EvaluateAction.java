package com.atlassian.jira.ext.commitacceptance.server.action;

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
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesInStatePredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.HasIssuePredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.AreIssuesUnresolvedPredicate;
import com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate.JiraPredicate;

import com.atlassian.jira.ext.commitacceptance.server.exception.AcceptanceException;

/**
 * Joins parameters boolean and string into the string using '|' as delimiter.
 * It is used for passing boolean/string pair to XML-RPC client.
 * @author <a href="mailto:istvan.vamosi@midori.hu">Istvan Vamosi</a>
 */
class Result {
	static String toString(boolean acceptance, String comment)
	{
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
 */
public class EvaluateAction {
   
    private IssueManager issueManager;
    private PermissionManager permissionManager;
    
    public EvaluateAction(IssueManager issueManager, PermissionManager permissionManager)
    {
        this.issueManager = issueManager;
        this.permissionManager = permissionManager;
    }
    
    public String acceptCommit(String userName, String password, String commiterName, String commitMessage)
    {
    	try
    	{
    		// Test SCM login and password.
	    	testUser(userName, password);
	        
	    	// Test a commiter name.
	    	User commiter = getCommiter(commiterName);

            // Parse the commit message and collect issues.
            Set issues = getIssues(commitMessage, commiter);

            // Load settings and construct criteria.
            AcceptanceSettings settings = new AcceptanceSettings(); // TODO load
            // Initial (phase 1) rules. 
            settings.setMustHaveIssue(true);
            settings.setMustBeAssignedToSpecificUser(true);
            settings.setAssigneeName(commiterName);
            settings.setMustBeUnresolved(true);

            // Check issues with acceptance settings. 
            checkIssuesAcceptance(issues, settings);            
    	}
    	catch(AcceptanceException e)
    	{
    		return Result.toString(false, e.getMessage());
    	}
    	
        return Result.toString(true, "Accepted.");
    }
    
    private void testUser(String userName, String password)
    {
        try
        {
            User user = UserUtils.getUser(userName);
            if ((user == null) || (!user.authenticate(password)))
            {
                throw new EntityNotFoundException();
            }
        }
        catch (EntityNotFoundException e)
        {
            throw new AcceptanceException("Wrong user name or password.");
        }
    }
    
    private User getCommiter(String commiterName)
    {
        User commiter = null;
        try
        {
            commiter = UserUtils.getUser(commiterName);
        }
        catch (EntityNotFoundException e)
        {
            throw new AcceptanceException("Wrong commiter name.");
        }
        
        return commiter;
    }
    
    private Issue getIssue(String issueKey, User commiter)
    {
        Issue issue = issueManager.getIssueObject(issueKey);
        
        try
        {
            // Ensure that an issue key is valid.
            if (issue == null || 
                !permissionManager.hasPermission(Permissions.BROWSE, issue.getGenericValue(), commiter))
            {
                throw new Exception();
            }
        }
        catch(Exception e)
        {
            throw new AcceptanceException(issueKey + " issue does not exist or you don't have permission.");
        }

        return issue;
    }
    
    private Set getIssues(String commitMessage, User commiter)
    {
        // Parse a commit message and get issue keys it contains.
        List issueKeys = JiraKeyUtils.getIssueKeysFromString(commitMessage);

        // Collect issues.
        Set issues = new HashSet();
        for (Iterator it=issueKeys.iterator(); it.hasNext();)
        {
            Issue issue = getIssue((String)it.next(), commiter);
            // Put it into the set of issues.
            issues.add(issue);
        }
        
        return issues;
    }
    
    private void checkIssuesAcceptance(Set issues, AcceptanceSettings settings)
    {
        Set predicates = new HashSet();

        // construct
        if(settings.isMustHaveIssue()) {
            predicates.add(new HasIssuePredicate());
        }
        if(settings.isMustBeAssignedToSpecificUser()) {
            predicates.add(new AreIssuesAssignedToPredicate(settings.getAssigneeName()));
        }
        if(settings.isMustBeInSpecificState()) {
            predicates.add(new AreIssuesInStatePredicate());
        }
        if(settings.isMustBeUnresolved()) {
            predicates.add(new AreIssuesUnresolvedPredicate());
        }

        // evaluate
        for(Iterator it = predicates.iterator(); it.hasNext();)
        {
            ((JiraPredicate)it.next()).evaluate(issues);
        }
    }
}
