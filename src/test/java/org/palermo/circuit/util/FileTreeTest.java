package org.palermo.circuit.util;

import org.junit.jupiter.api.Test;
import org.palermo.circuit.collection.Converter;
import org.palermo.circuit.collection.FileOrderedSet;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class FileTreeTest {

    /*
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

        FileTreeSet<Long> fileTreeSet = new FileTreeSet<Long>(File.createTempFile("tree_", ".tmp"), File.createTempFile("data_", ".tmp"), comparator, converter);
        assertThat(fileTreeSet.size()).isEqualTo(0);

        System.out.println(fileTreeSet.getHeight());

        fileTreeSet.add(0L);
        assertThat(fileTreeSet.size()).isEqualTo(1);
        assertThat(fileTreeSet.contains(0L)).isEqualTo(true);
        assertThat(fileTreeSet.contains(1L)).isEqualTo(false);

        System.out.println(fileTreeSet.getHeight());

        fileTreeSet.add(1L);
        assertThat(fileTreeSet.getSize()).isEqualTo(2);
        assertThat(fileTreeSet.contains(0L)).isEqualTo(true);
        assertThat(fileTreeSet.contains(1L)).isEqualTo(true);
        assertThat(fileTreeSet.contains(2L)).isEqualTo(false);

        System.out.println(fileTreeSet.getHeight());

        fileTreeSet.add(2L);
        assertThat(fileTreeSet.size()).isEqualTo(3);
        assertThat(fileTreeSet.contains(0L)).isEqualTo(true);
        assertThat(fileTreeSet.contains(1L)).isEqualTo(true);
        assertThat(fileTreeSet.contains(2L)).isEqualTo(true);
        assertThat(fileTreeSet.contains(3L)).isEqualTo(false);

        for (int i = 1; i < 100; i++) {
            fileTreeSet.add((long) 100 + i);
            System.out.println("Adding: " + i);
        }

        System.out.println(fileTreeSet.getHeight());
        fileTreeSet.reBalance();
        System.out.println("Size: " + fileTreeSet.size() + " Height: " + fileTreeSet.getHeight());
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
                Arguments.of(createTree(7), 4),
                Arguments.of(createTree(8), 4),
                Arguments.of(createTree(9), 4),
                Arguments.of(createTree(10), 4),
                Arguments.of(createTree(11), 4),
                Arguments.of(createTree(12), 4),
                Arguments.of(createTree(13), 4),
                Arguments.of(createTree(14), 4),
                Arguments.of(createTree(15), 4),
                Arguments.of(createTree(16), 4)
                );
    }


    @ParameterizedTest
    @MethodSource("scenarios")
    void testReBalance(FileTreeSet oldFileTree, int expectedHeight) {
        oldFileTree.reBalance();
        assertThat(oldFileTree.getHeight()).isEqualTo(expectedHeight);
    }
     */

    @Test
    void testContains() throws IOException {
        int size = 1000;
        FileOrderedSet<Long> fileOrderedSet = createTree(new File("c:\\temp\\tree.txt"), new File("c:\\temp\\data.txt"), size);
        assertThat(fileOrderedSet.size()).isEqualTo(size);
        for (int i = 0; i < size; i++) {
            assertThat(fileOrderedSet.contains((long) i)).isEqualTo(true);
        }

        assertThat(fileOrderedSet.contains((long) size)).isEqualTo(false);
        assertThat(fileOrderedSet.contains((long) size + 1)).isEqualTo(false);
    }

    @Test
    void testContainsRandom() throws IOException {
        int size = 1000;
        FileOrderedSet<Long> fileOrderedSet = createTree(new File("c:\\temp\\tree.txt"), new File("c:\\temp\\data.txt"));
        TreeSet<Long> treeSet = new TreeSet<Long>();

        for (int i = 0; i < size; i++) {
            fileOrderedSet.add((long) i);
            treeSet.add((long) i);
        }

        assertThat(treeSet.size()).isEqualTo(fileOrderedSet.size());
        for (Long l : treeSet) {
            assertThat(fileOrderedSet.contains(l)).isEqualTo(true);
        }
    }


    @Test
    void testPersisted() throws IOException {
        FileOrderedSet<Long> oldFileTree = createTree(new File("c:\\temp\\tree.txt"), new File("c:\\temp\\data.txt"), 6);
        //oldFileTree.reBalance();
        //oldFileTree.close();
    }

    @Test
    void testSelect() throws IOException {
        Random random = new Random();
        FileOrderedSet<Long> fileOrderedSet = createTree(new File("c:\\temp\\tree.txt"), new File("c:\\temp\\data.txt"), 6);

        for (int i = 0; i < 100; i++) {
            fileOrderedSet.add(random.nextLong());
        }

        for (int i = 0; i < 100; i++) {
            System.out.println(fileOrderedSet.select(i));
        }

        //oldFileTree.reBalance();
        //oldFileTree.close();
    }


    private static FileOrderedSet<Long> createTree(int elements) throws IOException {
        return createTree(File.createTempFile("tree_", ".tmp"), File.createTempFile("data_", ".tmp"), elements);
    }

    private static FileOrderedSet<Long> createTree(File treeFile, File dataFile, int elements) throws IOException {
        if (treeFile.exists()) {
            treeFile.delete();
        }
        if (dataFile.exists()) {
            dataFile.delete();
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

        FileOrderedSet<Long> oldFileTree = new FileOrderedSet<Long>(treeFile, dataFile, comparator, converter);

        for (int i = 0; i < elements; i++) {
            oldFileTree.add((long) i);
        }

        return oldFileTree;
    }

    private static FileOrderedSet<Long> createTree(File treeFile, File dataFile) throws IOException {
        if (treeFile.exists()) {
            treeFile.delete();
        }
        if (dataFile.exists()) {
            dataFile.delete();
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

        return new FileOrderedSet<Long>(treeFile, dataFile, comparator, converter);
    }
}
