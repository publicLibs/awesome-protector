/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

/**
 * @author freedom1b2830
 * @date 2023-февраля-22 21:15:44
 */
public class ProviderUtils {
	public static final BouncyCastleJsseProvider initBouncyCastleBCJSSEProvider() {
		Provider providerTmp = Security.getProvider(BouncyCastleJsseProvider.PROVIDER_NAME);
		if (providerTmp == null) {
			providerTmp = new BouncyCastleJsseProvider();
			Security.addProvider(providerTmp);
			return (BouncyCastleJsseProvider) providerTmp;
		}
		return (BouncyCastleJsseProvider) providerTmp;
	}

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
