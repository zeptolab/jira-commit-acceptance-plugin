#!/usr/bin/perl

# JIRA commit acceptance perl client for Subversion
# Author: istvan.vamosi@midori.hu
# $Id$

use strict;
use warnings;

use XMLRPC::Lite;

# configure JIRA access
my $jiraBaseURL = "http://127.0.0.1:8080";
my $jiraLogin = "root";
my $jiraPassword = "root";

# get committer
my $committer = "root"; #TODO: GET FROM SVN/CVS

# Set path to svnlook executable
my $svnlookPath = "C:\Program Files\svn-win32-1.4.0\bin\svnlook.exe"
# TODO call svnlook 
my $commitMessage = "Test commit message [TEST-1]"; #TODO: GET FROM SVN/CVS

# print arguments
select(STDERR);
print "Repository: " . $ARGV[0] . "\n";
print "Transaction: " . $ARGV[1] . "\n";
print "Committer: " . $committer . "\n";
print "Commit message: \"" . $commitMessage . "\"\n";

# invoke JIRA web service
# TODO move this common  part to a separate.pl and use it also from the SVN client,
my $acceptance;
my $comment;

eval {
	$jiraBaseURL =~ s/\/+$//; # remove trailing '/' if exists
	my $s = XMLRPC::Lite->proxy($jiraBaseURL . "/rpc/xmlrpc");
	my $result = $s->call("commitacc.acceptCommit", $jiraLogin, $jiraPassword, $committer, $commitMessage)->result();
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