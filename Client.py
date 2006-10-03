#!/usr/bin/python

import xmlrpclib

s = xmlrpclib.ServerProxy('http://127.0.0.1:8080/rpc/xmlrpc')
print s.commitacc.acceptCommit('root', 'root', 'testUser', 'Test commit message')
