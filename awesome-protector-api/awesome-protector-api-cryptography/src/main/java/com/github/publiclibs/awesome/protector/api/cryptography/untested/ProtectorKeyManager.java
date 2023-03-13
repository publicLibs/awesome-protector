/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography.untested;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;

import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import com.github.publiclibs.awesome.protector.api.cryptography.ProviderUtils;
import com.github.publiclibs.awesome.protector.api.cryptography.store.ProtectorStore;

/**
 * @author freedom1b2830
 * @date 2023-марта-12 01:54:46
 */
public class ProtectorKeyManager extends KeyManagerFactory {

	static BouncyCastleJsseProvider provider = ProviderUtils.initBouncyCastleBCJSSEProvider();
	private final ProtectorStore protectorStore;

	protected ProtectorKeyManager(final KeyManagerFactorySpi factorySpi, final String algorithm,
			final ProtectorStore protectorStoreIn) {
		super(factorySpi, provider, algorithm);
		this.protectorStore = protectorStoreIn;
	}

	public ProtectorKeyManager(final ProtectorStore protectorStoreIn)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		this(new SPI(protectorStoreIn), "PKIX", protectorStoreIn);
		init(protectorStoreIn.store, protectorStoreIn.getStoredPasswd().toCharArray());
	}

}
