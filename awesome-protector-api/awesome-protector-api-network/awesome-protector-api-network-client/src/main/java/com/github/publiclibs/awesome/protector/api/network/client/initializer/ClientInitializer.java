/**
 *
 */
package com.github.publiclibs.awesome.protector.api.network.client.initializer;

import com.github.publiclibs.awesome.protector.api.network.client.handler.ClientHandler;
import com.github.publiclibs.awesome.protector.network.proto.ProtectorProtocol.UserInfo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @author freedom1b2830
 * @date 2023-февраля-21 01:12:08
 */
public @Deprecated class ClientInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(final SocketChannel ch) throws Exception {
		final ChannelPipeline p = ch.pipeline();

		// p.addLast(new ProtobufDecoder(UserInfo.getDefaultInstance()));
		p.addLast(new ProtobufVarint32FrameDecoder());
		p.addLast(new ProtobufDecoder(UserInfo.getDefaultInstance()));

		p.addLast(new ProtobufVarint32LengthFieldPrepender());
		p.addLast(new ProtobufEncoder());

		// handler
		p.addLast(new ClientHandler());
	}

}
