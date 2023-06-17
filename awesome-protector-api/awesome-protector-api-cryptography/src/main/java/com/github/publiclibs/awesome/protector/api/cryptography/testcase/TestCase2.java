/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography.testcase;

import static com.github.publiclibs.awesome.protector.api.cryptography.store.ProtectorStore.PRIMARY_KEY;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.github.publiclibs.awesome.protector.api.cryptography.ProviderUtils;
import com.github.publiclibs.awesome.protector.api.cryptography.store.ProtectorStore;

/**
 * @author freedom1b2830
 * @date 2023-марта-08 00:04:23
 */
public class TestCase2 {
	/**
	 *
	 */
	private static final String SHA256WITH_ECDSA = "SHA256withECDSA";
	private static BouncyCastleProvider provider;
	private static PrivateKey signingKeyPriv;

	static {
		provider = ProviderUtils.initBouncyCastleProvider();
	}

	/**
	 * @param caPrivKey
	 * @return
	 * @throws OperatorCreationException
	 */
	private static ContentSigner genContentSigner(final PrivateKey signerPrivateKey) throws OperatorCreationException {
		return new JcaContentSignerBuilder(SHA256WITH_ECDSA).setProvider(provider).build(signerPrivateKey);
	}

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException
	 * @throws CertIOException
	 * @throws OperatorCreationException
	 */
	public static void main(final String[] args) throws Exception {
		final String password = "12344444";

		final ProtectorStore protectorStore = new ProtectorStore(password) {
		};

		final ArrayList<Certificate> certificateChain = new ArrayList<>();

		final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
		kpg.initialize(256); // 256 bit ECDSA key

		// create a key pair for the signature certificate, which is going to be used to
		// sign the receipts
		final KeyPair signingKeyPair = kpg.generateKeyPair();
		signingKeyPriv = signingKeyPair.getPrivate();

		// CA

		// get references to private keys for the CA and the signing key
		final KeyPair caKeyPair = kpg.generateKeyPair();
		final PrivateKey caPrivKey = caKeyPair.getPrivate();
		final PublicKey caPubKey = caKeyPair.getPublic();
		final Date caBefor = new Date(System.currentTimeMillis() - 10000);
		final Date caAfter = new Date(System.currentTimeMillis() + 24L * 3600 * 1000);
		final BigInteger caSerial = BigInteger.valueOf(new SecureRandom().nextLong());
		final X500Name caSName = new X500Name("CN=RegKassa CA");
		final X500Name caIName = new X500Name("CN=RegKassa ZDA");
		final SubjectPublicKeyInfo caPublicKeyInfo = SubjectPublicKeyInfo.getInstance(caPubKey.getEncoded());

		final X509v3CertificateBuilder caBuilder = new X509v3CertificateBuilder(caIName, caSerial, caBefor, caAfter,
				caSName, caPublicKeyInfo);
		caBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
		caBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
		final X509CertificateHolder caHolder = caBuilder.build(genContentSigner(caPrivKey));
		final X509Certificate caCertificate = new JcaX509CertificateConverter().setProvider(provider)
				.getCertificate(caHolder);

		// add ca to chain
		certificateChain.add(caCertificate);

		// create signing cert
		final long serialNumberCertificate = new SecureRandom().nextLong();
		// final String serialNumberOrKeyId = Long.toHexString(serialNumberCertificate);

		final X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(new X500Name("CN=RegKassa CA"),
				BigInteger.valueOf(Math.abs(serialNumberCertificate)), new Date(System.currentTimeMillis() - 10000),
				new Date(System.currentTimeMillis() + 24L * 3600 * 1000), new X500Name("CN=Signing certificate"),
				SubjectPublicKeyInfo.getInstance(signingKeyPair.getPublic().getEncoded()));
		certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
		certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
		final X509CertificateHolder certHolder = certBuilder.build(genContentSigner(caPrivKey));
		final X509Certificate signingCertificate = new JcaX509CertificateConverter().setProvider(provider)
				.getCertificate(certHolder);

		/*
		 * protectorTrustStore.store.setCertificateEntry("aaaa", signingCertificate);
		 * protectorTrustStore.store.setCertificateEntry(password, signingCertificate);
		 */
		/*
		 * protectorTrustStore.store.setEntry(password, new
		 * KeyStore.TrustedCertificateEntry(signingCertificate), null);
		 */
		protectorStore.store.setKeyEntry(PRIMARY_KEY, caPrivKey, password.toCharArray(),
				new X509Certificate[] { caCertificate });

		protectorStore.store.setKeyEntry(PRIMARY_KEY, caPrivKey, password.toCharArray(),
				new X509Certificate[] { caCertificate });

		protectorStore.store.setCertificateEntry(password, signingCertificate);
		/*
		 * protectorTrustStore.store.setKeyEntry(PRIMARY_KEY, null,
		 * password.toCharArray(), new X509Certificate[] { caCertificate });
		 */
		protectorStore.save(password);

	}

	public static byte[] signData(final byte[] dataToBeSigned)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

		final Signature signature = Signature.getInstance(SHA256WITH_ECDSA);
		signature.initSign(signingKeyPriv);
		signature.update(dataToBeSigned);
		return signature.sign();

	}
}
