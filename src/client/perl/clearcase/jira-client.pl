#!/usr/bin/perl

# $Id: jira-client.pl $

=head1 NAME

jira-client.pl - JIRA commit acceptance Perl client for ClearCase

=head1 DESCRIPTION

This script is an example of a client for the JIRA "Commit Acceptance Plugin".

In ClearCase the client is implemented as a pre checkin trigger that is executed
on the ClearCase client machine.

=head1 USAGE

This script should be edited to have correct values set for $jiraBaseURL, 
$jiraLogin and $jiraPassword.

The script should be copied so that it is accessible from all ClearCase clients.
A central location is preferred but it is quite acceptable to copy this script 
onto each machine that is setup as a ClearCase client. (This does however make 
maintenance harder)

Next the ClearCase administrator needs to create a ClearCase trigger for the
pre checkin operation. The following command is an example of how this can be 
done with the file stored in a common location accessible from both Windows and
UNIX clients and is in the path.

C<cleartool mktrtype -c 'JIRA checkin acceptance trigger' -element -all -preop checkin -exec 'perl -S jira-client.pl' trtype:jira-client.pl@vob:/vobs/source>

The trigger can be removed using the command

C<cleartool rmtype -rmall trtype:jira-client.pl@vob:/vobs/source>

The ClearCase administrator will probably want to ensure that the script is not
writable by general users so that they don't edit the script and bypass it.

=head2 Errors in configuration

If the JIRA access is not correctly configured the following error can be reported.

=over 4

=item C<Commit rejected: Unable to connect to the JIRA server at "http://...">

This error means that the server entered as the value of $jiraBaseURL is incorrect.

=item C<Commit rejected: Invalid user namd or password.>

This error means that one or both of the user name ($jiraLogin) or password 
($jiraPassword) are entered incorrect.

=back

=cut

use strict;
use warnings;
use Config;
use XMLRPC::Lite;

# configure JIRA access
my $jiraBaseURL = "http://localhost:8080";
my $jiraLogin = "jirauser";
my $jiraPassword = "password";
my $projectKey = "TST"

# If the script is not being called from within a ClearCase trigger environment
# Then the setup / usage is not correct so print the documentation.
# The environment variable CLEARCASE_OP_KIND is set for all trigger calls so 
# checking if it exists should be a suitable test.
unless (exists $ENV{'CLEARCASE_OP_KIND'})
{
	warn "Error: Incorrect usage. This script must be called as a ClearCase trigger.\n\n";
	warn `perldoc $0`;
	exit 1; # make sure we don't continue successfully.
}

# Get the users login name
my $committer;
if ($Config{'osname'} eq 'MSWin32')
{
	require Win32;
	$committer = lc Win32::LoginName();
	$committer =~ s#.*\\##;
}
else
{
	$committer = getpwuid($>);
}

# get commit message
my $commitMessage = "";
$commitMessage = $ENV{'CLEARCASE_COMMENT'} if scalar $ENV{'CLEARCASE_COMMENT'};

# print arguments
#print "Committer: " . $committer . "\n";
#print "Commit message: \"" . $commitMessage . "\"\n";

# invoke JIRA web service
# TODO move this common part to a separate.pl and use it also from the SVN client,
my $acceptance;
my $comment;

eval
{
	$jiraBaseURL =~ s/\/+$//; # remove trailing '/' if exists
	my $s = XMLRPC::Lite->proxy($jiraBaseURL . "/rpc/xmlrpc");
	my $result = $s->call("commitacc.acceptCommit", $jiraLogin, $jiraPassword, $committer, $projectKey, $commitMessage)->result();
	($acceptance, $comment) = split('\|', $result);
};

if ($@)
{
	($acceptance, $comment) = ("false", "Unable to connect to the JIRA server at \"$jiraBaseURL\".");
};

if($acceptance eq 'true')
{
	# Commented out as there is no need to display anything on success
	#print "Commit accepted.\n";
	exit 0;
}
else
{
	warn "Commit rejected: $comment\n";
	exit 1;
}

__END__

=head1 SEE ALSO

=over 4

=item JIRA

L<http://www.atlassian.com/software/jira>

=item JIRA Commit Acceptance Plugin

L<http://confluence.atlassian.com/display/JIRAEXT/Commit+Acceptance+Plugin>

=item ClearCase mktrtype man page

C<cleartool man mktrtype>

=back

=head1 AUTHOR

R Bernard Davison E<lt>rbdavison@cpan.orgE<gt>

=head1 COPYRIGHT

Copyright (c) 2007 R Bernard Davison. All rights reserved.

This program is free software; you can redistribute it and/or modify it under
the same terms as Perl itself.

=cut
