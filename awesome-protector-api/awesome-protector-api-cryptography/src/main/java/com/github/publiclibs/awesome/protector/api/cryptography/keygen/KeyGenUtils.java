/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography.keygen;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import com.github.publiclibs.awesome.protector.api.cryptography.ProviderUtils;

/**
 * @author freedom1b2830
 * @date 2023-февраля-22 21:18:13
 */
public class KeyGenUtils {
	private static BouncyCastleProvider provider = ProviderUtils.initBouncyCastleProvider();

	private static final String ALGO_EC = "EC";
	private static final String CURVE25519 = "Curve25519";

	/**
	 * для шифрования
	 *
	 * @return KeyPair
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static KeyPair generateCURVE25519Keys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		final X9ECParameters curveParams = CustomNamedCurves.getByName(CURVE25519);
		final ECParameterSpec ecSpec = new ECParameterSpec(curveParams.getCurve(), curveParams.getG(),
				curveParams.getN(), curveParams.getH(), curveParams.getSeed());
		final KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGO_EC, provider);
		kpg.initialize(ecSpec);
		return kpg.generateKeyPair();
	}

	public static AsymmetricCipherKeyPair generateED25519Keys() {
		final Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
		keyPairGenerator.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
		return keyPairGenerator.generateKeyPair();
	}

	public static void main(final String[] args) {
		final AsymmetricCipherKeyPair pair = generateED25519Keys();
		System.err.println(pair);
	}
}
