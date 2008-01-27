package it.com.atlassian.jira.ext.commitacceptance;


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
