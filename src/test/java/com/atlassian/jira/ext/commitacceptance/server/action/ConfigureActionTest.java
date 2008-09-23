package com.atlassian.jira.ext.commitacceptance.server.action;

import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.core.ofbiz.test.mock.MockGenericValue;
import com.atlassian.core.util.collection.EasyList;
import com.atlassian.core.util.map.EasyMap;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class ConfigureActionTest extends MockObjectTestCase
{

    private ProjectManager projectManager;

    private Mock mockProjectManager;

    private AcceptanceSettingsManager acceptanceSettingsManager;

    private Mock mockAcceptanceSettingsManager;

    private ConfigureAction configureAction;

    private PermissionManager permissionManager;

    private Mock mockPermissionManager;

    protected void setUp() throws Exception
    {
        super.setUp();

        mockProjectManager = new Mock(ProjectManager.class);
        projectManager = (ProjectManager) mockProjectManager.proxy();

        mockAcceptanceSettingsManager = new Mock(AcceptanceSettingsManager.class);
        acceptanceSettingsManager = (AcceptanceSettingsManager) mockAcceptanceSettingsManager.proxy();

        mockPermissionManager = new Mock(PermissionManager.class);
        permissionManager = (PermissionManager) mockPermissionManager.proxy();

        mockPermissionManager.expects(atLeastOnce()).method("hasPermission").with(eq(Permissions.ADMINISTER), eq(null)).will(returnValue(true));

        configureAction = new ConfigureAction(projectManager, acceptanceSettingsManager)
        {
            protected PermissionManager getPermissionManager()
            {
                return permissionManager;
            }
        };
    }

    public void testExecuteWhenUserHasNoAdminPermission() throws Exception 
    {
        mockPermissionManager.reset();
        mockPermissionManager.expects(once()).method("hasPermission").with(eq(Permissions.ADMINISTER), eq(null)).will(returnValue(false));

        assertEquals("error", configureAction.execute());
    }

    public void testLoadSettingsOnExecute() throws Exception
    {
        final AcceptanceSettings loadedSettings = new AcceptanceSettings();

        loadedSettings.setAcceptIssuesFor(Integer.MAX_VALUE); /* Sets a value which we will check for after execute is called */

        configureAction.setProjectKey("TST");
        configureAction.setSubmitted(null);

        mockAcceptanceSettingsManager.expects(once()).method("getSettings").with(eq("TST")).will(returnValue(loadedSettings));

        assertEquals("success", configureAction.execute());
        assertEquals(Integer.MAX_VALUE, loadedSettings.getAcceptIssuesFor()); /* Won't cause NPE */
    }

    public void testStoreSettingsOnExecute() throws Exception
    {
        final AcceptanceSettings loadedSettings = new AcceptanceSettings();

        loadedSettings.setUseGlobalRules(false);
        loadedSettings.setMustHaveIssue(false);
        loadedSettings.setMustBeUnresolved(false);
        loadedSettings.setMustBeAssignedToCommiter(false);
        loadedSettings.setAcceptIssuesFor(Integer.MAX_VALUE); /* Sets a value which we will check for after execute is called */

        configureAction.setProjectKey("TST");
        configureAction.setSubmitted(null);

        /* This will allow the loadedSettings to be used as the loaded settings, which we will save later */
        mockAcceptanceSettingsManager.expects(once()).method("getSettings").with(eq("TST")).will(returnValue(loadedSettings));
        assertEquals("success", configureAction.execute());

        /* Change acceptance settings */
        configureAction.setUseGlobalRules(true);
        configureAction.setMustHaveIssue(true);
        configureAction.setMustBeUnresolved(true);
        configureAction.setMustBeAssignedToCommiter(true);
        configureAction.setAcceptIssuesFor(Integer.MIN_VALUE);

        configureAction.setSubmitted("Random string to indicate that the submission is done.");

        /* This will be called if the acceptance settings is persisted */
        mockAcceptanceSettingsManager.expects(once()).method("setSettings").with(eq("TST"), eq(loadedSettings));


        assertEquals("success", configureAction.execute());

        assertTrue(configureAction.getUseGlobalRules());
        assertTrue(configureAction.isMustHaveIssue());
        assertTrue(configureAction.isMustBeUnresolved());
        assertTrue(configureAction.isMustBeAssignedToCommiter());
        assertEquals(Integer.MIN_VALUE, configureAction.getAcceptIssuesFor());
        /* If mocks verify successfully, the test passed */
    }

    public void testGetProjectSortedByKey() {
        final List projectGvs = EasyList.build(
                new MockGenericValue("Project", EasyMap.build("key", "ZZZ")),
                new MockGenericValue("Project", EasyMap.build("key", "AAA"))
        );
        final List sortedProjectGvs = new ArrayList(projectGvs);

        Collections.reverse(sortedProjectGvs);

        mockProjectManager.expects(once()).method("getProjects").withNoArguments().will(returnValue(projectGvs));
        mockPermissionManager.reset(); /* Reset expectations since we are not calling execute() */


        assertEquals(sortedProjectGvs, configureAction.getProjects());
    }
}
