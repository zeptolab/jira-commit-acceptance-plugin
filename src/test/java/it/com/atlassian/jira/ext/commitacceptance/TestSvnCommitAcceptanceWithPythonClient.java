package it.com.atlassian.jira.ext.commitacceptance;

import java.io.InputStream;

public class TestSvnCommitAcceptanceWithPythonClient extends AbstractSvnCommitAcceptanceTest {

    public TestSvnCommitAcceptanceWithPythonClient() {
        this(TestSvnCommitAcceptanceWithPythonClient.class.getName());
    }
    
    public TestSvnCommitAcceptanceWithPythonClient(final String name) {
        super(name);
    }

    protected String getScriptExecutor() {
        return testConfiguration.getProperty("client.scm.python.path");
    }

    protected InputStream getScriptContent() {
        return getClass().getClassLoader().getResourceAsStream("client/python/svn/jira-client.py");
    }
}
