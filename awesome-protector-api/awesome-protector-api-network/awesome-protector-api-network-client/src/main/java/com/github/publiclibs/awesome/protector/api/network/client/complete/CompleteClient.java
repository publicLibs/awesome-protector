/**
 *
 */
package com.github.publiclibs.awesome.protector.api.network.client.complete;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;

import com.github.publiclibs.awesome.protector.api.cryptography.ProviderUtils;
import com.github.publiclibs.awesome.protector.api.cryptography.store.ProtectorStore;
import com.github.publiclibs.awesome.protector.api.network.AbstractProtectorNetwork;
import com.github.publiclibs.awesome.protector.api.network.tlsdemo.client.SecureChatClientInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * @author freedom1b2830
 * @date 2023-марта-11 17:34:25
 */
public class CompleteClient extends AbstractProtectorNetwork<Bootstrap> {
	private static BouncyCastleProvider provider = ProviderUtils.initBouncyCastleProvider();

	public static void main(final String[] args) throws CertificateException, InterruptedException, KeyStoreException,
			NoSuchAlgorithmException, OperatorCreationException, IOException, UnrecoverableKeyException {
		final ProtectorStore store = new ProtectorStore("1233");
		final CompleteClient client = new CompleteClient(store);
		client.init();
		client.getBootstrap().connect("127.0.0.1", 3333).sync().channel().closeFuture().sync();
	}

	/**
	 * @param protectorStoreIn
	 */
	public CompleteClient(final ProtectorStore protectorStoreIn) {
		super(protectorStoreIn);
	}

	protected @Override void configureBootstrap(final Bootstrap bootstrapForCfg) {
		bootstrapForCfg.group(workerGroup).channel(NioSocketChannel.class)
				.handler(new SecureChatClientInitializer(getSslContext()));
	}

	public @Override Bootstrap createBootstrap() {
		return new Bootstrap();
	}

	protected @Override SslContext createSSLContext() throws CertificateException, SSLException {
		final SslContext sslCtx = SslContextBuilder.forClient()
				// .keyManager(getKeyManager())

				.trustManager(getTrustManager()).protocols("TLSv1.3")

				// .ciphers(new
				// ArrayList<>(Arrays.asList("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384")))

				.build();
		for (final String string : sslCtx.cipherSuites()) {
			System.out.println(string);
		}

		return sslCtx;
	}

}
