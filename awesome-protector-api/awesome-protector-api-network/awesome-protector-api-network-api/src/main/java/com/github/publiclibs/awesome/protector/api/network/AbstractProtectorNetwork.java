/**
 *
 */
package com.github.publiclibs.awesome.protector.api.network;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.net.ssl.SSLException;

import com.github.publiclibs.awesome.protector.api.cryptography.store.ProtectorStore;
import com.github.publiclibs.awesome.protector.api.cryptography.untested.protector.ProtectorKeyManager;
import com.github.publiclibs.awesome.protector.api.cryptography.untested.protector.ProtectorTrustManager;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContext;
import lombok.Getter;

/**
 * @author freedom1b2830
 * @date 2023-марта-10 11:13:19
 */
public abstract class AbstractProtectorNetwork<B> {

	private final ProtectorStore protectorStore;

	private @Getter B bootstrap;

	protected final EventLoopGroup bossGroup = new NioEventLoopGroup(1);

	protected final EventLoopGroup workerGroup = new NioEventLoopGroup();
	private @Getter SslContext sslContext;

	private ProtectorTrustManager trustManager;

	private ProtectorKeyManager keyManager;

	public AbstractProtectorNetwork(final ProtectorStore protectorStoreIn) {
		this.protectorStore = protectorStoreIn;

	}

	protected abstract void configureBootstrap(B bootstrapForCfg) throws CertificateException, SSLException;

	public abstract B createBootstrap();

	protected abstract SslContext createSSLContext() throws CertificateException, SSLException;

	/**
	 * @return the keyManager
	 */
	public ProtectorKeyManager getKeyManager() {
		Objects.requireNonNull(keyManager, "invoke init() before");

		return keyManager;
	}

	/**
	 * @return the trustManager
	 */
	public ProtectorTrustManager getTrustManager() {
		Objects.requireNonNull(trustManager, "invoke init() before");
		return trustManager;
	}

	public final void init() throws CertificateException, SSLException, UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException {
		trustManager = new ProtectorTrustManager(protectorStore);
		keyManager = new ProtectorKeyManager(protectorStore);
		bootstrap = createBootstrap();
		sslContext = createSSLContext();
		configureBootstrap(bootstrap);
	}

}
