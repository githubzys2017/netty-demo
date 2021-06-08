package com.shuai.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, client 1", CharsetUtil.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, client 3", CharsetUtil.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, client 4", CharsetUtil.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 5, TimeUnit.SECONDS);
//        System.out.println("server ctx = " + ctx);
//
//        ByteBuf buf = (ByteBuf) msg;
//
//        System.out.println("client is : " + buf.toString(CharsetUtil.UTF_8));
//        System.out.println("client ip is : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello client 2", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
