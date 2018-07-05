package com.batenzar.ldap.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class SimpleLdapSearch {

	public static void main(String[] args) {
		// test server
		String url = "ldap://localhost:10389";

		// querying user
		String user = "uid=admin,ou=system";
		String pass = "secret";

		// search attribute
		String searchBase = "dc=demo1,dc=com";
		String filter = "(objectClass=*)";
		int scope = SearchControls.SUBTREE_SCOPE;
		
		
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); // constants
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");	// auth type (simple/sasl)
		env.put(Context.SECURITY_PRINCIPAL, user); // username
		env.put(Context.SECURITY_CREDENTIALS, pass); // password

		try {
			DirContext ctx = new InitialDirContext(env);

			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope(scope);

			NamingEnumeration<?> answer = ctx.search(searchBase, filter, ctrl);
			while (answer.hasMore()) {
				Object next = answer.next();

				if (next instanceof SearchResult) {
					System.out.println(((SearchResult) next).getNameInNamespace());
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
}
