package it.com.atlassian.jira.ext.commitacceptance;

import java.io.InputStream;

public class TestSvnCommitAcceptanceWithPerlClient extends AbstractSvnCommitAcceptanceTest {
//
//    public TestSvnCommitAcceptanceWithPerlClient() {
//        super(TestSvnCommitAcceptanceWithPerlClient.class.getName());
//    }

    protected String getScriptExecutor() {
        return testConfiguration.getProperty("client.scm.perl.path");
    }

    protected InputStream getScriptContent() {
        return getClass().getClassLoader().getResourceAsStream("client/perl/svn/jira-client.pl");
    }

}
