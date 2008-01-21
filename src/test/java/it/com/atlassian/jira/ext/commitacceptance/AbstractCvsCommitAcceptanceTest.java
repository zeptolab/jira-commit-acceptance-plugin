package it.com.atlassian.jira.ext.commitacceptance;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.netbeans.lib.cvsclient.CVSRoot;
import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.add.AddCommand;
import org.netbeans.lib.cvsclient.command.commit.CommitCommand;
import org.netbeans.lib.cvsclient.commandLine.BasicListener;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.PServerConnection;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;

public abstract class AbstractCvsCommitAcceptanceTest extends AbstractRepositoryCommitAcceptanceTest {

    private static final Logger logger = Logger.getLogger(AbstractCvsCommitAcceptanceTest.class);

    public static final String LOCK_FILE_NAME = ".lock";

    protected String cvsRoot;

    protected File repository;

    protected File repositoryLockFile;

    protected FileChannel repositoryLockFileChannel;

    protected FileLock repositoryLock;

    protected File localWorkingDirectory;

    protected File fileToCommit;

    protected GlobalOptions globalOptions;

    public void setUp() {
        super.setUp();

        globalOptions = new GlobalOptions();
        globalOptions.setCVSRoot(this.cvsRoot);

        setUpLocalWorkingDirectory();
        addFile(fileToCommit);
    }

    public void tearDown() {
        try {
            if (localWorkingDirectory.exists())
                FileUtils.deleteDirectory(localWorkingDirectory);
        } catch (final IOException ioe) {
            fail("Unable to remove local working directory: " + localWorkingDirectory);
        } finally {
            unlockRepository();
            super.tearDown();
        }
    }

    protected void setUpRepository() {
        try {

            repository = new File(testConfiguration.getProperty("client.scm.cvs.repository.path"));
            lockRepository();

            if (repository.exists())
                FileUtils.deleteDirectory(repository);

            assertTrue("Error creating " + repository, repository.mkdirs());

            unpackCvsRepository();
            setupCvsRoot();

        } catch (final IOException ioe) {
            fail("Unable to delete existing repository at: " + repository);
        }
    }

    protected void lockRepository() {
        final File repositoryParentDir = repository.getParentFile();

        if (!repositoryParentDir.exists())
            assertTrue("Unable to create repository parent directory: " + repositoryParentDir, repositoryParentDir.mkdirs());

        repositoryLockFile = new File(repository.getParentFile(), LOCK_FILE_NAME);
        if (repositoryLockFile.isDirectory())
            fail("Unable to lock on the repository: " + repository);

        try {
            if (!repositoryLockFile.exists()) {
                FileUtils.touch(repositoryLockFile);
            }

            repositoryLockFileChannel = new RandomAccessFile(repositoryLockFile, "rw").getChannel();
            repositoryLock = repositoryLockFileChannel.lock();

        } catch (final IOException ioe) {
            fail("Unable to create lock \"" + repositoryLockFile + "\": " + ioe.getMessage());
        }
    }

    protected void unlockRepository() {
        if (null != repositoryLock) {
            try {
                repositoryLock.release();
            } catch (final IOException ioe) {
                fail("Unable to release lock on repository: " + ioe.getMessage());
            } finally {
                repositoryLock = null;
            }
        }

        if (null != repositoryLockFileChannel) {
            try {
                repositoryLockFileChannel.close();
            } catch (final IOException ioe) {
                fail("Unable to close lock file channel: " + ioe.getMessage());
            } finally {
                repositoryLockFileChannel = null;
            }
        }

        repositoryLockFile.delete();
    }

    protected void unpackCvsRepository() {
        ZipInputStream in = null;

        try {
            ZipEntry zipEntry;

            in = new ZipInputStream(getClass().getClassLoader().getResourceAsStream("cvs-repository.zip"));

            while (null != ((zipEntry = in.getNextEntry()))) {
                OutputStream out = null;

                try {
                    final File resource = new File(repository, zipEntry.getName());

                    if (zipEntry.isDirectory()) {

                        assertTrue("Unable to create repository sub directory: " + resource, resource.mkdirs());

                    } else {
                        final File resourceParent = resource.getParentFile();

                        if (!resourceParent.exists())
                            resourceParent.mkdirs();

                        out = new BufferedOutputStream(new FileOutputStream(resource));

                        IOUtils.copy(in, out);
                    }

                    setUnixFilePermissionOnResource(zipEntry.isDirectory() ? "a+rwx" : "a+rw", resource);

                } finally {
                    IOUtils.closeQuietly(out);

                    try {
                        in.closeEntry();
                    } catch (final IOException ioe) {
                        fail("Unable to close current ZIP entry: " + ioe.getMessage());
                    }
                }
            }

        } catch (final IOException ioe) {
            fail("Unable to read from " + in + ": " + ioe.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    protected void setupCvsRoot() {
        cvsRoot = new StringBuffer(":pserver:" + ADMIN_USERNAME + "@127.0.0.1:").append(repository.getAbsolutePath()).toString();
        if (logger.isDebugEnabled())
            logger.debug("CVS Root string: " + cvsRoot);
    }

    protected void tearDownRepository() {
        try {
            if (repository.exists())
                FileUtils.deleteDirectory(repository);
        } catch (final IOException ioe) {
            fail("Unable to remove the repository: " + repository + ". " + ioe.getMessage());
        }
    }

    protected void copyCommitHook() {
        File commitHookFile = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            commitHookFile = new File(new File(repository, "CVSROOT"), getCommitHookBaseName());

            in = getClass().getClassLoader().getResourceAsStream(getPathToCommitHookImpl());
            out = new BufferedOutputStream(new FileOutputStream(commitHookFile));

            IOUtils.copy(in, out);

        } catch (final IOException ioe) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unable to copy \"" + getPathToCommitHookImpl() + "\".", ioe);
            fail("Unable to copy setup CVS hook");
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);

            setUnixFilePermissionOnResource("a+x", commitHookFile);
        }
    }

    protected void copyCommitHookScript() {
        final String commitHookScriptPath = getCommitHookScriptPath();
        File commitHookScriptFile = null;
        InputStream in = null;
        OutputStream out = null;

        try {

            commitHookScriptFile = new File(new File(repository, "CVSROOT"), "verifymsg");

            in = getClass().getClassLoader().getResourceAsStream(commitHookScriptPath);
            out = new BufferedOutputStream(new FileOutputStream(commitHookScriptFile));


            IOUtils.copy(in, out);

        } catch (final IOException ioe) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unable to copy \"" + commitHookScriptPath + "\".", ioe);
            fail("Unable to copy setup CVS hook");
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);

            setUnixFilePermissionOnResource("a+x", commitHookScriptFile);
        }
    }

    protected void setUpLocalWorkingDirectory() {
        OutputStream out = null;

        do {
            localWorkingDirectory = new File(SystemUtils.JAVA_IO_TMPDIR, RandomStringUtils.randomAlphanumeric(16));
        } while (localWorkingDirectory.exists());
        assertTrue("Unable to create local working directory to commit files from: " + localWorkingDirectory, localWorkingDirectory.mkdir());

        unpackCheckedOutResources();

        try {
            fileToCommit = new File(localWorkingDirectory, RandomStringUtils.randomNumeric(16));

            out = new BufferedOutputStream(new FileOutputStream(fileToCommit));
            out.write("This is the content committed".getBytes());
            out.flush();

        } catch (final IOException ioe) {
            fail("Unable to create the file to commit: " + fileToCommit);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    protected void unpackCheckedOutResources() {
        ZipInputStream in = null;

        try {
            ZipEntry zipEntry;

            in = new ZipInputStream(getClass().getClassLoader().getResourceAsStream("cvs-workspace.zip"));

            while (null != (zipEntry = in.getNextEntry())) {
                OutputStream out = null;

                try {
                    if (zipEntry.isDirectory()) {
                        final File directory = new File(localWorkingDirectory, zipEntry.getName());

                        assertTrue("Unable to workspace sub directory: " + directory, directory.mkdirs());

                    } else {
                        final File entryOutputDestination = new File(localWorkingDirectory, zipEntry.getName());
                        final File entryOutputDestinationParent = entryOutputDestination.getParentFile();

                        if (!entryOutputDestinationParent.exists())
                            entryOutputDestinationParent.mkdirs();

                        out = new BufferedOutputStream(new FileOutputStream(entryOutputDestination));

                        IOUtils.copy(in, out);
                    }

                } finally {
                    IOUtils.closeQuietly(out);

                    try {
                        in.closeEntry();
                    } catch (final IOException ioe) {
                        fail("Unable to close current ZIP entry: " + ioe.getMessage());
                    }
                }
            }
        } catch (final IOException ioe) {
            fail("Unable to read from " + in + ": " + ioe.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    protected void addFile(final File file) {
        Client client = null;

        try {
            client = openSessionWithServer();
            client.executeCommand(createAddCommand(client, file), globalOptions);

        } catch (final Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            try { client.getConnection().close(); } catch (final Exception e) { /* Swallow */ }
        }
    }

    protected Client openSessionWithServer() {
        Client client = null;

        try {
            final CVSRoot cvsRoot = CVSRoot.parse(this.cvsRoot);
            final PServerConnection pServerConnection = new PServerConnection(cvsRoot);

            pServerConnection.open();

            client = new Client(pServerConnection, new StandardAdminHandler());
            client.setLocalPath(localWorkingDirectory.getAbsolutePath());
            client.getEventManager().addCVSListener(new BasicListener());

        } catch (final AuthenticationException ae) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unable to authenticate with CVS repository at : " + repository, ae);
            fail("Unable to authenticate with repository at: " + repository + ". Please check the --allow-root option set for cvs.");
        } catch (final CommandException ce) {
            fail("General foobar while opening connection to repository at: " + repository);
        }

        return client;
    }

    protected abstract String getCommitHookScriptPath();

    protected abstract String getCommitHookBaseName();

    protected abstract String getPathToCommitHookImpl();

    protected AddCommand createAddCommand(
            final Client client,
            final File fileToBeAdded) {
        final AddCommand addCommand = new AddCommand();

        addCommand.setFiles(new File[] { fileToBeAdded });

        return addCommand;
    }

    protected CommitCommand createCommitCommand(
            final Client client,
            final String message) {
        final CommitCommand commitCommand = new CommitCommand();


        commitCommand.setRecursive(false);
        commitCommand.setFiles(new File[] { fileToCommit });
        commitCommand.setMessage(message);

        return commitCommand;
    }

    public void testCommit() {
        Client client = null;

        try {
            client = openSessionWithServer();
            assertTrue(client.executeCommand(createCommitCommand(client, "[TST-4] Successful commit."), globalOptions));

        } catch (final Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            try { client.getConnection().close(); } catch (final Exception e) { /* Swallow */ }
        }
    }

    public void testCommmitWithNoIssueKey() {
        Client client = null;

        try {
            client = openSessionWithServer();
            assertFalse(client.executeCommand(createCommitCommand(client, "Commit without any isue keys using global rules."), globalOptions));
        } catch (final Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            try { client.getConnection().close(); } catch (final Exception e) { /* Swallow */ }
        }
    }

    public void testCommmitWithInvalidIssueKey() {
        Client client = null;

        try {
            client = openSessionWithServer();
            assertFalse(client.executeCommand(createCommitCommand(client, "Commit without any isue keys using global rules."), globalOptions));
        } catch (final Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            try { client.getConnection().close(); } catch (final Exception e) { /* Swallow */ }
        }
    }

    public void testCommitToResolvedIssue() {
        Client client = null;

        try {
            client = openSessionWithServer();
            assertFalse(client.executeCommand(createCommitCommand(client, "[TST-2] Test Commit To Resolved Issue."), globalOptions));
        } catch (final Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            try { client.getConnection().close(); } catch (final Exception e) { /* Swallow */ }
        }
    }

    public void testCommitIssueWithDifferentAssignee() {
        Client client = null;

        try {
            client = openSessionWithServer();
            assertFalse(client.executeCommand(createCommitCommand(client, "[TST-5] Test Commit To Issue Not Assigned To Commiter."), globalOptions));
        } catch (final Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            try { client.getConnection().close(); } catch (final Exception e) { /* Swallow */ }
        }
    }
}
