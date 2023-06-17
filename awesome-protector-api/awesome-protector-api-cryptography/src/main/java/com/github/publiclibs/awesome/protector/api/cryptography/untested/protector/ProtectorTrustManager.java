/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography.untested.protector;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jsse.BCX509ExtendedTrustManager;

import com.github.publiclibs.awesome.protector.api.cryptography.store.ProtectorStore;

/**
 * @author freedom1b2830
 * @date 2023-марта-12 00:50:39
 */
public class ProtectorTrustManager extends BCX509ExtendedTrustManager {
	private final ProtectorStore store;

	public ProtectorTrustManager(final ProtectorStore storeIn) {
		this.store = storeIn;
	}

	@Override
	public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
		// TODO Auto-generated method stub
		System.out.println(TrustManagerFactory.getDefaultAlgorithm());

		System.out.println("ProtectorTrustManager.checkServerTrusted(1)");
		System.err.println(authType);
		System.err.println(chain.length);

		for (final X509Certificate x509Certificate : chain) {
			System.err.println(x509Certificate);
		}
		System.out.println("ProtectorTrustManager.checkServerTrusted(2)");
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	public @Override void checkServerTrusted(final X509Certificate[] chain, final String authType,
			final SSLEngine engine) throws CertificateException {
		System.out.println(TrustManagerFactory.getDefaultAlgorithm());

		System.out.println("ProtectorTrustManager.checkServerTrusted(2.1)");
		System.err.println(authType);
		System.err.println(chain.length);

		for (final X509Certificate x509Certificate : chain) {
			System.err.println(x509Certificate);
		}
		System.err.println(engine);

		System.out.println("ProtectorTrustManager.checkServerTrusted(2.2)");

		// throw new CertificateException("None of the TrustManagers trust this
		// certificate chain");

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * public @Override void checkClientTrusted(final X509Certificate[] chain, final
	 * String authType) throws CertificateException {
	 * System.out.println("ProtectorTrustManager.checkClientTrusted(1)");
	 * System.err.println(authType); for (final X509Certificate x509Certificate :
	 * chain) { System.err.println(x509Certificate); }
	 * System.out.println("ProtectorTrustManager.checkClientTrusted(2)");
	 *
	 * }
	 */

}
