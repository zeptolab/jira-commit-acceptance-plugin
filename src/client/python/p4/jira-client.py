#!/usr/bin/python

# JIRA commit acceptance python client for P4
# Author: ferenc.kiss@midori.hu
# $Id: jira-client.py 10144 2007-09-12 10:13:37Z ferenc.kiss $

import os
import sys
import urlparse
import xmlrpclib

# configure JIRA access
# ("projectKey" can contain multiple comma-separated JIRA project keys like "projectKey = 'TST,ARP'".
# If you specify multiple keys, the commit will be accepted if at least one project listed accepts it.
# Or you can specify "projectKey = '*'" to force using the global commit acceptance settings if you don't
# want to specify any exact project key.)
jiraBaseURL = 'http://127.0.0.1:8080'
jiraLogin = 'admin'
jiraPassword = 'admin'
projectKey = 'TST'

# configure svnlook path
p4Path = 'p4'

# get committer passed as arg[1]
committer = sys.argv[1]

# get change description with change passed as arg[2]
try:
	f = os.popen(p4Path + ' -s describe ' + sys.argv[2])
	commitMessage = f.read()
	if f.close():
		raise 1
except:
	print >> sys.stderr, 'Unable to get change <' + sys.argv[2] + '> description with p4.'
	sys.exit(1)

# TODO trim leading whitespace
	
# print arguments
print 'Committer: ' + committer
print 'Commit message: "' + commitMessage + '"'

# invoke JIRA web service
try:
	s = xmlrpclib.ServerProxy(urlparse.urljoin(jiraBaseURL, '/rpc/xmlrpc'))
	acceptance, comment = s.commitacc.acceptCommit(jiraLogin, jiraPassword, committer, projectKey, commitMessage).split('|');
except:
	acceptance, comment = ['false', 'Unable to connect to the JIRA server at "' + jiraBaseURL + '".']

if acceptance == 'true':
	print 'Commit accepted.'
	sys.exit(0)
else:
	print 'Commit rejected: ' + comment
	sys.exit(1)
