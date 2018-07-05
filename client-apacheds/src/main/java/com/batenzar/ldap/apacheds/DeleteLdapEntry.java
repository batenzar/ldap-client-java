package com.batenzar.ldap.apacheds;

import java.io.IOException;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class DeleteLdapEntry {

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

			// 3. perform delete
			conn.delete("cn=testadd,dc=example,dc=com");

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
