package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.I18nBean;

public abstract class AbstractPredicate implements JiraPredicate {
	
	private I18nBean i18nBean;
	
	protected JiraAuthenticationContext getJiraAuthenticationContext() {
		return ComponentManager.getInstance().getJiraAuthenticationContext();
	}

	protected I18nBean getI18nBean() {
		if (null == i18nBean) {
			i18nBean = new I18nBean(getJiraAuthenticationContext().getUser(), "templates/commitacceptance-plugin");
		}
		
		return i18nBean;
	}
}
