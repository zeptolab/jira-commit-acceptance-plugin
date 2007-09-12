#!/usr/bin/python

# JIRA commit acceptance python client for CVS
# Syntax:
#   jira-client.py [[--allof|--oneof] projectList] committer messageFile
#

# Author: istvan.vamosi@midori.hu
# $Id: jira-client.py 6479 2007-03-28 17:54:09Z ferenc.kiss $

import sys
import urlparse
import xmlrpclib

class EvaluatorClient:

        def __init__(self):
                # defaults JIRA access
                self.jiraBaseURL = ''
                self.jiraContext = ''
                self.jiraLogin = ''
                self.jiraPassword = ''
                self.cache = {}

        def checkCommit(self, projectKey, committer, commitMessage):

                if projectKey in self.cache:
                        acceptance, comment = self.cache[projectKey].split('|');
                else:
                        try:
                                s = xmlrpclib.ServerProxy(urlparse.urljoin(self.
jiraBaseURL, self.jiraContext + '/rpc/xmlrpc'))
                                
                                result = s.commitacc.acceptCommit(self.jiraLogin
, self.jiraPassword, committer, projectKey, commitMessage);
                                acceptance, comment = result.split('|');
                                self.cache[projectKey] = result
                        except:
                                acceptance, comment = ['false', 'Unable to conne
ct to the JIRA server at "' + self.jiraBaseURL + self.jiraContext + '".']
                
                return (acceptance, comment)

# setup the objects
evaluator = EvaluatorClient()
listEvaluator = ListEvaluator(evaluator)


# configure JIRA access
evaluator.jiraBaseURL = 'http://127.0.0.1:8080'
evaluator.jiraLogin = 'root'
evaluator.jiraPassword = 'root'

mode = 'ALL'
projectKey = 'TST'

# Slurp a project key if there is one
lastArg = len(sys.argv) - 1
if (len(sys.argv) > 3):
	projectKey = sys.argv[1]
	if (len(sys.argv) > 4):
		if (projectKey.lower() == '--allof'):
			mode = 'ALL'
			projectKey = sys.argv[2]
		elif (projectKey.lower() == '--oneof'):
			mode = 'ONE'
			projectKey = sys.argv[2]
		else:
			print 'Invalid command line arguments'

	

# get committer passed as the second to last argument
committer = sys.argv[lastArg - 1]

# slurp log message from log message file passed as arg[2]
messageFile = sys.argv[lastArg]
try:
	f = open(messageFile)
	commitMessage = f.read()
	f.close()
	commitMessage = commitMessage.rstrip('\n\r')
except:
	print 'Unable to open ' + messageFile + '.'
	sys.exit(1)

# print arguments
print 'Committer: ' + committer
print 'Commit message: "' + commitMessage + '"'
print 'Checking JIRA projects: ' + projectKey

# invoke JIRA web service
if (mode == 'ALL'):
	(acceptance, comment) = listEvaluator.requireAll(projectKey, committer, 
commitMessage)
else:
	(acceptance, comment) = listEvaluator.requireOne(projectKey, committer, 
commitMessage)

if acceptance == 'true':
	print 'Commit accepted.'
	sys.exit(0)
else:
	print 'Commit rejected: ' + comment
	sys.exit(1)
