package org.palermo.circuit.collection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Comparator;

/*
https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/AVLTreeST.java.html
 */
public class FileOrderedSet<T> implements OrderedSet<T> {

    private RandomAccessFile tree;
    private RandomAccessFile data;
    private final Comparator<T> comparator;
    private final Converter<T> converter;

    public FileOrderedSet(File tree, File data, Comparator<T> comparator, Converter<T> converter) {
        try {
            this.tree = new RandomAccessFile(tree, "rw");
            this.data = new RandomAccessFile(data, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.comparator = comparator;
        this.converter = converter;
    }

    public boolean add(T value) {
        if (value == null) throw new IllegalArgumentException("first argument to put() is null");
        try {
            if (this.tree.length() == 0) {
                writeRootOffset(Long.BYTES);
                writeNode(value);
                return true;
            }
            long size = size();
            writeRootOffset(add(readRootOffset(), value));
            return size < size(readRootOffset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long add(Long offset, T value) throws IOException {
        if (offset == 0x00) {
            return writeNode(value);
        }
        int cmp = comparator.compare(value, readValue(offset));
        if (cmp < 0) {
            this.writeLeftOffset(offset, add(this.readLeftOffset(offset), value));
        }
        else if (cmp > 0) {
            this.writeRightOffset(offset, add(this.readRightOffset(offset), value));
        }
        else {
            return offset;
        }

        long leftOffset = this.readLeftOffset(offset);
        long rightOffset = this.readRightOffset(offset);
        this.writeSize(offset, 1 + size(leftOffset) + size(rightOffset));
        this.writeHeight(offset, 1 + Math.max(height(leftOffset), height(rightOffset)));
        return balance(offset);
        // return offset;
    }

    public long size() {
        try {
            return size(readRootOffset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long size(long offset) throws IOException {
        if (offset == 0x00) return 0;
        return readSize(offset);
    }

    @Override
    public int height() {
        try {
            if (this.tree.length() == 0) {
                return -1;
            }
            return this.height(this.readRootOffset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int height(long offset) throws IOException {
        if (offset == 0x00) return -1;
        return readHeight(offset);
    }

    private long writeNode(T value) throws IOException {
        long offset = this.tree.length();
        this.tree.seek(offset);
        this.tree.writeInt(0x00); // Height
        this.tree.writeLong(0x01); // Size
        this.tree.writeLong(0x00); // leftOffset
        this.tree.writeLong(0x00); // rightOffset
        this.tree.writeLong(writeValueToData(value));
        return offset;
    }

    private long readRootOffset() throws IOException {
        this.tree.seek(0x00);
        return this.tree.readLong();
    }

    private int readHeight(long offset) throws IOException {
        this.tree.seek(offset);
        return this.tree.readInt();
    }

    private long readSize(long offset) throws IOException {
        this.tree.seek(offset + Integer.BYTES);
        return this.tree.readLong();
    }

    private long readLeftOffset(long offset) throws IOException {
        this.tree.seek(offset + Integer.BYTES + Long.BYTES);
        return this.tree.readLong();
    }

    private long readRightOffset(long offset) throws IOException {
        this.tree.seek(offset + Integer.BYTES + 2 * Long.BYTES);
        return this.tree.readLong();
    }

    private T readValue(long offset) throws IOException {
        this.tree.seek(offset + Integer.BYTES + 3 * Long.BYTES);
        return this.readValueFromData(this.tree.readLong());
    }

    private void writeRootOffset(long offset) throws IOException {
        this.tree.seek(0x00);
        this.tree.writeLong(offset);
    }

    private void writeHeight(long offset, int height) throws IOException {
        this.tree.seek(offset);
        this.tree.writeInt(height);
    }

    private void writeSize(long offset, long size) throws IOException {
        this.tree.seek(offset + Integer.BYTES);
        this.tree.writeLong(size);
    }

    private void writeLeftOffset(long offset, long leftOffset) throws IOException {
        this.tree.seek(offset + Integer.BYTES + Long.BYTES);
        this.tree.writeLong(leftOffset);
    }

    private void writeRightOffset(long offset, long rightOffset) throws IOException {
        this.tree.seek(offset + Integer.BYTES + 2 * Long.BYTES);
        this.tree.writeLong(rightOffset);
    }

    private void setPositionValue(long position, long offset) throws IOException {
        this.data.seek(position);
        this.data.writeLong(offset);
    }

    private long writeValueToData(T value) throws IOException {
        long offset = this.data.length();
        byte[] serializedData = this.converter.serialize(value);
        this.data.seek(offset);
        this.data.writeInt(serializedData.length);
        this.data.write(serializedData);
        return offset;
    }

    private T readValueFromData(long offset) throws IOException {
        this.data.seek(offset);
        byte[] buffer = new byte[this.data.readInt()];
        this.data.readFully(buffer);
        return converter.deserialize(buffer);
    }

    public boolean contains(T value) {
        try {
            if (value == null) {
                throw new IllegalArgumentException("argument to get() is null");
            }
            if (this.tree.length() == 0) {
                return false;
            }
            return contains(this.readRootOffset(), value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean contains(long offset, T value) throws IOException {
        if (offset == 0x00) return false;
        int cmp = comparator.compare(value, readValue(offset));
        if (cmp < 0) return contains(this.readLeftOffset(offset), value);
        else if (cmp > 0) return contains(this.readRightOffset(offset), value);
        else return true;
    }

    private long balance(long offset) throws IOException {
        int balanceFactor = balanceFactor(offset);
        if (balanceFactor < -1) {
            if (balanceFactor(readRightOffset(offset)) > 0) {
                writeRightOffset(offset, rotateRight(readRightOffset(offset)));
            }
            return rotateLeft(offset);
        }
        else if (balanceFactor > 1) {
            if (balanceFactor(readLeftOffset(offset)) < 0) {
                writeLeftOffset(offset, rotateLeft(readLeftOffset(offset)));
            }
            return rotateRight(offset);
        }
        return offset;
    }

    private long rotateRight(long offset) throws IOException {
        long y = this.readLeftOffset(offset);
        this.writeLeftOffset(offset, this.readRightOffset(y));
        this.writeRightOffset(y, offset);
        this.writeSize(y, this.size(offset));
        this.writeSize(offset, 1 + this.size(this.readLeftOffset(offset)) + this.size(this.readRightOffset(offset)));
        this.writeHeight(offset, 1 + Math.max(this.height(this.readLeftOffset(offset)), this.height(this.readRightOffset(offset))));
        this.writeHeight(y, 1 + Math.max(this.height(this.readLeftOffset(y)), this.height(this.readRightOffset(y))));
        return y;
    }

    private long rotateLeft(long offset) throws IOException {
        long y = this.readRightOffset(offset);
        this.writeRightOffset(offset, this.readLeftOffset(y));
        this.writeLeftOffset(y, offset);
        this.writeSize(y, this.size(offset));
        this.writeSize(offset, 1 + this.size(this.readLeftOffset(offset)) + this.size(this.readRightOffset(offset)));
        this.writeHeight(offset, 1 + Math.max(this.height(this.readLeftOffset(offset)), this.height(this.readRightOffset(offset))));
        this.writeHeight(y, 1 + Math.max(this.height(this.readLeftOffset(y)), this.height(this.readRightOffset(y))));
        return y;
    }

    private int balanceFactor(long offset) throws IOException {
        return height(readLeftOffset(offset)) - height(readRightOffset(offset));
    }

    @Override
    public T select(long k) {
        try {
            if (k < 0 || k >= size()) throw new IllegalArgumentException("k is not in range 0-" + (size() - 1));
            long offset = select(this.readRootOffset(), k);
            return this.readValue(offset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long select(long offset, long k) throws IOException {
        if (offset == 0x00) throw new RuntimeException(String.format("Element not found on index %d", k));
        long leftOffset = this.readLeftOffset(offset);
        long t = this.size(leftOffset);
        if (t > k) return select(leftOffset, k);
        else if (t < k) return select(this.readRightOffset(offset), k - t - 1);
        else return offset;
    }
}
