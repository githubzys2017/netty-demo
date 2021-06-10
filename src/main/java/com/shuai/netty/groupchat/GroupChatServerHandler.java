package com.shuai.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

//    public static Map<String, Channel> channels = new HashMap<>();

    //定义channel组
    //GlobalEventExecutor.INSTANCE 全局事件执行器
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 连接建立后第一个被执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        //将该客户加入的消息推送给其他的客户端
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天\n");

        channelGroup.add(channel);
//        channels.put("user_id", channel);
    }

    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 离开\n");
    }

    /**
     * 表示channel处于活动状态，提示 xx上线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了~");
    }

    /**
     * channel处于非活动状态触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "离线了~");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        final Channel channel = ctx.channel();

        channelGroup.forEach(ch -> {
            if (channel != ch) {
                //不是当前客户端的channel，直接转发
                ch.writeAndFlush("[客户] " + channel.remoteAddress() + " 发送了消息 " + msg + "\n");
            } else {
                ch.writeAndFlush("[自己] 发送了消息 " + msg + "\n");
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭通道
        ctx.close();
    }
}
