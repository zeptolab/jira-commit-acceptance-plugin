package it.com.atlassian.jira.ext.commitacceptance;

import java.io.InputStream;

public class TestCvsCommitAcceptanceWithPerlClient extends AbstractCvsCommitAcceptanceTest {

    public TestCvsCommitAcceptanceWithPerlClient() {
        super(TestCvsCommitAcceptanceWithPerlClient.class.getName());
    }

    protected String getScriptExecutor() {
        return testConfiguration.getProperty("client.scm.perl.path");
    }

    protected InputStream getScriptContent() {
        return getClass().getClassLoader().getResourceAsStream("client/perl/cvs/jira-client.pl");
    }
}
