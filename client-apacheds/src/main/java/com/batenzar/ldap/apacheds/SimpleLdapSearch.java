package com.batenzar.ldap.apacheds;

import java.io.IOException;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class SimpleLdapSearch {

	public static void main(String[] args) {
		// test server
		String host = "localhost";
		int port = 10389;

		// querying user
		String user = "uid=admin,ou=system";
		String pass = "secret";

		// search attribute
		String searchBase = "dc=demo1,dc=com";
		String filter = "(objectClass=*)";
		SearchScope scope = SearchScope.SUBTREE;

		LdapConnection conn = null;
		try {
			// 1. create connection
			conn = new LdapNetworkConnection(host, port);

			// 2. bind with authorized user (log-in to LDAP)
			conn.bind(user, pass);

			// 3. perform search
			EntryCursor cursor = conn.search(searchBase, filter, scope);
			for (Entry e : cursor) {
				System.out.println(e.getDn().getNormName());
			}

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
