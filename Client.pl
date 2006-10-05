#!/usr/bin/perl

use strict;
use warnings;

use XMLRPC::Lite;

# Set URL of the JIRA server
my $jiraBaseURL = "http://127.0.0.1:8080";

# Set login and password to connect to the JIRA
my $scmLogin = "root";
my $scmPassword = "root";

my $commiter = "testUser"; #TODO: GET FROM SVN/CVS
my $commitMessage = "Test commit message"; #TODO: GET FROM SVN/CVS

$jiraBaseURL =~ s/\/+$//; # Remove trailing '/' if exists

my $acceptance;
my $comment;

eval {
	my $s = XMLRPC::Lite->proxy($jiraBaseURL . "/rpc/xmlrpc");
	my $result = $s->call("commitacc.acceptCommit", $scmLogin, $scmPassword, $commiter, $commitMessage)->result();
	($acceptance, $comment) = split('\|', $result);
};

if ($@) {
	($acceptance, $comment) = ("false", "Sorry, cannot connect to the JIRA.");
};


print $acceptance . "\n"; #TODO: CONVERT TO BOOLEAN AND PASS TO SVN/CVS

select(STDERR);
print $comment . "\n";
