package com.shuai.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class GroupChatClient {
    private final String HOST = "127.0.0.1";
    private final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    public GroupChatClient() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        userName = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(userName + " is ok ...");
    }

    public void sendInfo(String info) {
        info = userName + " 说：" + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInfo() {
        try {
            int readChannels = selector.select();
            if (readChannels > 0) {
                //有可用通道
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        sc.read(buffer);
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }
                }
            } else {
                System.out.println("没有可用通道");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        final GroupChatClient chatClient = new GroupChatClient();

        new Thread() {
          public void run() {
              while (true) {
                  chatClient.readInfo();
                  try {
                      Thread.currentThread().sleep(3000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
          }
        }.start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }

//        SocketChannel socketChannel = SocketChannel.open();
//        socketChannel.configureBlocking(false);
//        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
//
//        if (!socketChannel.connect(address)) {
//            while (!socketChannel.finishConnect()) {
////                System.out.println("to do other");
//            }
//        }
//        while (true) {
//            Scanner scanner = new Scanner(System.in);
//            String s = scanner.nextLine();
//            socketChannel.write(StandardCharsets.UTF_8.encode(s));
//        }
    }
}
