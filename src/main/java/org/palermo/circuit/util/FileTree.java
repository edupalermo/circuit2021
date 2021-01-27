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
            this.raf = new RandomAccessFile(file, "rd");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.comparator = comparator;
        this.converter = converter;
    }

    public synchronized boolean exists(T item) {
        try {
            if (this.raf.length() == 0) {
                return false;
            }

            long offset = 0;
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

    public synchronized boolean add(T item) {

        try {
            if (this.raf.length() == 0) {
                this.raf.writeLong(0); // quantity of elements
                this.raf.write(converter.serialize(item));
                this.raf.writeLong(0x00);
                this.raf.writeLong(0x00);
                return true;
            }
            else {
                boolean added = add(Long.BYTES, item);

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
}
