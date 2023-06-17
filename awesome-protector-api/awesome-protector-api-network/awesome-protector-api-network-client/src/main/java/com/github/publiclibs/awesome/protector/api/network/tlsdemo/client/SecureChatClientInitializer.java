/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.publiclibs.awesome.protector.api.network.tlsdemo.client;

import com.github.publiclibs.awesome.protector.api.network.client.handler.ClientHandler;
import com.github.publiclibs.awesome.protector.network.proto.ProtectorProtocol.UserInfo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class SecureChatClientInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslContext;

	public SecureChatClientInitializer(final SslContext sslContextInput) {
		this.sslContext = sslContextInput;
	}

	@Override
	public void initChannel(final SocketChannel ch) throws Exception {
		final ChannelPipeline pipeline = ch.pipeline();

		// ssl
		pipeline.addLast(sslContext.newHandler(ch.alloc(), SecureChatClient.HOST, SecureChatClient.PORT));

		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		pipeline.addLast(new ProtobufDecoder(UserInfo.getDefaultInstance()));

		pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
		pipeline.addLast(new ProtobufEncoder());

		// and then business logic.
		pipeline.addLast(new ClientHandler());
	}
}
