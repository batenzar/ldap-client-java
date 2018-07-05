package com.batenzar.ldap.apacheds;

import java.io.IOException;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class AddLdapEntry {

	public static void main(String[] args) {
		// test server
		String host = "localhost";
		int port = 10389;

		// querying user
		String user = "uid=admin,ou=system";
		String pass = "secret";

		LdapConnection conn = null;
		try {
			// 1. create connection
			conn = new LdapNetworkConnection(host, port);

			// 2. bind with authorized user (simple authentication)
			conn.bind(user, pass);

			// 3. perform add
			conn.add( //
					new DefaultEntry(//
							"cn=testadd,dc=example,dc=com", // The Dn
							"ObjectClass: top", // attr 1
							"ObjectClass: person", // attr 2
							"cn: testadd_cn", // attr 3
							"sn: testadd_sn" // attr 4
					) //
			);

			// 4. unbind (log-out from LDAP)
			conn.unBind();
		} catch (LdapException e) {
			e.printStackTrace();
		} finally {
			// 5. close connection
			if (conn != null) {
				try {
					conn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
