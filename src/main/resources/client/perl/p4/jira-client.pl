#!/usr/bin/perl

# JIRA commit acceptance perl client for P4
# Author: ferenc.kiss@midori.hu
# $Id: jira-client.pl 10144 2007-09-12 10:13:37Z ferenc.kiss $

use strict;
use warnings;

use XMLRPC::Lite;
use HTML::Entities ();

# configure JIRA access
# ($projectKey can contain multiple comma-separated JIRA project keys like 'my $projectKey = "TST,ARP";'.
# If you specify multiple keys, the commit will be accepted if at least one project listed accepts it.
# Or you can specify 'my $projectKey = "*";' to force using the global commit acceptance settings if you don't
# want to specify any exact project key.)
my $jiraBaseURL = "http://127.0.0.1:8080";
my $jiraLogin = "admin";
my $jiraPassword = "admin";
my $projectKey = "TST";

# configure p4 path
my $p4Path = "p4";

# get committer passed as arg[0]
my $committer = $ARGV[0];

# get change description with change passed as arg[1]
my $holdTerminator = $/;
undef $/;

open IN, '-|', "$p4Path -s describe $ARGV[1]" or die "Unable to get change $ARGV[1] description with p4.\n";
my $p4DescribeOutput = <IN>;
close IN;

#  trim leading whitespace
$/ = $holdTerminator;
my @lines = split /$holdTerminator/, $p4DescribeOutput;
@lines = map(substr($_, 1), grep(/^\t/, map(substr($_, 6) , grep(/^text:/, @lines))));
$p4DescribeOutput = "init";
$p4DescribeOutput = join $holdTerminator, @lines;
chomp($p4DescribeOutput);

my $commitMessage = $p4DescribeOutput;

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
        $comment = HTML::Entities::decode($comment);
	print "Commit rejected: " . $comment . "\n";
	exit 1;
}
