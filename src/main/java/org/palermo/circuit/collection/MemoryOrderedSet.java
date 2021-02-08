package org.palermo.circuit.collection;

import java.io.IOException;
import java.util.Comparator;

public class MemoryOrderedSet<T> implements OrderedSet<T> {

    private Node<T> root = null;
    private final Comparator<T> comparator;

    public MemoryOrderedSet(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public boolean add(T value) {
        if (value == null) throw new IllegalArgumentException("first argument to put() is null");
        if (this.root == null) {
            this.root = Node.of(value);
            return true;
        }
        long size = size();
        this.root = add(this.root, value);
        return size < size();
    }

    private Node<T> add(Node<T> node, T value) {
        if (node == null) {
            return Node.of(value);
        }
        int cmp = comparator.compare(value, node.data);
        if (cmp < 0) {
            node.left = add(node.left, value);
        }
        else if (cmp > 0) {
            node.right = add(node.right, value);
        }
        else {
            return node;
        }
        node.size = 1 + size(node.left) + size(node.right);
        node.height = 1 + Math.max(height(node.left), height(node.right));
        return balance(node);
        // return offset;
    }

    @Override
    public int height() {
        return this.height(this.root);
    }

    private int height(Node<T> node) {
        if (node == null) return -1;
        return node.height;
    }

    private Node<T> balance(Node<T> node) {
        int balanceFactor = balanceFactor(node);
        if (balanceFactor < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        else if (balanceFactor > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        return node;
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> y = node.left;
        node.left = y.right;
        y.right = node;
        y.size = this.size(node);
        node.size = 1 + this.size(node.left) + this.size(node.right);
        node.height = 1 + Math.max(this.height(node.left), this.height(node.right));
        y.height = 1 + Math.max(this.height(y.left), this.height(y.right));
        return y;
    }

    private Node<T> rotateLeft(Node<T> node) {
        Node<T> y = node.right;
        node.right = y.left;
        y.left = node;
        y.size = this.size(node);
        node.size = 1 + this.size(node.left) + this.size(node.right);
        node.height = 1 + Math.max(this.height(node.left), this.height(node.right));
        y.height = 1 + Math.max(this.height(y.left), this.height(y.right));
        return y;
    }

    private int balanceFactor(Node<T> node) {
        return height(node.left) - height(node.right);
    }

    @Override
    public boolean contains(T value) {
        try {
            if (value == null) {
                throw new IllegalArgumentException("argument to get() is null");
            }
            if (this.root == null) {
                return false;
            }
            return contains(this.root, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean contains(Node<T> node, T value) throws IOException {
        if (node == null) return false;
        int cmp = comparator.compare(value, node.data);
        if (cmp < 0) return contains(node.left, value);
        else if (cmp > 0) return contains(node.right, value);
        else return true;
    }


    @Override
    public long size() {
        return size(this.root);
    }

    private long size(Node node) {
        if (node == null) return 0;
        return node.size;
    }

    @Override
    public T select(long k) {
        if (k < 0 || k >= size()) throw new IllegalArgumentException("k is not in range 0-" + (size() - 1));
        return select(this.root, k).data;
    }

    private Node<T> select(Node<T> node, long k) {
        if (node == null) throw new RuntimeException(String.format("Element not found on index %d", k));
        long t = this.size(node.left);
        if (t > k) return select(node.left, k);
        else if (t < k) return select(node.right, k - t - 1);
        else return node;
    }

    private static class Node<T> {
        private Node left;
        private Node right;
        private int height;
        private long size;
        private T data;

        public static <T> Node<T> of(T data) {
            Node<T> node = new Node<T>();
            node.right =  null;
            node.left =  null;
            node.height =  0;
            node.size =  1L;
            node.data =  data;
            return node;
        }
    }
}
