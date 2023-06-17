package com.github.publiclibs.awesome.protector.api.cryptography.untested.protector;

import static java.util.Objects.requireNonNull;

import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.security.auth.x500.X500Principal;

public class KeyManagerImpl extends X509ExtendedKeyManager {

	// hashed key store information
	private final HashMap<String, PrivateKeyEntry> hash;

	/**
	 * Creates Key manager
	 */
	@SuppressWarnings("JdkObsolete") // KeyStore#aliases is the only way of enumerating all entries
	public KeyManagerImpl(final KeyStore keyStore, final char[] pwd) {
		this.hash = new HashMap<>();
		final Enumeration<String> aliases;
		try {
			aliases = keyStore.aliases();
		} catch (final KeyStoreException e) {
			return;
		}
		while (aliases.hasMoreElements()) {
			final String alias = aliases.nextElement();
			try {
				if (keyStore.entryInstanceOf(alias, PrivateKeyEntry.class)) {
					PrivateKeyEntry entry;
					try {
						entry = (PrivateKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(pwd));
					} catch (final UnsupportedOperationException e) {
						// If the KeyStore doesn't support getEntry(), as Android Keystore
						// doesn't, fall back to reading the two values separately.
						final PrivateKey key = (PrivateKey) keyStore.getKey(alias, pwd);
						final Certificate[] certs = keyStore.getCertificateChain(alias);
						entry = new PrivateKeyEntry(key, certs);
					}
					hash.put(alias, entry);
				}
			} catch (KeyStoreException | UnrecoverableEntryException | NoSuchAlgorithmException ignored) {
				// Ignored.
			}
		}
	}

	private String[] chooseAlias(final String[] keyTypes, final Principal[] issuers) {
		if (keyTypes == null || keyTypes.length == 0) {
			return null;
		}
		final List<Principal> issuersList = (issuers == null) ? null : Arrays.asList(issuers);
		final ArrayList<String> found = new ArrayList<>();
		for (final Map.Entry<String, PrivateKeyEntry> entry : hash.entrySet()) {
			final String alias = entry.getKey();
			final Certificate[] chain = entry.getValue().getCertificateChain();
			final Certificate cert = chain[0];
			final String certKeyAlg = cert.getPublicKey().getAlgorithm();
			final String certSigAlg = (cert instanceof X509Certificate
					? ((X509Certificate) cert).getSigAlgName().toUpperCase(Locale.US)
					: null);
			for (String keyAlgorithm : keyTypes) {
				if (keyAlgorithm == null) {
					continue;
				}
				final String sigAlgorithm;
				// handle cases like EC_EC and EC_RSA
				final int index = keyAlgorithm.indexOf('_');
				if (index == -1) {
					sigAlgorithm = null;
				} else {
					sigAlgorithm = keyAlgorithm.substring(index + 1);
					keyAlgorithm = keyAlgorithm.substring(0, index);
				}
				// key algorithm does not match
				if (!certKeyAlg.equals(keyAlgorithm)) {
					continue;
				}
				/*
				 * TODO find a more reliable test for signature algorithm. Unfortunately value
				 * varies with provider. For example for "EC" it could be "SHA1WithECDSA" or
				 * simply "ECDSA".
				 */
				// sig algorithm does not match
				if (sigAlgorithm != null && certSigAlg != null && !certSigAlg.contains(sigAlgorithm)) {
					continue;
				}
				// no issuers to match, just add to return list and continue
				if (issuers == null || issuers.length == 0) {
					found.add(alias);
					continue;
				}
				// check that a certificate in the chain was issued by one of the specified
				// issuers
				for (final Certificate certFromChain : chain) {
					if (!(certFromChain instanceof X509Certificate)) {
						// skip non-X509Certificates
						continue;
					}
					final X509Certificate xcertFromChain = (X509Certificate) certFromChain;
					/*
					 * Note use of X500Principal from getIssuerX500Principal as opposed to Principal
					 * from getIssuerDN. Principal.equals test does not work in the case where
					 * xcertFromChain.getIssuerDN is a bouncycastle
					 * org.bouncycastle.jce.X509Principal.
					 */
					final X500Principal issuerFromChain = xcertFromChain.getIssuerX500Principal();

					requireNonNull(issuersList);
					if (issuersList.contains(issuerFromChain)) {
						found.add(alias);
					}
				}
			}
		}
		if (!found.isEmpty()) {
			return found.toArray(new String[0]);
		}
		return null;
	}

	@Override
	public String chooseClientAlias(final String[] keyTypes, final Principal[] issuers, final Socket socket) {
		final String[] al = chooseAlias(keyTypes, issuers);
		return (al == null ? null : al[0]);
	}

	@Override
	public String chooseEngineClientAlias(final String[] keyTypes, final Principal[] issuers, final SSLEngine engine) {
		final String[] al = chooseAlias(keyTypes, issuers);
		return (al == null ? null : al[0]);
	}

	@Override
	public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
		final String[] al = chooseAlias(new String[] { keyType }, issuers);
		return (al == null ? null : al[0]);
	}

	@Override
	public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
		final String[] al = chooseAlias(new String[] { keyType }, issuers);
		return (al == null ? null : al[0]);
	}

	@Override
	public X509Certificate[] getCertificateChain(final String alias) {
		if (alias == null) {
			return null;
		}
		if (hash.containsKey(alias)) {
			final Certificate[] certs = hash.get(alias).getCertificateChain();
			if (certs[0] instanceof X509Certificate) {
				final X509Certificate[] xcerts = new X509Certificate[certs.length];
				for (int i = 0; i < certs.length; i++) {
					xcerts[i] = (X509Certificate) certs[i];
				}
				return xcerts;
			}
		}
		return null;

	}

	@Override
	public String[] getClientAliases(final String keyType, final Principal[] issuers) {
		return chooseAlias(new String[] { keyType }, issuers);
	}

	@Override
	public PrivateKey getPrivateKey(final String alias) {
		if (alias == null) {
			return null;
		}
		if (hash.containsKey(alias)) {
			return hash.get(alias).getPrivateKey();
		}
		return null;
	}

	@Override
	public String[] getServerAliases(final String keyType, final Principal[] issuers) {
		return chooseAlias(new String[] { keyType }, issuers);
	}
}