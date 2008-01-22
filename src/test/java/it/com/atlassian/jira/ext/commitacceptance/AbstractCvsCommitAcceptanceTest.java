package it.com.atlassian.jira.ext.commitacceptance;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import java.io.*;

public abstract class AbstractCvsCommitAcceptanceTest extends AbstractCommitAcceptanceTest {

    private static final Logger logger = Logger.getLogger(AbstractCvsCommitAcceptanceTest.class);

    protected File scriptFile;

    public AbstractCvsCommitAcceptanceTest(final String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
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

    protected File createCommitMessageFile(final String commitMessage) throws IOException {
        File commitMessageFile;
        OutputStream out = null;

        do {
            commitMessageFile = new File(SystemUtils.JAVA_IO_TMPDIR, RandomStringUtils.randomAlphabetic(16));
        } while (commitMessageFile.exists());

        try {
            out = new BufferedOutputStream(new FileOutputStream(commitMessageFile));
            out.write(commitMessage.getBytes("UTF-8"));
            out.flush();

            return commitMessageFile;
            
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    protected String doCommit(final String commiterName, final String commitMessage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream;
        InputStream input = null;
        File commitMessageFile = null;

        try {
            final Runtime runtime = Runtime.getRuntime();
            final Process process;
            final String processOutput;

            commitMessageFile = createCommitMessageFile(commitMessage);

            if (StringUtils.isNotBlank(getScriptExecutor())) {
                process = runtime.exec(
                        new String[] {
                                getScriptExecutor(),
                                scriptFile.getAbsolutePath(),
                                commiterName,
                                commitMessageFile.getAbsolutePath()
                        });
            } else {
                process = runtime.exec(
                        new String[] {
                                scriptFile.getAbsolutePath(),
                                commiterName,
                                commitMessageFile.getAbsolutePath()
                        });
            }

            byteArrayOutputStream = new ByteArrayOutputStream();
            input = new BufferedInputStream(process.getInputStream());

            IOUtils.copy(input, byteArrayOutputStream);

            processOutput = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            if (logger.isDebugEnabled())
                logger.debug("Process output: " + processOutput);

            process.waitFor();
            return processOutput;
            
        } catch (final InterruptedException ie) {
            throw (IOException) new IOException(ie.getMessage()).initCause(ie);
        } finally {
            IOUtils.closeQuietly(input);
            if (null != commitMessageFile)
                commitMessageFile.delete();
        }
    }

    public void testCommit() throws IOException {
        final String output = doCommit(ADMIN_USERNAME, "[TST-4] Successful commit.");
        assertTrue(output, output.indexOf("Commit accepted") >= 0);
    }

    public void testCommmitWithNoIssueKey() throws IOException {
        final String output = doCommit(ADMIN_USERNAME, "Commit without any isue keys using global rules.");
        assertTrue(output, output.indexOf("Commit rejected") >= 0);
        assertTrue(output, output.indexOf("Commit message must contain at least one valid issue key") >= 0);
    }

    public void testCommmitWithInvalidIssueKey() throws IOException {
        final String output = doCommit(ADMIN_USERNAME, "[XXX-1] Commit without any isue keys using global rules.");
        assertTrue(output, output.indexOf("Commit rejected") >= 0);
        assertTrue(output, output.indexOf("Issue [XXX-1] does not exist or you don't have permission to access it") >= 0);
    }

    public void testCommitToResolvedIssue() throws IOException {
        final String output = doCommit(ADMIN_USERNAME, "[TST-2] Test Commit To Resolved Issue.");
        assertTrue(output, output.indexOf("Commit rejected") >= 0);
        assertTrue(output, output.indexOf("Issue [TST-2] must be in UNRESOLVED") >= 0);
    }

    public void testCommitIssueWithDifferentAssignee() throws IOException {
        final String output = doCommit("fakeuser", "[TST-5] Test Commit To Issue Not Assigned To Commiter.");
        assertTrue(output, output.indexOf("Commit rejected") >= 0);
        assertTrue(output, output.indexOf("Issue [TST-5] is not assigned to the correct person.") >= 0);
    }
}
