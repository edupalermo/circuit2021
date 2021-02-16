package org.palermo.circuit.collection;

public class MixedOrderedSet implements OrderedSet {

    private Object readSemaphore = new Object();
    private Object writeSemaphore = new Object();
    private int ongoingReads = 0;
    private boolean ongoingWrite = false;


    @Override
    public boolean add(Object value) {

        writePreCondition();

        execute("Add executed");

        writePostCondition();

        return false;
    }

    @Override
    public long size() {

        this.readPreCondition();

        execute("Size executed");

        this.readPostCondition();


        return 0;
    }

    @Override
    public boolean contains(Object value) {

        this.readPreCondition();

        execute("Contains executed");

        this.readPostCondition();

        return false;
    }

    @Override
    public Object select(long index) {
        this.readPreCondition();

        execute("Select executed");

        this.readPostCondition();

        return null;
    }

    @Override
    public int height() {
        this.readPreCondition();

        execute("Height executed");

        this.readPostCondition();

        return 0;
    }

    private synchronized void readPreCondition() {
        try {
            while (ongoingWrite == true) {
                this.wait();
            }
            ongoingReads++;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void readPostCondition() {
            ongoingReads--;
            notifyAll();
    }

    private synchronized void writePreCondition() {
        try {
            while (ongoingWrite == true || ongoingReads > 0) {
                this.wait();
            }
            ongoingWrite = true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void writePostCondition() {
        ongoingWrite = false;
        notifyAll();
    }


    private void execute(String message) {
        try {
            System.out.println(message);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
