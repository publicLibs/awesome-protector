/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author freedom1b2830
 * @date 2023-февраля-22 21:15:44
 */
public class ProviderUtils {
	public static final BouncyCastleProvider initBouncyCastleProvider() {
		Provider providerTmp = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
		if (providerTmp == null) {
			providerTmp = new BouncyCastleProvider();
			Security.addProvider(providerTmp);
			return (BouncyCastleProvider) providerTmp;
		}
		return (BouncyCastleProvider) providerTmp;
	}

}
