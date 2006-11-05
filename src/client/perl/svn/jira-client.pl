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

# configure svnlook path
my $svnlookPath = "C:\\Progra~1\\svn-win32-1.4.0\\bin\\svnlook.exe";

# get committer
open IN, '-|', "$svnlookPath author $ARGV[0] --transaction $ARGV[1]" or die "Unable to get committer with svnlook.\n";
my $buffer1 = <IN>;
close IN;
chomp($buffer1);
my $committer = $buffer1;

# get commit message
open IN, '-|', "$svnlookPath log $ARGV[0] --transaction $ARGV[1]" or die "Unable to get commit message with svnlook.\n";
my $buffer2 = <IN>;
close IN;
chomp($buffer2);
my $commitMessage = $buffer2;

# print arguments
select(STDERR);
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