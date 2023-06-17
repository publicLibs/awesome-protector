package com.github.publiclibs.awesome.protector.api.cryptography;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public final class GeneratedCert {
	public final PrivateKey privateKey;
	public final X509Certificate certificate;

	public GeneratedCert(final PrivateKey privateKeyIn, final X509Certificate certificateIn) {
		this.privateKey = privateKeyIn;
		this.certificate = certificateIn;
	}
}