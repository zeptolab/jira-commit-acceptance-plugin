package it.com.atlassian.jira.ext.commitacceptance;

import com.atlassian.jira.webtests.JIRAWebTest;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class AbstractCommitAcceptanceTest extends JIRAWebTest {

    public static final long COMMIT_TIMEOUT = 30000;

    private static final Logger logger = Logger.getLogger(AbstractCommitAcceptanceTest.class);

    protected Properties testConfiguration;

    protected File scriptFile;

    public AbstractCommitAcceptanceTest(final String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        readTestConfiguration();
        restoreData("testdata-export.xml");
        copyScriptFile();
    }

    public void tearDown() {
        if (scriptFile.exists())
            scriptFile.delete();
		super.tearDown();
	}

	protected abstract String getScriptExecutor();

    protected abstract InputStream getScriptContent();

    protected void copyScriptFile() {
        InputStream in = null;
        OutputStream out = null;

        do {
            scriptFile = new File(SystemUtils.JAVA_IO_TMPDIR, RandomStringUtils.randomAlphabetic(16));
        } while (scriptFile.exists());

        try {
            in = getScriptContent();
            out = new BufferedOutputStream(new FileOutputStream(scriptFile));

            IOUtils.copy(in, out);

        } catch (final IOException ioe) {
            fail("Unable to copy script file: " + ioe);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
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
                final Process p = Runtime.getRuntime().exec(new StringBuffer("chmod ").append(permission).append(' ').append(resource.getAbsolutePath()).toString());
                
                p.waitFor();
                
            } catch (final InterruptedException ie) {
                if (logger.isEnabledFor(Level.ERROR))
            		logger.error("Error waiting for chmod to return.", ie);
            } catch (final IOException ioe) {
                if (logger.isEnabledFor(Level.ERROR))
                    logger.error("Unable to set execute permission on " + resource, ioe);
                fail("Unable to set permission " + permission + " on " + resource.getAbsolutePath());
            }
        }
    }
}
