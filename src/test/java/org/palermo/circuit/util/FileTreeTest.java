package org.palermo.circuit.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.palermo.circuit.converter.CharConverter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FileTreeTest {

    @Test
    void testTree() throws IOException {

        Comparator<Long> comparator = new Comparator<Long>() {

            @Override
            public int compare(Long o1, Long o2) {
                return Long.compare(o1, o2);
            }
        };

        Converter<Long> converter = new Converter<Long>() {

            @Override
            public byte[] serialize(Long input) {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(input);
                return buffer.array();
            }

            @Override
            public Long deserialize(byte[] input) {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.put(input);
                buffer.flip();//need flip
                return buffer.getLong();
            }

            @Override
            public int getSize() {
                return Long.BYTES;
            }
        };

        FileTree<Long> fileTree = new FileTree<Long>(File.createTempFile("___", ".tmp"), comparator, converter);
        assertThat(fileTree.getSize()).isEqualTo(0);

        System.out.println(fileTree.getHeight());

        fileTree.add(0L);
        assertThat(fileTree.getSize()).isEqualTo(1);
        assertThat(fileTree.contains(0L)).isEqualTo(true);
        assertThat(fileTree.contains(1L)).isEqualTo(false);

        System.out.println(fileTree.getHeight());

        fileTree.add(1L);
        assertThat(fileTree.getSize()).isEqualTo(2);
        assertThat(fileTree.contains(0L)).isEqualTo(true);
        assertThat(fileTree.contains(1L)).isEqualTo(true);
        assertThat(fileTree.contains(2L)).isEqualTo(false);

        System.out.println(fileTree.getHeight());

        fileTree.add(2L);
        assertThat(fileTree.getSize()).isEqualTo(3);
        assertThat(fileTree.contains(0L)).isEqualTo(true);
        assertThat(fileTree.contains(1L)).isEqualTo(true);
        assertThat(fileTree.contains(2L)).isEqualTo(true);
        assertThat(fileTree.contains(3L)).isEqualTo(false);

        for (int i = 1; i < 100; i++) {
            fileTree.add((long) 100 + i);
            System.out.println("Adding: " + i);
        }

        System.out.println(fileTree.getHeight());
        fileTree.reBalance();
        System.out.println("Size: " + fileTree.getSize() + " Height: " + fileTree.getHeight());
    }

    private static Stream<Arguments> scenarios() throws IOException {
        return Stream.of(
                Arguments.of(createTree(0), 0),
                Arguments.of(createTree(1), 1),
                Arguments.of(createTree(2), 2),
                Arguments.of(createTree(3), 2),
                Arguments.of(createTree(4), 3),
                Arguments.of(createTree(5), 3), // Here
                Arguments.of(createTree(6), 3),
                Arguments.of(createTree(7), 3));
    }


    @ParameterizedTest
    @MethodSource("scenarios")
    void testReBalance(FileTree fileTree, int expectedHeight) {
        fileTree.reBalance();
        assertThat(fileTree.getHeight()).isEqualTo(expectedHeight);
    }

    @Test
    void testPersisted() throws IOException {
        FileTree<Long> fileTree = createTree(new File("c:\\temp\\checkfile.txt"), 4);
        fileTree.reBalance();
        fileTree.close();
    }

    private static FileTree<Long> createTree(int elements) throws IOException {
        return createTree(File.createTempFile("___", ".tmp"), elements);
    }

    private static FileTree<Long> createTree(File file, int elements) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        Comparator<Long> comparator = new Comparator<Long>() {

            @Override
            public int compare(Long o1, Long o2) {
                return Long.compare(o1, o2);
            }
        };

        Converter<Long> converter = new Converter<Long>() {

            @Override
            public byte[] serialize(Long input) {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(input);
                return buffer.array();
            }

            @Override
            public Long deserialize(byte[] input) {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.put(input);
                buffer.flip();//need flip
                return buffer.getLong();
            }

            @Override
            public int getSize() {
                return Long.BYTES;
            }
        };

        FileTree<Long> fileTree = new FileTree<>(file, comparator, converter);

        for (int i = 0; i < elements; i++) {
            fileTree.add((long) i);
        }

        return fileTree;
    }
}
