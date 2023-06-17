/**
 *
 */
package com.github.publiclibs.awesome.protector.api.network.client.handler;

import com.github.publiclibs.awesome.protector.network.proto.ProtectorProtocol.UserInfo;
import com.google.protobuf.GeneratedMessageV3;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author freedom1b2830
 * @date 2023-февраля-21 01:14:21
 */
public class ClientHandler extends ChannelDuplexHandler {
	private Channel channel;

	public @Override void channelActive(final ChannelHandlerContext ctx) throws Exception {
		final UserInfo textMessage = UserInfo.newBuilder().setAaa("eeee").build();
		send(channel, textMessage);
	}

	public @Override void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		System.out.println("ClientHandler.channelInactive()");
	}

	public @Override void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		System.out.println(msg.getClass());

	}

	public @Override void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
		super.channelReadComplete(ctx);
	}

	public @Override void channelRegistered(final ChannelHandlerContext ctx) {
		channel = ctx.channel();
	}

	public @Override void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		cause.printStackTrace();

		super.exceptionCaught(ctx, cause);
	}

	/**
	 * @param channel2
	 * @param textMessage
	 */
	private void send(final Channel channelForSend, final GeneratedMessageV3 message) {
		channelForSend.writeAndFlush(message).addListener(f -> {
			if (!f.isSuccess()) {
				f.cause().printStackTrace();
			}
		});
	}
}
