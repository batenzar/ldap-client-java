package com.batenzar.ldap.apacheds;

import java.io.IOException;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class RenameLdapEntry {

	public static void main(String[] args) {
		// test server
		String host = "localhost";
		int port = 10389;

		// querying user
		String user = "uid=admin,ou=system";
		String pass = "secret";

		String sourceDn = "cn=testadd,dc=demo1,dc=com";
		String targetRdn = "cn=testadd1";
		String targetDn = "cn=testadd1,dc=demo1,dc=com";

		LdapConnection conn = null;
		try {
			// 1. create connection
			conn = new LdapNetworkConnection(host, port);

			// 2. bind with authorized user (simple authentication)
			conn.bind(user, pass);

			// 3. prepare entry
			if (conn.exists(sourceDn)) {
				conn.delete(sourceDn);
			}

			if (conn.exists(targetDn)) {
				conn.delete(targetDn);
			}

			conn.add( //
					new DefaultEntry(//
							sourceDn, // The Dn
							"ObjectClass: top", // attr 1
							"ObjectClass: person", // attr 2
							"cn: testadd_cn", // attr 3
							"sn: testadd_sn" // attr 4
					) //
			);

			// 4 modify
			conn.rename(sourceDn, targetRdn);

			boolean sourceExist = conn.exists(sourceDn);
			boolean targetExist = conn.exists(targetDn);

			System.out.println("Is \"" + sourceDn + "\" entry existed: " + sourceExist);
			System.out.println("Is \"" + targetDn +"\" entry existed: " + targetExist);

			// 5. unbind (log-out from LDAP)
			conn.unBind();
		} catch (LdapException e) {
			e.printStackTrace();
		} finally {
			// 6. close connection
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
