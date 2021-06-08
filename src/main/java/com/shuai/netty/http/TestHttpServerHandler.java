package com.shuai.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        //判断msg 是否喂http请求
        if (msg instanceof HttpRequest) {
            System.out.println("msg 类型 " + msg.getClass());
            System.out.println("客户端地址 " + ctx.channel().remoteAddress());
            System.out.println("pipline hashCode " + ctx.channel().pipeline().hashCode() + ", TestHttpServerHandler " +
                    "hashCode " + this.hashCode());
            HttpRequest httpRequest = (HttpRequest) msg;
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println("请求了 favicon.ico ，不做响应");
                return;
            }

            String str = "hello server, this is server ";
            ByteBuf byteBuf = Unpooled.copiedBuffer(str, CharsetUtil.UTF_8);
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    byteBuf);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(httpResponse);
        }
    }
}
