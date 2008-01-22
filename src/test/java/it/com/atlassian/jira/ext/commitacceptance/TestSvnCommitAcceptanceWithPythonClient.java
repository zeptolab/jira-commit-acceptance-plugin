package it.com.atlassian.jira.ext.commitacceptance;

import org.apache.commons.lang.SystemUtils;

public class TestSvnCommitAcceptanceWithPythonClient extends AbstractSvnCommitAcceptanceTest {

    public TestSvnCommitAcceptanceWithPythonClient() {
        super(TestSvnCommitAcceptanceWithPythonClient.class.getName());
    }

    protected String getPathToCommitHookImpl() {
        return "client/python/svn/" + getCommitHookBaseName();
    }

    protected String getCommitHookBaseName() {
        return "jira-client.py";
    }

    protected String getCommitHookScriptPath() {
        return "client/python/svn/pre-commit." + ( !SystemUtils.IS_OS_WINDOWS ? "sh" : "bat");
    }
}
