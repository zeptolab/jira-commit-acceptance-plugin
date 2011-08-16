#!/usr/bin/python

# JIRA commit acceptance python client for SVN
# Author: istvan.vamosi@midori.hu
# $Id$

import os
import sys
import urlparse
import xmlrpclib

# configure JIRA access
# ("projectKey" can contain multiple comma-separated JIRA project keys like "projectKey = 'TST,ARP'".
# If you specify multiple keys, the commit will be accepted if at least one project listed accepts it.
# Or you can specify "projectKey = '*'" to force using the global commit acceptance settings if you don't
# want to specify any exact project key.)
jiraBaseURL = '<JIRA base URL>'
jiraLogin = '<JIRA user name>'
jiraPassword = '<JIRA password>'
projectKey = '<JIRA project key>'

# configure svnlook path
svnlookPath = '<Full path to svnlook>'

# get committer
try:
	f = os.popen(svnlookPath + ' author ' + sys.argv[1] + ' --transaction ' + sys.argv[2])
	committer = f.read()
	if f.close():
		raise 1
	committer = committer.rstrip("\n\r")
except:
	print >> sys.stderr, 'Unable to get committer with svnlook.'
	sys.exit(1)

# get commit message
try:
	f = os.popen(svnlookPath + ' log ' + sys.argv[1] + ' --transaction ' + sys.argv[2])
	commitMessage = f.read()
	if f.close():
		raise 1
	commitMessage = commitMessage.rstrip('\n\r')
except:
	print >> sys.stderr, 'Unable to get commit message with svnlook.'
	sys.exit(1)

# print arguments
print >> sys.stderr, 'Committer: ' + committer
print >> sys.stderr, 'Commit message: "' + commitMessage + '"'

# invoke JIRA web service
xmlrpcUrl = jiraBaseURL + '/rpc/xmlrpc'
try:
	s = xmlrpclib.ServerProxy(xmlrpcUrl)
	acceptance, comment = s.commitacc.acceptCommit(jiraLogin, jiraPassword, committer, projectKey, commitMessage).split('|');
except:
	acceptance, comment = ['false', 'Unable to connect to the JIRA server at "' + jiraBaseURL + '".']

if acceptance == 'true':
	print >> sys.stderr, 'Commit accepted.'
	sys.exit(0)
else:
	print >> sys.stderr, 'Commit rejected: ' + comment
	sys.exit(1)
