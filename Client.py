#!/usr/bin/python

import sys
import urlparse
import xmlrpclib

# Set URL of the JIRA server
jiraBaseURL = 'http://127.0.0.1:8080'

# Set login and password to connect to the JIRA
scmLogin = 'root'
scmPassword = 'root'

commiter = 'root' #TODO: GET FROM SVN/CVS
commitMessage = 'Test commit message TST-2' #TODO: GET FROM SVN/CVS

try:
	s = xmlrpclib.ServerProxy(urlparse.urljoin(jiraBaseURL, '/rpc/xmlrpc'))
	acceptance, comment = s.commitacc.acceptCommit(scmLogin, scmPassword, commiter, commitMessage).split('|');
except:
	acceptance, comment = ['false', 'Sorry, cannot connect to the JIRA.']

print acceptance #TODO: CONVERT TO BOOLEAN AND PASS TO SVN/CVS
print >> sys.stderr, comment
