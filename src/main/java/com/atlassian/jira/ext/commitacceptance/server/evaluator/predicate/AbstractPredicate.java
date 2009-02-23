package com.atlassian.jira.ext.commitacceptance.server.evaluator.predicate;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.user.preferences.PreferenceKeys;
import com.atlassian.jira.user.preferences.JiraUserPreferences;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.jira.web.util.JiraLocaleUtils;
import com.opensymphony.user.User;

import java.util.ResourceBundle;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractPredicate implements JiraPredicate {
	
	private I18nBean i18nBean;
	
	protected JiraAuthenticationContext getJiraAuthenticationContext() {
		return ComponentManager.getInstance().getJiraAuthenticationContext();
	}

	protected I18nBean getI18nBean() {
		if (null == i18nBean) {
			i18nBean = new I18nBean(getJiraAuthenticationContext().getUser());
            i18nBean.addBundle(
                    ResourceBundle.getBundle(
                            "templates.commitacceptance-plugin",
                            getLocaleFromUser(getJiraAuthenticationContext().getUser())
                    )
            );
		}
		
		return i18nBean;
	}

    private Locale getLocaleFromUser(final User user)
    {
        if (user != null)
        {
            final JiraUserPreferences userPrefs = new JiraUserPreferences(user, false);
            final String localeStr = userPrefs.getString(PreferenceKeys.USER_LOCALE);

            if (!StringUtils.isBlank(localeStr))
            {
                return JiraLocaleUtils.parseLocale(localeStr);
            }
        }
        return ComponentManager.getInstance().getApplicationProperties().getDefaultLocale();
    }
}
