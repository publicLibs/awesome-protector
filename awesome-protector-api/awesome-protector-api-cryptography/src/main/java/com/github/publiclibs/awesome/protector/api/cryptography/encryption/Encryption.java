/**
 *
 * Для связи со мной используйте почту freedom1b2830@gmail.com
 * Ключ pgp 4388DF6D2D19DA0BD7BB0FBBDBA96F466835877C
 * был отправлен на hkps://keyserver.ubuntu.com
 * также в https://raw.githubusercontent.com/freedom1b2830/freedom1b2830/main/data/pgp.4388DF6D2D19DA0BD7BB0FBBDBA96F466835877C.pub
 */
package com.github.publiclibs.awesome.protector.api.cryptography.encryption;

/**
 * @author freedom1b2830
 * @date 2023-февраля-24 08:36:18
 */
public class Encryption {

	/*
	 * private static BouncyCastleProvider provider; static { provider =
	 * KeyGenUtils.provider; }
	 *
	 * public static byte[] name(final byte[] data, final Key fromKey) throws
	 * NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
	 * IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
	 * InvalidAlgorithmParameterException, IOException { final Cipher cipher =
	 * Cipher.getInstance("ECIESwithAES-CBC", provider); final byte[] params = new
	 * byte[16]; new Random().nextBytes(params);
	 * cipher.getParameters().init(params); cipher.init(Cipher.DECRYPT_MODE,
	 * fromKey, cipher.getParameters());// BUG ?
	 *
	 * return cipher.doFinal(data); }
	 */
}
