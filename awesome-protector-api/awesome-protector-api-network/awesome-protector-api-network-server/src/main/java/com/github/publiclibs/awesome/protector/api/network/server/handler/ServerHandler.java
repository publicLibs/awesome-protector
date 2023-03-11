/**
 *
 */
package com.github.publiclibs.awesome.protector.api.network.server.handler;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author freedom1b2830
 * @date 2023-февраля-20 19:06:20
 */
public class ServerHandler extends ChannelDuplexHandler {
	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private Channel channel;

	public @Override void channelActive(final ChannelHandlerContext handlerContext) throws Exception {
		// final UserInfo textMessage = UserInfo.newBuilder().setAaa("eeee").build();
		// ctx.writeAndFlush(textMessage);

		final SslHandler sslHandler = handlerContext.pipeline().get(SslHandler.class);

		sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>() {
			public @Override void operationComplete(final Future<Channel> future) throws Exception {
				/*
				 * final String localHostName = InetAddress.getLocalHost().getHostName();
				 * handlerContext.writeAndFlush("Welcome to " + localHostName +
				 * " secure chat service!\n");
				 */
				final SSLEngine sslHandlerEngine = sslHandler.engine();
				final SSLSession sslSession = sslHandlerEngine.getSession();
				final String cipherSuite = sslSession.getCipherSuite();
				/*
				 * handlerContext.writeAndFlush("Your session is protected by " + cipherSuite +
				 * " cipher suite.\n");
				 */
				System.err.println(cipherSuite);
				channels.add(handlerContext.channel());
			}
		});
	}

	public @Override void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		System.out.println("ServerHandler.channelInactive()");
	}

	/*
	 * final DemoResponse.Builder builder = DemoResponse.newBuilder();
	 * builder.setResponseMsg( "Accepted from Server, returning response for: " +
	 * msg.getRequestMsg()) .setRet(0); ctx.write(builder.build());
	 */
	public @Override void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		System.out.println("ServerHandler.channelRead():" + msg.getClass());
		/*
		 * channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
		 * public @Override void operationComplete(final ChannelFuture f) { if
		 * (!f.isSuccess()) { f.cause().printStackTrace(); } } });
		 */
	}

	public @Override void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
		super.channelReadComplete(ctx);
	}

	public @Override void channelRegistered(final ChannelHandlerContext ctx) {
		channel = ctx.channel();
	}

	public @Override void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		// super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
	}
}
