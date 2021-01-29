package org.palermo.circuit.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Comparator;

public class FileTree<T> {

    private RandomAccessFile raf;
    private final Comparator<T> comparator;
    private final Converter<T> converter;

    public FileTree(File file, Comparator<T> comparator, Converter converter) {
        try {
            this.raf = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.comparator = comparator;
        this.converter = converter;
    }

    public synchronized boolean contains(T item) {
        try {
            if (this.raf.length() == 0) {
                return false;
            }

            long offset = getRootOffset();
            byte[] buffer = new byte[converter.getSize()];

            do {
                raf.seek(offset);
                raf.readFully(buffer);
                int result = comparator.compare(item, converter.deserialize(buffer));
                if (result == 0) {
                    return true;
                }
                if (result > 0) { // first argument is greater than the second
                    raf.skipBytes(Long.BYTES);
                }
                offset = raf.readLong();
            } while (offset != 0x00);

            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long getRootOffset() throws IOException {
        this.raf.seek(Long.BYTES);
        return this.raf.readLong();
    }

    public synchronized boolean add(T item) {

        try {
            if (this.raf.length() == 0) {
                this.raf.writeLong(1); // quantity of elements
                this.raf.writeLong(2 * Long.BYTES); // offset of the root
                this.raf.write(converter.serialize(item));
                this.raf.writeLong(0x00);
                this.raf.writeLong(0x00);
                return true;
            }
            else {
                this.raf.seek(Long.BYTES);
                boolean added = add(this.raf.readLong(), item);

                if (added) {
                    this.raf.seek(0);
                    long quantity = this.raf.readLong();
                    this.raf.seek(0);
                    this.raf.writeLong(quantity + 1);
                }

                return added;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean add(long offset, T item) throws IOException {
        byte[] buffer = new byte[converter.getSize()];
        this.raf.seek(offset);
        this.raf.readFully(buffer);
        int result = comparator.compare(item, converter.deserialize(buffer));

        if (result == 0) {
            return false;
        }

        if (result > 0) { // first argument is greater than the second
            this.raf.skipBytes(Long.BYTES);
        }

        long newOffset = this.raf.readLong();
        if (newOffset != 0x00) {
            return add(newOffset, item);
        }

        this.raf.seek(this.raf.getFilePointer() - Long.BYTES);
        this.raf.writeLong(this.raf.length());

        this.raf.seek(this.raf.length());
        this.raf.write(converter.serialize(item));
        this.raf.writeLong(0x00);
        this.raf.writeLong(0x00);

        return true;
    }

    protected int getHeight() {
        try {
            if (this.raf.length() == 0) {
                return 0;
            }
            this.raf.seek(Long.BYTES);
            return getHeight(this.raf.readLong());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getHeight(long offset) throws IOException {
        if (offset == 0x00) {
            return 0;
        }
        else {
            this.raf.seek(offset);
            this.raf.skipBytes(converter.getSize());
            long leftOffset = this.raf.readLong();
            long rightOffset = this.raf.readLong();
            return Math.max(getHeight(leftOffset), getHeight(rightOffset)) + 1;
        }
    }
    public long getSize() {
        try {
            if (this.raf.length() == 0) {
                return 0;
            }
            this.raf.seek(0);
            return this.raf.readLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reBalance() {
        try {
            if (this.raf.length() > 0) {
                this.raf.seek(Long.BYTES);
                this.walk(Long.BYTES, this.raf.readLong());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void walk(long position, long offset) throws IOException {

        this.raf.seek(offset);
        this.raf.skipBytes(converter.getSize());
        long leftOffset = this.raf.readLong();
        long rightOffset = this.raf.readLong();

        if (leftOffset != 0x00) {
            walk(offset + converter.getSize(), leftOffset);
        }
        if (rightOffset != 0x00) {
            walk(offset + converter.getSize() + Long.BYTES, rightOffset);
        }

        this.reBalance(position, offset);
    }

    private void reBalance(long position, long offset) throws IOException {
        System.out.println("rebalance");

        this.raf.seek(offset);
        this.raf.skipBytes(converter.getSize());
        long leftOffset = this.raf.readLong();
        long rightOffset = this.raf.readLong();


        int balanceFactor = getBalanceFactor(offset);

        if (balanceFactor == 2) {
            if (getBalanceFactor(rightOffset) >= 0) {
                this.rotateRightRight(position, offset);
            }
            else {
                this.rotateRightLeft(position, offset);
            }

        } else if (balanceFactor == -2) {
            if (getBalanceFactor(leftOffset) <= 0) {
                this.rotateLeftLeft(position, offset);
            }
            else {
                this.rotateLeftRight(position, offset);
            }
        }

        /*
        if (Math.abs(getBalanceFactor(offset)) >= 2) {
            throw new RuntimeException("Fail to balance");
        }
        */
    }

    private int getBalanceFactor(long offset) throws IOException {
        this.raf.seek(offset);
        this.raf.skipBytes(converter.getSize());
        long leftOffset = this.raf.readLong();
        long rightOffset = this.raf.readLong();

        System.out.println("Factor: " + (getHeight(rightOffset) - getHeight(leftOffset)));
        return getHeight(rightOffset) - getHeight(leftOffset);
    }

    private void rotateRightRight(long parentPosition, long offset) throws IOException {
        System.out.println("rotateRightRight");
        this.raf.seek(offset);
        this.raf.skipBytes(converter.getSize() + Long.BYTES);
        long rightOffset = this.raf.readLong();
        long rightPosition = offset + converter.getSize() + Long.BYTES;

        this.raf.seek(rightOffset);
        this.raf.skipBytes(converter.getSize());
        long childLeftOffset = this.raf.readLong();
        long childLeftPosition = rightOffset + converter.getSize();

        setPositionValue(parentPosition, rightOffset);
        setPositionValue(childLeftPosition, offset);
        setPositionValue(rightPosition, childLeftOffset);
    }

    private void rotateRightLeft(long parentPosition, long offset) throws IOException {
        System.out.println("rotateRightLeft");

        this.raf.seek(offset);
        this.raf.skipBytes(converter.getSize() + Long.BYTES);
        long rightOffset = this.raf.readLong();
        long rightPosition = offset + converter.getSize() + Long.BYTES;

        this.raf.seek(rightOffset);
        this.raf.skipBytes(converter.getSize());
        long rightLeftOffset = this.raf.readLong();
        long rightLeftPosition = rightOffset + converter.getSize();

        this.raf.seek(rightLeftOffset);
        this.raf.skipBytes(converter.getSize());
        long rightLeftLeftOffset = this.raf.readLong();
        long rightLeftRightOffset = this.raf.readLong();
        long rightLeftLeftPosition = rightLeftOffset + converter.getSize();
        long rightLeftRightPosition = rightLeftOffset + converter.getSize() + Long.BYTES;

        setPositionValue(parentPosition, rightLeftOffset);
        setPositionValue(rightLeftLeftPosition, offset);
        setPositionValue(rightLeftRightPosition, rightOffset);
        setPositionValue(rightPosition, rightLeftLeftOffset);
        setPositionValue(rightLeftPosition, rightLeftRightOffset);
    }
    private void rotateLeftLeft(long parentPosition, long offset) throws IOException {
        System.out.println("rotateLeftLeft");
        this.raf.seek(offset);
        this.raf.skipBytes(converter.getSize());

        long leftOffset = this.raf.readLong();
        long leftPosition = offset + converter.getSize();

        this.raf.seek(leftOffset);
        this.raf.skipBytes(converter.getSize());
        this.raf.skipBytes(Long.BYTES);
        long childRightOffset = this.raf.readLong();
        long childRightPosition = leftOffset + converter.getSize() + Long.BYTES;

        setPositionValue(parentPosition, leftOffset);
        setPositionValue(childRightPosition, offset);
        setPositionValue(leftPosition, childRightOffset);
    }
    private void rotateLeftRight(long parentPosition, long offset) throws IOException {
        System.out.println("rotateLeftRight");
        this.raf.seek(offset);
        this.raf.skipBytes(converter.getSize());
        long leftOffset = this.raf.readLong();
        long leftPosition = offset + converter.getSize();

        this.raf.seek(leftOffset);
        this.raf.skipBytes(converter.getSize() + Long.BYTES);
        long leftRightOffset = this.raf.readLong();
        long leftRightPosition = leftOffset + converter.getSize() + Long.BYTES;

        this.raf.seek(leftRightOffset);
        this.raf.skipBytes(converter.getSize());
        long leftRightLeftOffset = this.raf.readLong();
        long leftRightRightOffset = this.raf.readLong();
        long leftRightLeftPosition = leftRightOffset + converter.getSize();
        long leftRightRightPosition = leftRightOffset + converter.getSize() + Long.BYTES;

        setPositionValue(parentPosition, leftRightOffset);
        setPositionValue(leftRightLeftPosition, offset);
        setPositionValue(leftRightRightPosition, leftOffset);
        setPositionValue(leftPosition, leftRightLeftOffset);
        setPositionValue(leftRightPosition, leftRightRightOffset);
    }

    private void setPositionValue(long position, long offset) throws IOException {
        this.raf.seek(position);
        this.raf.writeLong(offset);
    }

    public void close() {
        try {
            this.raf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
