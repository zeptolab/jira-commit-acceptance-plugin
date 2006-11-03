#!/usr/bin/perl

# JIRA commit acceptance perl client
# Author: istvan.vamosi@midori.hu
# $Id$

use strict;
use warnings;

use XMLRPC::Lite;

# Set URL of the JIRA server
my $jiraBaseURL = "http://127.0.0.1:8080";

# Set path to svnlook executable
my $svnlookPath = "C:\Program Files\svn-win32-1.4.0\bin\svnlook.exe"

# Set login and password to connect to the JIRA
my $jiraLogin = "root";
my $jiraPassword = "root";

my $commiter = "root"; #TODO: GET FROM SVN/CVS
my $commitMessage = "Test commit message [TEST-1]"; #TODO: GET FROM SVN/CVS

my $acceptance;
my $comment;

# get arguments from SVN
select(STDERR);

print "Repository: " . $ARGV[0] . "\n";
print "Transaction: " . $ARGV[1] . "\n";

# TODO call svnlook with these two
# TODO get committer and commitMessage from these

eval {
	$jiraBaseURL =~ s/\/+$//; # Remove trailing '/' if exists
	my $s = XMLRPC::Lite->proxy($jiraBaseURL . "/rpc/xmlrpc");
	my $result = $s->call("commitacc.acceptCommit", $jiraLogin, $jiraPassword, $commiter, $commitMessage)->result();
	($acceptance, $comment) = split('\|', $result);
};

if ($@) {
	($acceptance, $comment) = ("false", "Unable to connect to the JIRA server at \"" . $jiraBaseURL . "\".");
};


if($acceptance eq 'true') {
	print "Commit accepted.\n";
	exit 0;
} else {
	print "Commit rejected: " . $comment . "\n";
	exit 1;
}