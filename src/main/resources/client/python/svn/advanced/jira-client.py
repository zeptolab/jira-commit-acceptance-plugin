#!/usr/bin/python

# JIRA commit acceptance python client for SVN
# Author: peter.kolbus@unusalcode.com
# $Id$
#
# Usage:
#    jira-client.py {repository} {transaction} [-r|-t]
#
#    repository   The path to the Subversion repository
#    transaction  The change (transaction) number
#    -r           Process the change as a revision
#    -t           Process the change as a transaction (default)
#
#
# Do not edit this script directly; as of version 1.2, all parameters are in a
# separate configuration file, commitacceptance.conf.  A sample is provided
# with this script, it should be edited and moved to
# {repository path}/conf/commitacceptance.conf.

import os
import sys
import re
import urlparse
import xmlrpclib

class ConfigParseException(Exception):
	def __init__(self, value):
		self.value = value
	def __str__(self):
		return repr(self.value)

class SubversionClientException(Exception):
	def __init__(self, value):
		self.value = value
	def __str__(self):
		return repr(self.value)

class SubversionClient:

	def __init__(self):
		# configure svnlook path
		self.svnlookPath = 'C:\\Progra~1\\svn-win32-1.4.0\\bin\\svnlook.exe'

	# get committer
	def getCommitter(self):
		try:
			f = os.popen('%s author %s %s %s' % (self.svnlookPath, self.repository, self.mode, self.transaction))
			committer = f.read()
			if f.close():
				raise 'fail'
			committer = committer.rstrip("\n\r")
		except:
			raise SubversionClientException('Unable to get committer with svnlook.')

		return committer

	# get commit message
	def getCommitMessage(self):
		try:
			f = os.popen('%s log %s %s %s' % (self.svnlookPath, self.repository, self.mode, self.transaction))
			commitMessage = f.read()
			if f.close():
				raise 'fail'
			commitMessage = commitMessage.rstrip('\n\r')
		except:
			raise SubversionClientException('Unable to get commit message with svnlook.')

		return commitMessage

	# get changed files
	def getChangedFiles(self):
		try:
			f = os.popen('%s changed %s %s %s' % (self.svnlookPath, self.repository, self.mode, self.transaction))
			items = f.readlines()
			f.close()
			fileList = []
			for line in items:
				token = line.split()
				fileList.append(token[1])
		except:
			raise SubversionClientException('Unable to get file list with svnlook.')

		return fileList

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
				s = xmlrpclib.ServerProxy(urlparse.urljoin(self.jiraBaseURL, self.jiraContext + '/rpc/xmlrpc'))

				result = s.commitacc.acceptCommit(self.jiraLogin, self.jiraPassword, committer, projectKey, commitMessage);
				acceptance, comment = result.split('|');
				self.cache[projectKey] = result
			except:
				acceptance, comment = ['false', 'Unable to connect to the JIRA server at "' + self.jiraBaseURL + self.jiraContext + '".']

		return (acceptance, comment)

class ListEvaluator:

	def __init__(self, rootEvaluator):
		self.evaluator = rootEvaluator

	# Require that all projects in the list be accepted by the service.
	# The comment from the first failure is returned.
	def requireAll(self, projectList, committer, commitMessage):

		projects = projectList.split(',')

		for project in projects:
			(acceptance, comment) = self.evaluator.checkCommit(project, committer, commitMessage)
			if (acceptance == 'false'):
				return (acceptance, comment)

		return ('true', 'All projects accepted.')

	# Require one project in the list to be accepted by the service.
	# If all fail, the last message is returned.
	def requireOne(self, projectList, committer, commitMessage):

		projects = projectList.split(',')
		rejectMessage = ''

		for project in projects:
			(acceptance, comment) = self.evaluator.checkCommit(project, committer, commitMessage)
			if (acceptance == 'true'):
				return (acceptance, comment)
			else:
				rejectMessage = rejectMessage + '\n[%s]: %s' % (project, comment)

		return ('false', 'No projects in [' + projectList + '] accepted the log message.\n' + rejectMessage)

class AcceptanceException:

	def __init__(self, path, comment):
		self.path = path
		self.comment = comment


# construct helper objects
svn = SubversionClient()
evaluator = EvaluatorClient()
listEvaluator = ListEvaluator(evaluator)
defaultProject = ''

# get parameters for SVN from command line
try:
	svn.repository = sys.argv[1]
	svn.transaction = sys.argv[2]
	if len(sys.argv) > 3:
		svn.mode = sys.argv[3]
	else:
		svn.mode = '--transaction'
except:
	print >> sys.stderr, 'Unable to parse command line arguments'
	sys.exit(1)

try:
	f = open(svn.repository + '/conf/commitacceptance.conf','r')
	confLines = f.readlines()
	f.close()
	checkList=[]
	for line in confLines:
		line = line.strip()
		if line.startswith('#') or line == '':
			continue
		token = line.split()
		if (token[0].lower() == 'svnlookpath'):
			svn.svnlookPath = token[1]
		elif (token[0].lower() == 'jirabaseurl'):
			evaluator.jiraBaseURL = token[1]
		elif (token[0].lower() == 'jiracontext'):
			evaluator.jiraContext = token[1]
		elif (token[0].lower() == 'jiralogin'):
			evaluator.jiraLogin = token[1]
		elif (token[0].lower() == 'jirapassword'):
			evaluator.jiraPassword = token[1]
		elif (token[0].lower() == 'default'):
			defaultProject = token[1]
		else:
			if (len(token) == 3):
				if (token[1].lower() == 'allof'):
					checkList.append( (token[0], token[2], 'ALL') )
				elif (token[1].lower() == 'oneof'):
					checkList.append( (token[0], token[2], 'ONE') )
				else:
					raise ConfigParseException('Expected AllOf|OneOf, got ' + token[1])
			elif (len(token) == 2):
				checkList.append( (token[0], token[1], 'ALL') )
			else:
				raise ConfigParseException('Expected (regex) [AllOf|OneOf] (projectKey), got ' + line)
except ConfigParseException, ex:
	print >> sys.stderr, 'Unable to parse commitacceptance.conf: ' + ex
	sys.exit(1)
except:
	print >> sys.stderr, "Unexpected error: " + sys.exc_info()[0]
	sys.exit(1)

try:
	committer = svn.getCommitter()
	commitMessage = svn.getCommitMessage()
	fileList = svn.getChangedFiles()
except SubversionClientException, ex:
	print >> sys.stderr, ex
	sys.exit(1)
except:
	print >> sys.stderr, "Unexpected error: " + sys.exc_info()[0]
	sys.exit(1)

# print arguments
print >> sys.stderr, 'Committer: ' + committer
print >> sys.stderr, 'Commit message: "' + commitMessage + '"'

try:
	acceptance = 'true'

	# Parse the fileList
	for path in fileList:
		matchedAny = 'false'
		for check in checkList:
			(pattern, projects, mode) = check
			if (re.search(pattern,path) is not None):
				matchedAny = 'true'
				if (mode == 'ALL'):
					(acceptance, comment) = listEvaluator.requireAll(projects, committer, commitMessage)
				else:
					(acceptance, comment) = listEvaluator.requireOne(projects, committer, commitMessage)
			if acceptance == 'false':
				raise AcceptanceException(path, comment)

		if (matchedAny == 'false') and (defaultProject != ''):
			(acceptance, comment) = listEvaluator.requireAll(defaultProject, committer, commitMessage)
		if acceptance == 'false':
			raise AcceptanceException(path, comment)

except AcceptanceException, ex:
	print >> sys.stderr, 'Rejected file: ' + ex.path
	print >> sys.stderr, 'Reason: ' + ex.comment
	sys.exit(1)

# All files made it through all checks.
print >> sys.stderr, 'Commit accepted.'
sys.exit(0)
