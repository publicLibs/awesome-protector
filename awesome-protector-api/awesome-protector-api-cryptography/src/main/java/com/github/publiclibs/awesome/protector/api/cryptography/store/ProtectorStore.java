/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.github.publiclibs.awesome.protector.api.cryptography.GeneratedCert;
import com.github.publiclibs.awesome.protector.api.cryptography.ProviderUtils;

/**
 * @author freedom1b2830
 * @date 2023-марта-08 12:28:56
 */
public class ProtectorStore {

	public static final String PRIMARY_KEY = "primary-key-priv";

	private static final String BKS = "BKS";
	private static BouncyCastleProvider provider = ProviderUtils.initBouncyCastleProvider();
	private static final String SHA256WITH_ECDSA = "SHA256withECDSA";

	public static GeneratedCert createCert(final String cnName, final String[] domains, final GeneratedCert issuer,
			final boolean isCA, final boolean longTime)
			throws NoSuchAlgorithmException, CertIOException, OperatorCreationException, CertificateException {
		// 1 создаем ключи
		final KeyPairGenerator kpg = createP521KeyPairGenerator();
		final KeyPair certKeyPair = kpg.generateKeyPair();
		final X500Name name = new X500Name("CN=" + cnName);
		// If you issue more than just test certificates, you might want a decent serial
		// number schema ^.^
		// 2 серийный номер
		final BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

		// 3 время валидности
		final Instant validFrom = Instant.now();
		final Date validFromDate = Date.from(validFrom);
		Instant validUntil;
		if (longTime) {
			validUntil = validFrom.plus(30, ChronoUnit.YEARS);
		} else {
			validUntil = validFrom.plus(1, ChronoUnit.MONTHS);
		}
		final Date validUntilDate = Date.from(validUntil);

		// If there is no issuer, we self-sign our certificate.
		X500Name issuerName;
		PrivateKey issuerKey;
		if (issuer == null) {
			issuerName = name;
			issuerKey = certKeyPair.getPrivate();
		} else {
			issuerName = new X500Name(issuer.certificate.getSubjectDN().getName());
			issuerKey = issuer.privateKey;
		}

		// The cert builder to build up our certificate information
		final JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuerName, serialNumber,
				validFromDate, validUntilDate, name, certKeyPair.getPublic());

		// Make the cert to a Cert Authority to sign more certs when needed
		if (isCA) {
			builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCA));
		}
		// Modern browsers demand the DNS name entry
		if (domains != null) {// домены
			for (final String domain : domains) {
				builder.addExtension(Extension.subjectAlternativeName, false,
						new GeneralNames(new GeneralName(GeneralName.dNSName, domain)));
			}
		}

		// Finally, sign the certificate:
		// final ContentSigner signer = new
		// JcaContentSignerBuilder("SHA256WithRSA").build(issuerKey);
		final ContentSigner signer = genContentSigner(issuerKey);
		final X509CertificateHolder certHolder = builder.build(signer);
		final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

		return new GeneratedCert(certKeyPair.getPrivate(), cert);
	}

	public static KeyPairGenerator createP521KeyPairGenerator() throws NoSuchAlgorithmException {
		final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", provider);// ECDSA key
		kpg.initialize(521);
		return kpg;
	}

	private static ContentSigner genContentSigner(final PrivateKey signerPrivateKey) throws OperatorCreationException {
		return new JcaContentSignerBuilder(SHA256WITH_ECDSA).setProvider(provider).build(signerPrivateKey);
	}

	private final Path trustStorePath;

	public final KeyStore store;

	public ProtectorStore(final Path trustStorePathIn, final String password) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException, OperatorCreationException {
		this.trustStorePath = trustStorePathIn;
		store = KeyStore.getInstance(BKS, provider);
		if (Files.exists(trustStorePath)) {
			load(password.toCharArray());
		} else {
			createNew(password);
		}
	}

	public ProtectorStore(final String password) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, OperatorCreationException {
		this(Paths.get("STORE.BKS"), password);
	}

	private void createNew(final String password) throws NoSuchAlgorithmException, CertificateException, IOException,
			KeyStoreException, OperatorCreationException {
		initEmpty();
		createPrimaryKey(password);
		save(password);
	}

	private void createPrimaryKey(final String password) throws CertIOException, NoSuchAlgorithmException,
			OperatorCreationException, CertificateException, KeyStoreException {
		final KeyPairGenerator kpg = createP521KeyPairGenerator();

		final KeyPair caKeyPair = kpg.generateKeyPair();
		final PrivateKey caPrivKey = caKeyPair.getPrivate();
		final PublicKey caPubKey = caKeyPair.getPublic();

		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 20);

		final Date caBefor = new Date(System.currentTimeMillis() - 10000);
		final Date caAfter = calendar.getTime();

		final BigInteger caSerial = BigInteger.valueOf(new SecureRandom().nextLong());
		final X500Name caIName = new X500Name("CN=RegKassa ZDA");// кто издал
		final X500Name caSName = new X500Name("CN=RegKassa CA");// что издал
		final SubjectPublicKeyInfo caPublicKeyInfo = SubjectPublicKeyInfo.getInstance(caPubKey.getEncoded());

		final X509v3CertificateBuilder caBuilder = new X509v3CertificateBuilder(caIName, caSerial, caBefor, caAfter,
				caSName, caPublicKeyInfo);

		caBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
		caBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));

		final X509CertificateHolder caHolder = caBuilder.build(genContentSigner(caPrivKey));
		final X509Certificate caCertificate = new JcaX509CertificateConverter().setProvider(provider)
				.getCertificate(caHolder);

		store.setKeyEntry(PRIMARY_KEY, caPrivKey, password.toCharArray(), new X509Certificate[] { caCertificate });

		System.err.println(caKeyPair.getClass());
		System.err.println(caPrivKey.getClass());
		System.err.println(caPubKey.getClass());
		System.err.println(caCertificate.getClass());

	}

	public Certificate getCertificate(final String alias) throws KeyStoreException {
		return store.getCertificate(alias);
	}

	public Certificate[] getCertificates(final String alias) throws KeyStoreException {
		return store.getCertificateChain(alias);
	}
	/*
	 * X509CertificateObject getCert() throws CertificateException { final
	 * CertificateFactory factory = CertificateFactory.getInstance("X.509",
	 * provider); }
	 */

	Key getKey(final String keyAlias, final char[] password)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return store.getKey(keyAlias, password);
	}

	Key getKey(final String keyAlias, final String keyPassword)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return getKey(keyAlias, keyPassword.toCharArray());
	}

	private PrivateKey getPrivateKey(final String password)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		final PrivateKey key = getPrivateKey(PRIMARY_KEY, password);
		if (key == null) {
			throw new NoSuchElementException("cant get key with alias " + PRIMARY_KEY);
		}
		return key;
	}

	public PrivateKey getPrivateKey(final String keyAlias, final char[] password)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return (PrivateKey) getKey(keyAlias, password);
	}

	public PrivateKey getPrivateKey(final String keyAlias, final String password)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return getPrivateKey(keyAlias, password.toCharArray());
	}

	private void initEmpty() throws NoSuchAlgorithmException, CertificateException, IOException {
		store.load(null, null);
	}

	public void load(final char[] password) throws NoSuchAlgorithmException, CertificateException, IOException {
		try (InputStream fis = Files.newInputStream(trustStorePath)) {
			store.load(fis, password);
		}
	}

	public void save(final String password)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try (OutputStream fos = Files.newOutputStream(trustStorePath)) {
			store.store(fos, password.toCharArray());
		}
	}

}
