/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author freedom1b2830
 * @date 2023-февраля-21 20:18:06
 */
public class Cryptography {

	public static class ImageAndKey {
		public byte[] imageBytes;
		public byte[] aesKey;

		public ImageAndKey(final byte[] imageBytesIn, final byte[] aesKeyIn) {
			this.imageBytes = imageBytesIn;
			this.aesKey = aesKeyIn;
		}
	}

	/**
	 *
	 */
	private static final String PBKDF2_WITH_HMAC_SHA1 = "PBKDF2WithHmacSHA1";// need for create key

	/**
	 *
	 */
	private static final String AES_ECB_PKCS5_PADDING = "AES/ECB/PKCS5Padding";

	private static final String RSA = "RSA";

	private static final String AES = "AES";

	private static final byte[] salt = { 1, 2, 3, 4, 5, 6, 7, 8 };
	private static PublicKey publicKey;// RSA
	private static byte[] privateKeyArray;// AES

	final static String password = "PASSWORD";

	public static byte[] aesDecrypt(final byte[] cipherText, final byte[] secretKeyBytes) throws Exception {
		final Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
		cipher.init(Cipher.DECRYPT_MODE, getSecretKey(secretKeyBytes));
		return cipher.doFinal(cipherText);// plainText
	}

	private static byte[] aesEncrypt(final byte[] plainText, final byte[] secretKeyBytes) throws Exception {
		final Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
		cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(secretKeyBytes));
		return cipher.doFinal(plainText);// cipherText
	}

	public static byte[] decryptImage(final ImageAndKey imageAndKey, final String passwordIn) throws Exception {
		byte[] secretKeyBytes = generateAESKey(passwordIn);
		final byte[] decryptedPrivateKey = aesDecrypt(privateKeyArray, secretKeyBytes);
		final byte[] decryptedKey = rsaDecrypt(imageAndKey.aesKey, decryptedPrivateKey);
		final SecretKey secretKey = new SecretKeySpec(decryptedKey, AES);
		secretKeyBytes = secretKey.getEncoded();
		return aesDecrypt(imageAndKey.imageBytes, secretKeyBytes);// decryptedBytes from aes
	}

	public static ImageAndKey encryptImage(final byte[] imageBytes) throws Exception {
		final byte[] secretKeyBytes = generateAESKey();
		final byte[] encryptedFile = aesEncrypt(imageBytes, secretKeyBytes);
		final byte[] encryptedKey = rsaEncrypt(secretKeyBytes);
		return new ImageAndKey(encryptedFile, encryptedKey);
	}

	private static byte[] generateAESKey() throws Exception {
		final KeyGenerator keyGen = KeyGenerator.getInstance(AES);
		keyGen.init(256);
		final SecretKey secretKey = keyGen.generateKey();
		return secretKey.getEncoded();
	}

	private static byte[] generateAESKey(final String passwordIn) throws Exception {
		final SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_WITH_HMAC_SHA1);
		final KeySpec spec = new PBEKeySpec(passwordIn.toCharArray(), salt, 65536, 256);
		final SecretKey tmp = factory.generateSecret(spec);
		final SecretKey secret = new SecretKeySpec(tmp.getEncoded(), AES);
		return secret.getEncoded();
	}

	private static void generateRSAKeys(final String passwordIn) throws Exception {
		final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA);
		keyGen.initialize(512); // TODO: make this 2048 at least
		final KeyPair keyPair = keyGen.generateKeyPair();
		publicKey = keyPair.getPublic();
		final PrivateKey privateKey = keyPair.getPrivate();

		final byte[] secretKeyBytes = generateAESKey(passwordIn);
		final byte[] privateKeyBytes = privateKey.getEncoded();
		privateKeyArray = aesEncrypt(privateKeyBytes, secretKeyBytes);
	}

	private static SecretKey getSecretKey(final byte[] secretKeyBytes) throws Exception {
		return new SecretKeySpec(secretKeyBytes, AES);
	}

	public static void main(final String[] args) throws Exception {
		generateRSAKeys(password);
		test();
	}

	public static byte[] rsaDecrypt(final byte[] cipherText, final byte[] decryptedPrivateKeyArray) throws Exception {

		final PrivateKey privateKey = KeyFactory.getInstance(RSA)
				.generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKeyArray));

		final Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		return cipher.doFinal(cipherText);// plainText from rsa
	}

	public static byte[] rsaEncrypt(final byte[] plainText) throws Exception {
		final Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(plainText);// raw -> rsa->cipherText
	}

	public static void test() throws Exception {
		final String imageFile = "348756348975634897562398479623896";

		final ImageAndKey imageAndKey = encryptImage(imageFile.getBytes());
		final byte[] decryptedImage = decryptImage(imageAndKey, password);

		System.out.println(new String(imageFile));
		System.out.println(new String(imageAndKey.aesKey));
		System.out.println(new String(decryptedImage));
	}
}
