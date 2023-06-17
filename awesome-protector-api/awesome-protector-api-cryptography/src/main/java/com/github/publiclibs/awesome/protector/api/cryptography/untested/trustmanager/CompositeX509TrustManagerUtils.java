package com.github.publiclibs.awesome.protector.api.cryptography.untested.trustmanager;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.google.common.collect.Iterables;

public class CompositeX509TrustManagerUtils {
	public static X509TrustManager getDefaultTrustManager() {

		return getTrustManager(null);

	}

	public static X509TrustManager getTrustManager(final KeyStore keystore) {

		return getTrustManager(TrustManagerFactory.getDefaultAlgorithm(), keystore);

	}

	public static X509TrustManager getTrustManager(final String algorithm, final KeyStore keystore) {
		try {
			final TrustManagerFactory factory = TrustManagerFactory.getInstance(algorithm);
			factory.init(keystore);
			return Iterables.getFirst(
					Iterables.filter(Arrays.asList(factory.getTrustManagers()), X509TrustManager.class), null);
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static TrustManager[] getTrustManagers(final KeyStore keyStore) {

		return new TrustManager[] { new CompositeX509TrustManager(keyStore) };

	}
}