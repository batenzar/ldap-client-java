package com.batenzar.ldap.apacheds;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class SimpleLdapsSearch {

	public static void main(String[] args) {
		// test server
		String host = "localhost";
		int port = 10636; // default ldaps port for apacheds

		// querying user
		String user = "uid=admin,ou=system";
		String pass = "secret";

		// search attribute
		String searchBase = "dc=demo1,dc=com";
		String filter = "(objectClass=*)";
		SearchScope scope = SearchScope.SUBTREE;

		LdapConnection conn = null;
		try {
			// 1. config and create connection
			LdapConnectionConfig config = new LdapConnectionConfig();
			config.setLdapHost(host);
			config.setLdapPort(port);
			config.setUseSsl(true);
			config.setName(user);
			config.setCredentials(pass);

			// 1.1 add TrustManager for SSL.
			TrustManagerFactory instance = getTrustManagerFactory("/path/to/localapache.der");
			TrustManager[] trustManagers = instance.getTrustManagers();
			config.setTrustManagers(trustManagers);

			// 1.2 create connection
			conn = new LdapNetworkConnection(config);

			// 2. bind with authorized user (simple authentication)
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

	private static TrustManagerFactory getTrustManagerFactory(String filePath) {
		Path path = Paths.get(filePath);

		TrustManagerFactory result = null;
		try (InputStream fis = Files.newInputStream(path)) {

			// load server cert into keystore
			result = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, null);

			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Collection<? extends Certificate> certCollection = cf.generateCertificates(fis);

			int i = 0;
			for (Certificate cert : certCollection) {
				ks.setCertificateEntry(String.valueOf(i++), cert);
			}

			result.init(ks);
		} catch (NoSuchAlgorithmException | KeyStoreException | CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return result;
	}
}
