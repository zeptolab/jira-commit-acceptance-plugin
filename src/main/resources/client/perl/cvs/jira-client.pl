#!/usr/bin/perl

# JIRA commit acceptance perl client for CVS
# Author: istvan.vamosi@midori.hu
# $Id$

use strict;
use warnings;

use XMLRPC::Lite;

# configure JIRA access
# ($projectKey can contain multiple comma-separated JIRA project keys like 'my $projectKey = "TST,ARP";'.
# If you specify multiple keys, the commit will be accepted if at least one project listed accepts it.
# Or you can specify 'my $projectKey = "*";' to force using the global commit acceptance settings if you don't
# want to specify any exact project key.)
my $jiraBaseURL = "http://127.0.0.1:${http.port}/jira";
my $jiraLogin = "<JIRA user name>";
my $jiraPassword = "<JIRA password>";
my $projectKey = "<JIRA project key>";

# get committer passed as arg[0]
my $committer = $ARGV[0];

# slurp log message from log message file passed as arg[1]
my $holdTerminator = $/;
undef $/;

open IN, "< $ARGV[1]" or die "Unable to open $ARGV[1].\n";
my $buffer = <IN>;
close IN;

$/ = $holdTerminator;
my @lines = split /$holdTerminator/, $buffer;
$buffer = "init";
$buffer = join $holdTerminator, @lines;

my $commitMessage = $buffer;

# print arguments
print "Committer: " . $committer . "\n";
print "Commit message: \"" . $commitMessage . "\"\n";

# invoke JIRA web service
my $acceptance;
my $comment;

eval {
	$jiraBaseURL =~ s/\/+$//; # remove trailing '/' if exists
	my $s = XMLRPC::Lite->proxy($jiraBaseURL . "/rpc/xmlrpc");
	my $result = $s->call("commitacc.acceptCommit", $jiraLogin, $jiraPassword, $committer, $projectKey, $commitMessage)->result();
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