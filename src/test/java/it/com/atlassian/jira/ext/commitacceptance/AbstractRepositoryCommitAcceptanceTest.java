package it.com.atlassian.jira.ext.commitacceptance;

import com.atlassian.jira.webtests.JIRAWebTest;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.Properties;

public abstract class AbstractRepositoryCommitAcceptanceTest extends AbstractCommitAcceptanceTest {

    public AbstractRepositoryCommitAcceptanceTest(final String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        setUpRepository();
        copyCommitHook();
        copyCommitHookScript();
    }

    public void tearDown() {
        tearDownRepository();
        super.tearDown();
    }

    protected abstract void setUpRepository();

    protected abstract void tearDownRepository();

    protected abstract void copyCommitHook();

    protected abstract void copyCommitHookScript();
}
