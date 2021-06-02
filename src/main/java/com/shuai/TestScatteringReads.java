package com.shuai;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class TestScatteringReads {
    public static void main(String[] args) {
        try(FileChannel channel = new RandomAccessFile("src/words.txt", "r").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);

            channel.read(new ByteBuffer[] {b1, b2, b3});

            b1.flip();
            b2.flip();
            b3.flip();

            System.out.println(StandardCharsets.UTF_8.decode(b1).toString());;


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
