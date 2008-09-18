package it.com.atlassian.jira.ext.commitacceptance;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * This is temporarily disabled as "advanced" scripts are not released.
 */
public class DisabledTestSvnCommitAcceptanceWithAdvancedPythonClient extends TestSvnCommitAcceptanceWithPythonClient {
	
	protected File svnRepositoryConfigDirectory;

	public DisabledTestSvnCommitAcceptanceWithAdvancedPythonClient() {
		super(DisabledTestSvnCommitAcceptanceWithAdvancedPythonClient.class.getName());
	}

    public void setUp() {
		super.setUp();
    	
    	svnRepositoryConfigDirectory = new File(svnRepositoryDirectory, "conf");
    	assertTrue("Unable to create directory: " + svnRepositoryConfigDirectory, svnRepositoryConfigDirectory.mkdirs());
    	copyAdvancedScriptConfiguration();
	}
    
    public void tearDown() {
    	try {
	    	if (svnRepositoryConfigDirectory.isDirectory())
	    		FileUtils.deleteDirectory(svnRepositoryConfigDirectory);
    	} catch (final IOException ioe) {
    		ioe.printStackTrace();
    	} finally {
    		super.tearDown();
    	}
	}

	protected void copyAdvancedScriptConfiguration() {
    	InputStream in = null;
    	OutputStream out = null;
    	
    	try {
    		in = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("client/python/svn/advanced/commitacceptance.conf"));
    		out = new BufferedOutputStream(new FileOutputStream(new File(svnRepositoryConfigDirectory, "commitacceptance.conf")));
    		
    		IOUtils.copy(in, out);
    		
    	} catch (final IOException ioe) {
    		fail(ExceptionUtils.getFullStackTrace(ioe));
    	} finally {
    		IOUtils.closeQuietly(out);
    		IOUtils.closeQuietly(in);
    	}
    }

	protected InputStream getScriptContent() {
        return getClass().getClassLoader().getResourceAsStream("client/python/svn/advanced/jira-client.py");
    }
}
