package com.github.publiclibs.awesome.protector.api.cryptography.untested;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Objects;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;

import com.github.publiclibs.awesome.protector.api.cryptography.store.ProtectorStore;

public class SPI extends KeyManagerFactorySpi {

	private final ProtectorStore protectorStore;

	private KeyStore keyStore;

	// password
	private char[] pwd;

	/**
	 * @param protectorStoreIn
	 */
	public SPI(final ProtectorStore protectorStoreIn) {
		this.protectorStore = protectorStoreIn;
	}

	protected @Override KeyManager[] engineGetKeyManagers() {
		if (protectorStore.store == null) {
			throw new IllegalStateException("KeyManagerFactory is not initialized");
		}
		return new KeyManager[] {
				new KeyManagerImpl(protectorStore.store, protectorStore.getStoredPasswd().toCharArray()) };
	}

	protected @Override void engineInit(final KeyStore ks, final char[] password)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
		Objects.requireNonNull(ks);
		Objects.requireNonNull(password);
		keyStore = ks;
		pwd = password.clone();
	}

	protected @Override void engineInit(final ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
		throw new InvalidAlgorithmParameterException("ManagerFactoryParameters not supported");
	}

}