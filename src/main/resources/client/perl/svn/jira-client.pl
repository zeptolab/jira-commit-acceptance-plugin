#!/usr/bin/perl

# JIRA commit acceptance perl client for Subversion
# Author: ferenc.kiss@midori.hu
# $Id$

use strict;
use warnings;

use XMLRPC::Lite;

# configure JIRA access
# ($projectKey can contain multiple comma-separated JIRA project keys like 'my $projectKey = "TST,ARP";'.
# If you specify multiple keys, the commit will be accepted if at least one project listed accepts it.
# Or you can specify 'my $projectKey = "*";' to force using the global commit acceptance settings if you don't
# want to specify any exact project key.)
my $jiraBaseURL = "http://127.0.0.1:8080";
my $jiraLogin = "root";
my $jiraPassword = "root";
my $projectKey = "TST";

# configure svnlook path
my $svnlookPath = "C:\\Progra~1\\svn-win32-1.4.0\\bin\\svnlook.exe";

# get committer
open IN, '-|', "$svnlookPath author $ARGV[0] --transaction $ARGV[1]" or die "Unable to get committer with svnlook.\n";
my $committer = <IN>;
close IN;
chomp($committer);

# get commit message
my $holdTerminator = $/;
undef $/;

open IN, '-|', "$svnlookPath log $ARGV[0] --transaction $ARGV[1]" or die "Unable to get commit message with svnlook.\n";
my $buffer = <IN>;
close IN;

$/ = $holdTerminator;
my @lines = split /$holdTerminator/, $buffer;
$buffer = "init";
$buffer = join $holdTerminator, @lines;
chomp($buffer);

my $commitMessage = $buffer;

# print arguments
select(STDERR);
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