/**
 *
 */
package com.github.publiclibs.awesome.protector.api.cryptography;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.github.publiclibs.awesome.protector.api.cryptography.keygen.KeyGenUtils;

/**
 * @author freedom1b2830
 * @date 2023-марта-01 14:33:22
 */
public class Certs {
	private static BouncyCastleProvider provider = ProviderUtils.initBouncyCastleProvider();

	public static GeneratedCert createCert(final String commonDnsNameIn, final String[] alternativeDnsNames,
			final GeneratedCert issuer, final boolean isCA, KeyPair keypair) throws Exception {
		// ключ
		if (keypair == null) {
			keypair = KeyGenUtils.generateCURVE25519Keys();
		}

		final X500Name commonDnsName = new X500Name("CN=" + commonDnsNameIn);

		// 2 серийный номер
		final BigInteger serialNumber = genSerial();
		// даты
		final Instant validFrom = Instant.now();
		final Instant validUntil = validFrom.plus(10 * 360, ChronoUnit.DAYS);

		X500Name issuerName;
		PrivateKey issuerKey;
		if (issuer == null) {
			issuerName = commonDnsName;
			issuerKey = keypair.getPrivate();
		} else {
			issuerName = new X500Name(issuer.certificate.getSubjectDN().getName());
			issuerKey = issuer.privateKey;
		}

		final JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuerName, serialNumber,
				Date.from(validFrom), Date.from(validUntil), commonDnsName, keypair.getPublic());

		if (isCA) {
			builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCA));
		}

		if (alternativeDnsNames != null && alternativeDnsNames.length > 0) {
			for (final String alternativeDnsName : alternativeDnsNames) {
				builder.addExtension(Extension.subjectAlternativeName, false,
						new GeneralNames(new GeneralName(GeneralName.dNSName, alternativeDnsName)));
			}
		}

		// подписываем
		final ContentSigner signer = new JcaContentSignerBuilder("SHA256withECDSA").setProvider(provider)
				.build(issuerKey);

		final X509CertificateHolder certHolder = builder.build(signer);
		final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

		return new GeneratedCert(keypair.getPrivate(), cert);

	}

	public static BigInteger genSerial() {
		return BigInteger.valueOf(System.currentTimeMillis());
	}

	public static void main(final String[] args) throws Exception {
		final GeneratedCert rootCa = createCert("cn1", new String[] { "dns1", "dns2" }, null, true, null);
		final GeneratedCert ca = createCert("cn1", new String[] { "dns3" }, rootCa, true, null);
		final GeneratedCert domainCert = createCert("cn1", new String[] { "dns3" }, ca, false, null);
		System.out.println(ToStringBuilder.reflectionToString(domainCert, ToStringStyle.MULTI_LINE_STYLE));
	}
}
