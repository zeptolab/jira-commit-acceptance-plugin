package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.I18nHelper;

public abstract class AbstractPredicate implements JiraPredicate {
	
	protected JiraAuthenticationContext getJiraAuthenticationContext() {
		return ComponentManager.getInstance().getJiraAuthenticationContext();
	}

	protected I18nHelper getI18nHelper() {
		return getJiraAuthenticationContext().getI18nHelper();
	}

//    private Locale getLocaleFromUser(final User user)
//    {
//        if (user != null)
//        {
//            final JiraUserPreferences userPrefs = new JiraUserPreferences(user);
//            final String localeStr = userPrefs.getString(PreferenceKeys.USER_LOCALE);
//
//            if (!StringUtils.isBlank(localeStr))
//            {
//                return JiraLocaleUtils.parseLocale(localeStr);
//            }
//        }
//        return ComponentManager.getInstance().getApplicationProperties().getDefaultLocale();
//    }
}
