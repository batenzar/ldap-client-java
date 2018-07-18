package com.batenzar.ldap.apacheds;

import java.io.IOException;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class ModifyLdapEntry {

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

			// 3. prepare entry
			if (conn.exists("cn=testadd,dc=demo1,dc=com")) {
				conn.delete("cn=testadd,dc=demo1,dc=com");
			}

			conn.add( //
					new DefaultEntry(//
							"cn=testadd,dc=demo1,dc=com", // The Dn
							"ObjectClass: top", // attr 1
							"ObjectClass: person", // attr 2
							"cn: testadd_cn", // attr 3
							"sn: testadd_sn" // attr 4
					) //
			);

			Entry entry = conn.lookup("cn=testadd,dc=demo1,dc=com");
			System.out.println(String.valueOf(entry.toString()));

			// 4 modify
			conn.modify("cn=testadd,dc=demo1,dc=com",
					new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, "description", "this is description"));

			Entry modifiedEntry = conn.lookup("cn=testadd,dc=demo1,dc=com");
			System.out.println(String.valueOf(modifiedEntry.toString()));

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
