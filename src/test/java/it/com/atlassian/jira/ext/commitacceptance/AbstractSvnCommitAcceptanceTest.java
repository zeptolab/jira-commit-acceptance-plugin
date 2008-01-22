package it.com.atlassian.jira.ext.commitacceptance;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.*;

public abstract class AbstractSvnCommitAcceptanceTest extends AbstractRepositoryCommitAcceptanceTest {

    private static final Logger logger = Logger.getLogger(AbstractSvnCommitAcceptanceTest.class);

    protected File svnRepositoryDirectory;

    protected SVNRepository svnRepository;

    public AbstractSvnCommitAcceptanceTest(final String name) {
        super(name);
    }

    protected void setUpRepository() {
        try {
            svnRepositoryDirectory = new File(SystemUtils.getUserDir(), "svn-repository");
            if (svnRepositoryDirectory.exists())
                FileUtils.deleteDirectory(svnRepositoryDirectory);

            /* Setup SVN tools */
            DAVRepositoryFactory.setup();
            SVNRepositoryFactoryImpl.setup();
            FSRepositoryFactory.setup();

            svnRepository = SVNRepositoryFactory.create(SVNRepositoryFactory.createLocalRepository(svnRepositoryDirectory, true, false));
            

        } catch (final IOException ioe) {
            fail("Unable to remove previously created SVN repository: " + svnRepositoryDirectory);
        } catch (final SVNException svne) {
            fail("Unable to create SVN repository at: " + svnRepositoryDirectory);
        }
    }

    protected void tearDownRepository() {
        try {
            svnRepository.closeSession();
        } catch (final SVNException svne) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unable to close current SVN session.", svne);
            
        } finally {
            
            try {
                if (svnRepositoryDirectory.isDirectory()) {
                    FileUtils.deleteDirectory(svnRepositoryDirectory);
                }
            } catch (final IOException ioe) {
                fail("Unable to delete SVN repository at: " + svnRepositoryDirectory);
            }
        }
    }

    protected void copyCommitHook() {
        File commitHookFile = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            commitHookFile = new File(new File(svnRepositoryDirectory, "hooks"), getCommitHookBaseName());
            in = getClass().getClassLoader().getResourceAsStream(getPathToCommitHookImpl());
            out = new BufferedOutputStream(new FileOutputStream(commitHookFile));

            IOUtils.copy(in, out);

        } catch (final IOException ioe) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unable to copy \"" + getPathToCommitHookImpl() + "\".", ioe);
            fail("Unable to copy setup SVN hook");
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);

            setUnixFilePermissionOnResource("a+x", commitHookFile);
        }
    }

    protected abstract String getCommitHookBaseName();

    protected abstract String getPathToCommitHookImpl();

    protected void copyCommitHookScript() {
        final String commitHookScriptPath = getCommitHookScriptPath();
        File commitHookScriptFile = null;
        InputStream in = null;
        OutputStream out = null;

        try {

            commitHookScriptFile = new File(
                    new File(svnRepositoryDirectory, "hooks"),
                    "pre-commit");

            in = getClass().getClassLoader().getResourceAsStream(commitHookScriptPath);
            out = new BufferedOutputStream(new FileOutputStream(commitHookScriptFile));


            IOUtils.copy(in, out);

        } catch (final IOException ioe) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unable to copy \"" + commitHookScriptPath + "\".", ioe);
            fail("Unable to copy setup SVN hook");
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);

            setUnixFilePermissionOnResource("a+x", commitHookScriptFile);
        }
    }

    protected abstract String getCommitHookScriptPath();

    protected SVNCommitInfo doCommit(final String directoryPath, final String filePath, final byte[] content, final String commitMessage) throws SVNException {
        final ISVNEditor isvnEditor = svnRepository.getCommitEditor(commitMessage, null);
        final SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        final String checksum;

        isvnEditor.openRoot(-1);
        isvnEditor.addDir(directoryPath, null, -1);
        isvnEditor.addFile(filePath, null, -1);
        isvnEditor.applyTextDelta(filePath, null);

        checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(content), isvnEditor, true);

        isvnEditor.closeFile(filePath, checksum);
        isvnEditor.closeDir();
        isvnEditor.closeDir();

        return isvnEditor.closeEdit();
    }

    public void testCommmit() {
        /* Commits to an existing unresolved issue which its assignee is equal to the commiter name */
        svnRepository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager(ADMIN_USERNAME, ADMIN_PASSWORD));

        try {
            doCommit("test", "test/test.txt", "Test".getBytes(), "[TST-4] Successful commit.");
        } catch (final SVNException svne) {
            fail(svne.getMessage());
        }
    }

    public void testCommmitWithNoIssueKey() {
        try {
            doCommit("test", "test/test.txt", "Test".getBytes(), "Commit without any isue keys using global rules.");
            fail("Commit should fail because the commit message does not contain any valid JIRA issue key.");
        } catch (final SVNException svne) {
            assertTrue(0 < svne.getMessage().indexOf("Commit message must contain at least one valid issue key"));
        }
    }

    public void testCommmitWithInvalidIssueKey() {
        try {
            doCommit("test", "test/test.txt", "Test".getBytes(), "[XXX-1] Commit without any isue keys using global rules.");
            fail("Commit should fail because the commit message does not contain any valid JIRA issue key.");
        } catch (final SVNException svne) {
            assertTrue(0 < svne.getMessage().indexOf("Issue [XXX-1] does not exist or you don't have permission to access it"));
        }
    }

    public void testCommitToResolvedIssue() {
        try {
            doCommit("test", "test/test.txt", "Test".getBytes(), "[TST-2] Test Commit To Resolved Issue.");
            fail("Commit should fail because the commit issue is resolved.");
        } catch (final SVNException svne) {
            assertTrue(0 < svne.getMessage().indexOf("Issue [TST-2] must be in UNRESOLVED"));
        }
    }

    public void testCommitIssueWithDifferentAssignee() {
        svnRepository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager("fakeuser", "fakepassword"));

        try {
            doCommit("test", "test/test.txt", "Test".getBytes(), "[TST-3] Test Commit To Issue Not Assigned To Commiter.");
            fail("Commit should fail because the commit issue is resolved.");
        } catch (final SVNException svne) {
            assertTrue(0 < svne.getMessage().indexOf("Issue [TST-3] is not assigned to the correct person."));
        }
    }
}
