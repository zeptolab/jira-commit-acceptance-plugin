package it.com.atlassian.jira.ext.commitacceptance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class AbstractSvnCommitAcceptanceTest extends AbstractCommitAcceptanceTest {
	
	private static final String LOCK_FILE_NAME = ".jira-commitacceptance-test.lock";

    private static final Logger logger = Logger.getLogger(AbstractSvnCommitAcceptanceTest.class);
    
    protected RandomAccessFile lockFile;
    
    protected FileChannel svnCommitAcceptanceTestMutexFileChannel;
    
    protected FileLock svnCommitAcceptanceTestFileLock;

    protected File svnRepositoryDirectory;
    
    private File svnLookPathFile;
    
    private String commiterNameToReturnInSvnLook;
    
    private String commitMessageToReturnInSvnLook;
    
    private StringBuffer commitStandardOutputBuffer;
    
    private StringBuffer commitStandardErrorBuffer;
    

    public AbstractSvnCommitAcceptanceTest(final String name) {
        super(name);
    }

	public void setUp() {
		super.setUp();
		
		assertTrue(obtainLock());

		commiterNameToReturnInSvnLook = ADMIN_USERNAME;
		commitStandardOutputBuffer = new StringBuffer();
		commitStandardErrorBuffer = new StringBuffer();
	}

	public void tearDown() {
		try {
			releaseLock();
		} finally {
			super.tearDown();
		}
	}

	protected boolean obtainLock() {
    	boolean success = false;
    	
    	try {
			lockFile = new RandomAccessFile(new File(SystemUtils.JAVA_IO_TMPDIR, LOCK_FILE_NAME), "rw");
			svnCommitAcceptanceTestMutexFileChannel = lockFile.getChannel();
			svnCommitAcceptanceTestFileLock = svnCommitAcceptanceTestMutexFileChannel.lock();
			
			success = true;
			
    	} catch (final IOException ioe) {
    		if (logger.isEnabledFor(Level.ERROR))
    			logger.error("Error obtaining lock file", ioe);
    	}
    	
    	return success;
    }
    
    protected void releaseLock() {
    	if (null != svnCommitAcceptanceTestFileLock) {
	    	try {
	    		svnCommitAcceptanceTestFileLock.release();
	    	} catch (final IOException ioe) {
	    		if (logger.isEnabledFor(Level.ERROR))
	    			logger.error("Error releasing lock", ioe);
	    	} finally {
	    		svnCommitAcceptanceTestFileLock = null;
	    	}
    	}
    	
    	if (null != svnCommitAcceptanceTestMutexFileChannel) {
	    	try {
	    		svnCommitAcceptanceTestMutexFileChannel.close();
	    	} catch (final IOException ioe) {
	    		if (logger.isEnabledFor(Level.ERROR))
	    			logger.error("Error closing lock file channel", ioe);
	    	} finally {
	    		svnCommitAcceptanceTestMutexFileChannel = null;
	    	}
    	}
    	
    	if (null != lockFile) {
	    	try {
	    		lockFile.close();
	    	} catch (final IOException ioe) {
	    		if (logger.isEnabledFor(Level.ERROR))
	    			logger.error("Error closing lock file", ioe);
	    	} finally {
	    		lockFile = null;
	    	}
    	}
    }
    
    protected void writeSvnLookAsScript() {
    	Writer out = null;
    	
    	try {
    		out = new BufferedWriter(new FileWriter(svnLookPathFile = new File(testConfiguration.getProperty("client.scm.svn.svnlook.path"))));
    		
    		if (!SystemUtils.IS_OS_WINDOWS) {
	    		out.write(
	    				new StringBuffer()
	    						.append("#!/bin/bash\n")
	    						.append("if [ \"$1\" = \"author\" ]; then echo \"").append(commiterNameToReturnInSvnLook).append("\"; elif [ \"$1\" = \"log\" ]; then echo \"").append(commitMessageToReturnInSvnLook).append("\"; fi;")
	    						.toString());
    		} else {
	    		out.write(
	    				new StringBuffer()
	    						.append("@ECHO OFF\r\n")
	    						.append("if \"%1\" == \"author\" echo ").append(commiterNameToReturnInSvnLook).append("\r\n")
	    						.append("if \"%1\" == \"log\" echo ").append(commitMessageToReturnInSvnLook).append("\r\n")
	    						.toString());
    		}
    		
    	} catch (final IOException ioe) {
    		if (logger.isEnabledFor(Level.ERROR))
    			logger.error("Unable to create a mock svnlookpath: " + svnLookPathFile, ioe);
    		fail("Unable to create a mock svnlookpath: " + svnLookPathFile);
    	} finally {
    		IOUtils.closeQuietly(out);
    	}
    }

    protected int doCommit(final String directoryPath, final String filePath, final byte[] content, final String commitMessage) throws Exception {
    	final Runtime runtime = Runtime.getRuntime();
    	final Process process;
    	final int processExitValue;


    	commitMessageToReturnInSvnLook = commitMessage; 
		writeSvnLookAsScript();
		setUnixFilePermissionOnResource("a+x", svnLookPathFile);
    	
    	process = runtime.exec(
    			new String[] {
		    			getScriptExecutor(),
		    			scriptFile.getAbsolutePath(),
		    			filePath,
		    			RandomStringUtils.randomAlphanumeric(8)
    			}
		);
    	processExitValue = process.waitFor();
    	
    	commitStandardOutputBuffer.append(readStandardOutput(process));
    	commitStandardErrorBuffer.append(readStandardError(process));
    	
    	return processExitValue;
    }
    
    private String readStandardOutput(final Process process) throws IOException {
    	StringWriter writer = null;
    	Reader reader = null;
    	
    	try {
    		reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    		writer = new StringWriter();
    		
    		IOUtils.copy(reader, writer);
    		
    		return writer.toString();
    		
    	} finally {
    		IOUtils.closeQuietly(writer);
    		IOUtils.closeQuietly(reader);
    	}
    }
    
    private String readStandardError(final Process process) throws IOException {
    	StringWriter writer = null;
    	Reader reader = null;
    	
    	try {
    		reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    		writer = new StringWriter();
    		
    		IOUtils.copy(reader, writer);
    		
    		return writer.toString();
    		
    	} finally {
    		IOUtils.closeQuietly(writer);
    		IOUtils.closeQuietly(reader);
    	}
    }

    public void testCommit() {
        /* Commits to an existing unresolved issue which its assignee is equal to the commiter name */

        try {
            assertEquals(0, doCommit("test", "test/test.txt", "Test".getBytes(), "[TST-4] Successful commit."));
        } catch (final Exception e) {
        	if (logger.isEnabledFor(Level.INFO))
    			logger.info("Error committing", e);
            fail(ExceptionUtils.getFullStackTrace(e));
        }
    }

    public void testCommitWithNoIssueKey() {
        try {
            assertTrue(doCommit("test", "test/test.txt", "Test".getBytes(), "Commit without any isue keys using global rules.") > 0);
            assertTrue(0 < commitStandardErrorBuffer.indexOf("Commit message must contain at least one valid issue key"));
        } catch (final Exception e) {
            fail("Unknown error. See the stdin and stderr dump." +
            		SystemUtils.LINE_SEPARATOR + "Standard output: " + commitStandardOutputBuffer.toString() +
            		SystemUtils.LINE_SEPARATOR + "Error output: " + commitStandardErrorBuffer.toString());
        }
    }

    public void testCommitWithInvalidIssueKey() {
        try {
        	assertTrue(doCommit("test", "test/test.txt", "Test".getBytes(), "[XXX-1] Commit without any isue keys using global rules.") > 0);
            assertTrue(0 <commitStandardErrorBuffer.indexOf("Issue [XXX-1] does not exist or you don't have permission to access it"));
        } catch (final Exception e) {
            fail("Unknown error. See the stdin and stderr dump." +
            		SystemUtils.LINE_SEPARATOR + "Standard output: " + commitStandardOutputBuffer.toString() +
            		SystemUtils.LINE_SEPARATOR + "Error output: " + commitStandardErrorBuffer.toString());
        }
    }

    public void testCommitToResolvedIssue() {
        try {
        	assertTrue(doCommit("test", "test/test.txt", "Test".getBytes(), "[TST-2] Test Commit To Resolved Issue.") > 0);
            assertTrue(0 < commitStandardErrorBuffer.indexOf("Issue [TST-2] must be in UNRESOLVED"));
        } catch (final Exception e) {
            fail("Unknown error. See the stdin and stderr dump." +
            		SystemUtils.LINE_SEPARATOR + "Standard output: " + commitStandardOutputBuffer.toString() +
            		SystemUtils.LINE_SEPARATOR + "Error output: " + commitStandardErrorBuffer.toString());
        }
    }

    public void testCommitIssueWithDifferentAssignee() {
    	commiterNameToReturnInSvnLook = "fakeuser";

        try {
        	assertTrue(doCommit("test", "test/test.txt", "Test".getBytes(), "[TST-3] Test Commit To Issue Not Assigned To Commiter.") > 0);
            assertTrue(0 < commitStandardErrorBuffer.indexOf("Issue [TST-3] is not assigned to the correct person."));
        } catch (final Exception e) {
            fail("Unknown error. See the stdin and stderr dump." +
            		SystemUtils.LINE_SEPARATOR + "Standard output: " + commitStandardOutputBuffer.toString() +
            		SystemUtils.LINE_SEPARATOR + "Error output: " + commitStandardErrorBuffer.toString());
        }
    }
}
