package com.shuai;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to.txt").getChannel();
            ) {
            final long size = from.size();
            for (long left = size; left > 0;) {

                left -= from.transferTo((size - left), size, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
