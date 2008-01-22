package it.com.atlassian.jira.ext.commitacceptance;

import com.atlassian.jira.webtests.JIRAWebTest;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.File;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class AbstractCommitAcceptanceTest extends JIRAWebTest {

    public static final long COMMIT_TIMEOUT = 30000;

    private static final Logger logger = Logger.getLogger(AbstractCommitAcceptanceTest.class);

    protected Properties testConfiguration;

    public AbstractCommitAcceptanceTest(final String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        readTestConfiguration();
        restoreData("testdata-export.xml");
    }

    protected void readTestConfiguration() {
        InputStream in = null;

        try {
            in = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("test-configuration.properties"));

            testConfiguration = new Properties();
            testConfiguration.load(in);

        } catch (final IOException ioe) {
            fail("Unable to read test-configuration.properties: " + ioe.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    protected void setUnixFilePermissionOnResource(final String permission, final File resource) {
        if (SystemUtils.IS_OS_UNIX && null != resource && resource.exists()) {
            try {
                Runtime.getRuntime().exec(new StringBuffer("chmod ").append(permission).append(' ').append(resource.getAbsolutePath()).toString());
            } catch (final IOException ioe) {
                if (logger.isEnabledFor(Level.ERROR))
                    logger.error("Unable to set execute permission on " + resource, ioe);
                fail("Unable to set permission " + permission + " on " + resource.getAbsolutePath());
            }
        }
    }
}
