package com.batenzar.ldap.apacheds;

import java.io.IOException;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class SimpleLdapSearch {

	public static void main(String[] args) {
		// test server
		String host = "localhost";
		int port = 10389;
		String user = "uid=admin,ou=system";
		String pass = "secret";

		SimpleLdapSearch search = new SimpleLdapSearch();
		try (LdapConnection conn = search.createLdapConnection(host, port, user, pass); // default apache ds password
		) {
			Dn base = new Dn("dc=demo1,dc=com");
			search.searchLdap(conn, base, "(objectClass=*)");	// simple searching
//			search.searchLdap2(conn, base, "(objectClass=*)");	// manual searching
		} catch (IOException | LdapException e) {
			e.printStackTrace();
		}
	}

	private LdapConnection createLdapConnection(String host, int port, String user, String pass) {
		LdapConnectionConfig config = new LdapConnectionConfig();
		config.setLdapHost(host);
		config.setLdapPort(port);
		config.setName(user);
		config.setCredentials(pass);

		LdapNetworkConnection conn = new LdapNetworkConnection(config);
		return conn;
	}

	private void searchLdap(LdapConnection conn, Dn baseDn, String filter) throws LdapException {
		conn.bind();
		EntryCursor search = conn.search(baseDn, filter, SearchScope.SUBTREE);
		for (Entry e : search) {
			System.out.println(e.getDn().getNormName());
		}

		conn.unBind();
	}

	private void searchLdap2(LdapConnection conn, Dn baseDn, String filter) throws LdapException {
		SearchRequest req = new SearchRequestImpl();
		req.setBase(baseDn);
		req.setFilter(filter);
		req.setScope(SearchScope.SUBTREE);
		// other search option
		// req.ignoreReferrals();
		// ..

		conn.bind();
		SearchCursor search = conn.search(req);
		search.forEach( e -> { 
			if (e instanceof SearchResultEntry
					) {
				Entry entry = ((SearchResultEntry) e).getEntry();
				System.out.println(entry.getDn().getNormName());
			}
		});

		conn.unBind();
	}
}
