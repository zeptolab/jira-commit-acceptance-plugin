#!/usr/bin/perl

# JIRA commit acceptance perl client
# Author: istvan.vamosi@midori.hu
# $Id$

use strict;
use warnings;

use XMLRPC::Lite;

# Set URL of the JIRA server
my $jiraBaseURL = "http://127.0.0.1:8080";

# Set login and password to connect to the JIRA
my $scmLogin = "root";
my $scmPassword = "root";

my $commiter = "root"; #TODO: GET FROM SVN/CVS
my $commitMessage = "Test commit message [TEST-1]"; #TODO: GET FROM SVN/CVS

$jiraBaseURL =~ s/\/+$//; # Remove trailing '/' if exists

my $acceptance;
my $comment;

eval {
	my $s = XMLRPC::Lite->proxy($jiraBaseURL . "/rpc/xmlrpc");
	my $result = $s->call("commitacc.acceptCommit", $scmLogin, $scmPassword, $commiter, $commitMessage)->result();
	($acceptance, $comment) = split('\|', $result);
};

if ($@) {
	($acceptance, $comment) = ("false", "Unable to connect to the JIRA server at \"" . $jiraBaseURL . "\".");
};


print "Commit accepted: " . $acceptance . "\n"; #TODO: CONVERT acceptance TO BOOLEAN AND PASS TO SVN/CVS

select(STDERR);
print $comment . "\n";
