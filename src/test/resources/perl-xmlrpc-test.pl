#!/usr/bin/perl

# toy jira perl client using XMLRPC
# logs in, creates an issue
# handles failure or prints issue fields
# logs out.

use strict;
use warnings;

use XMLRPC::Lite;
use Data::Dumper;

my $jira = XMLRPC::Lite->proxy('http://localhost:${jira.http.port}/jira/rpc/xmlrpc');

print "Testing XMLRPC against: http://localhost:${jira.http.port}/jira/rpc/xmlrpc";

my $call = $jira->call("jira1.login", "admin", "admin");
my $result = $call->result();
my $fault = $call->fault();
if (defined $fault) {
    print "Perl XMLRPC test failed.\n";
    die $call->faultstring();
} else {
    print "Perl XMLRPC test successful.\n";
    print Dumper($call->result());
}
$jira->call("jira1.logout", $result);
