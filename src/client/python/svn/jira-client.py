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
# If you specify multiple keys, the commit will be accepted if at least one project listed accepts it.)
jiraBaseURL = 'http://127.0.0.1:8080'
jiraLogin = 'root'
jiraPassword = 'root'
projectKey = 'TST'

# configure svnlook path
svnlookPath = 'C:\\Progra~1\\svn-win32-1.4.0\\bin\\svnlook.exe'

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
try:
	s = xmlrpclib.ServerProxy(urlparse.urljoin(jiraBaseURL, '/rpc/xmlrpc'))
	acceptance, comment = s.commitacc.acceptCommit(jiraLogin, jiraPassword, committer, projectKey, commitMessage).split('|');
except:
	acceptance, comment = ['false', 'Unable to connect to the JIRA server at "' + jiraBaseURL + '".']

if acceptance == 'true':
	print >> sys.stderr, 'Commit accepted.'
	sys.exit(0)
else:
	print >> sys.stderr, 'Commit rejected: ' + comment
	sys.exit(1)
