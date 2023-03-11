/**
 *
 */
package com.github.publiclibs.awesome.protector.api.network;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContext;
import lombok.Getter;

/**
 * @author freedom1b2830
 * @date 2023-марта-10 11:13:19
 */
public abstract class AbstractProtectorNetwork<B> {

	private @Getter B bootstrap;

	protected final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	protected final EventLoopGroup workerGroup = new NioEventLoopGroup();

	private @Getter SslContext sslContext;

	protected abstract void configureBootstrap(B bootstrapForCfg);

	public abstract B createBootstrap();

	protected abstract SslContext createSSLContext() throws CertificateException, SSLException;

	public final void init() throws CertificateException, SSLException {
		bootstrap = createBootstrap();
		sslContext = createSSLContext();
		configureBootstrap(bootstrap);
	}

}
