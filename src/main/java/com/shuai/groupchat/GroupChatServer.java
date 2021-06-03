package com.shuai.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    private ServerSocketChannel listenchannel;
    private Selector selector;
    private static final int PORT = 6667;

    public GroupChatServer() {
        try {
            selector = Selector.open();
            listenchannel = ServerSocketChannel.open();
            listenchannel.socket().bind(new InetSocketAddress(PORT));
            listenchannel.configureBlocking(false);
            listenchannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            while (true) {
                int count = selector.select();
                if (count > 0) {
                    //有事件
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()) {
                            SocketChannel sc = listenchannel.accept();
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                            System.out.println(sc.getRemoteAddress() + " 上线 ");
                        }

                        if (key.isReadable()) {
                            readData(key);
                        }
                    }
                } else {
                    System.out.println("wait ... ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    //读取客户端消息
    private void readData(SelectionKey key) {
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            if (count > 0) {
                String msg = new String(buffer.array());
                System.out.println("from client : " + msg);

                //向其他客户端转发消息
                sendInfoToOtherClient(msg, channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + " 离线 。。。");
                key.cancel();
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 给其他客户端发送消息
     * @param msg
     */
    private void sendInfoToOtherClient(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中 。。。。");
        for (SelectionKey key : selector.keys()) {
            Channel targetChannel = key.channel();
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                SocketChannel dest = (SocketChannel) targetChannel;
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();



//        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//        serverSocketChannel.configureBlocking(false);
//        serverSocketChannel.bind(new InetSocketAddress(6666));
//        Selector selector = Selector.open();
//        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
//
//        while (true) {
//
//            if (selector.select(2000) == 0) {
////                System.out.println("无连接");
//            }
//
//            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//            while (iterator.hasNext()) {
//                SelectionKey key = iterator.next();
//                iterator.remove();
//                if (key.isAcceptable()) {
//                    SocketChannel socketChannel = serverSocketChannel.accept();
//                    socketChannel.configureBlocking(false);
//                    System.out.println(((InetSocketAddress)socketChannel.getRemoteAddress()).getHostString() + " 上线 ");
//                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
//                }
//
//                if (key.isReadable()) {
//                    SocketChannel socketChannel = (SocketChannel) key.channel();
//                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
//                    socketChannel.read(byteBuffer);
//                    byteBuffer.flip();
//                    System.out.println(Charset.defaultCharset().decode(byteBuffer));
//                    byteBuffer.clear();
//                }
//
//            }
//        }
    }
}
