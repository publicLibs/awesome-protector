/**
 *
 */
package com.github.publiclibs.awesome.protector.api.network.server;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import com.github.publiclibs.awesome.protector.api.network.AbstractProtectorNetwork;
import com.github.publiclibs.awesome.protector.api.network.tlsdemo.server.SecureChatServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * @author freedom1b2830
 * @date 2023-марта-11 00:06:18
 */
public class DemoServer {
	public static class SRV extends AbstractProtectorNetwork<ServerBootstrap> {
		protected @Override void configureBootstrap(final ServerBootstrap bootstrapForCfg) {
			bootstrapForCfg.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new SecureChatServerInitializer(getSslContext()));
		}

		public @Override ServerBootstrap createBootstrap() {
			return new ServerBootstrap();
		}

		protected @Override SslContext createSSLContext() throws CertificateException, SSLException {
			final SelfSignedCertificate ssc = new SelfSignedCertificate();
			final SslContext sslCtx = SslContextBuilder

					.forServer(ssc.certificate(), ssc.privateKey())

					.protocols("TLSv1.3")
					// .ciphers(new
					// ArrayList<>(Arrays.asList("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384")))

					.build();
			for (final String string : sslCtx.cipherSuites()) {
				System.out.println(string);
			}
			return sslCtx;
		}

	}

	public static void main(final String[] args) throws CertificateException, SSLException, InterruptedException {
		final SRV server = new SRV();
		server.init();
		server.getBootstrap().bind(3333).sync().channel().closeFuture().sync();
	}

}
