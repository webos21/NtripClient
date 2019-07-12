package com.gmail.webos21.crypto;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class SelfSignedSSL {

	public static SSLContext createSSLContext(InputStream is) {
		try {
			// Load CAs from an InputStream
			// (could be from a resource or ByteArrayInputStream or ...)
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream caInput = new BufferedInputStream(is);
			Certificate ca = null;
			try {
				ca = cf.generateCertificate(caInput);
				System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
			} finally {
				caInput.close();
			}

			// Create a KeyStore containing our trusted CAs
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			// Create an SSLContext that uses our TrustManager
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);

			return context;
		} catch (CertificateException ce) {
			ce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (KeyStoreException ke) {
			ke.printStackTrace();
		} catch (NoSuchAlgorithmException nae) {
			nae.printStackTrace();
		} catch (KeyManagementException kme) {
			kme.printStackTrace();
		}

		return null;
	}

	public static HostnameVerifier createHostnameVerifier(final String[] allowedHosts) {
		return new CustomHostnameVerifier(allowedHosts);
	}

	private static class CustomHostnameVerifier implements HostnameVerifier {

		private List<String> allowedHosts;

		public CustomHostnameVerifier(String[] allowedHosts) {
			this.allowedHosts = Arrays.asList(allowedHosts);
		}

		public boolean verify(String hostname, SSLSession session) {
			if (allowedHosts.contains(hostname)) {
				return true;
			}
			HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
			return hv.verify("example.com", session);
		}
	}

}
