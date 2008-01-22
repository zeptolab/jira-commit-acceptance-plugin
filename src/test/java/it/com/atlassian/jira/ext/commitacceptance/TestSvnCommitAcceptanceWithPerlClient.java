package it.com.atlassian.jira.ext.commitacceptance;

import org.apache.commons.lang.SystemUtils;

public class TestSvnCommitAcceptanceWithPerlClient extends AbstractSvnCommitAcceptanceTest {

    public TestSvnCommitAcceptanceWithPerlClient() {
        super(TestSvnCommitAcceptanceWithPerlClient.class.getName());
    }

    protected String getPathToCommitHookImpl() {
        return "client/perl/svn/" + getCommitHookBaseName();
    }

    protected String getCommitHookBaseName() {
        return "jira-client.pl";
    }

    protected String getCommitHookScriptPath() {
        return "client/perl/svn/pre-commit." + ( !SystemUtils.IS_OS_WINDOWS ? "sh" : "bat");
    }


}
