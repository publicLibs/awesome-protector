package com.github.publiclibs.awesome.protector.api.cryptography.untested.trustmanager;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Represents an ordered list of {@link X509TrustManager}s with additive trust.
 * If any one of the composed managers trusts a certificate chain, then it is
 * trusted by the composite manager.
 *
 * This is necessary because of the fine-print on {@link SSLContext#init}: Only
 * the first instance of a particular key and/or trust manager implementation
 * type in the array is used. (For example, only the first
 * javax.net.ssl.X509KeyManager in the array will be used.)
 *
 * @author codyaray
 * @since 4/22/2013
 * @see <a href=
 *      "http://stackoverflow.com/questions/1793979/registering-multiple-keystores-in-jvm">
 *      http://stackoverflow.com/questions/1793979/registering-multiple-keystores-in-jvm
 *      </a>
 */
@SuppressWarnings("unused")
public class CompositeX509TrustManager implements X509TrustManager {

	private final List<X509TrustManager> trustManagers;

	public CompositeX509TrustManager(final KeyStore keystore) {
		this.trustManagers = ImmutableList.of(CompositeX509TrustManagerUtils.getDefaultTrustManager(),
				CompositeX509TrustManagerUtils.getTrustManager(keystore));
	}

	public CompositeX509TrustManager(final List<X509TrustManager> trustManagersIn) {
		this.trustManagers = ImmutableList.copyOf(trustManagersIn);
	}

	@Override
	public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
		for (final X509TrustManager trustManager : trustManagers) {
			try {
				trustManager.checkClientTrusted(chain, authType);
				return; // someone trusts them. success!
			} catch (final CertificateException e) {
				// maybe someone else will trust them
			}
		}
		throw new CertificateException("None of the TrustManagers trust this certificate chain");
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
		for (final X509TrustManager trustManager : trustManagers) {
			try {
				trustManager.checkServerTrusted(chain, authType);
				return; // someone trusts them. success!
			} catch (final CertificateException e) {
				// maybe someone else will trust them
			}
		}
		throw new CertificateException("None of the TrustManagers trust this certificate chain");
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		final ImmutableList.Builder<X509Certificate> certificates = ImmutableList.builder();
		for (final X509TrustManager trustManager : trustManagers) {
			for (final X509Certificate cert : trustManager.getAcceptedIssuers()) {
				certificates.add(cert);
			}
		}
		return Iterables.toArray(certificates.build(), X509Certificate.class);
	}

}