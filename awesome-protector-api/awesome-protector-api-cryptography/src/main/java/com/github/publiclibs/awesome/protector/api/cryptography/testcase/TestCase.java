/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography.testcase;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.github.publiclibs.awesome.protector.api.cryptography.GeneratedCert;
import com.github.publiclibs.awesome.protector.api.cryptography.store.ProtectorStore;

/**
 * @author freedom1b2830
 * @date 2023-февраля-28 00:42:08
 */
public class TestCase {

	/**
	 * @param cnName The CN={name} of the certificate. When the certificate is for a
	 *               domain it should be the domain name
	 * @param domain Nullable. The DNS domain for the certificate.
	 * @param issuer Issuer who signs this certificate. Null for a self-signed
	 *               certificate
	 * @param isCA   Can this certificate be used to sign other certificates
	 * @return Newly created certificate with its private key
	 */
	private static GeneratedCert createCertificate(final String cnName, final String domain, final GeneratedCert issuer,
			final boolean isCA) throws Exception {
		// 1 создаем ключи
		final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		final KeyPair certKeyPair = keyGen.generateKeyPair();
		final X500Name name = new X500Name("CN=" + cnName);
		// If you issue more than just test certificates, you might want a decent serial
		// number schema ^.^
		// 2 серийный номер
		final BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

		// 3 время валидности
		final Instant validFrom = Instant.now();
		final Instant validUntil = validFrom.plus(10 * 360, ChronoUnit.DAYS);

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
				Date.from(validFrom), Date.from(validUntil), name, certKeyPair.getPublic());

		// Make the cert to a Cert Authority to sign more certs when needed
		if (isCA) {
			builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCA));
		}
		// Modern browsers demand the DNS name entry
		if (domain != null) {// домены
			builder.addExtension(Extension.subjectAlternativeName, false,
					new GeneralNames(new GeneralName(GeneralName.dNSName, domain + "2")));
			builder.addExtension(Extension.subjectAlternativeName, false,
					new GeneralNames(new GeneralName(GeneralName.dNSName, domain + "2")));
		}

		// Finally, sign the certificate:
		final ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(issuerKey);
		final X509CertificateHolder certHolder = builder.build(signer);
		final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

		return new GeneratedCert(certKeyPair.getPrivate(), cert);
	}

	public static void main(final String[] args) throws Exception {
		final GeneratedCert rootCA = createCertificate("cn", null, null, true);
		final GeneratedCert custCA = createCertificate("cn2", null, rootCA, true);
		final GeneratedCert hostcert = createCertificate("cn", "example.com", custCA, false);
		final String passwd = "passwd";
		final ProtectorStore keystore = new ProtectorStore(passwd);
		keystore.store.setKeyEntry("local.gamlor.info", hostcert.privateKey, null,
				new X509Certificate[] { hostcert.certificate, custCA.certificate, rootCA.certificate });
		keystore.save(passwd);

	}
}
