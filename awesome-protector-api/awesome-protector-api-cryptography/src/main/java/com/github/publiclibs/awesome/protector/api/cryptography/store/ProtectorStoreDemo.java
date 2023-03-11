/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography.store;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.bouncycastle.operator.OperatorCreationException;

/**
 * @author freedom1b2830
 * @date 2023-марта-08 12:41:48
 */
public class ProtectorStoreDemo {
	/**
	 *
	 */
	private static final String PASSWORD = "1234";

	public static void main(final String[] args) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, OperatorCreationException, IOException {
		final ProtectorStore protectorStore = new ProtectorStore(PASSWORD);
		protectorStore.save(PASSWORD);
	}
}
