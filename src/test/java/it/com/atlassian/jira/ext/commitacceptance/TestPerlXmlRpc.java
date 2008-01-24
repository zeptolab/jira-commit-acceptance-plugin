package it.com.atlassian.jira.ext.commitacceptance;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;

import com.atlassian.jira.webtests.JIRAWebTest;


public class TestPerlXmlRpc extends JIRAWebTest {
	
	protected Properties testConfiguration;
	
	protected File perlScript;
	
	protected Process perlProcess;
	
	public TestPerlXmlRpc() {
		super(TestPerlXmlRpc.class.getName());
	}
	
	public void tearDown() {
		perlScript.delete();
		super.tearDown();
	}
	
	public void setUp() {
		InputStream in = null;
		InputStream scriptInput = null;
		OutputStream scriptOutput = null;
		
		super.setUp();
		restoreData("testdata-export.xml");
		
		try {
			testConfiguration = new Properties();
			
			in = getClass().getClassLoader().getResourceAsStream("test-configuration.properties");
			testConfiguration.load(in);
			
		} catch (final IOException ioe) {
			fail("Unable to read test configuration: " + ioe.getMessage());
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		do {
			perlScript = new File(SystemUtils.JAVA_IO_TMPDIR, RandomStringUtils.randomAlphanumeric(16));
		} while (perlScript.exists());
		
		try {
			scriptInput = getClass().getClassLoader().getResourceAsStream("perl-xmlrpc-test.pl");
			scriptOutput = new BufferedOutputStream(new FileOutputStream(perlScript));
			
			IOUtils.copy(scriptInput, scriptOutput);
			
		} catch (final IOException ioe) {
			fail("Unable to copy Perl test script: "+ ioe.getMessage());
		} finally {
			IOUtils.closeQuietly(scriptInput);
			IOUtils.closeQuietly(scriptOutput);
		}
	}
	
	protected String getStandardOutputText() throws IOException {
		ByteArrayOutputStream stdoutBuffer = null;
		InputStream stdout = null;
		
		try {
			stdout = perlProcess.getInputStream();
			stdoutBuffer = new ByteArrayOutputStream();
			
			IOUtils.copy(stdout, stdoutBuffer);
			
			return new String(stdoutBuffer.toByteArray());
		
		} finally {
			IOUtils.closeQuietly(stdout);
			IOUtils.closeQuietly(stdoutBuffer);
		}
	}
	
	protected String getStandardErrorText() throws IOException {
		ByteArrayOutputStream stderrBuffer = null;
		InputStream stderr = null;
		
		try {
			stderr = perlProcess.getErrorStream();
			stderrBuffer = new ByteArrayOutputStream();
			
			IOUtils.copy(stderr, stderrBuffer);
			
			return new String(stderrBuffer.toByteArray());
		
		} finally {
			IOUtils.closeQuietly(stderr);
			IOUtils.closeQuietly(stderrBuffer);
		}
	}
	
	public void testConnect() throws IOException, InterruptedException {
		final String perlExecutable = testConfiguration.getProperty("client.scm.perl.path");
		final int scriptExecutionExitValue;
		
		
		assertNotNull("Unable to find Perl executable. Have you defined it in test-configuration.properties", perlExecutable);
		
		perlProcess = Runtime.getRuntime().exec(
				new String[] {
						perlExecutable,
						perlScript.getAbsolutePath()
				}
		);
		
		scriptExecutionExitValue = perlProcess.waitFor();
//		System.out.println("Process exited with value: " + scriptExecutionExitValue);
//		System.out.println("Standard output dump:");
//		System.out.println(getStandardOutputText());
//		System.out.println("Standard error dump:");
//		System.out.println(getStandardErrorText());
		
		if (scriptExecutionExitValue != 0)
			fail(new StringBuffer("Perl script exited with: ").append(scriptExecutionExitValue).append(SystemUtils.LINE_SEPARATOR)
					.append("Script standard output:").append(SystemUtils.LINE_SEPARATOR)
					.append(getStandardOutputText())
					.append("Script error output:").append(SystemUtils.LINE_SEPARATOR)
					.append(getStandardErrorText())
					.toString());
	}
}
