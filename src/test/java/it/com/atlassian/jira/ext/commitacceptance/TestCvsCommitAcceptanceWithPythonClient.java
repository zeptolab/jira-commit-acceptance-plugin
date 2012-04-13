package it.com.atlassian.jira.ext.commitacceptance;

import java.io.InputStream;

public class TestCvsCommitAcceptanceWithPythonClient extends AbstractCvsCommitAcceptanceTest {

//    public TestCvsCommitAcceptanceWithPythonClient() {
//        super(TestCvsCommitAcceptanceWithPythonClient.class.getName());
//    }

    protected String getScriptExecutor() {
        return testConfiguration.getProperty("client.scm.python.path");
    }

    protected InputStream getScriptContent() {
        return getClass().getClassLoader().getResourceAsStream("client/python/cvs/jira-client.py");
    }
}
