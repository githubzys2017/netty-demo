package com.shuai;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFileWalkFileTree {
    public static void main(String[] args) throws IOException {
        final AtomicInteger dirCount = new AtomicInteger();
        final AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\work\\tools\\Java\\jdk1.6.0_45"),new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>" + dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("+++++++" + file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("dirs count " + dirCount);
        System.out.println("files count " + fileCount);
    }
}
