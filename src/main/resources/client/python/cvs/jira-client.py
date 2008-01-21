#!/usr/bin/python

# JIRA commit acceptance python client for CVS
# Author: istvan.vamosi@midori.hu
# $Id$

import sys
import urlparse
import xmlrpclib

# configure JIRA access
# ("projectKey" can contain multiple comma-separated JIRA project keys like "projectKey = 'TST,ARP'".
# If you specify multiple keys, the commit will be accepted if at least one project listed accepts it.
# Or you can specify "projectKey = '*'" to force using the global commit acceptance settings if you don't
# want to specify any exact project key.)
jiraBaseURL = 'http://127.0.0.1:${jira.http.port}/jira'
jiraLogin = '${client.scm.username}'
jiraPassword = '${client.scm.password}'
projectKey = '${client.scm.projectkey}'

# get committer passed as arg[1]
committer = sys.argv[1]

# slurp log message from log message file passed as arg[2]
try:
	f = open(sys.argv[2])
	commitMessage = f.read()
	f.close()
	commitMessage = commitMessage.rstrip('\n\r')
except:
	print 'Unable to open ' + sys.argv[2] + '.'
	sys.exit(1)

# print arguments
print 'Committer: ' + committer
print 'Commit message: "' + commitMessage + '"'

# invoke JIRA web service
xmlrpcUrl = jiraBaseURL + '/rpc/xmlrpc'
try:
	s = xmlrpclib.ServerProxy(xmlrpcUrl)
	acceptance, comment = s.commitacc.acceptCommit(jiraLogin, jiraPassword, committer, projectKey, commitMessage).split('|');
except:
	acceptance, comment = ['false', 'Unable to connect to the JIRA server at "' + jiraBaseURL + '".']

if acceptance == 'true':
	print 'Commit accepted.'
	sys.exit(0)
else:
	print 'Commit rejected: ' + comment
	sys.exit(1)
